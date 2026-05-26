# Spring AI RAG Learning - 停止脚本
# 使用方法: .\stop.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Spring AI RAG Learning - 停止脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 停止 Milvus
Write-Host "[1/2] 停止 Milvus..." -ForegroundColor Yellow
docker-compose down 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Milvus 已停止" -ForegroundColor Green
} else {
    Write-Host "⚠️  Milvus 可能未运行" -ForegroundColor Yellow
}
Write-Host ""

# 提示手动停止的服务
Write-Host "[2/2] 请手动停止以下服务:" -ForegroundColor Yellow
Write-Host ""
Write-Host "❌ 后端服务:" -ForegroundColor Red
Write-Host "   关闭运行 'mvn spring-boot:run' 的 PowerShell 窗口" -ForegroundColor Gray
Write-Host ""
Write-Host "❌ 前端服务:" -ForegroundColor Red
Write-Host "   在运行 'npm run dev' 的窗口按 Ctrl+C" -ForegroundColor Gray
Write-Host ""

Write-Host "========================================" -ForegroundColor Green
Write-Host "  清理完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "💡 下次启动请使用: .\start.ps1" -ForegroundColor Cyan
