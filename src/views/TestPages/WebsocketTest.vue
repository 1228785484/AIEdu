<template>
  <div class="websocket-test">
    <h2>WebSocket 测试页面</h2>
    
    <div class="connection-form">
      <div class="form-group">
        <label>用户ID：</label>
        <input v-model="userId" type="number" placeholder="请输入用户ID">
      </div>
      <div class="form-group">
        <label>课程ID：</label>
        <input v-model="courseId" type="number" placeholder="请输入课程ID">
      </div>
      <button @click="connectWebSocket" :disabled="isConnected">连接</button>
      <button @click="disconnectWebSocket" :disabled="!isConnected">断开</button>
    </div>

    <div class="status-panel">
      <div class="status-item">
        <span class="label">连接状态：</span>
        <span :class="['status', isConnected ? 'connected' : 'disconnected']">
          {{ isConnected ? '已连接' : '未连接' }}
        </span>
      </div>
      <div class="status-item">
        <span class="label">学习时长：</span>
        <span class="time">{{ formatTime(studyTime) }}</span>
      </div>
      <div class="status-item">
        <span class="label">最后心跳：</span>
        <span>{{ lastHeartbeat || '无' }}</span>
      </div>
    </div>

    <div class="log-panel">
      <h3>消息日志</h3>
      <div class="log-content">
        <div v-for="(log, index) in logs" :key="index" :class="['log-item', log.type]">
          {{ log.time }} - {{ log.message }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue';

const userId = ref('');
const courseId = ref('');
const isConnected = ref(false);
const studyTime = ref(0);
const lastHeartbeat = ref('');
const logs = ref([]);
let ws = null;
let heartbeatTimer = null;

// 连接WebSocket
const connectWebSocket = () => {
  if (!userId.value || !courseId.value) {
    addLog('error', '请输入用户ID和课程ID');
    return;
  }

  const wsUrl = `ws://localhost:8008/study-time?userId=${userId.value}&courseId=${courseId.value}`;
  ws = new WebSocket(wsUrl);

  ws.onopen = () => {
    isConnected.value = true;
    addLog('success', 'WebSocket连接已建立');
    startHeartbeat();
  };

  ws.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data);
      if (data.type === 'heartbeat_ack') {
        lastHeartbeat.value = new Date().toLocaleTimeString();
        // 计算当前学习时长（从连接建立到现在的秒数）
        const currentTime = Math.floor(Date.now() / 1000);
        const startTime = Math.floor(ws.startTime / 1000);
        studyTime.value = currentTime - startTime;
        addLog('info', `收到心跳响应，当前学习时长：${formatTime(studyTime.value)}`);
      } else if (data.type === 'connected') {
        addLog('success', '连接成功：' + data.message);
        // 记录连接开始时间
        ws.startTime = Date.now();
      }
    } catch (error) {
      addLog('error', '解析消息失败：' + error.message);
    }
  };

  ws.onclose = () => {
    isConnected.value = false;
    addLog('warning', 'WebSocket连接已关闭');
    stopHeartbeat();
  };

  ws.onerror = (error) => {
    addLog('error', '连接错误：' + error.message);
    isConnected.value = false;
  };
};

// 断开WebSocket连接
const disconnectWebSocket = () => {
  if (ws) {
    ws.close();
    stopHeartbeat();
  }
};

// 开始发送心跳
const startHeartbeat = () => {
  // 立即发送第一次心跳
  sendHeartbeat();
  // 设置定时发送
  heartbeatTimer = setInterval(sendHeartbeat, 30000); // 每30秒发送一次
};

// 发送心跳包
const sendHeartbeat = () => {
  if (ws && ws.readyState === WebSocket.OPEN) {
    const heartbeat = {
      type: 'heartbeat',
      timestamp: new Date().toISOString()
    };
    ws.send(JSON.stringify(heartbeat));
    addLog('info', '发送心跳包');
  }
};

// 停止发送心跳
const stopHeartbeat = () => {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer);
    heartbeatTimer = null;
  }
};

// 格式化时间（秒转换为时分秒）
const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const remainingSeconds = seconds % 60;
  return `${hours}小时${minutes}分${remainingSeconds}秒`;
};

// 添加日志
const addLog = (type, message) => {
  logs.value.unshift({
    type,
    time: new Date().toLocaleTimeString(),
    message
  });
  // 最多保留50条日志
  if (logs.value.length > 50) {
    logs.value.pop();
  }
};

// 组件卸载时清理
onUnmounted(() => {
  disconnectWebSocket();
});
</script>

<style scoped>
.websocket-test {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.connection-form {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: inline-block;
  width: 80px;
}

.form-group input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 200px;
}

button {
  padding: 8px 20px;
  margin-right: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background: #4CAF50;
  color: white;
}

button:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

.status-panel {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid #eee;
}

.status-item {
  margin-bottom: 10px;
}

.status {
  font-weight: bold;
}

.connected {
  color: #4CAF50;
}

.disconnected {
  color: #f44336;
}

.log-panel {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #eee;
}

.log-content {
  height: 300px;
  overflow-y: auto;
  background: #f8f8f8;
  padding: 10px;
  border-radius: 4px;
}

.log-item {
  padding: 5px;
  margin-bottom: 5px;
  border-radius: 4px;
}

.log-item.success {
  color: #4CAF50;
}

.log-item.error {
  color: #f44336;
}

.log-item.warning {
  color: #ff9800;
}

.log-item.info {
  color: #2196F3;
}
</style>
