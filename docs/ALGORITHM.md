# 算法文档

本文档详细说明了 ThinkGenius 画布中使用的核心算法，包括块的动态布局、避让机制和连接线渲染。所有这些算法都在前端 `BlockCanvas.vue` 组件中实现。

## 1. 动态块布局

### 1.1 块高度自适应

为了避免不必要的滚动条并确保内容完全可见，文本块（`text`）的高度会根据其内容动态计算。

**计算逻辑**:
1. 创建一个临时的、不可见的 `div` 元素。
2. 将其宽度设置为与实际内容区域的宽度相同（块的总宽度减去水平内边距）。
3. 将其实际的 CSS 类（如 `text-block-content`）应用到这个临时 `div` 上，以确保字体、行高等样式一致。
4. 将 Markdown 渲染后的 HTML 内容注入其中。
5. 将其附加到 DOM 中，测量其 `offsetHeight`。
6. 从 DOM 中移除该临时 `div`。
7. 最终高度 = 内容高度 + 标题栏高度 + 垂直内边距。

### 1.2 初始块定位

- **问题块 (`question`)**: 始终放置在画布的中心。
- **主关键词块 (`keyword`)**: 放置在问题块的右侧。
- **新文本块 (`text`)**: 当用户点击一个关键词时，新的文本块会生成在源关键词块的右侧。
- **派生块 (总结、关键词)**:
  - **总结块**: 生成在源文本块的右侧。
  - **关键词块**: 生成在源文本块的下方。

## 2. 块避让算法 (斥力模型)

为了防止块重叠并实现自然的动态布局，我们采用了一个基于物理的斥力模型。该算法在 `requestAnimationFrame` 循环中持续运行。

**实现步骤**:
1. **遍历所有块**: 在每一帧中，遍历画布上的所有块（问题、关键词、文本块等）。
2. **检测重叠**: 对每一对块进行矩形重叠检测。
3. **施加斥力**:
   - 如果两个块发生重叠，计算它们中心点之间的向量 (dx, dy)。
   - 施加一个与中心距离成反比的斥力。距离越近，力越大。
   - `force = repel_factor * (sum_of_sizes) / distance`
   - 将这个力分解到 x 和 y 方向，并累加到每个块的速度（`velocities`）上。
4. **应用速度和阻尼**:
   - 将计算出的速度应用到每个块的位置上 (`block.x += velocity.x`)。
   - 为了防止块无限加速，每次更新后都对速度应用一个阻尼系数 (`velocity.x *= damping_factor`)，使其随时间衰减。
5. **边界限制**: 确保所有块都保持在画布的可视区域内。
6. **排除拖动块**: 被用户拖动的块不受此算法影响，以确保其响应用户操作。

## 3. 连接线渲染算法

连接线用于可视化块之间的关系，我们采用动态锚点和贝塞尔曲线来实现清晰、美观的连接。

### 3.1 动态锚点定位

每个块的四条边中心外侧都定义了四个不可见的**锚点**（上、下、左、右）。

### 3.2 最近锚点匹配

当需要连接两个块时，算法会执行以下操作：
1. 获取源块和目标块的所有锚点（共 4x4=16 种组合）。
2. 计算每一对锚点之间的欧几里得距离。
3. 选择距离最短的那一对锚点作为连接线的起点和终点。

这个策略确保了连接线总是走最短、最直接的路径，避免了不必要的交叉和混乱。

### 3.3 智能贝塞尔曲线

连接线本身是一条三次贝塞尔曲线 (`<path>`)，其控制点的计算方式如下：

1. **获取起点 (from) 和终点 (to)**: 即上一步中找到的最近锚点对。
2. **计算向量**: 计算从起点到终点的向量 (dx, dy)。
3. **智能控制点**: 根据锚点的位置（是垂直方向还是水平方向）来智能地调整控制点，使曲线的弧度更自然。
   - 如果锚点在块的左/右侧，则第一个控制点在水平方向上偏移：`cx1 = from.x + dx * curveFactor`。
   - 如果锚点在块的上/下侧，则第一个控制点在垂直方向上偏移：`cy1 = from.y + dy * curveFactor`。
   - 对第二个控制点应用相似的逻辑。

这种方法使得连接线能够优雅地绕过块的边缘，而不是生硬地从中心点穿出。

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