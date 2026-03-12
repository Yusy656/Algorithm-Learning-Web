from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import requests
import json
from typing import List, Optional, Dict
import logging

# 配置日志（方便调试）
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 初始化FastAPI应用
app = FastAPI(title="算法代码调试与优化API", version="1.0")

# ====================== 模型配置（适配 Gemini-3-Flash-Preview） ======================
LLM_API_URL = "https://api.viviai.cc/v1/chat/completions"  # 保持接口地址不变（兼容Gemini）
LLM_API_KEY = "sk-EFPWnbcJmp6WzyUSt6vcIrMDzFIChkmaJuZudUyRDCrQ0C1f"
LLM_HEADERS = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {LLM_API_KEY}"
}
# Gemini 模型名称
LLM_MODEL = "gemini-3-flash-preview"


# ====================== 数据模型定义 ======================
class CodeError(BaseModel):
    type: str  # 错误类型：语法错误/逻辑错误/性能问题
    location: str  # 错误位置：第X行/函数XXX
    desc: str  # 错误描述
    fix_suggestion: str  # 修复建议


class DebugResult(BaseModel):
    errors: List[CodeError]
    fixed_code: str  # 调试后的可运行代码


class OptimizeResult(BaseModel):
    original_complexity: str  # 原代码复杂度
    optimized_complexity: str  # 优化后复杂度
    optimize_idea: str  # 优化思路
    optimized_code: str  # 优化后的代码


class CodeRequest(BaseModel):
    code: str  # 待调试/优化的代码
    language: str  # 代码语言：python/java/c/cpp
    problem_desc: Optional[str] = ""  # 算法题目描述（可选）
    debug_only: Optional[bool] = False  # 是否仅调试（不优化）
    optimize_level: Optional[str] = "basic"  # 优化等级：basic/advanced


class CodeResponse(BaseModel):
    success: bool
    data: Optional[Dict] = None
    error_msg: Optional[str] = ""


# ====================== 核心功能函数 ======================
def build_gemini_prompt(request: CodeRequest) -> str:
    """构建适配Gemini的提示词（更简洁、指令更明确）"""
    prompt = f"""
    你是专业的算法教学AI助手，精通Python、Java、C、C++四种语言的算法代码调试与优化。
    请严格按照以下要求处理代码：
    【任务1：代码调试】
    - 检测{request.language}代码的所有错误：语法错误、逻辑错误（边界条件、递归、计算逻辑）、性能问题；
    - 为每个错误标注类型、位置、详细描述、修复建议；
    - 生成修复后的完整可运行代码（确保无语法/逻辑错误）。

    【任务2：代码优化】
    - 仅当debug_only={request.debug_only}为False时执行；
    - 分析原代码的时间/空间复杂度；
    - 按{request.optimize_level}等级优化（basic=修复低效逻辑，advanced=替换更优算法）；
    - 给出优化思路和优化后的完整可运行代码。

    【输出格式】
    必须返回纯JSON字符串，不要加代码块、解释性文字，确保JSON可直接解析：
    {{
        "debug_result": {{
            "errors": [{{"type": "", "location": "", "desc": "", "fix_suggestion": ""}}],
            "fixed_code": ""
        }},
        "optimize_result": {{
            "original_complexity": "",
            "optimized_complexity": "",
            "optimize_idea": "",
            "optimized_code": ""
        }}
    }}

    【输入信息】
    代码语言：{request.language}
    待处理代码：
    {request.code}
    题目描述：{request.problem_desc}
    仅调试：{request.debug_only}
    优化等级：{request.optimize_level}
    """
    return prompt.strip()


def call_gemini_api(prompt: str) -> Dict:
    """调用Gemini模型API（适配其接口格式）"""
    # Gemini 兼容的请求体格式
    llm_request_data = {
        "model": LLM_MODEL,
        "messages": [{"role": "user", "content": prompt}],
        "temperature": 0.1,  # 更低随机性，保证结果稳定
        "max_tokens": 3000,  # 增加token上限，容纳更长代码
        "stream": False  # 关闭流式输出，返回完整结果
    }

    try:
        logger.info(f"调用Gemini API，模型：{LLM_MODEL}")
        response = requests.post(
            url=LLM_API_URL,
            headers=LLM_HEADERS,
            data=json.dumps(llm_request_data),
            timeout=60  # 延长超时时间（Gemini处理代码可能稍慢）
        )
        response.raise_for_status()  # 抛出HTTP错误（4xx/5xx）
        llm_response = response.json()

        # 解析Gemini返回的内容
        content = llm_response["choices"][0]["message"]["content"].strip()
        # 清理Gemini可能返回的多余字符（如markdown标记）
        content = content.replace("```json", "").replace("```", "").replace("\n", "").strip()

        # 解析JSON
        result = json.loads(content)
        logger.info("Gemini API调用成功，返回结果正常")
        return result

    except requests.exceptions.Timeout:
        raise HTTPException(status_code=504, detail="调用Gemini模型超时，请稍后重试")
    except requests.exceptions.RequestException as e:
        raise HTTPException(status_code=500, detail=f"Gemini API请求失败：{str(e)}")
    except json.JSONDecodeError as e:
        raise HTTPException(status_code=500, detail=f"解析Gemini返回的JSON失败，原始内容：{content}，错误：{str(e)}")
    except KeyError as e:
        raise HTTPException(status_code=500, detail=f"Gemini返回格式异常，缺少字段：{str(e)}，原始响应：{llm_response}")


# ====================== 接口路由 ======================
@app.post("/api/algorithm-debug-optimize", response_model=CodeResponse)
async def debug_and_optimize_code(request: CodeRequest):
    """算法代码调试与优化核心接口（支持Python/Java/C/C++）"""
    # 1. 校验语言参数
    supported_languages = ["python", "java", "c", "cpp"]
    if request.language.lower() not in supported_languages:
        raise HTTPException(
            status_code=400,
            detail=f"不支持的编程语言，仅支持：{supported_languages}，当前传入：{request.language}"
        )

    # 2. 校验代码非空
    if not request.code.strip():
        raise HTTPException(status_code=400, detail="待处理的代码不能为空")

    try:
        # 3. 构建提示词并调用Gemini API
        prompt = build_gemini_prompt(request)
        llm_result = call_gemini_api(prompt)

        # 4. 处理仅调试场景（清空优化结果）
        if request.debug_only:
            llm_result["optimize_result"] = {
                "original_complexity": "",
                "optimized_complexity": "",
                "optimize_idea": "",
                "optimized_code": ""
            }

        # 5. 返回标准化结果
        return CodeResponse(
            success=True,
            data=llm_result,
            error_msg=""
        )

    except HTTPException as e:
        # 抛出已知的HTTP异常
        raise e
    except Exception as e:
        # 捕获未知异常
        logger.error(f"接口处理异常：{str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"服务器内部错误：{str(e)}")


# 健康检查接口
@app.get("/health")
async def health_check():
    """服务健康检查"""
    return {
        "status": "ok",
        "message": "服务运行正常",
        "model": LLM_MODEL,
        "supported_languages": ["python", "java", "c", "cpp"]
    }


# 根路径接口（提示文档地址）
@app.get("/")
async def root():
    return {
        "message": "算法代码调试与优化API服务已启动",
        "docs_url": "http://localhost:8000/docs",  # Swagger文档地址
        "redoc_url": "http://localhost:8000/redoc"
    }