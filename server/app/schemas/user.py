from pydantic import BaseModel, EmailStr
from typing import Optional

# Shared properties
class UserBase(BaseModel):
    email: Optional[EmailStr] = None
    username: Optional[str] = None
    is_active: Optional[bool] = True
    is_admin: bool = False

# Properties to receive via API on creation
class UserCreate(UserBase):
    email: EmailStr
    username: str
    password: str

# Properties to receive via API on update
class UserUpdate(UserBase):
    password: Optional[str] = None

class UserLogin(BaseModel):
    email: str
    password: str

class UserInDBBase(UserBase):
    id: Optional[int] = None
    mana: int
    star_coin: int

    class Config:
        from_attributes = True

# Additional properties to return via API
class User(UserInDBBase):
    pass

# Additional properties stored in DB
class UserInDB(UserInDBBase):
    hashed_password: str
