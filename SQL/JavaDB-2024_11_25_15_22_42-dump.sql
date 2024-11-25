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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节进度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter_progress`
--

LOCK TABLES `chapter_progress` WRITE;
/*!40000 ALTER TABLE `chapter_progress` DISABLE KEYS */;
INSERT INTO `chapter_progress` VALUES (6,6,4,20.00,'in_progress','2024-11-22 17:22:47');
/*!40000 ALTER TABLE `chapter_progress` ENABLE KEYS */;
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
  `sequence_number` int NOT NULL COMMENT '章节序号',
  `unit_id` bigint unsigned NOT NULL COMMENT '单元ID',
  `content_prompt` text COLLATE utf8mb4_unicode_ci COMMENT '章节内容提示词',
  PRIMARY KEY (`chapter_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_sequence_number` (`sequence_number`),
  KEY `idx_unit_id` (`unit_id`),
  CONSTRAINT `fk_course_chapters_course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_course_chapters_unit_id` FOREIGN KEY (`unit_id`) REFERENCES `units` (`unit_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程章节表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_chapters`
--

LOCK TABLES `course_chapters` WRITE;
/*!40000 ALTER TABLE `course_chapters` DISABLE KEYS */;
INSERT INTO `course_chapters` VALUES (4,2,'第1章：指针基础',1,2,NULL),(5,2,'第2章：动态内存分配',2,2,NULL),(16,1,'C语言的发展历程、特点及应用领域',1,1,'介绍C语言的历史发展、主要特点和应用场景'),(17,1,'基本语法规则',2,1,'讲解C语言的基本语法规则和代码结构'),(18,1,'变量、数据类型和常量',3,1,'详细介绍C语言的变量定义、基本数据类型和常量使用'),(19,1,'输入输出函数',4,1,'讲解scanf和printf等基本输入输出函数的使用'),(20,1,'算术运算符、关系运算符和逻辑运算符',1,4,'详细讲解各类运算符的使用方法和优先级'),(21,1,'赋值运算符和复合赋值运算符',2,4,'介绍简单赋值和复合赋值运算符的使用'),(22,1,'表达式的概念及运算规则',3,4,'讲解表达式的构成和计算规则'),(23,1,'顺序结构和选择结构',1,5,'讲解if语句和switch语句的使用'),(24,1,'循环结构',2,5,'详细介绍for、while和do-while循环的使用'),(25,1,'break和continue',3,5,'讲解循环控制语句的使用方法'),(26,1,'函数的定义、声明和调用',1,6,'讲解如何定义和使用函数'),(27,1,'函数的参数传递方式',2,6,'介绍值传递和地址传递的概念和使用'),(28,1,'局部变量和全局变量',3,6,'讲解变量作用域和生命周期'),(29,1,'一维数组和二维数组的定义、初始化和遍历',1,7,'详细讲解数组的使用方法'),(30,1,'字符串的基本操作',2,7,'介绍字符串处理函数的使用'),(31,1,'字符数组与字符串',3,7,'讲解字符数组和字符串的关系'),(32,1,'指针的基本概念和用法',1,8,'介绍指针的基本概念和声明方法'),(33,1,'指针与数组的关系',2,8,'讲解指针和数组的关系及使用'),(34,1,'指针与函数的参数传递',3,8,'详细介绍指针作为函数参数的使用'),(35,1,'结构体的定义和初始化',1,9,'讲解结构体的基本用法'),(36,1,'结构体数组、结构体指针和结构体嵌套',2,9,'介绍结构体的高级用法'),(37,1,'共用体的概念和用法',3,9,'讲解共用体的使用方法'),(38,1,'文件的基本概念和分类',1,10,'介绍文件的基本概念'),(39,1,'文件的基本操作',2,10,'讲解文件的打开、关闭、读写等操作'),(40,1,'文件定位函数',3,10,'介绍文件指针的移动和定位'),(41,1,'宏定义的基本用法',1,11,'讲解宏定义的语法和使用方法'),(42,1,'文件包含指令',2,11,'介绍头文件的包含和使用'),(43,1,'条件编译指令',3,11,'讲解条件编译的概念和使用'),(44,1,'动态内存分配的函数',1,12,'讲解malloc、free等函数的使用'),(45,1,'动态内存分配的原理和注意事项',2,12,'介绍内存管理的注意事项'),(46,1,'链表的基本操作',3,12,'讲解链表的创建、插入、删除等操作');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程报名表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_enrollments`
--

