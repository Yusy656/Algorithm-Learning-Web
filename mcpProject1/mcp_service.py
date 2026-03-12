from mcp import Server, stdio_server
from mcp.server import ServerOptions
import asyncio
import subprocess
import ast
from typing import Optional

# 创建MCP服务器
server = Server("algorithm-code-evaluator")

def analyze_time_complexity(code: str) -> str:
    """简单的时间复杂度静态分析（基于循环嵌套层数）"""
    try:
        tree = ast.parse(code)
        max_depth = 0

        class LoopVisitor(ast.NodeVisitor):
            def __init__(self):
                self.max_depth = 0
                self.current_depth = 0

            def visit_For(self, node):
                self.current_depth += 1
                self.max_depth = max(self.max_depth, self.current_depth)
                self.generic_visit(node)
                self.current_depth -= 1

            def visit_While(self, node):
                self.current_depth += 1
                self.max_depth = max(self.max_depth, self.current_depth)
                self.generic_visit(node)
                self.current_depth -= 1

        visitor = LoopVisitor()
        visitor.visit(tree)

        if visitor.max_depth == 0:
            return "O(1)"
        elif visitor.max_depth == 1:
            return "O(n)"
        elif visitor.max_depth == 2:
            return "O(n²)"
        else:
            return f"O(n^{visitor.max_depth})"
    except:
        return "无法分析（代码解析失败）"

def run_python_code(code: str, timeout: int = 5) -> tuple[bool, str]:
    """运行Python代码，返回是否成功和输出/报错"""
    try:
        result = subprocess.run(
            ["python", "-c", code],
            capture_output=True,
            text=True,
            timeout=timeout
        )
        if result.returncode == 0:
            return True, result.stdout
        else:
            return False, result.stderr
    except subprocess.TimeoutExpired:
        return False, "代码运行超时（超过5秒）"
    except Exception as e:
        return False, str(e)

@server.list_tools()
async def list_tools() -> list:
    return [
        {
            "name": "evaluate_algorithm_code",
            "description": "运行算法代码，返回运行结果、时间复杂度分析或报错信息",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "code": {
                        "type": "string",
                        "description": "要运行的Python算法代码"
                    },
                    "language": {
                        "type": "string",
                        "description": "编程语言（目前仅支持Python）",
                        "default": "python"
                    }
                },
                "required": ["code"]
            }
        }
    ]

@server.call_tool()
async def call_tool(name: str, arguments: dict) -> str:
    if name == "evaluate_algorithm_code":
        code = arguments["code"]
        language = arguments.get("language", "python")

        if language != "python":
            return "❌ 目前仅支持Python语言"

        # 运行代码
        success, output = run_python_code(code)

        if success:
            # 分析时间复杂度
            time_complexity = analyze_time_complexity(code)
            return (
                f"✅ 代码运行成功！\n"
                f"📝 运行输出：\n{output}\n"
                f"⏱️ 时间复杂度分析：{time_complexity}"
            )
        else:
            return f"❌ 代码运行失败！\n🐛 报错信息：\n{output}"

    return "❌ 未知工具"

async def main():
    await stdio_server(server, ServerOptions())

if __name__ == "__main__":
    asyncio.run(main())