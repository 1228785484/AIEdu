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
    user_id = Column(Integer, ForeignKey('users.user_id', ondelete='CASCADE'))
    chapter_id = Column(Integer, ForeignKey('chapters.chapter_id', ondelete='CASCADE'), primary_key=True)
    unit_id = Column(Integer, ForeignKey('units.unit_id', ondelete='CASCADE'))  # 外键关联到 units 表
    user = relationship("User", backref="course_chapters")
    chapter = relationship("Chapter", backref="course_chapters")
    unit = relationship("CourseUnit", backref="course_chapters")

class Quizzes(Base):
    __tablename__ = 'quizzes'
    chapter_id = Column(Integer, ForeignKey('course_chapters.chapter_id', ondelete='CASCADE'), index=True)
    quiz_id = Column(Integer, nullable=False, primary_key=True, index=True)


class Quiz_submissions(Base):
    __tablename__ = 'quiz_submissions'
    user_id = Column(Integer, primary_key=True, index=True)
    quiz_id = Column(Integer, ForeignKey('quizzes.quiz_id', ondelete='CASCADE'), nullable=False, index=True)
    finish_point = Column(Boolean, default=False)


class UserRequest(BaseModel):
    user_id: int
    chapter_id: int

async def get_db():
    async with AsyncSessionLocal() as db:
        yield db

async def get_user_chapter(db: AsyncSession, user_id: int, chapter_id: int):
    # 别名
    course_chapter_alias = aliased(CourseChapter)
    quiz_alias = aliased(Quizzes)
    quiz_submission_alias_1 = aliased(Quiz_submissions)
    quiz_submission_alias_2 = aliased(Quiz_submissions)

    result = await db.execute(
        select(quiz_submission_alias_1.finish_point)
        .join(CourseEnrollment, CourseEnrollment.user_id == user_id)
        .join(CourseUnit, CourseUnit.course_id == CourseEnrollment.course_id)
        .join(course_chapter_alias, course_chapter_alias.unit_id == CourseUnit.unit_id) # unit->chapters
        .join(quiz_alias, quiz_alias.chapter_id == course_chapter_alias.chapter_id)
        .join(quiz_submission_alias_2 , quiz_submission_alias_2.quiz_id == quiz_alias.quiz_id)
        # .filter(quiz_submission_alias_2.chapter_id == chapter_id)
        .distinct()
    )
    return result.scalar_one_or_none()

    if finish_point:
        return all(finish_point)
    return None

app = FastAPI()

@app.post("/quiz/verify_finish_point")
async def verify_chapter_completion(request: UserRequest, db: AsyncSession = Depends(get_db)):
    finish_point = await get_user_chapter(db, request.user_id, request.chapter_id)
    print(f"用户 {request.user_id} 在章节 {request.chapter_id} 的完成状态：{finish_point}")

    if finish_point is not None:
        return JSONResponse(content=jsonable_encoder({
            "user_id": request.user_id,
            "chapter_id": request.chapter_id,
            "finish_point": finish_point
        }))
    else:
        return JSONResponse(status_code=404, content={"detail": "未作答"})


if __name__ == '__main__':
    import uvicorn

    uvicorn.run(app="finish_point:app", host="127.0.0.1", port=8000, reload=True)
