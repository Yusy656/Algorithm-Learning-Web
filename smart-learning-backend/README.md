# 智能学习交互网站运行与测试指南

本文档将指导您如何运行智能学习交互网站的前端和后端，并在没有实际数据库的情况下进行测试。

## 目录结构

```
workspace/
├── smart-learning-website/     # 前端代码
│   ├── index.html
│   ├── login.html
│   ├── home.html
│   ├── question-generate.html
│   ├── knowledge-answer.html
│   ├── style.css
│   └── script.js
└── smart-learning-backend/     # 后端代码
    ├── src/
    └── pom.xml
```

## 一、前端运行

前端是纯HTML/CSS/JavaScript代码，不需要特殊的构建工具，可以直接在浏览器中运行。

### 方法1：直接打开HTML文件

1. 进入 `smart-learning-website` 目录
2. 双击 `login.html` 文件，即可在浏览器中打开登录页面

### 方法2：使用本地服务器（推荐）

使用本地服务器可以避免跨域问题，推荐使用以下方法：

#### 使用Python内置服务器

```bash
# 进入前端目录
cd /home/user/vibecoding/workspace/smart-learning-website

# 启动Python服务器
python -m http.server 8000
```

然后在浏览器中访问：`http://localhost:8000/login.html`

#### 使用Node.js的http-server

```bash
# 安装http-server（如果未安装）
npm install -g http-server

# 进入前端目录
cd /home/user/vibecoding/workspace/smart-learning-website

# 启动服务器
http-server -p 8000
```

然后在浏览器中访问：`http://localhost:8000/login.html`

## 二、后端运行

后端使用Spring Boot框架，需要Java环境和Maven构建工具。

### 1. 环境要求

- JDK 8 或更高版本
- Maven 3.6 或更高版本

### 2. 修改配置文件

在运行前，需要修改配置文件以支持无数据库测试：

```bash
# 编辑配置文件
vi /home/user/vibecoding/workspace/smart-learning-backend/src/main/resources/application.properties
```

将数据库配置修改为使用H2内存数据库：

```properties
# 数据库配置 - 使用H2内存数据库
spring.datasource.url=jdbc:h2:mem:smart_learning;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2控制台配置
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA配置
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

### 3. 添加H2依赖

需要在`pom.xml`中添加H2数据库依赖：

```bash
vi /home/user/vibecoding/workspace/smart-learning-backend/pom.xml
```

添加以下依赖：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 4. 创建数据初始化脚本

创建H2数据库初始化脚本：

```bash
mkdir -p /home/user/vibecoding/workspace/smart-learning-backend/src/main/resources/db/migration
vi /home/user/vibecoding/workspace/smart-learning-backend/src/main/resources/db/migration/V1__init.sql
```

添加初始化数据：

```sql
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
```

### 5. 编译和运行后端

```bash
# 进入后端目录
cd /home/user/vibecoding/workspace/smart-learning-backend

# 编译项目
mvn clean package

# 运行应用
java -jar target/smart-learning-backend-0.0.1-SNAPSHOT.jar
```

后端服务将在 `http://localhost:8080` 上运行。

### 6. 访问H2控制台

在浏览器中访问 `http://localhost:8080/h2-console`，使用以下信息登录：

- JDBC URL: `jdbc:h2:mem:smart_learning`
- 用户名: `sa`
- 密码: 空

## 三、Python服务模拟

由于前端需要与Python服务交互，但我们可能没有实际的Python服务，可以使用以下方法模拟：

### 方法1：使用Spring Boot模拟Python服务接口

在后端添加Python服务模拟控制器：

```bash
vi /home/user/vibecoding/workspace/smart-learning-backend/src/main/java/com/smartlearning/controller/PythonServiceController.java
```

添加以下代码：

