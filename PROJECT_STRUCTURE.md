# 项目结构图

## 📁 完整目录结构

```
spring-ai-rag-learning/
│
├── 📄 README.md                          # 项目主文档
├── 📄 QUICKSTART.md                      # 快速启动指南
├── 📄 PROJECT_SUMMARY.md                 # 项目总结
├── 📄 CHECKLIST.md                       # 检查清单
├── 📄 test-document.md                   # 测试文档
├── 📄 .gitignore                         # Git 忽略规则
├── 📄 pom.xml                            # Maven 配置
├── 📄 docker-compose.yml                 # Docker Compose 配置
│
├── 📂 src/
│   └── 📂 main/
│       ├── 📂 java/
│       │   └── 📂 com/
│       │       └── 📂 learning/
│       │           └── 📂 rag/
│       │               ├── 📄 SpringAiRagLearningApplication.java  # 主启动类
│       │               │
│       │               ├── 📂 config/
│       │               │   └── 📄 RagProperties.java               # RAG 配置属性
│       │               │
│       │               ├── 📂 controller/
│       │               │   ├── 📄 KnowledgeBaseController.java     # REST API 控制器
│       │               │   └── 📄 GlobalExceptionHandler.java      # 全局异常处理
│       │               │
│       │               ├── 📂 dto/
│       │               │   ├── 📄 KnowledgeBaseDTO.java            # 知识库 DTO
│       │               │   ├── 📄 DocumentDTO.java                 # 文档 DTO
│       │               │   ├── 📄 ChatRequest.java                 # 聊天请求 DTO
│       │               │   └── 📄 ChatResponse.java                # 聊天响应 DTO
│       │               │
│       │               ├── 📂 entity/
│       │               │   ├── 📄 KnowledgeBase.java               # 知识库实体
│       │               │   ├── 📄 Document.java                    # 文档实体
│       │               │   ├── 📄 Conversation.java                # 对话实体
│       │               │   └── 📄 Message.java                     # 消息实体
│       │               │
│       │               ├── 📂 exception/
│       │               │   ├── 📄 DocumentProcessingException.java # 文档处理异常
│       │               │   ├── 📄 AIServiceException.java          # AI 服务异常
│       │               │   └── 📄 ResourceNotFoundException.java   # 资源未找到异常
│       │               │
│       │               ├── 📂 repository/
│       │               │   ├── 📄 KnowledgeBaseRepository.java     # 知识库 Repository
│       │               │   ├── 📄 DocumentRepository.java          # 文档 Repository
│       │               │   ├── 📄 ConversationRepository.java      # 对话 Repository
│       │               │   └── 📄 MessageRepository.java           # 消息 Repository
│       │               │
│       │               └── 📂 service/
│       │                   ├── 📄 DocumentProcessingService.java   # 文档处理服务
│       │                   ├── 📄 RagService.java                  # RAG 问答服务
│       │                   └── 📄 KnowledgeBaseService.java        # 知识库管理服务
│       │
│       └── 📂 resources/
│           └── 📄 application.yml                          # 应用配置文件
│
└── 📂 frontend/
    ├── 📄 package.json                                     # npm 依赖配置
    ├── 📄 vite.config.js                                   # Vite 配置
    ├── 📄 index.html                                       # HTML 入口
    │
    └── 📂 src/
        ├── 📄 main.js                                      # 应用入口
        ├── 📄 App.vue                                      # 根组件
        │
        ├── 📂 router/
        │   └── 📄 index.js                                 # Vue Router 配置
        │
        ├── 📂 services/
        │   └── 📄 api.js                                   # Axios API 封装
        │
        ├── 📂 stores/
        │   └── 📄 knowledgeBase.js                         # Pinia Store
        │
        └── 📂 views/
            ├── 📄 ChatView.vue                             # 聊天界面
            └── 📄 KnowledgeBasesView.vue                   # 知识库管理
```

## 🏗️ 分层架构图

```
┌─────────────────────────────────────────────────┐
│              Frontend (Vue 3)                    │
│                                                  │
│  ┌──────────────┐    ┌──────────────────┐      │
│  │  ChatView    │    │ KnowledgeBases   │      │
│  │              │    │ View             │      │
│  └──────────────┘    └──────────────────┘      │
│           │                    │                 │
│           └────────┬───────────┘                 │
│                    │                              │
│           ┌────────▼────────┐                    │
│           │  API Services   │                    │
│           │   (Axios)       │                    │
│           └─────────────────┘                    │
└────────────────────────┬────────────────────────┘
                         │ HTTP/REST
┌────────────────────────▼────────────────────────┐
│         Backend (Spring Boot)                    │
│                                                  │
│  ┌──────────────────────────────────┐           │
│  │     Controller Layer             │           │
│  │  - KnowledgeBaseController       │           │
│  │  - GlobalExceptionHandler        │           │
│  └──────────────┬───────────────────┘           │
│                 │                                │
│  ┌──────────────▼───────────────────┐           │
│  │     Service Layer                │           │
│  │  - KnowledgeBaseService          │           │
│  │  - DocumentProcessingService     │           │
│  │  - RagService                    │           │
│  └──────────────┬───────────────────┘           │
│                 │                                │
│  ┌──────────────▼───────────────────┐           │
│  │     Repository Layer             │           │
│  │  - KnowledgeBaseRepository       │           │
│  │  - DocumentRepository            │           │
│  │  - ConversationRepository        │           │
│  │  - MessageRepository             │           │
│  └──────────────┬───────────────────┘           │
│                 │                                │
│  ┌──────────────▼───────────────────┐           │
│  │     Entity Layer                 │           │
│  │  - KnowledgeBase                 │           │
│  │  - Document                      │           │
│  │  - Conversation                  │           │
│  │  - Message                       │           │
│  └──────────────────────────────────┘           │
└────────┬──────────────────────┬─────────────────┘
         │                      │
         │                      │
┌────────▼────────┐   ┌────────▼────────┐
│   H2 Database   │   │  External APIs  │
│   (JPA/Hibernate)│  │                 │
│                 │   │  ┌───────────┐  │
│  - Knowledge    │   │  │  Ollama   │  │
│    Base Table   │   │  │  (LLM)    │  │
│  - Document     │   │  └───────────┘  │
│    Table        │   │                 │
│  - Conversation │   │  ┌───────────┐  │
│    Table        │   │  │  Milvus   │  │
│  - Message      │   │  │(Vector DB)│  │
│    Table        │   │  └───────────┘  │
└─────────────────┘   └─────────────────┘
```

