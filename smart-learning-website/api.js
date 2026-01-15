/**
 * API接口配置与调用逻辑
 * 预留与SpringBoot后端及Python服务的相同接口
 */

// API基础URL配置
const API_CONFIG = {
    // SpringBoot后端API
    springBoot: {
        baseUrl: '/api',
        user: {
            register: '/user/register',
            login: '/user/login'
        },
        question: {
            knowledgePoints: '/question/knowledge-points',
            generate: '/question/generate',
            submit: '/question/submit'
        },
        knowledge: {
            ask: '/knowledge/ask',
            history: '/knowledge/history',
            conversation: '/knowledge/conversation'
        }
    },
    // Python服务API
    pythonService: {
        baseUrl: '/python-api',
        question: {
            generate: '/question/generate',
            evaluate: '/question/evaluate'
        },
        knowledge: {
            ask: '/knowledge/ask'
        }
    }
};

// 当前使用的API类型（可根据环境配置切换）
let currentApiType = 'springBoot';

/**
 * 设置当前使用的API类型
 * @param {string} type - API类型 ('springBoot' 或 'pythonService')
 */
function setApiType(type) {
    if (type === 'springBoot' || type === 'pythonService') {
        currentApiType = type;
    }
}

/**
 * 构建完整的API URL
 * @param {string} module - API模块
 * @param {string} action - API操作
 * @param {string} [id] - 可选的ID参数
 * @returns {string} 完整的API URL
 */
function buildApiUrl(module, action, id = '') {
    const config = API_CONFIG[currentApiType];
    let url = `${config.baseUrl}`;
    
    if (config[module] && config[module][action]) {
        url += config[module][action];
    }
    
    if (id) {
        url = url.replace(/\/$/, '') + `/${id}`;
    }
    
    return url;
}

/**
 * 发送API请求
 * @param {string} url - API URL
 * @param {Object} options - 请求选项
 * @returns {Promise} Promise对象
 */
async function sendRequest(url, options = {}) {
    try {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
            },
            credentials: 'include'
        };
        
        const mergedOptions = { ...defaultOptions, ...options };
        
        const response = await fetch(url, mergedOptions);
        
        // 检查响应状态
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        // 解析响应数据
        const data = await response.json();
        
        return data;
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

/**
 * 用户认证相关API
 */
const userApi = {
    /**
     * 用户注册
     * @param {string} studentId - 学号
     * @param {string} password - 密码
     * @returns {Promise} Promise对象
     */
    async register(studentId, password) {
        const url = buildApiUrl('user', 'register');
        const options = {
            method: 'POST',
            body: JSON.stringify({ studentId, password })
        };
        
        return await sendRequest(url, options);
    },
    
    /**
     * 用户登录
     * @param {string} studentId - 学号
     * @param {string} password - 密码
     * @returns {Promise} Promise对象
     */
    async login(studentId, password) {
        const url = buildApiUrl('user', 'login');
        const options = {
            method: 'POST',
            body: JSON.stringify({ studentId, password })
        };
        
        const data = await sendRequest(url, options);
        
        // 保存token到localStorage
        if (data.success && data.data && data.data.token) {
            localStorage.setItem('token', data.data.token);
            localStorage.setItem('userId', data.data.userId);
            localStorage.setItem('studentId', studentId);
        }
        
        return data;
    },
    
    /**
     * 用户登出
     */
    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('studentId');
    },
    
    /**
     * 检查用户是否已登录
     * @returns {boolean} 是否已登录
     */
    isLoggedIn() {
        return !!localStorage.getItem('token');
    },
    
    /**
     * 获取当前登录用户信息
     * @returns {Object} 用户信息
     */
    getCurrentUser() {
        return {
            userId: localStorage.getItem('userId'),
            studentId: localStorage.getItem('studentId')
        };
    }
};

/**
 * 智能出题相关API
 */