```java
package com.smartlearning.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Python服务模拟控制器
 */
@RestController
@RequestMapping("/python-api")
public class PythonServiceController {

    /**
     * 模拟生成题目
     */
    @PostMapping("/question/generate")
    public Map<String, Object> generateQuestions(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "生成题目成功");
        
        List<Map<String, Object>> questions = new ArrayList<>();
        
        // 生成解答题
        if ("essay".equals(request.get("questionType"))) {
            int count = (int) request.get("count");
            String knowledgePointId = (String) request.get("knowledgePointId");
            String difficulty = (String) request.get("difficulty");
            
            for (int i = 0; i < count; i++) {
                Map<String, Object> question = new HashMap<>();
                question.put("id", "essay_" + System.currentTimeMillis() + "_" + i);
                question.put("type", "essay");
                question.put("content", "请详细解答以下问题：" + getKnowledgePointName(knowledgePointId) + "中关于" + difficulty + "难度的知识点。");
                question.put("score", difficulty.equals("simple") ? 10 : (difficulty.equals("medium") ? 15 : 20));
                questions.add(question);
            }
        }
        
        result.put("data", Map.of("questions", questions));
        return result;
    }
    
    /**
     * 模拟评估答案
     */
    @PostMapping("/question/evaluate")
    public Map<String, Object> evaluateAnswers(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "评分完成");
        
        List<Map<String, Object>> questions = (List<Map<String, Object>>) request.get("questions");
        
        // 简单评分逻辑
        int totalScore = 0;
        int correctCount = 0;
        
        for (Map<String, Object> question : questions) {
            if ("essay".equals(question.get("type"))) {
                String answer = (String) question.get("answer");
                // 简单判断答案长度
                if (answer != null && answer.length() > 50) {
                    correctCount++;
                    totalScore += 15; // 假设每题15分
                }
            }
        }
        
        result.put("data", Map.of(
                "score", totalScore,
                "correctCount", correctCount,
                "totalCount", questions.size()
        ));
        
        return result;
    }
    
    /**
     * 模拟知识解答
     */
    @PostMapping("/knowledge/ask")
    public Map<String, Object> askQuestion(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取答案成功");
        
        String content = (String) request.get("content");
        String sessionId = (String) request.get("sessionId");
        
        // 生成会话ID
        if (sessionId == null) {
            sessionId = "session_" + System.currentTimeMillis();
        }
        
        // 简单的答案生成逻辑
        String answer = generateAnswer(content);
        
        result.put("data", Map.of(
                "answer", answer,
                "sessionId", sessionId
        ));
        
        return result;
    }
    
    /**
     * 根据知识点ID获取知识点名称
     */
    private String getKnowledgePointName(String knowledgePointId) {
        Map<String, String> knowledgePoints = new HashMap<>();
        knowledgePoints.put("1", "数学");
        knowledgePoints.put("2", "代数");
        knowledgePoints.put("3", "几何");
        knowledgePoints.put("4", "物理");
        knowledgePoints.put("5", "力学");
        knowledgePoints.put("6", "电学");
        
        return knowledgePoints.getOrDefault(knowledgePointId, "未知知识点");
    }
    
    /**
     * 生成答案
     */
    private String generateAnswer(String question) {
        // 简单的关键词匹配
        if (question.contains("二次函数")) {
            return "二次函数是指最高次数为2的多项式函数，一般形式为：f(x) = ax² + bx + c，其中a、b、c为常数且a≠0。\n\n二次函数的图像是一条抛物线，当a>0时，抛物线开口向上；当a<0时，抛物线开口向下。\n\n二次函数的主要性质：\n1. 对称轴：x = -b/(2a)\n2. 顶点坐标：(-b/(2a), f(-b/(2a)))\n3. 判别式：Δ = b² - 4ac\n   - Δ>0时，函数有两个不同的实数根\n   - Δ=0时，函数有一个重根\n   - Δ<0时，函数没有实数根";
        } else if (question.contains("物理") || question.contains("力学")) {
            return "力学是物理学的一个分支，主要研究物体的运动和力的作用。\n\n力学的基本概念包括：\n1. 力：物体间的相互作用，可以改变物体的运动状态或使物体发生形变\n2. 质量：物体所含物质的多少，是物体惯性大小的量度\n3. 加速度：物体速度变化率的物理量\n\n牛顿三大定律是力学的基础：\n1. 牛顿第一定律（惯性定律）：一个物体如果不受外力作用，将保持静止状态或匀速直线运动状态\n2. 牛顿第二定律：物体的加速度与所受的合外力成正比，与物体的质量成反比\n3. 牛顿第三定律：作用力与反作用力大小相等，方向相反，作用在不同物体上";
        } else if (question.contains("化学") || question.contains("方程式")) {
            return "化学方程式是用化学式表示化学反应的式子，它反映了化学反应中反应物和生成物之间的质量关系和物质的量关系。\n\n化学方程式的配平原则：\n1. 质量守恒定律：反应前后各元素的原子种类和数目不变\n2. 电荷守恒：对于离子反应，反应前后电荷总数相等\n\n配平化学方程式的常用方法：\n1. 观察法：通过观察直接配平简单的化学方程式\n2. 最小公倍数法：找出反应前后各元素原子数的最小公倍数进行配平\n3. 奇数配偶法：将奇数原子变为偶数，再进行配平\n4. 氧化还原法：根据氧化还原反应中电子转移的数目进行配平";
        } else {
            return "您好！我是智能学习助手，可以帮您解答各种学习问题。您的问题是：\"" + question + "\"\n\n这是一个模拟的回答。在实际应用中，这里会调用大语言模型来生成更准确、更详细的答案。\n\n如果您有更具体的问题，请提供更多细节，我会尽力为您提供更好的解答。";
        }
    }
}
```

