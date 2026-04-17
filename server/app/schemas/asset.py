from pydantic import BaseModel
from datetime import datetime
from typing import Optional
from app.models.asset import AssetStatus

class AssetBase(BaseModel):
    title: Optional[str] = None
    prompt: str
    model_url: str
    preview_url: str

class AssetCreate(AssetBase):
    creator_id: int

class AssetUpdate(BaseModel):
    status: AssetStatus

class Asset(AssetBase):
    id: int
    status: AssetStatus
    created_at: datetime
    creator_id: int

    class Config:
        from_attributes = True
