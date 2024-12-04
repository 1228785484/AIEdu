/*
 Navicat Premium Dump SQL

 Source Server         : hou
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : hhh

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 29/11/2024 14:44:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chapter_progress
-- ----------------------------
DROP TABLE IF EXISTS `chapter_progress`;
CREATE TABLE `chapter_progress`  (
  `progress_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `enrollment_id` bigint UNSIGNED NOT NULL COMMENT '报名ID',
  `chapter_id` bigint UNSIGNED NOT NULL COMMENT '章节ID',
  `completion_percentage` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '完成百分比',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'not_started' COMMENT '状态(not_started:未开始,in_progress:进行中,completed:已完成)',
  `last_accessed` datetime NULL DEFAULT NULL COMMENT '最后访问时间',
  PRIMARY KEY (`progress_id`) USING BTREE,
  UNIQUE INDEX `idx_enrollment_chapter`(`enrollment_id` ASC, `chapter_id` ASC) USING BTREE,
  INDEX `idx_chapter_id`(`chapter_id` ASC) USING BTREE,
  CONSTRAINT `fk_chapter_progress_chapter_id` FOREIGN KEY (`chapter_id`) REFERENCES `course_chapters` (`chapter_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_chapter_progress_enrollment_id` FOREIGN KEY (`enrollment_id`) REFERENCES `course_enrollments` (`enrollment_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '章节进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chapter_progress
-- ----------------------------

-- ----------------------------
-- Table structure for course_chapters
-- ----------------------------
DROP TABLE IF EXISTS `course_chapters`;
CREATE TABLE `course_chapters`  (
  `chapter_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` bigint UNSIGNED NOT NULL COMMENT '课程ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节标题',
  `sequence_number` int NOT NULL COMMENT '章节序号',
  `unit_id` bigint UNSIGNED NOT NULL COMMENT '单元ID',
  `content_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '章节内容提示词',
  `finish_points` tinyint UNSIGNED NOT NULL COMMENT '章节任务点',
  `total_mission_points` tinyint NOT NULL COMMENT '总任务点\r\n',
  PRIMARY KEY (`chapter_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_sequence_number`(`sequence_number` ASC) USING BTREE,
  INDEX `idx_unit_id`(`unit_id` ASC) USING BTREE,
  CONSTRAINT `fk_course_chapters_course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_course_chapters_unit_id` FOREIGN KEY (`unit_id`) REFERENCES `units` (`unit_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程章节表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_chapters
-- ----------------------------
INSERT INTO `course_chapters` VALUES (4, 2, '第1章：指针基础', 1, 2, NULL, 0, 0);
INSERT INTO `course_chapters` VALUES (5, 2, '第2章：动态内存分配', 2, 2, NULL, 0, 0);
INSERT INTO `course_chapters` VALUES (16, 1, 'C语言的发展历程、特点及应用领域', 1, 1, '介绍C语言的历史发展、主要特点和应用场景', 0, 1);
INSERT INTO `course_chapters` VALUES (17, 1, '基本语法规则', 2, 1, '讲解C语言的基本语法规则和代码结构', 0, 0);
INSERT INTO `course_chapters` VALUES (18, 1, '变量、数据类型和常量', 3, 1, '详细介绍C语言的变量定义、基本数据类型和常量使用', 0, 0);
INSERT INTO `course_chapters` VALUES (19, 1, '输入输出函数', 4, 1, '讲解scanf和printf等基本输入输出函数的使用', 0, 0);
INSERT INTO `course_chapters` VALUES (20, 1, '算术运算符、关系运算符和逻辑运算符', 1, 4, '详细讲解各类运算符的使用方法和优先级', 0, 0);
INSERT INTO `course_chapters` VALUES (21, 1, '赋值运算符和复合赋值运算符', 2, 4, '介绍简单赋值和复合赋值运算符的使用', 0, 0);
INSERT INTO `course_chapters` VALUES (22, 1, '表达式的概念及运算规则', 3, 4, '讲解表达式的构成和计算规则', 0, 0);
INSERT INTO `course_chapters` VALUES (23, 1, '顺序结构和选择结构', 1, 5, '讲解if语句和switch语句的使用', 0, 0);
INSERT INTO `course_chapters` VALUES (24, 1, '循环结构', 2, 5, '详细介绍for、while和do-while循环的使用', 0, 0);
INSERT INTO `course_chapters` VALUES (25, 1, 'break和continue', 3, 5, '讲解循环控制语句的使用方法', 0, 0);
INSERT INTO `course_chapters` VALUES (26, 1, '函数的定义、声明和调用', 1, 6, '讲解如何定义和使用函数', 0, 0);
INSERT INTO `course_chapters` VALUES (27, 1, '函数的参数传递方式', 2, 6, '介绍值传递和地址传递的概念和使用', 0, 0);
INSERT INTO `course_chapters` VALUES (28, 1, '局部变量和全局变量', 3, 6, '讲解变量作用域和生命周期', 0, 0);
INSERT INTO `course_chapters` VALUES (29, 1, '一维数组和二维数组的定义、初始化和遍历', 1, 7, '详细讲解数组的使用方法', 0, 0);
INSERT INTO `course_chapters` VALUES (30, 1, '字符串的基本操作', 2, 7, '介绍字符串处理函数的使用', 0, 0);
INSERT INTO `course_chapters` VALUES (31, 1, '字符数组与字符串', 3, 7, '讲解字符数组和字符串的关系', 0, 0);
INSERT INTO `course_chapters` VALUES (32, 1, '指针的基本概念和用法', 1, 8, '介绍指针的基本概念和声明方法', 0, 0);
INSERT INTO `course_chapters` VALUES (33, 1, '指针与数组的关系', 2, 8, '讲解指针和数组的关系及使用', 0, 0);
INSERT INTO `course_chapters` VALUES (34, 1, '指针与函数的参数传递', 3, 8, '详细介绍指针作为函数参数的使用', 0, 0);
INSERT INTO `course_chapters` VALUES (35, 1, '结构体的定义和初始化', 1, 9, '讲解结构体的基本用法', 0, 0);
INSERT INTO `course_chapters` VALUES (36, 1, '结构体数组、结构体指针和结构体嵌套', 2, 9, '介绍结构体的高级用法', 0, 0);
INSERT INTO `course_chapters` VALUES (37, 1, '共用体的概念和用法', 3, 9, '讲解共用体的使用方法', 0, 0);
INSERT INTO `course_chapters` VALUES (38, 1, '文件的基本概念和分类', 1, 10, '介绍文件的基本概念', 0, 0);
INSERT INTO `course_chapters` VALUES (39, 1, '文件的基本操作', 2, 10, '讲解文件的打开、关闭、读写等操作', 0, 0);
INSERT INTO `course_chapters` VALUES (40, 1, '文件定位函数', 3, 10, '介绍文件指针的移动和定位', 0, 0);
INSERT INTO `course_chapters` VALUES (41, 1, '宏定义的基本用法', 1, 11, '讲解宏定义的语法和使用方法', 0, 0);
INSERT INTO `course_chapters` VALUES (42, 1, '文件包含指令', 2, 11, '介绍头文件的包含和使用', 0, 0);
INSERT INTO `course_chapters` VALUES (43, 1, '条件编译指令', 3, 11, '讲解条件编译的概念和使用', 0, 0);
INSERT INTO `course_chapters` VALUES (44, 1, '动态内存分配的函数', 1, 12, '讲解malloc、free等函数的使用', 0, 0);
INSERT INTO `course_chapters` VALUES (45, 1, '动态内存分配的原理和注意事项', 2, 12, '介绍内存管理的注意事项', 0, 0);
INSERT INTO `course_chapters` VALUES (46, 1, '链表的基本操作', 3, 12, '讲解链表的创建、插入、删除等操作', 0, 0);

-- ----------------------------
-- Table structure for course_enrollments
-- ----------------------------
DROP TABLE IF EXISTS `course_enrollments`;
CREATE TABLE `course_enrollments`  (
  `enrollment_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `course_id` bigint UNSIGNED NOT NULL COMMENT '课程ID',
  `enrolled_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `completed_at` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ongoing' COMMENT '状态(ongoing:进行中,completed:已完成,dropped:已退出)',
  PRIMARY KEY (`enrollment_id`) USING BTREE,
  UNIQUE INDEX `idx_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_enrolled_at`(`enrolled_at` ASC) USING BTREE,
  INDEX `fk_course_enrollments_course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `fk_course_enrollments_course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_course_enrollments_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_enrollments
-- ----------------------------
INSERT INTO `course_enrollments` VALUES (1, 2, 1, '2024-01-04 10:00:00', NULL, 'ongoing');
INSERT INTO `course_enrollments` VALUES (2, 3, 1, '2024-01-04 11:00:00', NULL, 'ongoing');
INSERT INTO `course_enrollments` VALUES (3, 4, 1, '2024-01-04 14:00:00', NULL, 'ongoing');
INSERT INTO `course_enrollments` VALUES (4, 2, 2, '2024-01-05 10:00:00', NULL, 'ongoing');

-- ----------------------------
-- Table structure for courses
-- ----------------------------
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses`  (
  `course_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课程描述',
  `category_id` bigint UNSIGNED NOT NULL COMMENT '分类ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否激活',
  PRIMARY KEY (`course_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of courses
-- ----------------------------
INSERT INTO `courses` VALUES (1, 'C语言基础教程', 'C语言入门课程，涵盖基本语法、数据类型、控制结构等', 2, '2024-01-03 09:00:00', 1);
INSERT INTO `courses` VALUES (2, 'C语言进阶教程', '深入学习指针、内存管理、结构体等概念', 3, '2024-01-03 09:30:00', 1);

-- ----------------------------
-- Table structure for personalized_contents
-- ----------------------------
DROP TABLE IF EXISTS `personalized_contents`;
CREATE TABLE `personalized_contents`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `chapter_id` bigint UNSIGNED NOT NULL COMMENT '章节ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '个性化内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否激活',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_chapter`(`user_id` ASC, `chapter_id` ASC) USING BTREE,
  INDEX `fk_pers_content_chapter_id`(`chapter_id` ASC) USING BTREE,
  CONSTRAINT `fk_pers_content_chapter_id` FOREIGN KEY (`chapter_id`) REFERENCES `course_chapters` (`chapter_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_pers_content_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '个性化章节内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of personalized_contents
-- ----------------------------

-- ----------------------------
-- Table structure for quiz_submissions
-- ----------------------------
DROP TABLE IF EXISTS `quiz_submissions`;
CREATE TABLE `quiz_submissions`  (
  `submission_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `quiz_id` bigint UNSIGNED NOT NULL COMMENT '测验ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `answers` json NOT NULL COMMENT '答案(JSON格式)',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '得分',
  `submitted_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `generated_questions` json NOT NULL COMMENT '实际生成的题目',
  `finish_point` tinyint NOT NULL,
  PRIMARY KEY (`submission_id`) USING BTREE,
  INDEX `idx_quiz_id`(`quiz_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_submitted_at`(`submitted_at` ASC) USING BTREE,
  CONSTRAINT `fk_quiz_submissions_quiz_id` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`quiz_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_quiz_submissions_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '测验提交表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of quiz_submissions
-- ----------------------------

-- ----------------------------
-- Table structure for quizzes
-- ----------------------------
DROP TABLE IF EXISTS `quizzes`;
CREATE TABLE `quizzes`  (
  `quiz_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '测验ID',
  `chapter_id` bigint UNSIGNED NOT NULL COMMENT '章节ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '测验标题',
  `quiz_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '出题提示词',
  PRIMARY KEY (`quiz_id`) USING BTREE,
  INDEX `idx_chapter_id`(`chapter_id` ASC) USING BTREE,
  CONSTRAINT `fk_quizzes_chapter_id` FOREIGN KEY (`chapter_id`) REFERENCES `course_chapters` (`chapter_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '测验表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of quizzes
-- ----------------------------
INSERT INTO `quizzes` VALUES (3, 16, 'C语言的发展历程、特点及应用领域 - 课后测验', '针对C语言的发展历程、特点及应用领域的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (4, 17, '基本语法规则 - 课后测验', '针对基本语法规则的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (5, 18, '变量、数据类型和常量 - 课后测验', '针对变量、数据类型和常量的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (6, 19, '输入输出函数 - 课后测验', '针对输入输出函数的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (7, 20, '算术运算符、关系运算符和逻辑运算符 - 课后测验', '针对算术运算符、关系运算符和逻辑运算符的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (8, 21, '赋值运算符和复合赋值运算符 - 课后测验', '针对赋值运算符和复合赋值运算符的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (9, 22, '表达式的概念及运算规则 - 课后测验', '针对表达式的概念及运算规则的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (10, 23, '顺序结构和选择结构 - 课后测验', '针对顺序结构和选择结构的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (11, 24, '循环结构 - 课后测验', '针对循环结构的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (12, 25, 'break和continue - 课后测验', '针对break和continue的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (13, 26, '函数的定义、声明和调用 - 课后测验', '针对函数的定义、声明和调用的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (14, 27, '函数的参数传递方式 - 课后测验', '针对函数的参数传递方式的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (15, 28, '局部变量和全局变量 - 课后测验', '针对局部变量和全局变量的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (16, 29, '一维数组和二维数组的定义、初始化和遍历 - 课后测验', '针对一维数组和二维数组的定义、初始化和遍历的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (17, 30, '字符串的基本操作 - 课后测验', '针对字符串的基本操作的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (18, 31, '字符数组与字符串 - 课后测验', '针对字符数组与字符串的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (19, 32, '指针的基本概念和用法 - 课后测验', '针对指针的基本概念和用法的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (20, 33, '指针与数组的关系 - 课后测验', '针对指针与数组的关系的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (21, 34, '指针与函数的参数传递 - 课后测验', '针对指针与函数的参数传递的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (22, 35, '结构体的定义和初始化 - 课后测验', '针对结构体的定义和初始化的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (23, 36, '结构体数组、结构体指针和结构体嵌套 - 课后测验', '针对结构体数组、结构体指针和结构体嵌套的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (24, 37, '共用体的概念和用法 - 课后测验', '针对共用体的概念和用法的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (25, 38, '文件的基本概念和分类 - 课后测验', '针对文件的基本概念和分类的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (26, 39, '文件的基本操作 - 课后测验', '针对文件的基本操作的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (27, 40, '文件定位函数 - 课后测验', '针对文件定位函数的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (28, 41, '宏定义的基本用法 - 课后测验', '针对宏定义的基本用法的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (29, 42, '文件包含指令 - 课后测验', '针对文件包含指令的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (30, 43, '条件编译指令 - 课后测验', '针对条件编译指令的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (31, 44, '动态内存分配的函数 - 课后测验', '针对动态内存分配的函数的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (32, 45, '动态内存分配的原理和注意事项 - 课后测验', '针对动态内存分配的原理和注意事项的知识点进行测验，包含选择题和编程题');
INSERT INTO `quizzes` VALUES (33, 46, '链表的基本操作 - 课后测验', '针对链表的基本操作的知识点进行测验，包含选择题和编程题');

-- ----------------------------
-- Table structure for sorted_units
-- ----------------------------
DROP TABLE IF EXISTS `sorted_units`;
CREATE TABLE `sorted_units`  (
  `unit_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '单元ID',
  `sequence` bigint NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sorted_units
-- ----------------------------
INSERT INTO `sorted_units` VALUES (1, 1);
INSERT INTO `sorted_units` VALUES (4, 2);
INSERT INTO `sorted_units` VALUES (5, 3);
INSERT INTO `sorted_units` VALUES (6, 4);
INSERT INTO `sorted_units` VALUES (7, 5);
INSERT INTO `sorted_units` VALUES (8, 6);
INSERT INTO `sorted_units` VALUES (9, 7);
INSERT INTO `sorted_units` VALUES (10, 8);
INSERT INTO `sorted_units` VALUES (11, 9);
INSERT INTO `sorted_units` VALUES (12, 10);

-- ----------------------------
-- Table structure for student_profiles
-- ----------------------------
DROP TABLE IF EXISTS `student_profiles`;
CREATE TABLE `student_profiles`  (
  `profile_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `education_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '教育水平',
  `preferences` json NULL COMMENT '学习偏好配置(JSON格式)',
  PRIMARY KEY (`profile_id`) USING BTREE,
  UNIQUE INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_student_profiles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_profiles
-- ----------------------------
INSERT INTO `student_profiles` VALUES (1, 2, '大学一年级', '{\"learning_style\": \"视觉型\", \"preferred_time\": \"晚上\", \"study_duration\": \"1小时\"}');
INSERT INTO `student_profiles` VALUES (2, 3, '大学二年级', '{\"learning_style\": \"听觉型\", \"preferred_time\": \"上午\", \"study_duration\": \"2小时\"}');
INSERT INTO `student_profiles` VALUES (3, 4, '大学一年级', '{\"learning_style\": \"实践型\", \"preferred_time\": \"下午\", \"study_duration\": \"1.5小时\"}');

-- ----------------------------
-- Table structure for units
-- ----------------------------
DROP TABLE IF EXISTS `units`;
CREATE TABLE `units`  (
  `unit_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '单元ID',
  `course_id` bigint UNSIGNED NOT NULL COMMENT '课程ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单元标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '单元描述',
  `sequence_number` int NOT NULL COMMENT '单元序号',
  PRIMARY KEY (`unit_id`) USING BTREE,
  INDEX `idx_units_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_units_sequence_number`(`sequence_number` ASC) USING BTREE,
  CONSTRAINT `fk_units_course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程单元表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of units
-- ----------------------------
INSERT INTO `units` VALUES (1, 1, 'C语言基础', '了解C语言的基础知识和基本语法', 1);
INSERT INTO `units` VALUES (2, 2, 'C语言进阶教程 - 默认单元', '课程 C语言进阶教程 的默认单元', 1);
INSERT INTO `units` VALUES (4, 1, '运算符与表达式', '掌握C语言中的各类运算符和表达式使用', 2);
INSERT INTO `units` VALUES (5, 1, '控制结构', '学习C语言的程序控制结构', 3);
INSERT INTO `units` VALUES (6, 1, '函数', '深入理解C语言函数的使用方法', 4);
INSERT INTO `units` VALUES (7, 1, '数组和字符串', '掌握数组和字符串的操作', 5);
INSERT INTO `units` VALUES (8, 1, '指针', '理解和掌握C语言指针的使用', 6);
INSERT INTO `units` VALUES (9, 1, '结构体和共用体', '学习复杂数据类型的定义和使用', 7);
INSERT INTO `units` VALUES (10, 1, '文件操作', '掌握C语言的文件操作方法', 8);
INSERT INTO `units` VALUES (11, 1, '预处理器和宏定义', '了解C语言的预处理机制', 9);
INSERT INTO `units` VALUES (12, 1, '动态内存分配', '学习内存管理和链表操作', 10);

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `role_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `role_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称(student:学生,teacher:教师,admin:管理员)',
  `assigned_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '角色分配时间',
  PRIMARY KEY (`role_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES (1, 1, 'teacher', '2024-01-01 10:00:00');
INSERT INTO `user_roles` VALUES (2, 2, 'student', '2024-01-02 11:00:00');
INSERT INTO `user_roles` VALUES (3, 3, 'student', '2024-01-02 11:30:00');
INSERT INTO `user_roles` VALUES (4, 4, 'student', '2024-01-02 14:00:00');
INSERT INTO `user_roles` VALUES (5, 5, 'admin', '2024-01-01 09:00:00');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希值',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电子邮箱',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_login` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否激活(1:激活,0:未激活)',
  `number_sessions` int NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'teacher_zhang', '$2a$10$xK2jfKjd8', 'zhang@example.com', '2024-01-01 10:00:00', '2024-03-15 14:30:00', 1, NULL);
INSERT INTO `users` VALUES (2, 'student_wang', '$2a$10$kL9jfKjd8', 'wang@example.com', '2024-01-02 11:00:00', '2024-03-15 15:20:00', 1, NULL);
INSERT INTO `users` VALUES (3, 'student_li', '$2a$10$mN8jfKjd8', 'li@example.com', '2024-01-02 11:30:00', '2024-03-15 16:00:00', 1, NULL);
INSERT INTO `users` VALUES (4, 'student_zhao', '$2a$10$pQ8jfKjd8', 'zhao@example.com', '2024-01-02 14:00:00', '2024-03-15 16:30:00', 1, NULL);
INSERT INTO `users` VALUES (5, 'admin_chen', '$2a$10$rT8jfKjd8', 'chen@example.com', '2024-01-01 09:00:00', '2024-03-15 17:00:00', 1, NULL);
INSERT INTO `users` VALUES (6, 'SevenGod', '$2a$10$96AsPJhMo1O3h/qefiyi0.y3CBP2pFdvGln.E9dlp2f/ZWxByWBPa', '1228785484@qq.com', '2024-11-18 00:34:38', NULL, 1, NULL);
INSERT INTO `users` VALUES (7, 'ZJH123', '$2a$10$YMu97E5zDKF3NsGsVrzC6.f1cQJrU5bHJanyXfuPGWImmj/uHjaA.', '3244236643@qq.com', '2024-11-18 14:27:25', NULL, 1, NULL);

SET FOREIGN_KEY_CHECKS = 1;
