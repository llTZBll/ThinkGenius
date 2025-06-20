@echo off
echo === ThinkGenius2 API 测试 (curl) ===
echo.

set BASE_URL=http://localhost:8081
set USER_ID=test-user-123

echo 1. 测试基础连接...
curl -X GET "%BASE_URL%/api/test/ping"
echo.
echo.

echo 2. 测试块管理API...
echo 2.1 创建块（自动位置）
curl -X POST "%BASE_URL%/api/blocks/auto-position?userId=%USER_ID%" ^
  -H "Content-Type: application/json" ^
  -d "{\"type\":\"question\",\"content\":\"这是一个测试问题吗？\",\"size\":{\"width\":300,\"height\":150}}"
echo.
echo.

echo 2.2 获取所有块
curl -X GET "%BASE_URL%/api/blocks?userId=%USER_ID%"
echo.
echo.

echo 2.3 获取画布配置
curl -X GET "%BASE_URL%/api/blocks/canvas-config"
echo.
echo.

echo 3. 测试关系管理API...
echo 3.1 创建关系
curl -X POST "%BASE_URL%/api/relations?userId=%USER_ID%" ^
  -H "Content-Type: application/json" ^
  -d "{\"sourceBlockId\":\"test-block-123\",\"targetBlockId\":\"test-block-456\",\"relationType\":\"related\"}"
echo.
echo.

echo 3.2 获取所有关系
curl -X GET "%BASE_URL%/api/relations?userId=%USER_ID%"
echo.
echo.

echo 3.3 获取关系统计
curl -X GET "%BASE_URL%/api/relations/stats?userId=%USER_ID%"
echo.
echo.

echo 4. 测试AI服务API...
echo 4.1 生成关键词
curl -X POST "%BASE_URL%/api/ai/keywords" ^
  -H "Content-Type: application/json" ^
  -d "{\"content\":\"人工智能是计算机科学的一个分支\"}"
echo.
echo.

echo 4.2 生成问题
curl -X POST "%BASE_URL%/api/ai/questions" ^
  -H "Content-Type: application/json" ^
  -d "{\"content\":\"人工智能是计算机科学的一个分支\"}"
echo.
echo.

echo 5. 测试位置算法API...
echo 5.1 检查位置是否可用
curl -X POST "%BASE_URL%/api/blocks/check-position?userId=%USER_ID%" ^
  -H "Content-Type: application/json" ^
  -d "{\"x\":100,\"y\":100,\"width\":200,\"height\":100}"
echo.
echo.

echo 5.2 找到最近可用位置
curl -X POST "%BASE_URL%/api/blocks/find-nearest-position?userId=%USER_ID%" ^
  -H "Content-Type: application/json" ^
  -d "{\"x\":100,\"y\":100,\"width\":200,\"height\":100}"
echo.
echo.

echo === API测试完成 ===
pause 