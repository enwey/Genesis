from typing import Any, List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app import crud, schemas, models
from app.api import deps
from app.models.asset import TradeRequestStatus

router = APIRouter()

@router.get("/assets", response_model=List[schemas.Asset])
async def get_market_assets(
    *,
    db: AsyncSession = Depends(deps.get_db),
    query: str = "",
    skip: int = 0,
    limit: int = 50,
) -> Any:
    """獲取大集市資產列表"""
    stmt = select(models.Asset).where(models.Asset.status == "approved")
    
    if query:
        # 支持在 prompt 或 attributes 中搜索
        stmt = stmt.where(models.Asset.prompt.ilike(f"%{query}%"))
    
    stmt = stmt.offset(skip).limit(limit)
    result = await db.execute(stmt)
    return result.scalars().all()

@router.post("/{asset_id}/list")
async def list_asset_for_sale(
    *,
    db: AsyncSession = Depends(deps.get_db),
    asset_id: int,
    price: int,
    current_user: models.User = Depends(deps.get_current_active_user),
) -> Any:
    """掛牌出售資產"""
    asset = await crud.asset.get(db, id=asset_id)
    if not asset or asset.creator_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized")
    
    asset.is_listed = True
    asset.price = price
    await db.commit()
    return {"status": "success", "message": f"Asset listed for {price} StarCoin"}

@router.post("/{asset_id}/offer")
async def make_buy_offer(
    *,
    db: AsyncSession = Depends(deps.get_db),
    asset_id: int,
    bid_price: int,
    current_user: models.User = Depends(deps.get_current_active_user),
) -> Any:
    """發起求購報價（預付資金託管）"""
    if current_user.star_coin < bid_price:
        raise HTTPException(status_code=400, detail="Insufficient StarCoin for escrow")
    
    # 扣除並託管資金
    current_user.star_coin -= bid_price
    
    offer = models.TradeRequest(
        asset_id=asset_id,
        buyer_id=current_user.id,
        bid_price=bid_price,
        status=TradeRequestStatus.PENDING
    )
    db.add(offer)
    await db.commit()
    
    # 通過 WebSocket 通知所有者
    from app.websocket.connection_manager import manager
    asset = await crud.asset.get(db, id=asset_id)
    await manager.send_personal_message(
        {"type": "new_offer", "asset_id": asset_id, "bid": bid_price, "message": "有人對你的資產發起了報價！"},
        user_id=asset.creator_id
    )
    
    return {"status": "success", "offer_id": offer.id}

@router.post("/offers/{offer_id}/respond")
async def respond_to_offer(
    *,
    db: AsyncSession = Depends(deps.get_db),
    offer_id: int,
    accept: bool,
    current_user: models.User = Depends(deps.get_current_active_user),
) -> Any:
    """所有者回應報價"""
    result = await db.execute(select(models.TradeRequest).filter(models.TradeRequest.id == offer_id))
    offer = result.scalars().first()
    
    if not offer:
        raise HTTPException(status_code=404, detail="Offer not found")
        
    asset = await crud.asset.get(db, id=offer.asset_id)
    if asset.creator_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized")

    result_buyer = await db.execute(select(models.User).filter(models.User.id == offer.buyer_id))
    buyer = result_buyer.scalars().first()

    if accept:
        # 1. 接受報價：資金轉給賣家（扣除 10%）
        current_user.star_coin += int(offer.bid_price * 0.9)
        # 2. 所有權轉移
        asset.creator_id = buyer.id
        asset.is_listed = False
        offer.status = TradeRequestStatus.ACCEPTED
    else:
        # 3. 拒絕報價：資金退還給買家
        buyer.star_coin += offer.bid_price
        offer.status = TradeRequestStatus.REJECTED

    await db.commit()
    return {"status": "success", "new_status": offer.status}
