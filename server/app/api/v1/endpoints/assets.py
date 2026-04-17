from typing import Any, List, Optional
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from app import crud, schemas, models
from app.api import deps
from app.models.asset import AssetStatus

router = APIRouter()

@router.get("/", response_model=List[schemas.Asset])
async def read_assets(
    db: AsyncSession = Depends(deps.get_db),
    skip: int = 0,
    limit: int = 100,
    status: Optional[AssetStatus] = None,
    current_user: models.User = Depends(deps.get_current_active_user),
) -> Any:
    """
    Retrieve assets. (Filtered by status for admin/moderation)
    """
    assets = await crud.asset.get_multi(db, skip=skip, limit=limit, status=status)
    return assets

@router.put("/{id}/status", response_model=schemas.Asset)
async def update_asset_status(
    *,
    db: AsyncSession = Depends(deps.get_db),
    id: int,
    status_in: schemas.AssetUpdate,
    current_user: models.User = Depends(deps.get_current_active_admin),
) -> Any:
    """
    Update asset status (Approve/Reject). Admin only.
    """
    asset = await crud.asset.get(db, id=id)
    if not asset:
        raise HTTPException(status_code=404, detail="Asset not found")
    asset = await crud.asset.update_status(db, db_obj=asset, status=status_in.status)
    
    # 實時通知創作者
    from app.websocket.connection_manager import manager
    await manager.send_personal_message(
        {
            "type": "asset_update",
            "asset_id": asset.id,
            "status": asset.status,
            "message": f"你的資產 '{asset.title}' 已被管理員{asset.status}"
        },
        user_id=asset.creator_id
    )
    
    return asset
