## 技术栈
**后端**:Spring Boot,Spring Security,MySQL,(Redis)Redisson,MyBatisPlus,JWT,MinIO,FastJson2

**前端**:Swagger,JS,Vue3,Element-Plus,Shiki,Markdown-it

该项目是为了把教育和AI进行合并而产出的项目，项目功能如下
# Part-0注册登录模块
![Pasted image 20250105170106](https://github.com/user-attachments/assets/ddd130d7-62ca-4cf5-8b9a-ed3aa444cb27)
支持通过**邮箱注册**，默认分配初始**学生身份**，便于后续**鉴权**操作。前后端采用**JWT**机制，实现安全高效的接口调用。

# Part1-学生端
![Pasted image 20250105170326](https://github.com/user-attachments/assets/2a3876d1-085d-440d-90a6-d85f7ff38ba4)
![Pasted image 20250105170932](https://github.com/user-attachments/assets/492236d9-a4d4-444a-a627-ae561f2a7f16)
![Pasted image 20250105171507](https://github.com/user-attachments/assets/3e8abd13-78b8-410f-a688-d88b5ad0180f)
### AI生成学习内容

系统可以基于每个学生的学习习惯，通过AI驱动的工作流动态生成**个性化**学习内容，并自动保存，方便后续继续学习。此外，还可以使用AI生成**独特**的测验，以**JSON**格式呈现，帮助学生更好地巩固知识。

为了保障接口安全并防止**恶意调用**，在AI方法调用前，通过**AOP**拦截请求，同时为每个用户及全局实现**令牌桶**限流和**分布式锁**机制，利用**Redis**高效管理并确保系统的稳定性和安全性。
![Pasted image 20250105171551](https://github.com/user-attachments/assets/3b50639c-670a-4506-a1d6-0f27c6c5be8b)
![Pasted image 20250105171730](https://github.com/user-attachments/assets/897b461e-e010-4299-b337-19bff3acbc06)
### 笔记功能
为学生在学习过程中记录知识点提供了便捷的笔记功能。系统集成了**MinIO**用于高效存储和管理笔记中的图片资源，同时支持将所有笔记一键导出为**Word文档**，帮助学生系统化复习和巩固知识，提升学习效率。

![Pasted image 20250105171833](https://github.com/user-attachments/assets/a7fd1f26-fc4f-45f5-b7c3-9244f98e91e1)
![Pasted image 20250105171920](https://github.com/user-attachments/assets/a9d4a7ca-2059-4362-bcdb-553dd1c1b46e)
![Pasted image 20250105172048](https://github.com/user-attachments/assets/01abb027-4302-4070-80b2-4b1a8eeeee12)
### 学习报告功能
针对于学生对于每个章节的学习得分和学习习惯，用AI给出**独特**的学习建议和分析，方便用户更好的进行针对性学习，用**WebSocket**记录了用户的学习时长并在前端绘制出记录图片。
![Pasted image 20250105172322](https://github.com/user-attachments/assets/97f98563-f8bf-4178-aaa0-5e0c192e02ef)
### 助教功能
模仿ChatGPT，完成**流式传输**和**代码+Markdown实时高亮**的实现，方便用户在学习的过程中及时解惑

# Part-2管理端
![Pasted image 20250105172746](https://github.com/user-attachments/assets/6aeb36c6-660d-46e3-a5d1-e6ba82435870)
管理端可以实现基于角色的精细化权限管理，让前端操作更加便捷。例如，可以灵活配置学生、教师和管理员等不同角色的权限。并且也可以管理课程，学生学习情况，并根据学生情况生成AI教案
