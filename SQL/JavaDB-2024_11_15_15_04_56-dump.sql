-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: stu_review
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chapter_progress`
--

DROP TABLE IF EXISTS `chapter_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter_progress` (
  `progress_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `enrollment_id` bigint unsigned NOT NULL COMMENT '报名ID',
  `chapter_id` bigint unsigned NOT NULL COMMENT '章节ID',
  `completion_percentage` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '完成百分比',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'not_started' COMMENT '状态(not_started:未开始,in_progress:进行中,completed:已完成)',
  `last_accessed` datetime DEFAULT NULL COMMENT '最后访问时间',
  PRIMARY KEY (`progress_id`),
  UNIQUE KEY `idx_enrollment_chapter` (`enrollment_id`,`chapter_id`),
  KEY `idx_chapter_id` (`chapter_id`),
  CONSTRAINT `fk_chapter_progress_chapter_id` FOREIGN KEY (`chapter_id`) REFERENCES `course_chapters` (`chapter_id`),
  CONSTRAINT `fk_chapter_progress_enrollment_id` FOREIGN KEY (`enrollment_id`) REFERENCES `course_enrollments` (`enrollment_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节进度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter_progress`
--

LOCK TABLES `chapter_progress` WRITE;
/*!40000 ALTER TABLE `chapter_progress` DISABLE KEYS */;
INSERT INTO `chapter_progress` VALUES (1,1,1,100.00,'completed','2024-01-10 15:00:00'),(2,1,2,75.00,'in_progress','2024-01-15 16:00:00'),(3,2,1,100.00,'completed','2024-01-12 14:00:00'),(4,2,2,50.00,'in_progress','2024-01-16 10:00:00'),(5,3,1,80.00,'in_progress','2024-01-14 11:00:00');
/*!40000 ALTER TABLE `chapter_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_categories`
--

DROP TABLE IF EXISTS `course_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_categories` (
  `category_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '分类描述',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '父分类ID',
  PRIMARY KEY (`category_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_categories`
--

LOCK TABLES `course_categories` WRITE;
/*!40000 ALTER TABLE `course_categories` DISABLE KEYS */;
INSERT INTO `course_categories` VALUES (1,'编程语言','各类编程语言课程',NULL),(2,'基础课程','C语言基础知识',1),(3,'进阶课程','C语言进阶知识',1);
/*!40000 ALTER TABLE `course_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_chapters`
--

DROP TABLE IF EXISTS `course_chapters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_chapters` (
  `chapter_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` bigint unsigned NOT NULL COMMENT '课程ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '章节内容',
  `sequence_number` int NOT NULL COMMENT '章节序号',
  PRIMARY KEY (`chapter_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_sequence_number` (`sequence_number`),
  CONSTRAINT `fk_course_chapters_course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程章节表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_chapters`
--

LOCK TABLES `course_chapters` WRITE;
/*!40000 ALTER TABLE `course_chapters` DISABLE KEYS */;
INSERT INTO `course_chapters` VALUES (1,1,'第1章：C语言简介','本章介绍C语言的发展历史、特点及应用领域',1),(2,1,'第2章：基本数据类型','详细讲解C语言的基本数据类型及其使用方法',2),(3,1,'第3章：控制结构','学习if-else、switch、循环等控制结构',3),(4,2,'第1章：指针基础','理解指针的概念和基本使用方法',1),(5,2,'第2章：动态内存分配','学习内存的动态分配和释放',2);
/*!40000 ALTER TABLE `course_chapters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_enrollments`
--

DROP TABLE IF EXISTS `course_enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_enrollments` (
  `enrollment_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `course_id` bigint unsigned NOT NULL COMMENT '课程ID',
  `enrolled_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ongoing' COMMENT '状态(ongoing:进行中,completed:已完成,dropped:已退出)',
  PRIMARY KEY (`enrollment_id`),
  UNIQUE KEY `idx_user_course` (`user_id`,`course_id`),
  KEY `idx_enrolled_at` (`enrolled_at`),
  KEY `fk_course_enrollments_course_id` (`course_id`),
  CONSTRAINT `fk_course_enrollments_course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`),
  CONSTRAINT `fk_course_enrollments_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程报名表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_enrollments`
--

LOCK TABLES `course_enrollments` WRITE;
/*!40000 ALTER TABLE `course_enrollments` DISABLE KEYS */;
INSERT INTO `course_enrollments` VALUES (1,2,1,'2024-01-04 10:00:00',NULL,'ongoing'),(2,3,1,'2024-01-04 11:00:00',NULL,'ongoing'),(3,4,1,'2024-01-04 14:00:00',NULL,'ongoing'),(4,2,2,'2024-01-05 10:00:00',NULL,'ongoing');
/*!40000 ALTER TABLE `course_enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_reviews`
--

DROP TABLE IF EXISTS `course_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_reviews` (
  `review_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `course_id` bigint unsigned NOT NULL COMMENT '课程ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `rating` tinyint unsigned NOT NULL COMMENT '评分(1-5)',
  `comment` text COLLATE utf8mb4_unicode_ci COMMENT '评价内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`review_id`),
  UNIQUE KEY `idx_user_course` (`user_id`,`course_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_course_reviews_course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_course_reviews_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_reviews`
--

LOCK TABLES `course_reviews` WRITE;
/*!40000 ALTER TABLE `course_reviews` DISABLE KEYS */;
INSERT INTO `course_reviews` VALUES (1,1,2,5,'课程内容浅显易懂，非常适合入门学习！','2024-01-20 16:00:00'),(2,1,3,4,'讲解详细，练习题很有帮助，希望能多一些实践案例。','2024-01-21 14:00:00'),(3,1,4,5,'老师讲解很清晰，课程结构安排合理。','2024-01-22 15:00:00');
/*!40000 ALTER TABLE `course_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `course_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '课程描述',
  `category_id` bigint unsigned NOT NULL COMMENT '分类ID',
  `difficulty_id` bigint unsigned NOT NULL COMMENT '难度ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否激活',
  PRIMARY KEY (`course_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_difficulty_id` (`difficulty_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_courses_category_id` FOREIGN KEY (`category_id`) REFERENCES `course_categories` (`category_id`),
  CONSTRAINT `fk_courses_difficulty_id` FOREIGN KEY (`difficulty_id`) REFERENCES `difficulty_levels` (`difficulty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'C语言基础教程','C语言入门课程，涵盖基本语法、数据类型、控制结构等',2,1,'2024-01-03 09:00:00',1),(2,'C语言进阶教程','深入学习指针、内存管理、结构体等概念',3,2,'2024-01-03 09:30:00',1);
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `difficulty_levels`
--

DROP TABLE IF EXISTS `difficulty_levels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `difficulty_levels` (
  `difficulty_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '难度ID',
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '难度名称',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '难度描述',
  PRIMARY KEY (`difficulty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='难度等级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `difficulty_levels`
--

LOCK TABLES `difficulty_levels` WRITE;
/*!40000 ALTER TABLE `difficulty_levels` DISABLE KEYS */;
INSERT INTO `difficulty_levels` VALUES (1,'入门级','适合零基础学习者'),(2,'基础级','需要一些编程基础'),(3,'中级','需要较好的编程基础'),(4,'高级','需要扎实的编程功底');
/*!40000 ALTER TABLE `difficulty_levels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz_submissions`
--

DROP TABLE IF EXISTS `quiz_submissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz_submissions` (
  `submission_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `quiz_id` bigint unsigned NOT NULL COMMENT '测验ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `answers` json NOT NULL COMMENT '答案(JSON格式)',
  `score` decimal(5,2) DEFAULT NULL COMMENT '得分',
  `submitted_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`submission_id`),
  KEY `idx_quiz_id` (`quiz_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_submitted_at` (`submitted_at`),
  CONSTRAINT `fk_quiz_submissions_quiz_id` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`quiz_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_quiz_submissions_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测验提交表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz_submissions`
--

LOCK TABLES `quiz_submissions` WRITE;
/*!40000 ALTER TABLE `quiz_submissions` DISABLE KEYS */;
INSERT INTO `quiz_submissions` VALUES (1,1,2,'{\"answers\": [{\"question_id\": 1, \"selected_answer\": 0}, {\"question_id\": 2, \"selected_answers\": [0, 1, 3]}]}',100.00,'2024-01-11 15:30:00'),(2,1,3,'{\"answers\": [{\"question_id\": 1, \"selected_answer\": 1}, {\"question_id\": 2, \"selected_answers\": [0, 1]}]}',50.00,'2024-01-13 14:30:00');
/*!40000 ALTER TABLE `quiz_submissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quizzes`
--

DROP TABLE IF EXISTS `quizzes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quizzes` (
  `quiz_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '测验ID',
  `chapter_id` bigint unsigned NOT NULL COMMENT '章节ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '测验标题',
  `quiz_config` json NOT NULL COMMENT '测验配置(JSON格式)',
  `time_limit` int DEFAULT NULL COMMENT '时间限制(分钟)',
  PRIMARY KEY (`quiz_id`),
  KEY `idx_chapter_id` (`chapter_id`),
  CONSTRAINT `fk_quizzes_chapter_id` FOREIGN KEY (`chapter_id`) REFERENCES `course_chapters` (`chapter_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测验表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quizzes`
--

LOCK TABLES `quizzes` WRITE;
/*!40000 ALTER TABLE `quizzes` DISABLE KEYS */;
INSERT INTO `quizzes` VALUES (1,1,'C语言基础知识测试','{\"questions\": [{\"id\": 1, \"type\": \"single_choice\", \"options\": [\"Dennis Ritchie\", \"Bjarne Stroustrup\", \"James Gosling\", \"Guido van Rossum\"], \"question\": \"C语言的创始人是谁？\", \"correct_answer\": 0}, {\"id\": 2, \"type\": \"multiple_choice\", \"options\": [\"高效性\", \"可移植性\", \"面向对象\", \"简洁性\"], \"question\": \"以下哪些是C语言的特点？\", \"correct_answers\": [0, 1, 3]}]}',30),(2,2,'数据类型测试','{\"questions\": [{\"id\": 1, \"type\": \"single_choice\", \"options\": [\"2\", \"4\", \"8\", \"1\"], \"question\": \"int类型在大多数系统中占用几个字节？\", \"correct_answer\": 1}, {\"id\": 2, \"type\": \"true_false\", \"question\": \"char类型在C语言中总是占用1个字节\", \"correct_answer\": true}]}',20);
/*!40000 ALTER TABLE `quizzes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_profiles`
--

DROP TABLE IF EXISTS `student_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_profiles` (
  `profile_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `education_level` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '教育水平',
  `preferences` json DEFAULT NULL COMMENT '学习偏好配置(JSON格式)',
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_student_profiles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_profiles`
--

LOCK TABLES `student_profiles` WRITE;
/*!40000 ALTER TABLE `student_profiles` DISABLE KEYS */;
INSERT INTO `student_profiles` VALUES (1,2,'大学一年级','{\"learning_style\": \"视觉型\", \"preferred_time\": \"晚上\", \"study_duration\": \"1小时\"}'),(2,3,'大学二年级','{\"learning_style\": \"听觉型\", \"preferred_time\": \"上午\", \"study_duration\": \"2小时\"}'),(3,4,'大学一年级','{\"learning_style\": \"实践型\", \"preferred_time\": \"下午\", \"study_duration\": \"1.5小时\"}');
/*!40000 ALTER TABLE `student_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `role_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `role_name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称(student:学生,teacher:教师,admin:管理员)',
  `assigned_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '角色分配时间',
  PRIMARY KEY (`role_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,1,'teacher','2024-01-01 10:00:00'),(2,2,'student','2024-01-02 11:00:00'),(3,3,'student','2024-01-02 11:30:00'),(4,4,'student','2024-01-02 14:00:00'),(5,5,'admin','2024-01-01 09:00:00');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希值',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电子邮箱',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_login` datetime DEFAULT NULL COMMENT '最后登录时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否激活(1:激活,0:未激活)',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_email` (`email`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'teacher_zhang','$2a$10$xK2jfKjd8','zhang@example.com','2024-01-01 10:00:00','2024-03-15 14:30:00',1),(2,'student_wang','$2a$10$kL9jfKjd8','wang@example.com','2024-01-02 11:00:00','2024-03-15 15:20:00',1),(3,'student_li','$2a$10$mN8jfKjd8','li@example.com','2024-01-02 11:30:00','2024-03-15 16:00:00',1),(4,'student_zhao','$2a$10$pQ8jfKjd8','zhao@example.com','2024-01-02 14:00:00','2024-03-15 16:30:00',1),(5,'admin_chen','$2a$10$rT8jfKjd8','chen@example.com','2024-01-01 09:00:00','2024-03-15 17:00:00',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-15 15:04:56
