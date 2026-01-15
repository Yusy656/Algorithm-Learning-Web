-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建知识点表
CREATE TABLE IF NOT EXISTS knowledge_point (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id BIGINT,
    level INT
);

-- 创建题目表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    score INT NOT NULL,
    answer TEXT,
    analysis TEXT,
    knowledge_point_id BIGINT
);

-- 创建题目选项表
CREATE TABLE IF NOT EXISTS question_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_id VARCHAR(10) NOT NULL,
    content TEXT NOT NULL,
    question_id BIGINT
);

-- 创建会话表
CREATE TABLE IF NOT EXISTS conversation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200),
    session_id VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建消息表
CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    image TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    conversation_id BIGINT
);

-- 插入测试数据
-- 插入用户数据
INSERT INTO user (student_id, password) VALUES ('20210001', '123456');
INSERT INTO user (student_id, password) VALUES ('20210002', '123456');

-- 插入知识点数据
INSERT INTO knowledge_point (name, description, level) VALUES ('数学', '数学学科', 1);
INSERT INTO knowledge_point (name, description, parent_id, level) VALUES ('代数', '代数学科', 1, 2);
INSERT INTO knowledge_point (name, description, parent_id, level) VALUES ('几何', '几何学科', 1, 2);
INSERT INTO knowledge_point (name, description, level) VALUES ('物理', '物理学科', 1);
INSERT INTO knowledge_point (name, description, parent_id, level) VALUES ('力学', '力学学科', 4, 2);
INSERT INTO knowledge_point (name, description, parent_id, level) VALUES ('电学', '电学学科', 4, 2);

-- 插入题目数据
-- 代数单选题
INSERT INTO question (content, type, difficulty, score, answer, knowledge_point_id) 
VALUES ('一元二次方程 x² - 5x + 6 = 0 的解为？', 'single_choice', 'simple', 10, 'C', 2);

-- 代数单选题选项
INSERT INTO question_option (option_id, content, question_id) VALUES ('A', 'x = 1, x = 6', 1);
INSERT INTO question_option (option_id, content, question_id) VALUES ('B', 'x = 2, x = 4', 1);
INSERT INTO question_option (option_id, content, question_id) VALUES ('C', 'x = 2, x = 3', 1);
INSERT INTO question_option (option_id, content, question_id) VALUES ('D', 'x = 3, x = 4', 1);

-- 几何多选题
INSERT INTO question (content, type, difficulty, score, answer, knowledge_point_id) 
VALUES ('以下哪些是三角形的性质？', 'multiple_choice', 'medium', 15, 'A,C,D', 3);

-- 几何多选题选项
INSERT INTO question_option (option_id, content, question_id) VALUES ('A', '三角形内角和为180度', 2);
INSERT INTO question_option (option_id, content, question_id) VALUES ('B', '三角形外角和为360度', 2);
INSERT INTO question_option (option_id, content, question_id) VALUES ('C', '两边之和大于第三边', 2);
INSERT INTO question_option (option_id, content, question_id) VALUES ('D', '等边三角形三边相等', 2);

-- 力学填空题
INSERT INTO question (content, type, difficulty, score, answer, knowledge_point_id) 
VALUES ('牛顿第二定律的数学表达式为？', 'fill_blank', 'simple', 10, 'F=ma', 5);

-- 电学解答题
INSERT INTO question (content, type, difficulty, score, answer, knowledge_point_id) 
VALUES ('简述欧姆定律及其在电路分析中的应用。', 'essay', 'difficult', 20, '欧姆定律表述为：在恒温条件下，导体中的电流与导体两端的电压成正比，与导体的电阻成反比。数学表达式为：I=U/R，其中I为电流，U为电压，R为电阻。', 6);