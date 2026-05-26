# Spring AI RAG Learning - Windows 启动脚本
# 使用方法: .\start.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Spring AI RAG Learning - 启动脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Docker
Write-Host "[1/5] 检查 Docker..." -ForegroundColor Yellow
$dockerRunning = docker ps 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Docker 未运行，请先启动 Docker Desktop" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Docker 正在运行" -ForegroundColor Green
Write-Host ""

# 启动 Milvus
Write-Host "[2/5] 启动 Milvus..." -ForegroundColor Yellow
if (Get-Process | Where-Object {$_.Name -like "*docker*"}) {
    docker-compose up -d 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Milvus 已启动" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Milvus 可能已经在运行" -ForegroundColor Yellow
    }
}
Write-Host "💡 等待 Milvus 完全启动（约30秒）..." -ForegroundColor Cyan
Start-Sleep -Seconds 5
Write-Host ""

# 检查 Ollama
Write-Host "[3/5] 检查 Ollama..." -ForegroundColor Yellow
try {
    $ollamaVersion = ollama --version 2>$null
    Write-Host "✅ Ollama 已安装: $ollamaVersion" -ForegroundColor Green
    
    # 检查模型
    Write-Host "检查模型..." -ForegroundColor Cyan
    $models = ollama list 2>$null
    if ($models -match "llama3.2") {
        Write-Host "✅ llama3.2 模型已安装" -ForegroundColor Green
    } else {
        Write-Host "⚠️  llama3.2 模型未安装，正在下载..." -ForegroundColor Yellow
        ollama pull llama3.2
    }
    
    if ($models -match "nomic-embed-text") {
        Write-Host "✅ nomic-embed-text 模型已安装" -ForegroundColor Green
    } else {
        Write-Host "⚠️  nomic-embed-text 模型未安装，正在下载..." -ForegroundColor Yellow
        ollama pull nomic-embed-text
    }
} catch {
    Write-Host "❌ Ollama 未安装或未运行" -ForegroundColor Red
    Write-Host "请从 https://ollama.com/download 下载并安装" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# 启动后端
Write-Host "[4/5] 启动后端服务..." -ForegroundColor Yellow
Write-Host "💡 后端将在 http://localhost:8080 启动" -ForegroundColor Cyan
Write-Host "按 Ctrl+C 停止后端" -ForegroundColor Gray
Write-Host ""

# 在新窗口启动后端
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot'; mvn spring-boot:run"
Write-Host "⏳ 等待后端启动..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

# 启动前端
Write-Host ""
Write-Host "[5/5] 启动前端服务..." -ForegroundColor Yellow
Write-Host "💡 前端将在 http://localhost:5173 启动" -ForegroundColor Cyan
Write-Host ""

$frontendPath = Join-Path $PSScriptRoot.Parent.FullName "spring-ai-rag-learning-frontend"
if (Test-Path "$frontendPath\node_modules") {
    Write-Host "✅ node_modules 存在" -ForegroundColor Green
} else {
    Write-Host "📦 首次运行，安装依赖..." -ForegroundColor Yellow
    Set-Location $frontendPath
    npm install
    Set-Location $PSScriptRoot
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  启动完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "📱 前端: http://localhost:5173" -ForegroundColor Cyan
Write-Host "🔧 后端: http://localhost:8080" -ForegroundColor Cyan
Write-Host "💾 H2 Console: http://localhost:8080/h2-console" -ForegroundColor Cyan
Write-Host ""
Write-Host "按任意键打开浏览器..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# 打开浏览器
Start-Process "http://localhost:5173"

Write-Host ""
Write-Host "提示: 后端在单独的 PowerShell 窗口中运行" -ForegroundColor Gray
Write-Host "要停止所有服务:" -ForegroundColor Gray
Write-Host "  1. 关闭后端 PowerShell 窗口" -ForegroundColor Gray
Write-Host "  2. 在前端窗口按 Ctrl+C" -ForegroundColor Gray
Write-Host "  3. 运行: docker-compose down" -ForegroundColor Gray
