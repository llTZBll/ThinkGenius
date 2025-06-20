# 后端项目说明

## 项目结构
```
backend/
├── src/
│   └── main/
│       ├── java/        # Java 源代码
│       └── resources/   # 配置文件
├── pom.xml             # Maven 配置
└── mvnw.cmd           # Maven Wrapper
```

## 技术栈
- Spring Boot 2.x
- Spring Data JPA
- MySQL
- Maven

## 开发环境要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+

## 开发环境设置
1. 配置数据库
   - 创建数据库
   - 修改 `application.yml` 中的数据库配置

2. 安装依赖：
```bash
mvn install
```

3. 启动应用：
```bash
mvn spring-boot:run
```

## 主要功能
1. 图片上传
   - 支持文件上传
   - 文件类型验证
   - 文件大小限制

2. 图片管理
   - 图片存储
   - 图片列表查询
   - 图片删除

3. 数据持久化
   - 使用 JPA 进行数据管理
   - 图片元数据存储

## API 文档
详细的 API 文档请参考 `API.md`

## 注意事项
1. 确保数据库服务已启动
2. 检查配置文件中的数据库连接信息
3. 确保上传目录具有写入权限
4. 生产环境部署时注意修改相关配置 