const questionApi = {
    /**
     * 获取知识点列表
     * @returns {Promise} Promise对象
     */
    async getKnowledgePoints() {
        const url = buildApiUrl('question', 'knowledgePoints');
        return await sendRequest(url);
    },
    
    /**
     * 生成题目
     * @param {string} knowledgePointId - 知识点ID
     * @param {string} difficulty - 难度 ('simple', 'medium', 'difficult')
     * @param {string} questionType - 题型 ('single_choice', 'multiple_choice', 'fill_blank', 'essay')
     * @param {number} count - 题目数量 (1-10)
     * @param {string} expansionLevel - 知识拓展度 ('simple', 'medium', 'difficult')
     * @returns {Promise} Promise对象
     */
    async generateQuestions(knowledgePointId, difficulty, questionType, count, expansionLevel) {
        // 根据题型决定调用哪个服务
        let apiType = currentApiType;
        if (questionType === 'essay') {
            // 解答题优先使用Python服务
            apiType = 'pythonService';
        }
        
        const url = apiType === 'pythonService' 
            ? `${API_CONFIG.pythonService.baseUrl}${API_CONFIG.pythonService.question.generate}`
            : buildApiUrl('question', 'generate');
            
        const options = {
            method: 'POST',
            body: JSON.stringify({ 
                knowledgePointId, 
                difficulty, 
                questionType, 
                count, 
                expansionLevel 
            })
        };
        
        return await sendRequest(url, options);
    },
    
    /**
     * 提交答案
     * @param {Array} questions - 题目及答案数组
     * @returns {Promise} Promise对象
     */
    async submitAnswers(questions) {
        // 检查是否包含解答题
        const hasEssayQuestions = questions.some(q => q.type === 'essay');
        
        // 根据是否有解答题决定调用哪个服务
        let apiType = currentApiType;
        if (hasEssayQuestions) {
            // 有解答题时使用Python服务进行评估
            apiType = 'pythonService';
        }
        
        const url = apiType === 'pythonService'
            ? `${API_CONFIG.pythonService.baseUrl}${API_CONFIG.pythonService.question.evaluate}`
            : buildApiUrl('question', 'submit');
            
        const options = {
            method: 'POST',
            body: JSON.stringify({ questions })
        };
        
        return await sendRequest(url, options);
    }
};

/**
 * 智能知识解答相关API
 */
const knowledgeApi = {
    /**
     * 发送问题
     * @param {string} content - 问题内容
     * @param {string} [image] - 可选的图片数据（base64编码）
     * @param {string} [sessionId] - 可选的会话ID
     * @returns {Promise} Promise对象
     */
    async askQuestion(content, image = null, sessionId = null) {
        // 知识解答优先使用Python服务
        const apiType = 'pythonService';
        
        const url = apiType === 'pythonService'
            ? `${API_CONFIG.pythonService.baseUrl}${API_CONFIG.pythonService.knowledge.ask}`
            : buildApiUrl('knowledge', 'ask');
            
        const options = {
            method: 'POST',
            body: JSON.stringify({ 
                content, 
                image,
                sessionId
            })
        };
        
        return await sendRequest(url, options);
    },
    
    /**
     * 获取历史对话列表
     * @returns {Promise} Promise对象
     */
    async getHistoryConversations() {
        const url = buildApiUrl('knowledge', 'history');
        return await sendRequest(url);
    },
    
    /**
     * 获取对话详情
     * @param {string} conversationId - 对话ID
     * @returns {Promise} Promise对象
     */
    async getConversationDetail(conversationId) {
        const url = buildApiUrl('knowledge', 'conversation', conversationId);
        return await sendRequest(url);
    }
};

// 导出API对象
const api = {
    setApiType,
    user: userApi,
    question: questionApi,
    knowledge: knowledgeApi
};

// 为了兼容不使用模块化的情况，将api挂载到window对象
if (typeof window !== 'undefined') {
    window.api = api;
}