from sqlalchemy import Column, Integer, String, Boolean, ForeignKey, select
from sqlalchemy.orm import relationship, aliased
from fastapi import FastAPI, Depends
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker, declarative_base
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from fastapi.encoders import jsonable_encoder

DATABASE_URL = "mysql+asyncmy://root:Hzy041011@localhost/hhh"

engine = create_async_engine(
    DATABASE_URL,
    echo=True,
    pool_size=10,  # 增加连接池大小
    max_overflow=20  # 设置最大溢出连接数
)

AsyncSessionLocal = sessionmaker(
    bind=engine,
    class_=AsyncSession,
    expire_on_commit=False,
)

Base = declarative_base()

class User(Base):
    __tablename__ = 'users'
    user_id = Column(Integer, primary_key=True, index=True, nullable=False)
    username = Column(String, nullable=False)
    number_sessions = Column(Integer, nullable=False, index=True)

class UserRequest(BaseModel):
    user_id: int

async def get_db():
    async with AsyncSessionLocal() as db:
        yield db
async def get_number_sessions(db, user_id: int):
    result = await db.execute(
        select(User.number_sessions)
        .filter(User.user_id == user_id)
    )
    return result.scalar_one_or_none()

app = FastAPI()

@app.post("/users/number_sessions")
async def get_sessions(request: UserRequest, db: AsyncSession = Depends(get_db)):
    user_id = request.user_id
    number_sessions = await get_number_sessions(db, user_id)

    if number_sessions is not None:
        return JSONResponse(content=jsonable_encoder({
            "user_id": user_id,
            "number_sessions": number_sessions
        }))
    else:
        return JSONResponse(status_code=404, content={"detail": "用户尚未学习"})

if __name__ == '__main__':
    import uvicorn
    uvicorn.run(app="number_sessions:app", host="127.0.0.1", port=8000, reload=True)
