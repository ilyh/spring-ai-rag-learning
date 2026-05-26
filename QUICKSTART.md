# Spring AI RAG Learning - 快速启动指南

## 🚀 一键启动（推荐顺序）

### 第一步：启动 Milvus
```bash
docker-compose up -d
```

等待 30-60 秒让 Milvus 完全启动。

### 第二步：确保 Ollama 运行并拉取模型
```bash
# 拉取聊天模型
ollama pull llama3.2

# 拉取嵌入模型
ollama pull nomic-embed-text

# 验证模型
ollama list
```

### 第三步：启动后端
```bash
mvn clean spring-boot:run
```

等待看到 "Started SpringAiRagLearningApplication" 消息。

### 第四步：启动前端
```bash
cd ../spring-ai-rag-learning-frontend
npm install
npm run dev
```

### 第五步：访问应用
打开浏览器访问：http://localhost:5173

## ✅ 验证安装

### 检查服务状态
```bash
# 检查 Milvus
docker ps | grep milvus

# 检查 Ollama
ollama list

# 检查后端（应该返回 404 或 API 响应）
curl http://localhost:8080/api/knowledge-bases

# 检查前端
# 在浏览器中访问 http://localhost:5173
```

## 🎯 快速测试

1. **创建知识库**
   - 访问 http://localhost:5173/knowledge-bases
   - 点击"创建知识库"
   - 输入名称："测试知识库"
   - 点击创建

2. **上传文档**
   - 点击"查看文档"
   - 上传一个 PDF/TXT/MD 文件
   - 等待处理完成

3. **开始聊天**
   - 访问 http://localhost:5173/
   - 选择刚创建的知识库
   - 提问关于文档内容的问题

## 🔧 故障排除

### 端口被占用
如果 8080 或 5173 端口被占用，修改对应配置：
- 后端：`src/main/resources/application.yml` 修改 `server.port`
- 前端：`frontend/vite.config.js` 修改 `server.port`

### Maven 依赖下载失败
```bash
# 清理并重新下载
mvn clean install -U
```

### npm install 失败
```bash
# 清除缓存
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

## 📊 H2 数据库控制台

访问：http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:file:./data/ragdb`
- 用户名: `sa`
- 密码: (留空)

## 🛑 停止服务

```bash
# 停止前端
# 在前端终端按 Ctrl+C

# 停止后端
# 在后端终端按 Ctrl+C

# 停止 Milvus
docker-compose down
```

## 💡 提示

- 首次启动时，Maven 和 npm 需要下载依赖，可能需要几分钟
- Ollama 模型首次拉取可能需要较长时间（取决于网络）
- 建议使用 SSD 以获得更好的性能
- 确保至少有 8GB 可用内存
