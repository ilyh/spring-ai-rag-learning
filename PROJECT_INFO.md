# 项目说明

## 📁 项目结构

本项目分为两个独立的目录：

### 1. spring-ai-rag-learning (后端)
Spring Boot 后端应用，包含：
- REST API 服务
- RAG 核心逻辑
- 数据库管理
- 文档处理

### 2. spring-ai-rag-learning-frontend (前端)
Vue 3 前端应用，包含：
- 用户界面
- API 调用
- 状态管理
- 路由配置

## 🚀 快速启动

### 前置条件
1. 安装 Java 17+
2. 安装 Node.js 18+
3. 安装 Docker Desktop
4. 安装 Ollama 并拉取模型

### 启动步骤

#### 第一步：启动 Milvus
```bash
cd spring-ai-rag-learning
docker-compose up -d
```

#### 第二步：确保 Ollama 运行
```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

#### 第三步：启动后端
```bash
cd spring-ai-rag-learning
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动

#### 第四步：启动前端
```bash
cd spring-ai-rag-learning-frontend
npm install
npm run dev
```

前端将在 http://localhost:5173 启动

#### 第五步：访问应用
打开浏览器访问：http://localhost:5173

## 📖 详细文档

请查看以下文档获取更多信息：

- **README.md** - 完整项目文档
- **QUICKSTART.md** - 快速启动指南
- **PROJECT_STRUCTURE.md** - 项目结构说明
- **COMPLETION_REPORT.md** - 项目完成报告

## 🔧 开发说明

### 后端开发
- 工作目录：`spring-ai-rag-learning`
- 启动命令：`mvn spring-boot:run`
- 端口：8080
- H2控制台：http://localhost:8080/h2-console

### 前端开发
- 工作目录：`spring-ai-rag-learning-frontend`
- 启动命令：`npm run dev`
- 端口：5173
- API代理：自动代理到 http://localhost:8080

## 📝 API 配置

前端的 API 请求会自动代理到后端：
- 前端请求：`/api/*`
- 实际转发：`http://localhost:8080/api/*`

如果需要修改，请编辑：
- 前端：`spring-ai-rag-learning-frontend/vite.config.js`
- 后端：`spring-ai-rag-learning/src/main/resources/application.yml`

## 🛑 停止服务

```bash
# 停止前端
# 在前端终端按 Ctrl+C

# 停止后端
# 在后端终端按 Ctrl+C

# 停止 Milvus
cd spring-ai-rag-learning
docker-compose down
```

---

**Happy Coding! 🎉**
