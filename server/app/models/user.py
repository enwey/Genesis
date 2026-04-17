from sqlalchemy import Column, Integer, String, Boolean, Float
from app.db.base_class import Base

class User(Base):
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, index=True, nullable=False)
    email = Column(String, unique=True, index=True, nullable=False)
    hashed_password = Column(String, nullable=False)
    is_active = Column(Boolean(), default=True)
    is_admin = Column(Boolean(), default=False)
    
    # Wallet balances
    mana = Column(Integer, default=1000)
    star_coin = Column(Integer, default=0)