## 🔄 RAG 数据流图

```
文档上传流程:
┌──────────┐
│ 用户上传  │
│  文档    │
└────┬─────┘
     │
     ▼
┌──────────────┐
│ 文件解析      │
│ (PDF/TXT/MD) │
└────┬─────────┘
     │
     ▼
┌──────────────┐
│ 文本分片      │
│ (Chunking)   │
└────┬─────────┘
     │
     ▼
┌──────────────┐
│ 向量化        │
│ (Embedding)  │
└────┬─────────┘
     │
     ▼
┌──────────────┐
│ 存储到        │
│ Milvus       │
└──────────────┘

问答流程:
┌──────────┐
│ 用户提问  │
└────┬─────┘
     │
     ▼
┌──────────────┐
│ 问题向量化    │
└────┬─────────┘
     │
     ▼
┌──────────────┐
│ 向量检索      │
│ (Top-K)      │
└────┬─────────┘
     │
     ▼
┌──────────────┐
│ 构建 Prompt   │
│ 上下文+问题   │
└────┬─────────┘
     │
     ▼
┌──────────────┐
│ LLM 生成回答  │
│ (Ollama)     │
└────┬─────────┘
     │
     ▼
┌──────────────┐
│ 返回结果      │
│ +引用来源     │
└──────────────┘
```

## 🔌 API 端点映射

```
/api/knowledge-bases
│
├── POST /                          → createKnowledgeBase()
├── GET /                           → getAllKnowledgeBases()
├── GET /{id}                       → getKnowledgeBase()
├── DELETE /{id}                    → deleteKnowledgeBase()
│
├── POST /{id}/documents            → uploadDocument()
├── GET /{id}/documents             → getDocuments()
├── DELETE /{kbId}/documents/{docId}→ deleteDocument()
│
├── POST /{id}/chat                 → chat()
│
├── GET /{id}/conversations         → getConversations()
└── DELETE /{kbId}/conversations/{convId} → deleteConversation()
```

## 🎨 前端路由结构

```
/
├── / (ChatView)
│   ├── 知识库选择器
│   ├── 消息列表
│   ├── 引用来源展示
│   └── 输入框
│
└── /knowledge-bases (KnowledgeBasesView)
    ├── 知识库列表
    ├── 创建知识库模态框
    ├── 文档管理区域
    │   ├── 文档上传
    │   └── 文档列表
    └── 对话历史（可选）
```

## 📊 数据库 ER 图

```
┌─────────────────┐
│ KnowledgeBase   │
├─────────────────┤
│ *id (UUID)      │──┐
│ name            │  │
│ description     │  │
│ collectionName  │  │
│ createdAt       │  │
│ updatedAt       │  │
└────────┬────────┘  │
         │           │
         │ 1:N       │
         │           │
    ┌────▼────────┐  │
    │ Document    │  │
    ├─────────────┤  │
    │ *id (UUID)  │  │
    │ fileName    │  │
    │ fileType    │  │
    │ fileSize    │  │
    │ filePath    │  │
    │ chunkCount  │  │
    │ uploadedAt  │  │
    │ status      │  │
    │ kb_id (FK)  │◄─┘
    └─────────────┘


┌─────────────────┐
│ KnowledgeBase   │──┐
└────────┬────────┘  │
         │           │
         │ 1:N       │
         │           │
    ┌────▼──────────┐│
    │ Conversation  ││
    ├───────────────┤│
    │ *id (UUID)    ││
    │ title         ││
    │ createdAt     ││
    │ updatedAt     ││
    │ kb_id (FK)    │◄┘
    └────┬──────────┘
         │
         │ 1:N
         │
    ┌────▼────────┐
    │ Message     │
    ├─────────────┤
    │ *id (UUID)  │
    │ type        │
    │ content     │
    │ sources     │
    │ createdAt   │
    │ conv_id (FK)│◄──┘
    └─────────────┘
```

## 🔧 技术栈层次

```
┌─────────────────────────────────────┐
│        Presentation Layer           │
│  Vue 3 + Vite + Pinia + Router     │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Application Layer            │
│     Spring Boot 3.2.5               │
│  Controllers + Services + DTOs      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Domain Layer                 │
│     Entities + Repositories         │
│     JPA/Hibernate                   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Infrastructure Layer            │
│                                     │
│  ┌──────────┐  ┌────────┐         │
│  │   H2 DB  │  │ Milvus │         │
│  └──────────┘  └────────┘         │
│                                     │
│  ┌────────────────────────┐       │
│  │      Ollama            │       │
│  │  - llama3.2 (Chat)     │       │
│  │  - nomic-embed-text    │       │
│  └────────────────────────┘       │
└─────────────────────────────────────┘
```

---

**提示**: 可以使用树状图更好地理解项目结构和各组件之间的关系。
