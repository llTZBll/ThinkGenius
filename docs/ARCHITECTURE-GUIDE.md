# ThinkGenius2 架构图使用指南

## 概述

ThinkGenius2 项目提供了两种格式的系统架构图，方便不同场景下的使用和编辑。

## 架构图文件

### 1. SVG格式架构图
- **文件**: `docs/system-architecture.svg`
- **特点**: 
  - 矢量图形，可无损缩放
  - 适合嵌入网页和文档
  - 支持现代浏览器的交互功能
  - 文件大小较小

### 2. Draw.io格式架构图
- **文件**: `docs/system-architecture.drawio`
- **特点**:
  - 可在Draw.io中直接编辑
  - 支持团队协作
  - 丰富的图形库和模板
  - 可导出多种格式

## 使用方法

### 查看SVG架构图

1. **浏览器查看**:
   - 直接在浏览器中打开 `system-architecture.svg`
   - 支持缩放、打印等功能

2. **嵌入文档**:
   ```markdown
   ![系统架构图](./docs/system-architecture.svg)
   ```

3. **下载使用**:
   - 右键保存图片
   - 在演示文稿中使用

### 编辑Draw.io架构图

1. **在线编辑**:
   - 访问 [draw.io](https://app.diagrams.net/)
   - 选择 "Open Existing Diagram"
   - 上传 `system-architecture.drawio` 文件

2. **桌面应用**:
   - 下载 [Draw.io Desktop](https://github.com/jgraph/drawio-desktop)
   - 打开 `system-architecture.drawio` 文件

3. **VS Code插件**:
   - 安装 "Draw.io Integration" 插件
   - 在VS Code中直接编辑

## 架构图内容说明

### 三层架构设计

#### 展示层 (Presentation Layer)
- **Web前端**: Vue 3 + Vite + Tailwind CSS
- **移动端App**: React Native / Flutter
- **功能**: 用户界面、交互逻辑、数据展示

#### 业务层 (Business Layer)
- **API服务器集群**: 3个节点，负载均衡
- **核心服务**:
  - AI服务 (百度千帆API集成)
  - 块管理服务 (位置算法)
  - 用户认证服务 (JWT)
  - 关系管理服务 (连接线算法)

#### 数据层 (Data Layer)
- **MongoDB数据库**: 文档型数据库
- **集合**: users, blocks, block_relations, keywords

### 数据流向

1. **用户请求**: 前端 → API服务器 → 业务服务 → 数据库
2. **AI服务**: 用户输入 → AI服务 → 百度千帆API → 结果处理
3. **数据同步**: 前端操作 → API调用 → 数据库更新 → 实时同步

## 自定义和扩展

### 修改颜色主题

在Draw.io中，可以轻松修改颜色主题：

1. 选择要修改的组件
2. 在右侧面板中修改填充颜色
3. 使用渐变效果增强视觉效果

### 添加新组件

1. 从左侧图形库拖拽新组件
2. 设置样式和属性
3. 添加连接线表示关系

### 导出其他格式

Draw.io支持导出多种格式：
- PNG (位图)
- PDF (文档)
- HTML (网页)
- XML (可编辑)

## 最佳实践

### 1. 版本控制
- 将架构图文件纳入Git版本控制
- 在重要变更时提交新版本
- 使用有意义的提交信息

### 2. 团队协作
- 使用Draw.io的协作功能
- 建立架构图更新流程
- 定期审查和更新

### 3. 文档同步
- 保持架构图与代码的一致性
- 更新相关文档
- 在README中引用最新版本

### 4. 性能考虑
- SVG格式适合网页嵌入
- 大图可考虑使用缩略图
- 提供多种分辨率版本

## 故障排除

### 常见问题

1. **SVG显示异常**:
   - 检查浏览器兼容性
   - 确认文件编码为UTF-8
   - 验证SVG语法

2. **Draw.io无法打开**:
   - 检查文件格式是否正确
   - 尝试在线版本
   - 更新Draw.io版本

3. **连接线显示问题**:
   - 检查组件ID是否正确
   - 确认连接点设置
   - 调整连接线样式

### 技术支持

- 查看 [Draw.io官方文档](https://desk.draw.io/)
- 参考 [SVG规范](https://www.w3.org/TR/SVG/)
- 提交Issue到项目仓库

## 更新日志

### v1.0.0 (2024-01-01)
- 创建基础三层架构图
- 支持SVG和Draw.io格式
- 添加详细的技术栈标注
- 包含数据流向和连接关系

---

*本指南将随着项目发展持续更新，确保架构图的准确性和实用性。* 