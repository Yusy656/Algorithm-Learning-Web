from fastapi import FastAPI, Request
from fastapi.responses import StreamingResponse
import asyncio
import subprocess
import json

app = FastAPI()

@app.get("/")
async def root():
    return {"status": "ok", "message": "算法代码评测MCP服务运行中", "sse_endpoint": "/sse"}

@app.get("/sse")
async def sse_endpoint(request: Request):
    async def event_generator():
        # 启动MCP服务进程
        process = await asyncio.create_subprocess_exec(
            "python", "mcp_service.py",
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )

        try:
            # 发送MCP初始化消息
            init_message = json.dumps({
                "jsonrpc": "2.0",
                "id": 1,
                "method": "initialize",
                "params": {
                    "protocolVersion": "2024-11-05",
                    "capabilities": {},
                    "clientInfo": {
                        "name": "chaoxing-mcp-client",
                        "version": "1.0.0"
                    }
                }
            }) + "\n"
            process.stdin.write(init_message.encode())
            await process.stdin.drain()

            # 持续转发MCP服务输出到SSE
            while True:
                if await request.is_disconnected():
                    break
                line = await process.stdout.readline()
                if not line:
                    break
                yield f"data: {line.decode()}\n\n"

                # 发送initialized通知（简化处理）
                try:
                    msg = json.loads(line.decode())
                    if msg.get("id") == 1:
                        initialized_msg = json.dumps({
                            "jsonrpc": "2.0",
                            "method": "notifications/initialized"
                        }) + "\n"
                        process.stdin.write(initialized_msg.encode())
                        await process.stdin.drain()
                except:
                    pass

        finally:
            process.terminate()
            await process.wait()

    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no"
        }
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)