### 方法2：使用Node.js模拟Python服务

如果您熟悉Node.js，也可以使用Node.js创建一个简单的服务器来模拟Python服务：

```bash
# 创建Python服务模拟目录
mkdir -p /home/user/vibecoding/workspace/python-service-mock
cd /home/user/vibecoding/workspace/python-service-mock

# 创建package.json
npm init -y

# 安装依赖
npm install express cors body-parser

# 创建server.js
vi server.js
```

添加以下代码：

```javascript
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
app.use(cors());
app.use(bodyParser.json({ limit: '50mb' }));

// 模拟生成题目
app.post('/python-api/question/generate', (req, res) => {
    const { knowledgePointId, difficulty, questionType, count, expansionLevel } = req.body;
    
    let questions = [];
    
    // 生成解答题
    if (questionType === 'essay') {
        for (let i = 0; i < count; i++) {
            questions.push({
                id: `essay_${Date.now()}_${i}`,
                type: 'essay',
                content: `请详细解答以下问题：${getKnowledgePointName(knowledgePointId)}中关于${difficulty}难度的知识点。`,
                score: difficulty === 'simple' ? 10 : (difficulty === 'medium' ? 15 : 20)
            });
        }
    }
    
    res.json({
        success: true,
        message: '生成题目成功',
        data: { questions }
    });
});

// 模拟评估答案
app.post('/python-api/question/evaluate', (req, res) => {
    const { questions } = req.body;
    
    // 简单评分逻辑
    let totalScore = 0;
    let correctCount = 0;
    
    questions.forEach(question => {
        if (question.type === 'essay') {
            const answer = question.answer;
            // 简单判断答案长度
            if (answer && answer.length > 50) {
                correctCount++;
                totalScore += 15; // 假设每题15分
            }
        }
    });
    
    res.json({
        success: true,
        message: '评分完成',
        data: {
            score: totalScore,
            correctCount,
            totalCount: questions.length
        }
    });
});

// 模拟知识解答
app.post('/python-api/knowledge/ask', (req, res) => {
    const { content, image, sessionId } = req.body;
    
    // 生成会话ID
    const newSessionId = sessionId || `session_${Date.now()}`;
    
    // 简单的答案生成逻辑
    const answer = generateAnswer(content);
    
    res.json({
        success: true,
        message: '获取答案成功',
        data: {
            answer,
            sessionId: newSessionId
        }
    });
});

// 根据知识点ID获取知识点名称
function getKnowledgePointName(knowledgePointId) {
    const knowledgePoints = {
        '1': '数学',
        '2': '代数',
        '3': '几何',
        '4': '物理',
        '5': '力学',
        '6': '电学'
    };
    
    return knowledgePoints[knowledgePointId] || '未知知识点';
}

// 生成答案
function generateAnswer(question) {
    // 简单的关键词匹配
    if (question.includes('二次函数')) {
        return '二次函数是指最高次数为2的多项式函数，一般形式为：f(x) = ax² + bx + c，其中a、b、c为常数且a≠0。\n\n二次函数的图像是一条抛物线，当a>0时，抛物线开口向上；当a<0时，抛物线开口向下。\n\n二次函数的主要性质：\n1. 对称轴：x = -b/(2a)\n2. 顶点坐标：(-b/(2a), f(-b/(2a)))\n3. 判别式：Δ = b² - 4ac\n   - Δ>0时，函数有两个不同的实数根\n   - Δ=0时，函数有一个重根\n   - Δ<0时，函数没有实数根';
    } else if (question.includes('物理') || question.includes('力学')) {
        return '力学是物理学的一个分支，主要研究物体的运动和力的作用。\n\n力学的基本概念包括：\n1. 力：物体间的相互作用，可以改变物体的运动状态或使物体发生形变\n2. 质量：物体所含物质的多少，是物体惯性大小的量度\n3. 加速度：物体速度变化率的物理量\n\n牛顿三大定律是力学的基础：\n1. 牛顿第一定律（惯性定律）：一个物体如果不受外力作用，将保持静止状态或匀速直线运动状态\n2. 牛顿第二定律：物体的加速度与所受的合外力成正比，与物体的质量成反比\n3. 牛顿第三定律：作用力与反作用力大小相等，方向相反，作用在不同物体上';
    } else if (question.includes('化学') || question.includes('方程式')) {
        return '化学方程式是用化学式表示化学反应的式子，它反映了化学反应中反应物和生成物之间的质量关系和物质的量关系。\n\n化学方程式的配平原则：\n1. 质量守恒定律：反应前后各元素的原子种类和数目不变\n2. 电荷守恒：对于离子反应，反应前后电荷总数相等\n\n配平化学方程式的常用方法：\n1. 观察法：通过观察直接配平简单的化学方程式\n2. 最小公倍数法：找出反应前后各元素原子数的最小公倍数进行配平\n3. 奇数配偶法：将奇数原子变为偶数，再进行配平\n4. 氧化还原法：根据氧化还原反应中电子转移的数目进行配平';
    } else {
        return `您好！我是智能学习助手，可以帮您解答各种学习问题。您的问题是："${question}"\n\n这是一个模拟的回答。在实际应用中，这里会调用大语言模型来生成更准确、更详细的答案。\n\n如果您有更具体的问题，请提供更多细节，我会尽力为您提供更好的解答。`;
    }
}

// 启动服务器
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Python服务模拟服务器运行在 http://localhost:${PORT}`);
});
```

运行Node.js服务器：

```bash
# 运行服务器
node server.js
```

然后修改后端配置文件中的Python服务URL：

```bash
vi /home/user/vibecoding/workspace/smart-learning-backend/src/main/resources/application.properties
```

将Python服务URL修改为：

```properties
python.service.url=http://localhost:5000
```

## 四、完整运行流程

### 1. 启动后端服务

```bash
# 进入后端目录
cd /home/user/vibecoding/workspace/smart-learning-backend

