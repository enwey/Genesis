from typing import List, Optional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.models.asset import Asset, AssetStatus
from app.schemas.asset import AssetCreate, AssetUpdate

class CRUDAsset:
    async def create(self, db: AsyncSession, *, obj_in: AssetCreate) -> Asset:
        db_obj = Asset(
            title=obj_in.title,
            prompt=obj_in.prompt,
            model_url=obj_in.model_url,
            preview_url=obj_in.preview_url,
            creator_id=obj_in.creator_id,
            status=AssetStatus.PENDING
        )
        db.add(db_obj)
        await db.commit()
        await db.refresh(db_obj)
        return db_obj

    async def get_multi(
        self, db: AsyncSession, *, skip: int = 0, limit: int = 100, status: Optional[AssetStatus] = None
    ) -> List[Asset]:
        query = select(Asset)
        if status:
            query = query.filter(Asset.status == status)
        result = await db.execute(query.offset(skip).limit(limit))
        return result.scalars().all()

    async def update_status(
        self, db: AsyncSession, *, db_obj: Asset, status: AssetStatus
    ) -> Asset:
        db_obj.status = status
        await db.commit()
        await db.refresh(db_obj)
        return db_obj

    async def get(self, db: AsyncSession, id: int) -> Optional[Asset]:
        result = await db.execute(select(Asset).filter(Asset.id == id))
        return result.scalars().first()

asset = CRUDAsset()