LOCK TABLES `course_enrollments` WRITE;
/*!40000 ALTER TABLE `course_enrollments` DISABLE KEYS */;
INSERT INTO `course_enrollments` VALUES (1,2,1,'2024-01-04 10:00:00',NULL,'ongoing'),(2,3,1,'2024-01-04 11:00:00',NULL,'ongoing'),(3,4,1,'2024-01-04 14:00:00',NULL,'ongoing'),(4,2,2,'2024-01-05 10:00:00',NULL,'ongoing'),(5,6,1,'2024-11-21 10:13:29',NULL,'ongoing'),(6,7,1,'2024-11-21 11:08:08',NULL,'ongoing');
/*!40000 ALTER TABLE `course_enrollments` ENABLE KEYS */;
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
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否激活',
  PRIMARY KEY (`course_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'C语言基础教程','C语言入门课程，涵盖基本语法、数据类型、控制结构等',2,'2024-01-03 09:00:00',1),(2,'C语言进阶教程','深入学习指针、内存管理、结构体等概念',3,'2024-01-03 09:30:00',1);
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personalized_contents`
--

DROP TABLE IF EXISTS `personalized_contents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `personalized_contents` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `chapter_id` bigint unsigned NOT NULL COMMENT '章节ID',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '个性化内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否激活',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_chapter` (`user_id`,`chapter_id`),
  KEY `fk_pers_content_chapter_id` (`chapter_id`),
  CONSTRAINT `fk_pers_content_chapter_id` FOREIGN KEY (`chapter_id`) REFERENCES `course_chapters` (`chapter_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_pers_content_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='个性化章节内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personalized_contents`
--

LOCK TABLES `personalized_contents` WRITE;
/*!40000 ALTER TABLE `personalized_contents` DISABLE KEYS */;
INSERT INTO `personalized_contents` VALUES (1,6,16,'C语言的历史发展、主要特点和应用场景\n\n### C语言的历史发展\n\nC语言是一种通用的计算机编程语言，最初由Dennis Ritchie于1970年代在贝尔实验室开发。C语言的设计初衷是为了能够在Unix操作系统上进行系统编程。随着时间的推移，C语言经历了多个版本的演变，并逐渐成为一门标准化的编程语言。\n\n1972年，C语言首次被广泛使用，并成为Unix操作系统的主要编程语言。1989年，C语言的标准化工作开始进行，最终于1998年发布了ISO/IEC 9899:1999，即C99标准。其后，又于2011年发布了C11标准，进一步增强了语言的功能和安全性。\n\nC语言的高效性、简洁性和可移植性使其成为现代编程的基础。它不仅用于系统软件的开发，还广泛应用于嵌入式系统、游戏开发、图形界面应用程序等多个领域。\n\n### C语言的主要特点\n\n1. **高效性**：C语言编写的程序通常运行速度快，资源消耗低，非常适合需要高性能的应用场景。\n  \n2. **简洁性**：C语言的语法相对简单，易于学习和使用，尤其适合初学者。\n\n3. **可移植性**：C语言程序可以在不同的计算机平台上编译和运行，只需少量修改。\n\n4. **灵活性**：C语言允许直接操作内存，提供了强大的指针操作能力，这使得开发者可以高效地管理资源。\n\n5. **丰富的库支持**：C语言拥有丰富的标准库和第三方库，使得开发者能够快速实现各种功能。\n\n### C语言的应用场景\n\n1. **系统编程**：C语言是操作系统内核、设备驱动程序等系统软件开发的首选语言。\n\n2. **嵌入式系统**：许多嵌入式设备，如家电、汽车控制系统等，使用C语言进行开发。\n\n3. **游戏开发**：由于其高性能，C语言常用于游戏引擎和游戏开发中的底层编程。\n\n4. **图形界面应用**：C语言用于开发一些图形界面应用程序，通常结合图形库（如SDL、OpenGL等）进行开发。\n\n5. **科学计算**：C语言在科学应用领域也有广泛的应用，很多高性能计算项目使用C语言编写。\n\n### 总结\n\nC语言作为一种经典的编程语言，凭借其高效性、简洁性和可移植性，仍在多个领域中占据重要地位。无论是系统级编程还是应用程序开发，C语言都是一个不可或缺的工具。\n\n如果想深入了解C语言的更多信息，可以参考以下链接：\n- [C语言发展历史及应用](https://blog.csdn.net/weixin_48579910/article/details/141716734#:~:text=C%E8%AF%AD%E8%A8%80%E4%BB%8E1970%E5%B9%B4%E4%BB%A3%E8%AF%9E%E7%94%9F%E4%BB%A5%E6%9D%A5%EF%BC%8C%E7%BB%8F%E5%8E%86%E4%BA%86%E4%BB%8E%E6%9C%80%E5%88%9D%E7%9A%84Unix%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F%E5%BC%80%E5%8F%91%E5%88%B0%E6%A0%87%E5%87%86%E5%8C%96%E8%BF%9B%E7%A8%8B%EF%BC%8C%E5%86%8D%E5%88%B0%E5%B9%BF%E6%B3%9B%E5%BA%94%E7%94%A8%E4%BA%8E%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F%E3%80%81%E5%B5%8C%E5%85%A5%E5%BC%8F%E7%B3%BB%E7%BB%9F%E3%80%81%E7%BC%96%E8%AF%91%E5%99%A8%E5%92%8C%E5%BA%94%E7%94%A8%E8%BD%AF%E4%BB%B6%E7%9A%84%E6%95%B4%E4%B8%8A%E5%8F%91%E5%B1%95%E5%8E%86%E7%A8%8B%EF%BC%8C...)','2024-11-22 21:16:51',1);
/*!40000 ALTER TABLE `personalized_contents` ENABLE KEYS */;
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
  `generated_questions` json NOT NULL COMMENT '实际生成的题目',
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
  `quiz_prompt` text COLLATE utf8mb4_unicode_ci COMMENT '出题提示词',
  PRIMARY KEY (`quiz_id`),
  KEY `idx_chapter_id` (`chapter_id`),
  CONSTRAINT `fk_quizzes_chapter_id` FOREIGN KEY (`chapter_id`) REFERENCES `course_chapters` (`chapter_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测验表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quizzes`
--

LOCK TABLES `quizzes` WRITE;
/*!40000 ALTER TABLE `quizzes` DISABLE KEYS */;
INSERT INTO `quizzes` VALUES (3,16,'C语言的发展历程、特点及应用领域 - 课后测验','针对C语言的发展历程、特点及应用领域的知识点进行测验，包含选择题和编程题'),(4,17,'基本语法规则 - 课后测验','针对基本语法规则的知识点进行测验，包含选择题和编程题'),(5,18,'变量、数据类型和常量 - 课后测验','针对变量、数据类型和常量的知识点进行测验，包含选择题和编程题'),(6,19,'输入输出函数 - 课后测验','针对输入输出函数的知识点进行测验，包含选择题和编程题'),(7,20,'算术运算符、关系运算符和逻辑运算符 - 课后测验','针对算术运算符、关系运算符和逻辑运算符的知识点进行测验，包含选择题和编程题'),(8,21,'赋值运算符和复合赋值运算符 - 课后测验','针对赋值运算符和复合赋值运算符的知识点进行测验，包含选择题和编程题'),(9,22,'表达式的概念及运算规则 - 课后测验','针对表达式的概念及运算规则的知识点进行测验，包含选择题和编程题'),(10,23,'顺序结构和选择结构 - 课后测验','针对顺序结构和选择结构的知识点进行测验，包含选择题和编程题'),(11,24,'循环结构 - 课后测验','针对循环结构的知识点进行测验，包含选择题和编程题'),(12,25,'break和continue - 课后测验','针对break和continue的知识点进行测验，包含选择题和编程题'),(13,26,'函数的定义、声明和调用 - 课后测验','针对函数的定义、声明和调用的知识点进行测验，包含选择题和编程题'),(14,27,'函数的参数传递方式 - 课后测验','针对函数的参数传递方式的知识点进行测验，包含选择题和编程题'),(15,28,'局部变量和全局变量 - 课后测验','针对局部变量和全局变量的知识点进行测验，包含选择题和编程题'),(16,29,'一维数组和二维数组的定义、初始化和遍历 - 课后测验','针对一维数组和二维数组的定义、初始化和遍历的知识点进行测验，包含选择题和编程题'),(17,30,'字符串的基本操作 - 课后测验','针对字符串的基本操作的知识点进行测验，包含选择题和编程题'),(18,31,'字符数组与字符串 - 课后测验','针对字符数组与字符串的知识点进行测验，包含选择题和编程题'),(19,32,'指针的基本概念和用法 - 课后测验','针对指针的基本概念和用法的知识点进行测验，包含选择题和编程题'),(20,33,'指针与数组的关系 - 课后测验','针对指针与数组的关系的知识点进行测验，包含选择题和编程题'),(21,34,'指针与函数的参数传递 - 课后测验','针对指针与函数的参数传递的知识点进行测验，包含选择题和编程题'),(22,35,'结构体的定义和初始化 - 课后测验','针对结构体的定义和初始化的知识点进行测验，包含选择题和编程题'),(23,36,'结构体数组、结构体指针和结构体嵌套 - 课后测验','针对结构体数组、结构体指针和结构体嵌套的知识点进行测验，包含选择题和编程题'),(24,37,'共用体的概念和用法 - 课后测验','针对共用体的概念和用法的知识点进行测验，包含选择题和编程题'),(25,38,'文件的基本概念和分类 - 课后测验','针对文件的基本概念和分类的知识点进行测验，包含选择题和编程题'),(26,39,'文件的基本操作 - 课后测验','针对文件的基本操作的知识点进行测验，包含选择题和编程题'),(27,40,'文件定位函数 - 课后测验','针对文件定位函数的知识点进行测验，包含选择题和编程题'),(28,41,'宏定义的基本用法 - 课后测验','针对宏定义的基本用法的知识点进行测验，包含选择题和编程题'),(29,42,'文件包含指令 - 课后测验','针对文件包含指令的知识点进行测验，包含选择题和编程题'),(30,43,'条件编译指令 - 课后测验','针对条件编译指令的知识点进行测验，包含选择题和编程题'),(31,44,'动态内存分配的函数 - 课后测验','针对动态内存分配的函数的知识点进行测验，包含选择题和编程题'),(32,45,'动态内存分配的原理和注意事项 - 课后测验','针对动态内存分配的原理和注意事项的知识点进行测验，包含选择题和编程题'),(33,46,'链表的基本操作 - 课后测验','针对链表的基本操作的知识点进行测验，包含选择题和编程题');
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
-- Table structure for table `units`
--

DROP TABLE IF EXISTS `units`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `units` (
  `unit_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '单元ID',
  `course_id` bigint unsigned NOT NULL COMMENT '课程ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单元标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '单元描述',
  `sequence_number` int NOT NULL COMMENT '单元序号',
  PRIMARY KEY (`unit_id`),
  KEY `idx_units_course_id` (`course_id`),
  KEY `idx_units_sequence_number` (`sequence_number`),
  CONSTRAINT `fk_units_course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程单元表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `units`
--

LOCK TABLES `units` WRITE;
/*!40000 ALTER TABLE `units` DISABLE KEYS */;
INSERT INTO `units` VALUES (1,1,'C语言基础','了解C语言的基础知识和基本语法',1),(2,2,'C语言进阶教程 - 默认单元','课程 C语言进阶教程 的默认单元',1),(4,1,'运算符与表达式','掌握C语言中的各类运算符和表达式使用',2),(5,1,'控制结构','学习C语言的程序控制结构',3),(6,1,'函数','深入理解C语言函数的使用方法',4),(7,1,'数组和字符串','掌握数组和字符串的操作',5),(8,1,'指针','理解和掌握C语言指针的使用',6),(9,1,'结构体和共用体','学习复杂数据类型的定义和使用',7),(10,1,'文件操作','掌握C语言的文件操作方法',8),(11,1,'预处理器和宏定义','了解C语言的预处理机制',9),(12,1,'动态内存分配','学习内存管理和链表操作',10);
/*!40000 ALTER TABLE `units` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'teacher_zhang','$2a$10$xK2jfKjd8','zhang@example.com','2024-01-01 10:00:00','2024-03-15 14:30:00',1),(2,'student_wang','$2a$10$kL9jfKjd8','wang@example.com','2024-01-02 11:00:00','2024-03-15 15:20:00',1),(3,'student_li','$2a$10$mN8jfKjd8','li@example.com','2024-01-02 11:30:00','2024-03-15 16:00:00',1),(4,'student_zhao','$2a$10$pQ8jfKjd8','zhao@example.com','2024-01-02 14:00:00','2024-03-15 16:30:00',1),(5,'admin_chen','$2a$10$rT8jfKjd8','chen@example.com','2024-01-01 09:00:00','2024-03-15 17:00:00',1),(6,'SevenGod','$2a$10$96AsPJhMo1O3h/qefiyi0.y3CBP2pFdvGln.E9dlp2f/ZWxByWBPa','1228785484@qq.com','2024-11-18 00:34:38',NULL,1),(7,'ZJH123','$2a$10$YMu97E5zDKF3NsGsVrzC6.f1cQJrU5bHJanyXfuPGWImmj/uHjaA.','3244236643@qq.com','2024-11-18 14:27:25',NULL,1);
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

-- Dump completed on 2024-11-25 15:22:42
