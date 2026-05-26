# Spring AI RAG Learning Project

一个基于 Spring AI 的 RAG（检索增强生成）学习项目，实现了一个通用的知识库聊天机器人框架。

## 📋 项目概述

该项目旨在帮助开发者理解 RAG 的核心概念和实现流程，同时提供一个可扩展的基础架构，支持后续添加不同领域的知识库。

### 核心功能

- ✅ **文档管理**：支持上传多种格式文档（PDF、TXT、Markdown），自动解析、分片和向量化
- ✅ **知识库管理**：通过集合（Collection）机制实现多知识库隔离，可创建、切换和删除知识库
- ✅ **智能问答**：基于 RAG 技术的对话接口，结合向量检索和大语言模型生成回答
- ✅ **对话历史**：保存用户对话记录，支持查看历史对话

## 🛠️ 技术栈

### 后端
- **Spring Boot 3.2.5** - Web 框架
- **Spring AI 1.0.0-M6** - AI 集成框架
- **Ollama** - 本地 LLM 服务（llama3.2 + nomic-embed-text）
- **Milvus 2.4.0** - 向量数据库
- **H2 Database** - 嵌入式数据库（开发环境）
- **JPA/Hibernate** - ORM 框架
- **Apache PDFBox** - PDF 文档解析

### 前端
- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 现代前端构建工具
- **Pinia** - Vue 状态管理
- **Vue Router** - 路由管理
- **Axios** - HTTP 客户端

## 📦 前置要求

1. **Java 17+**
   ```bash
   java -version
   ```

2. **Node.js 18+**
   ```bash
   node -v
   npm -v
   ```

3. **Docker & Docker Compose**（用于运行 Milvus）
   ```bash
   docker --version
   docker-compose --version
   ```

