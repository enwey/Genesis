import asyncio
import os
import sys

# 將當前目錄加入路徑
sys.path.append(os.getcwd())

from app.db.session import engine
from app.db.base import Base
from app.models.user import User
from app.core.security import get_password_hash
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import sessionmaker

async def init_db():
    print("Creating tables...")
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all) # 清理舊的（如果有）
        await conn.run_sync(Base.metadata.create_all)
    
    print("Creating initial user...")
    async_session = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)
    async with async_session() as db:
        test_user = User(
            username="genesis_pioneer",
            email="test@genesis.com",
            hashed_password=get_password_hash("password123"),
            is_active=True,
            mana=1000,
            star_coin=5000
        )
        db.add(test_user)
        await db.commit()
    
    print("Database initialized successfully!")

if __name__ == "__main__":
    asyncio.run(init_db())
