# 简化的API测试脚本
Write-Host "=== ThinkGenius2 API 测试 ===" -ForegroundColor Green

# 1. 测试基础连接
Write-Host "1. 测试基础连接..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/test/ping" -Method GET
    Write-Host "✅ 基础连接成功: $response" -ForegroundColor Green
} catch {
    Write-Host "❌ 基础连接失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. 测试获取画布配置
Write-Host "2. 测试获取画布配置..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/blocks/canvas-config" -Method GET
    Write-Host "✅ 画布配置获取成功" -ForegroundColor Green
    Write-Host "   画布宽度: $($response.width)" -ForegroundColor Yellow
    Write-Host "   画布高度: $($response.height)" -ForegroundColor Yellow
} catch {
    Write-Host "❌ 画布配置获取失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 3. 测试创建块
Write-Host "3. 测试创建块..." -ForegroundColor Cyan
$blockData = @{
    type = "question"
    content = "这是一个测试问题吗？"
    size = @{
        width = 300
        height = 150
    }
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/blocks/auto-position?userId=test-user" -Method POST -Body $blockData -ContentType "application/json"
    Write-Host "✅ 块创建成功，ID: $($response.id)" -ForegroundColor Green
    Write-Host "   位置: ($($response.position.x), $($response.position.y))" -ForegroundColor Yellow
} catch {
    Write-Host "❌ 块创建失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 4. 测试获取所有块
Write-Host "4. 测试获取所有块..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/blocks?userId=test-user" -Method GET
    Write-Host "✅ 获取块成功，数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "❌ 获取块失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 5. 测试AI服务
Write-Host "5. 测试AI服务..." -ForegroundColor Cyan
$aiData = @{
    content = "人工智能是计算机科学的一个分支"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/ai/keywords" -Method POST -Body $aiData -ContentType "application/json"
    Write-Host "✅ 关键词生成成功: $($response.keywords)" -ForegroundColor Green
} catch {
    Write-Host "❌ 关键词生成失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== 测试完成 ===" -ForegroundColor Green 