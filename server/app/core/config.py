from pydantic_settings import BaseSettings
from typing import Optional

class Settings(BaseSettings):
    PROJECT_NAME: str = "Genesis API"
    VERSION: str = "1.0.0"
    API_V1_STR: str = "/api/v1"
    
    # Security
    SECRET_KEY: str = "supersecretkey"  # Change in production
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 7  # 7 days
    
    # Database
    DATABASE_URL: str = "postgresql+asyncpg://genesis:genesis_pass@localhost:5432/genesis_db"
    
    # Redis
    REDIS_URL: str = "redis://localhost:6379/0"
    
    # AI APIs
    OPENAI_API_KEY: Optional[str] = None
    MESHY_API_KEY: Optional[str] = None
    
    # Object Storage
    R2_BUCKET_NAME: Optional[str] = None
    R2_ACCESS_KEY_ID: Optional[str] = None
    R2_SECRET_ACCESS_KEY: Optional[str] = None

    class Config:
        env_file = ".env"
        case_sensitive = True

settings = Settings()