# 运行应用
java -jar target/smart-learning-backend-0.0.1-SNAPSHOT.jar
```

### 2. 启动Python服务模拟（如果使用方法2）

```bash
# 进入Python服务模拟目录
cd /home/user/vibecoding/workspace/python-service-mock

# 运行服务器
node server.js
```

### 3. 启动前端服务

```bash
# 进入前端目录
cd /home/user/vibecoding/workspace/smart-learning-website

# 启动Python服务器
python -m http.server 8000
```

### 4. 访问应用

在浏览器中访问 `http://localhost:8000/login.html`，使用以下测试账号登录：

- 学号：20210001
- 密码：123456

## 五、测试功能

### 1. 登录注册功能

- 尝试使用测试账号登录
- 尝试注册新账号

### 2. 智能出题功能

- 选择知识点、难度、题型等筛选条件
- 点击"生成题目"按钮
- 尝试回答题目并提交
- 查看得分

### 3. 智能知识解答功能

- 输入问题并发送
- 尝试上传图片并提问
- 查看历史对话记录

## 六、常见问题解决

### 1. 跨域问题

如果遇到跨域问题，可以尝试以下解决方法：

- 确保后端已配置CORS（本项目已配置）
- 使用相同的域名和端口运行前端和后端
- 使用代理服务器

### 2. 数据库连接问题

如果遇到数据库连接问题：

- 检查H2数据库配置是否正确
- 确保H2依赖已添加到pom.xml
- 检查数据库初始化脚本是否正确

### 3. Python服务调用失败

如果遇到Python服务调用失败：

- 确保Python服务模拟已启动
- 检查后端配置文件中的Python服务URL是否正确
- 查看后端日志以获取详细错误信息

## 七、后续开发建议

1. **完善数据库设计**：根据实际需求优化数据库结构
2. **实现真实的Python服务**：使用Python和大语言模型实现真正的智能出题和知识解答功能
3. **添加更多题型**：支持更多类型的题目，如判断题、连线题等
4. **优化用户界面**：改进前端界面，提供更好的用户体验
5. **添加更多功能**：如学习进度跟踪、知识点掌握度分析等