4. **Ollama**
   - 从 [https://ollama.com/download](https://ollama.com/download) 下载并安装
   - 拉取所需模型：
     ```bash
     ollama pull llama3.2
     ollama pull nomic-embed-text
     ```

## 🚀 快速开始

### 1. 启动 Milvus 向量数据库

```bash
docker-compose up -d
```

验证 Milvus 是否正常运行：
```bash
docker ps | grep milvus
```

### 2. 启动 Ollama 服务

确保 Ollama 正在运行：
```bash
# Windows/Mac: Ollama 应该作为后台服务运行
# Linux: 
ollama serve
```

验证模型已安装：
```bash
ollama list
```

### 3. 启动后端

```bash
cd spring-ai-rag-learning
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动

### 4. 启动前端

```bash
cd ../spring-ai-rag-learning-frontend
npm install
npm run dev
```

前端将在 http://localhost:5173 启动

## 📖 使用指南

### 创建知识库

1. 访问 http://localhost:5173/knowledge-bases
2. 点击"创建知识库"按钮
3. 输入知识库名称和描述
4. 点击"创建"

### 上传文档

1. 在知识库列表中选择一个知识库
2. 点击"查看文档"
3. 点击"上传文档"按钮
4. 选择 PDF、TXT 或 Markdown 文件
5. 等待文档处理完成

### 开始聊天

1. 访问 http://localhost:5173/
2. 从下拉菜单选择一个知识库
3. 在输入框中输入问题
4. 按 Enter 或点击"发送"
5. 查看 AI 回答和引用来源

## 🏗️ 项目结构

本项目采用前后端分离的架构：

```
spring-ai-rag-learning/              # 后端项目目录
├── src/main/java/com/learning/rag/
│   ├── config/              # 配置类
│   ├── controller/          # REST API 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # JPA 实体类
│   ├── exception/           # 自定义异常
│   ├── repository/          # 数据访问层
│   └── service/             # 业务逻辑层
├── src/main/resources/
│   └── application.yml      # 应用配置
├── docker-compose.yml       # Milvus Docker 配置
└── pom.xml                  # Maven 配置

spring-ai-rag-learning-frontend/     # 前端项目目录
├── src/
│   ├── services/        # API 服务
│   ├── stores/          # Pinia 状态管理
│   ├── views/           # 页面组件
│   └── router/          # 路由配置
└── package.json         # npm 依赖
```

## 🔌 API 文档

### 知识库管理

```
POST   /api/knowledge-bases              创建知识库
GET    /api/knowledge-bases              获取知识库列表
GET    /api/knowledge-bases/{id}         获取知识库详情
DELETE /api/knowledge-bases/{id}         删除知识库
```

### 文档管理

```
POST   /api/knowledge-bases/{id}/documents           上传文档
GET    /api/knowledge-bases/{id}/documents           获取文档列表
DELETE /api/knowledge-bases/{kbId}/documents/{docId} 删除文档
```

### 对话 API

```
POST   /api/knowledge-bases/{id}/chat                发送问题
GET    /api/knowledge-bases/{id}/conversations       获取对话历史
DELETE /api/knowledge-bases/{kbId}/conversations/{convId} 删除对话
```

## ⚙️ 配置说明

主要配置项在 `src/main/resources/application.yml`：

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: llama3.2
      embedding:
        model: nomic-embed-text
    
    milvus:
      connection:
        host: localhost
        port: 19530

app:
  rag:
    chunk-size: 800          # 文本分片大小
    chunk-overlap: 100       # 分片重叠大小
    top-k: 5                 # 检索的最相关文档数量
    similarity-threshold: 0.7 # 相似度阈值
```

## 🔍 核心概念

### RAG 工作流程

1. **文档处理流程**：
   - 用户上传文档 → 解析文本 → 分片 → 向量化 → 存储到 Milvus

2. **问答流程**：
   - 用户提问 → 问题向量化 → 检索相关文档 → 构建 Prompt → LLM 生成回答

### 关键技术点

- **向量嵌入**：使用 nomic-embed-text 模型将文本转换为 768 维向量
- **相似度搜索**：使用余弦相似度在 Milvus 中检索最相关的文档片段
- **文本分片**：将长文档切分为 800 字符的片段，重叠 100 字符以保持上下文
- **Prompt 工程**：将检索到的文档作为上下文，引导 LLM 生成准确回答

## 🧪 测试

### 手动测试场景

1. 上传不同类型的文档（PDF、TXT、MD）
2. 创建多个知识库并切换
3. 提问并验证回答质量
4. 查看引用来源的准确性
5. 测试边界情况（空知识库、无关问题等）

## 🐛 常见问题

### 1. Ollama 连接失败

**问题**：`Connection refused: localhost:11434`

**解决**：
```bash
# 确保 Ollama 正在运行
ollama serve

# 验证模型已安装
ollama list
```

### 2. Milvus 连接失败

**问题**：无法连接到 Milvus

**解决**：
```bash
# 检查 Docker 容器状态
docker ps | grep milvus

# 重启 Milvus
docker-compose restart

# 查看日志
docker logs milvus-standalone
```

### 3. 文档处理失败

**问题**：上传文档后状态为 FAILED

**解决**：
- 检查文件格式是否支持（PDF、TXT、MD）
- 查看后端日志了解具体错误
- 确保文件大小不超过 10MB

## 📝 扩展方向

### 短期扩展
- [ ] 支持更多文档格式（Word、Excel）
- [ ] 添加文档预览功能
- [ ] 实现流式响应（SSE）
- [ ] 添加评分反馈机制

### 长期扩展
- [ ] 支持多模态（图片、音频）
- [ ] 实现混合检索（关键词 + 向量）
- [ ] 添加权限管理（多用户）
- [ ] 集成更多 AI 模型提供商
- [ ] 实现增量索引

## 📄 许可证

本项目仅用于学习和教育目的。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题或建议，请创建 Issue。

---

**Happy Learning! 🎉**
