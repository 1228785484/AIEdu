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

class Chapter(Base):
    __tablename__ = 'chapters'
    chapter_id = Column(Integer, primary_key=True, index=True)
    chapter_name = Column(String, nullable=False)
    finish_points = Column(Boolean, default=False)

class User(Base):
    __tablename__ = 'users'
    user_id = Column(Integer, primary_key=True, index=True, nullable=False)
    username = Column(String, nullable=False)

class Course(Base):
    __tablename__ = 'courses'
    course_id = Column(Integer, primary_key=True, index=True)
    course_name = Column(String, nullable=False)

class CourseUnit(Base):
    __tablename__ = 'units'
    unit_id = Column(Integer, primary_key=True, index=True)
    course_id = Column(Integer, ForeignKey('courses.course_id'))
    chapter_id = Column(Integer, ForeignKey('chapters.chapter_id'))

    course = relationship('Course', backref='course_units')
    chapter = relationship('Chapter', backref='course_units')

class CourseEnrollment(Base):
    __tablename__ = 'course_enrollments'
    enrollment_id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey('users.user_id'))
    course_id = Column(Integer, ForeignKey('courses.course_id'))
    user = relationship('User', backref='enrollments')
    course = relationship('Course', backref='enrollments')

class CourseChapter(Base):
    __tablename__ = 'course_chapters'
    user_id = Column(Integer, ForeignKey('users.user_id', ondelete='CASCADE'), primary_key=True)
    chapter_id = Column(Integer, ForeignKey('chapters.chapter_id', ondelete='CASCADE'), primary_key=True)
    unit_id = Column(Integer, ForeignKey('units.unit_id', ondelete='CASCADE'))
    finish_points = Column(Boolean, default=False)
    user = relationship("User", backref="course_chapters")
    chapter = relationship("Chapter", backref="course_chapters")
    unit = relationship("CourseUnit", backref="course_chapters")
class UserRequest(BaseModel):
    user_id: int
    chapter_id: int

async def get_db():
    async with AsyncSessionLocal() as db:
        yield db

async def get_user_chapter(db, user_id: int, chapter_id: int):
    course_chapter_alias_1 = aliased(CourseChapter)
    course_chapter_alias_2 = aliased(CourseChapter)

    result = await db.execute(
        select(course_chapter_alias_1.finish_points)
        .join(CourseEnrollment, CourseEnrollment.user_id == user_id)
        .join(CourseUnit, CourseUnit.course_id == CourseEnrollment.course_id)
        .join(course_chapter_alias_2, course_chapter_alias_2.unit_id == CourseUnit.unit_id)
        .filter(course_chapter_alias_2.chapter_id == chapter_id)
        .distinct()
    )
    return result.scalar_one_or_none()

app = FastAPI()

@app.post("/courses/Verify_courses")
async def verify_chapter_completion(request: UserRequest, db: AsyncSession = Depends(get_db)):
    finish_points = await get_user_chapter(db, request.user_id, request.chapter_id)
    print(f"Finish points for user {request.user_id} and chapter {request.chapter_id}: {finish_points}")
    if finish_points is not None:
        return JSONResponse(content=jsonable_encoder({
            "user_id": request.user_id,
            "chapter_id": request.chapter_id,
            "finish_points": finish_points
        }))
    else:
        return JSONResponse(status_code=404, content={"detail": "User or chapter not found"})


if __name__ == '__main__':
    import uvicorn

    uvicorn.run(app="Quest_points:app", host="127.0.0.1", port=8000, reload=True)
