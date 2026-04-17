from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey, Enum
from sqlalchemy.sql import func
from app.db.base_class import Base
import enum

class AssetStatus(str, enum.Enum):
    PENDING = "pending"
    APPROVED = "approved"
    REJECTED = "rejected"

from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey, Enum, JSON
...
from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey, Enum, JSON, Float
...
class Asset(Base):
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, index=True)
    prompt = Column(String, nullable=False)
    model_url = Column(String, nullable=False)
    preview_url = Column(String, nullable=False)
    status = Column(Enum(AssetStatus), default=AssetStatus.PENDING)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    attributes = Column(JSON, nullable=True) 
    creator_id = Column(Integer, ForeignKey("user.id"))
    
    # 新增：市場字段
    price = Column(Integer, default=0) # 掛牌價格
    is_listed = Column(Boolean, default=False) # 是否正在出售

class TradeRequestStatus(str, enum.Enum):
    PENDING = "pending"
    ACCEPTED = "accepted"
    REJECTED = "rejected"
    CANCELED = "cancelled"

class TradeRequest(Base):
    id = Column(Integer, primary_key=True, index=True)
    asset_id = Column(Integer, ForeignKey("asset.id"))
    bid_price = Column(Integer, nullable=False) # 出價
    buyer_id = Column(Integer, ForeignKey("user.id"))
    status = Column(Enum(TradeRequestStatus), default=TradeRequestStatus.PENDING)
    created_at = Column(DateTime(timezone=True), server_default=func.now())

