from typing import Any
from fastapi import APIRouter, Depends, HTTPException, BackgroundTasks
from sqlalchemy.ext.asyncio import AsyncSession
from app import crud, schemas, models
from app.api import deps
import asyncio
import random

router = APIRouter()

from app.websocket.connection_manager import manager
import json

async def simulate_ai_generation(user_id: int, asset_id: int, prompt: str, db_session_factory):
    """
    實例化異步 AI 生成過程，並通過 WebSocket 推送實時進度。
    """
    stages = [
        ("Analyzing your prompt...", 0.1),
        ("Allocating GPU resources...", 0.25),
        ("Forging 3D geometry...", 0.5),
        ("Baking PBR textures...", 0.75),
        ("Finalizing your creation...", 0.9)
    ]

    for message, progress in stages:
        await asyncio.sleep(2) # 模擬每一步的計算耗時
        # 向 WebSocket 推送進度
        await manager.send_personal_message(
            {"type": "progress", "progress": progress, "message": message, "asset_id": asset_id},
            user_id
        )

    async with db_session_factory() as db:
        from sqlalchemy import update
        # 這裡模擬調用 Meshy API 獲取結果
        model_url = f"https://models.genesis-platform.com/v1/{asset_id}.glb"
        preview_url = f"https://models.genesis-platform.com/v1/{asset_id}.png"
        
        # 更新數據庫狀態
        from app.models.asset import Asset
        stmt = update(Asset).where(Asset.id == asset_id).values(
            model_url=model_url,
            preview_url=preview_url,
            status="approved" # 自動標記為已批准以供測試
        )
        await db.execute(stmt)
        await db.commit()
    
    # 發送最終完成通知
    await manager.send_personal_message(
        {"type": "success", "progress": 1.0, "message": "Creation complete!", "asset_id": asset_id},
        user_id
    )

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

    # 1. 扣除 Mana (需持久化到 DB)
    current_user.mana -= 50
    db.add(current_user)
    
    # 2. 創建資產佔位記錄
    from app.services.appraisal import appraise_asset
    attributes = appraise_asset(creation_in.prompt)
    
    # 確保 attributes 包含 owner 標記供前端判斷
    attributes["is_owner"] = "true"
    attributes["price"] = str(random.randint(100, 1000))
    
    asset_obj = models.Asset(
        prompt=creation_in.prompt,
        creator_id=current_user.id,
        status="pending",
        attributes=attributes
    )
    db.add(asset_obj)
    await db.commit()
    await db.refresh(asset_obj)
    
    # 3. 啟動後臺任務 (傳入 user_id 用於 WebSocket 通信)
    from app.db.session import async_session
    background_tasks.add_task(
        simulate_ai_generation, 
        current_user.id, 
        asset_obj.id, 
        creation_in.prompt, 
        async_session
    )
    
    return asset_obj
