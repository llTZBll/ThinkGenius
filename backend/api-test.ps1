# ThinkGenius2 API 测试脚本
# 使用方法：在PowerShell中运行 .\api-test.ps1

$baseUrl = "http://localhost:8081"
$userId = "test-user-123"

Write-Host "=== ThinkGenius2 API 测试 ===" -ForegroundColor Green
Write-Host "基础URL: $baseUrl" -ForegroundColor Yellow
Write-Host "测试用户ID: $userId" -ForegroundColor Yellow
Write-Host ""

# 1. 测试基础连接
Write-Host "1. 测试基础连接..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/test/ping" -Method GET
    Write-Host "✅ 基础连接成功: $response" -ForegroundColor Green
} catch {
    Write-Host "❌ 基础连接失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. 测试块管理API
Write-Host "2. 测试块管理API..." -ForegroundColor Cyan

# 2.1 创建块（自动位置）
Write-Host "  2.1 创建块（自动位置）..." -ForegroundColor Yellow
$blockData = @{
    type = "question"
    content = "这是一个测试问题吗？"
    size = @{
        width = 300
        height = 150
    }
} | ConvertTo-Json

try {
    $uri = "$baseUrl/api/blocks/auto-position?userId=$userId"
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $blockData -ContentType "application/json"
    $blockId = $response.id
    Write-Host "   ✅ 块创建成功，ID: $blockId" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 块创建失败: $($_.Exception.Message)" -ForegroundColor Red
    $blockId = "test-block-123"
}

# 2.2 获取所有块
Write-Host "  2.2 获取所有块..." -ForegroundColor Yellow
try {
    $uri = "$baseUrl/api/blocks?userId=$userId"
    $response = Invoke-RestMethod -Uri $uri -Method GET
    Write-Host "   ✅ 获取块成功，数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 获取块失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2.3 获取画布配置
Write-Host "  2.3 获取画布配置..." -ForegroundColor Yellow
try {
    $uri = "$baseUrl/api/blocks/canvas-config"
    $response = Invoke-RestMethod -Uri $uri -Method GET
    Write-Host "   ✅ 画布配置获取成功" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 画布配置获取失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 3. 测试关系管理API
Write-Host "3. 测试关系管理API..." -ForegroundColor Cyan

# 3.1 创建关系
Write-Host "  3.1 创建关系..." -ForegroundColor Yellow
$relationData = @{
    sourceBlockId = $blockId
    targetBlockId = "test-block-456"
    relationType = "related"
} | ConvertTo-Json

try {
    $uri = "$baseUrl/api/relations?userId=$userId"
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $relationData -ContentType "application/json"
    $relationId = $response.id
    Write-Host "   ✅ 关系创建成功，ID: $relationId" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 关系创建失败: $($_.Exception.Message)" -ForegroundColor Red
    $relationId = "test-relation-123"
}

# 3.2 获取所有关系
Write-Host "  3.2 获取所有关系..." -ForegroundColor Yellow
try {
    $uri = "$baseUrl/api/relations?userId=$userId"
    $response = Invoke-RestMethod -Uri $uri -Method GET
    Write-Host "   ✅ 获取关系成功，数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 获取关系失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3.3 获取关系统计
Write-Host "  3.3 获取关系统计..." -ForegroundColor Yellow
try {
    $uri = "$baseUrl/api/relations/stats?userId=$userId"
    $response = Invoke-RestMethod -Uri $uri -Method GET
    Write-Host "   ✅ 关系统计获取成功，总关系数: $($response.totalRelations)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 关系统计获取失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 4. 测试AI服务API
Write-Host "4. 测试AI服务API..." -ForegroundColor Cyan

# 4.1 生成关键词
Write-Host "  4.1 生成关键词..." -ForegroundColor Yellow
$aiData = @{
    content = "人工智能是计算机科学的一个分支，它企图了解智能的实质，并生产出一种新的能以人类智能相似的方式做出反应的智能机器。"
} | ConvertTo-Json

try {
    $uri = "$baseUrl/api/ai/keywords"
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $aiData -ContentType "application/json"
    Write-Host "   ✅ 关键词生成成功: $($response.keywords)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 关键词生成失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4.2 生成问题
Write-Host "  4.2 生成问题..." -ForegroundColor Yellow
try {
    $uri = "$baseUrl/api/ai/questions"
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $aiData -ContentType "application/json"
    Write-Host "   ✅ 问题生成成功: $($response.questions)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 问题生成失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4.3 生成摘要
Write-Host "  4.3 生成摘要..." -ForegroundColor Yellow
try {
    $uri = "$baseUrl/api/ai/summary"
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $aiData -ContentType "application/json"
    Write-Host "   ✅ 摘要生成成功: $($response.summary)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 摘要生成失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 5. 测试位置算法API
Write-Host "5. 测试位置算法API..." -ForegroundColor Cyan

# 5.1 检查位置是否可用
Write-Host "  5.1 检查位置是否可用..." -ForegroundColor Yellow
$positionData = @{
    x = 100
    y = 100
    width = 200
    height = 100
} | ConvertTo-Json

try {
    $uri = "$baseUrl/api/blocks/check-position?userId=$userId"
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $positionData -ContentType "application/json"
    Write-Host "   ✅ 位置检查成功，可用: $($response.available)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 位置检查失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 5.2 找到最近可用位置
Write-Host "  5.2 找到最近可用位置..." -ForegroundColor Yellow
try {
    $uri = "$baseUrl/api/blocks/find-nearest-position?userId=$userId"
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $positionData -ContentType "application/json"
    Write-Host "   ✅ 最近位置查找成功: ($($response.x), $($response.y))" -ForegroundColor Green
} catch {
    Write-Host "   ❌ 最近位置查找失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== API测试完成 ===" -ForegroundColor Green
Write-Host "注意：某些AI服务可能需要配置有效的API密钥才能正常工作" -ForegroundColor Yellow 