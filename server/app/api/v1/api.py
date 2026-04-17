from fastapi import APIRouter
from app.api.v1.endpoints import auth, assets, creation, market

api_router = APIRouter()
api_router.include_router(auth.router, prefix="/auth", tags=["auth"])
api_router.include_router(assets.router, prefix="/assets", tags=["assets"])
api_router.include_router(creation.router, prefix="/creation", tags=["creation"])
api_router.include_router(market.router, prefix="/market", tags=["market"])
