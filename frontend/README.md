# 前端项目说明

## 项目结构
```
frontend/
├── src/                # 源代码目录
│   ├── components/     # 组件目录
│   ├── assets/        # 静态资源
│   ├── styles/        # 样式文件
│   └── utils/         # 工具函数
├── public/            # 公共资源
└── package.json       # 项目依赖配置
```

## 技术栈
- Vue.js 3.x
- Element Plus
- Axios
- Vite

## 开发环境设置
1. 安装依赖：
```bash
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

3. 构建生产版本：
```bash
npm run build
```

## 主要功能
1. 图片上传
   - 支持拖拽上传
   - 支持多文件上传
   - 支持图片预览

2. 图片管理
   - 图片列表展示
   - 图片删除
   - 图片预览

3. 用户界面
   - 响应式设计
   - 现代化UI
   - 友好的用户交互

## API 集成
前端通过 Axios 与后端 API 进行通信，所有 API 接口配置在 `src/utils/api.js` 中。

## 注意事项
1. 开发时请确保后端服务已启动
2. 图片上传大小限制为 10MB
3. 支持的图片格式：JPG、PNG、GIF 