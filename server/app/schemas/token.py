from pydantic import BaseModel
from typing import Optional

class Token(BaseModel):
    token: str
    userId: str
    username: str

class TokenPayload(BaseModel):
    sub: Optional[int] = None
