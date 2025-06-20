# Block位置算法文档

## 概述

Block位置算法是一个智能的块布局系统，能够自动计算和优化块在画布上的位置，避免重叠，并根据块之间的关系进行合理的布局。

## 核心功能

### 1. 自动位置计算
- 为新创建的块自动计算最佳位置
- 根据块类型选择不同的布局策略
- 避免与现有块的重叠

### 2. 布局策略
- **Center Focus**: question类型块使用中心聚焦布局
- **Circular**: keyword类型块围绕相关question块进行圆形布局
- **Hierarchical**: text类型块在底部进行层次布局

### 3. 重叠检测
- 实时检测块之间的重叠
- 支持边界检查（画布范围）
- 提供最近可用位置查找

### 4. 关系布局
- 根据块之间的关系类型进行布局
- 支持parent、child、related、similar关系
- 自动调整相关块的位置

## 画布配置

```json
{
  "width": 2000.0,
  "height": 1500.0,
  "minBlockDistance": 50.0,
  "gridSize": 20.0,
  "defaultSizes": {
    "question": {"width": 300.0, "height": 150.0},
    "keyword": {"width": 200.0, "height": 100.0},
    "text": {"width": 400.0, "height": 200.0}
  }
}
```

## API接口

### 1. 创建块并自动计算位置
```http
POST /api/blocks/auto-position?userId={userId}
Content-Type: application/json

{
  "type": "question",
  "content": "What is the main topic?",
  "size": {
    "width": 300.0,
    "height": 150.0
  }
}
```

### 2. 重新布局所有块
```http
POST /api/blocks/relayout?userId={userId}
```

### 3. 检查位置是否可用
```http
POST /api/blocks/check-position?userId={userId}
Content-Type: application/json

{
  "x": 100.0,
  "y": 100.0,
  "width": 200.0,
  "height": 100.0,
  "excludeBlockId": "optional-block-id"
}
```

### 4. 找到最近的可用位置
```http
POST /api/blocks/find-nearest-position?userId={userId}
Content-Type: application/json

{
  "x": 100.0,
  "y": 100.0,
  "width": 200.0,
  "height": 100.0,
  "excludeBlockId": "optional-block-id"
}
```

### 5. 获取画布配置
```http
GET /api/blocks/canvas-config
```

## 算法实现

### 位置计算算法

1. **中心聚焦布局 (Center Focus)**
   - 将question块放在画布中心
   - 多个question块水平排列
   - 相关块围绕question块布局

2. **圆形布局 (Circular)**
   - keyword块围绕相关question块进行圆形排列
   - 自动计算角度和半径
   - 最多支持8个keyword围绕一个question

3. **层次布局 (Hierarchical)**
   - text块在画布底部水平排列
   - 按创建顺序从左到右排列
   - 保持适当的间距

### 重叠检测算法

```java
private boolean blocksOverlap(Block.Position pos1, Block.Size size1, 
                            Block.Position pos2, Block.Size size2) {
    return pos1.getX() < pos2.getX() + size2.getWidth() &&
           pos1.getX() + size1.getWidth() > pos2.getX() &&
           pos1.getY() < pos2.getY() + size2.getHeight() &&
           pos1.getY() + size1.getHeight() > pos2.getY();
}
```

### 螺旋搜索算法

当首选位置不可用时，使用螺旋搜索算法找到最近的可用位置：

```java
// 螺旋搜索参数
double angle = 0;
double radius = MIN_BLOCK_DISTANCE;
double angleStep = Math.PI / 8; // 45度步长
double radiusStep = MIN_BLOCK_DISTANCE;

while (radius < Math.max(CANVAS_WIDTH, CANVAS_HEIGHT)) {
    double x = preferredPosition.getX() + radius * Math.cos(angle);
    double y = preferredPosition.getY() + radius * Math.sin(angle);
    
    Block.Position testPosition = new Block.Position(x, y);
    if (isPositionAvailable(testPosition, size, existingBlocks, excludeBlockId)) {
        return testPosition;
    }
    
    angle += angleStep;
    if (angle >= 2 * Math.PI) {
        angle = 0;
        radius += radiusStep;
    }
}
```

## 关系布局规则

### 关系类型
- **parent**: 父块在上方
- **child**: 子块在下方
- **related**: 相关块在右侧
- **similar**: 相似块在左侧

### 位置偏移计算
```java
switch (relationType) {
    case "parent":
        offsetY = -(sourceSize.getHeight() + targetSize.getHeight()) / 2 - MIN_BLOCK_DISTANCE;
        break;
    case "child":
        offsetY = (sourceSize.getHeight() + targetSize.getHeight()) / 2 + MIN_BLOCK_DISTANCE;
        break;
    case "related":
        offsetX = (sourceSize.getWidth() + targetSize.getWidth()) / 2 + MIN_BLOCK_DISTANCE;
        break;
    case "similar":
        offsetX = -(sourceSize.getWidth() + targetSize.getWidth()) / 2 - MIN_BLOCK_DISTANCE;
        break;
}
```

## 使用示例

### 1. 创建第一个块
```java
BlockRequest request = new BlockRequest();
request.setType("question");
request.setContent("What is the main topic?");
request.setSize(new Block.Size(300.0, 150.0));

Block block = blockService.createBlockWithAutoPosition(request, "user1");
// 块将被放置在画布中心 (1000, 750)
```

### 2. 创建相关keyword块
```java
BlockRequest keywordRequest = new BlockRequest();
keywordRequest.setType("keyword");
keywordRequest.setContent("Important concept");
keywordRequest.setSize(new Block.Size(200.0, 100.0));

Block keywordBlock = blockService.createBlockWithAutoPosition(keywordRequest, "user1");
// keyword块将围绕question块进行圆形布局
```

### 3. 重新布局所有块
```java
List<Block> relayoutedBlocks = blockService.relayoutUserBlocks("user1");
// 所有块将根据类型和关系重新排列
```

## 测试

运行测试类来验证算法功能：

```bash
mvn spring-boot:run -Dspring-boot.run.main-class=com.photo.BlockPositionAlgorithmTest
```

测试将验证：
1. 新块位置计算
2. 重叠检测
3. 最近位置查找
4. 重新布局功能
5. 关系位置计算

## 性能优化

1. **空间索引**: 使用网格索引加速重叠检测
2. **缓存**: 缓存计算结果避免重复计算
3. **批量操作**: 支持批量重新布局
4. **异步处理**: 大量块重新布局时使用异步处理

## 扩展性

算法设计具有良好的扩展性：

1. **新的布局策略**: 可以轻松添加新的布局算法
2. **自定义关系类型**: 支持自定义块关系类型
3. **动态配置**: 画布配置可以通过配置文件调整
4. **插件化**: 支持插件化的位置计算器 