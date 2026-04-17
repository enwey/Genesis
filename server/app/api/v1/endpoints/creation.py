from typing import Any
from fastapi import APIRouter, Depends, HTTPException, BackgroundTasks
from sqlalchemy.ext.asyncio import AsyncSession
from app import crud, schemas, models
from app.api import deps
import asyncio
import random

router = APIRouter()

async def simulate_ai_generation(db_session_factory, asset_id: int, prompt: str):
    """
    模擬異步 AI 生成過程
    """
    await asyncio.sleep(10) # 模擬 AI 處理時間
    
    async with db_session_factory() as db:
        asset = await crud.asset.get(db, id=asset_id)
        if asset:
            # 這裡在生產環境會調用 Meshy 並獲取真實 URL
            asset.model_url = f"https://assets.genesis.com/models/{asset_id}.glb"
            asset.preview_url = f"https://assets.genesis.com/previews/{asset_id}.png"
            # 自動審核示例（或保持 PENDING 等待管理員）
            asset.status = "pending" 
            await db.commit()

@router.post("/generate", response_model=schemas.Asset)
async def generate_asset(
    *,
    db: AsyncSession = Depends(deps.get_db),
    creation_in: schemas.AssetBase,
    current_user: models.User = Depends(deps.get_current_active_user),
    background_tasks: BackgroundTasks
) -> Any:
    """
    發起 AI 3D 生成任務。消耗 50 Mana。
    """
    if current_user.mana < 50:
        raise HTTPException(status_code=400, detail="Insufficient Mana")

    # 1. 扣除 Mana
    current_user.mana -= 50
    
    # 2. 創建資產佔位記錄
    from app.services.appraisal import appraise_asset
    attributes = appraise_asset(creation_in.prompt)
    
    asset_in = schemas.AssetCreate(
        **creation_in.dict(),
        creator_id=current_user.id
    )
    asset_data = asset_in.dict()
    db_obj = models.Asset(**asset_data, attributes=attributes)
    db.add(db_obj)
    await db.commit()
    await db.refresh(db_obj)
    
    # 3. 啟動後臺任務
    from app.db.session import async_session
    background_tasks.add_task(simulate_ai_generation, async_session, db_obj.id, creation_in.prompt)
    
    return db_obj
