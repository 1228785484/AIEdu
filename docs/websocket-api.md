# WebSocket 接口文档

## 学习时长追踪接口

该WebSocket接口用于实时追踪用户的课程学习时长。通过建立WebSocket连接，客户端可以与服务器保持实时通信，定期发送心跳包来记录用户的学习时间。

### 连接信息

- **WebSocket URL**: `ws://localhost:8080/api/study-time`
- **请求参数**:
  - `userId`: 用户ID（必填）
  - `courseId`: 课程ID（必填）

示例URL:
```
ws://localhost:8080/api/study-time?userId=123&courseId=456
```

### 消息格式

#### 1. 客户端发送消息（心跳包）

```json
{
    "type": "heartbeat",
    "timestamp": "2023-12-25T10:00:00"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| type | String | 消息类型，固定为 "heartbeat" |
| timestamp | String | 当前时间戳，ISO 8601格式 |

#### 2. 服务端响应消息

```json
{
    "type": "heartbeat_response",
    "status": "success",
    "studyTime": 300
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| type | String | 消息类型，固定为 "heartbeat_response" |
| status | String | 响应状态：success/error |
| studyTime | Number | 当前累计学习时长（秒） |

### 使用流程

1. **建立连接**
   - 客户端通过WebSocket URL建立连接
   - 必须提供userId和courseId参数
   - 连接成功后服务器会记录开始时间

2. **保持连接**
   - 客户端需要每30秒发送一次心跳包
   - 服务器会返回当前累计的学习时长
   - 如果60秒内未收到心跳包，服务器会断开连接

3. **关闭连接**
   - 客户端主动关闭连接
   - 或服务器因超时自动断开连接
   - 连接关闭时会自动保存学习记录

### 错误处理

1. **连接错误**
   - 缺少必要参数（userId或courseId）
   - 参数格式错误
   - 用户认证失败

2. **心跳超时**
   - 超过60秒未收到心跳包
   - 服务器会主动断开连接
   - 已记录的学习时长会被保存

### 注意事项

1. **心跳频率**
   - 建议每30秒发送一次心跳包
   - 不要频繁发送（小于10秒）
   - 不要过久不发送（大于50秒）

2. **数据保存**
   - 学习时长按秒计算
   - 连接断开时自动保存
   - 重新连接会继续累加时长

3. **并发限制**
   - 同一用户同一课程只允许一个连接
   - 新连接会导致旧连接断开

### 示例代码

#### JavaScript 客户端示例

```javascript
// 建立连接
const ws = new WebSocket(`ws://localhost:8080/api/study-time?userId=123&courseId=456`);

// 连接建立后的处理
ws.onopen = () => {
    console.log('WebSocket连接已建立');
    // 开始定时发送心跳
    startHeartbeat();
};

// 接收消息的处理
ws.onmessage = (event) => {
    const response = JSON.parse(event.data);
    console.log(`当前学习时长: ${response.studyTime}秒`);
};

// 连接关闭的处理
ws.onclose = () => {
    console.log('WebSocket连接已关闭');
    // 清理心跳定时器
    stopHeartbeat();
};

// 发送心跳包
function sendHeartbeat() {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({
            type: 'heartbeat',
            timestamp: new Date().toISOString()
        }));
    }
}

// 启动心跳定时器
let heartbeatTimer;
function startHeartbeat() {
    heartbeatTimer = setInterval(sendHeartbeat, 30000); // 每30秒发送一次
}

// 停止心跳定时器
function stopHeartbeat() {
    if (heartbeatTimer) {
        clearInterval(heartbeatTimer);
    }
}
```

### 调试建议

1. 使用WebSocket调试工具（如Postman或wscat）测试连接
2. 观察服务器日志了解连接状态
3. 确保心跳包按时发送
4. 测试异常情况（如网络断开）的处理
