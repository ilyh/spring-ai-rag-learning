# 📚 项目目录结构说明

## 🗂️ 双目录结构

本项目采用**前后端分离**的架构，分为两个独立的目录：

```
D:\githubproject\
├── spring-ai-rag-learning/              ← 后端项目（当前目录）
└── spring-ai-rag-learning-frontend/     ← 前端项目（独立目录）
```

---

## 📁 后端目录 (spring-ai-rag-learning)

### 核心文件
```
spring-ai-rag-learning/
├── 📄 pom.xml                    Maven 配置
├── 📄 docker-compose.yml         Milvus Docker 配置
├── 📄 .gitignore                 Git 忽略规则
├── 📄 start.ps1                  Windows 启动脚本
├── 📄 stop.ps1                   Windows 停止脚本
└── 📄 test-document.md           测试文档
```

### 源代码
```
src/main/java/com/learning/rag/
├── SpringAiRagLearningApplication.java    # 主启动类
├── config/
│   └── RagProperties.java                 # RAG 配置
├── controller/
│   ├── KnowledgeBaseController.java       # REST API 控制器
│   └── GlobalExceptionHandler.java        # 全局异常处理
├── dto/
│   ├── KnowledgeBaseDTO.java              # 知识库 DTO
│   ├── DocumentDTO.java                   # 文档 DTO
│   ├── ChatRequest.java                   # 聊天请求 DTO
│   └── ChatResponse.java                  # 聊天响应 DTO
├── entity/
│   ├── KnowledgeBase.java                 # 知识库实体
│   ├── Document.java                      # 文档实体
│   ├── Conversation.java                  # 对话实体
│   └── Message.java                       # 消息实体
├── exception/
│   ├── DocumentProcessingException.java   # 文档处理异常
│   ├── AIServiceException.java            # AI 服务异常
│   └── ResourceNotFoundException.java     # 资源未找到异常
├── repository/
│   ├── KnowledgeBaseRepository.java       # 知识库 Repository
│   ├── DocumentRepository.java            # 文档 Repository
│   ├── ConversationRepository.java        # 对话 Repository
│   └── MessageRepository.java             # 消息 Repository
└── service/
    ├── DocumentProcessingService.java     # 文档处理服务
    ├── RagService.java                    # RAG 问答服务
    └── KnowledgeBaseService.java          # 知识库管理服务
```

### 配置文件
```
src/main/resources/
└── application.yml                        # 应用配置
```

### 文档
```
├── README.md                              # 项目主文档
├── QUICKSTART.md                          # 快速启动指南
├── PROJECT_INFO.md                        # 项目说明
├── PROJECT_STRUCTURE.md                   # 项目结构图
├── PROJECT_SUMMARY.md                     # 项目总结
├── COMPLETION_REPORT.md                   # 完成报告
└── CHECKLIST.md                           # 检查清单
```

---

## 📁 前端目录 (spring-ai-rag-learning-frontend)

### 核心文件
```
spring-ai-rag-learning-frontend/
├── 📄 package.json                   npm 依赖配置
├── 📄 vite.config.js                 Vite 构建配置
└── 📄 index.html                     HTML 入口
```

### 源代码
```
src/
├── main.js                           # 应用入口
├── App.vue                           # 根组件
├── router/
│   └── index.js                      # Vue Router 配置
├── services/
│   └── api.js                        # Axios API 封装
├── stores/
│   └── knowledgeBase.js              # Pinia 状态管理
└── views/
    ├── ChatView.vue                  # 聊天界面
    └── KnowledgeBasesView.vue        # 知识库管理
```

---

## 🔄 工作流程

### 启动顺序

```
1. 启动 Milvus (Docker)
   ↓
2. 启动 Ollama (本地服务)
   ↓
3. 启动后端 (Spring Boot - 端口 8080)
   ↓
4. 启动前端 (Vue 3 - 端口 5173)
   ↓
5. 访问 http://localhost:5173
```

### API 通信

```
前端 (5173) --HTTP--> Vite Proxy --转发--> 后端 (8080)
     ↓                                        ↓
  /api/*                              /api/*
```

---

## 💡 开发提示

### 后端开发
```bash
cd D:\githubproject\spring-ai-rag-learning
mvn spring-boot:run
```
- 端口: 8080
- H2控制台: http://localhost:8080/h2-console
- API文档: 查看 KnowledgeBaseController.java

### 前端开发
```bash
cd D:\githubproject\spring-ai-rag-learning-frontend
npm run dev
```
- 端口: 5173
- 热重载: 自动
- API代理: 自动转发到 localhost:8080

---

## 🔧 配置修改

### 修改后端端口
编辑: `spring-ai-rag-learning/src/main/resources/application.yml`
```yaml
server:
  port: 8080  # 修改此处
```

### 修改前端端口
编辑: `spring-ai-rag-learning-frontend/vite.config.js`
```javascript
server: {
  port: 5173,  // 修改此处
  ...
}
```

### 修改 API 地址
如果后端不在 localhost:8080，需要同时修改：

1. **前端代理配置** (`vite.config.js`):
```javascript
proxy: {
  '/api': {
    target: 'http://YOUR_BACKEND_URL',
    changeOrigin: true
  }
}
```

2. **CORS 配置** (后端已配置允许所有来源)

---

## 📊 技术栈对照

| 层级 | 后端 | 前端 |
|-----|------|------|
| 框架 | Spring Boot 3.2.5 | Vue 3.4.21 |
| 语言 | Java 17 | JavaScript |
| 构建工具 | Maven | Vite |
| 数据库 | H2 + Milvus | - |
| AI 集成 | Spring AI + Ollama | - |
| 状态管理 | - | Pinia |
| 路由 | - | Vue Router |
| HTTP客户端 | - | Axios |

---

## 🎯 快速命令参考

### 一键启动（推荐）
```powershell
cd D:\githubproject\spring-ai-rag-learning
.\start.ps1
```

### 手动启动
```bash
# 终端1: Milvus
cd D:\githubproject\spring-ai-rag-learning
docker-compose up -d

# 终端2: 后端
cd D:\githubproject\spring-ai-rag-learning
mvn spring-boot:run

# 终端3: 前端
cd D:\githubproject\spring-ai-rag-learning-frontend
npm run dev
```

### 停止服务
```powershell
cd D:\githubproject\spring-ai-rag-learning
.\stop.ps1
```

---

## 📝 重要提醒

1. **前端和后端是独立的项目**
   - 需要分别安装依赖
   - 需要分别启动
   - 可以独立开发和部署

2. **确保正确的目录**
   - 后端命令在 `spring-ai-rag-learning/` 执行
   - 前端命令在 `spring-ai-rag-learning-frontend/` 执行

3. **API 代理已配置**
   - 前端请求 `/api/*` 会自动转发到后端
   - 无需在前端配置完整的后端 URL

4. **CORS 已处理**
   - 后端已配置允许跨域请求
   - 前端开发时无需担心跨域问题

---

## 🚀 下一步

1. ✅ 阅读 [README.md](README.md) 了解项目详情
2. ✅ 查看 [QUICKSTART.md](QUICKSTART.md) 快速启动
3. ✅ 参考 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) 理解架构
4. ✅ 使用 `test-document.md` 进行测试

---

**祝开发愉快！🎉**
