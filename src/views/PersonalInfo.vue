<template>
  <div class="personal-info">
    <h2>个人信息</h2>
    <div class="info-item">
      <span class="label">用户名:</span>
      <span class="value">{{ username }}</span>
    </div>
    <div class="info-item">
      <span class="label">邮箱:</span>
      <span class="value">{{ email }}</span>
    </div>
    <div class="info-item">
      <span class="label">手机号:</span>
      <span class="value">{{ phoneNumber }}</span>
    </div>
    <!-- 你可以在这里添加更多个人信息 -->
    <div v-if="address" class="info-item">
      <span class="label">地址:</span>
      <span class="value">{{ address }}</span>
    </div>
  </div>
</template>

<script setup>
// 定义用户信息
import { ref } from 'vue';

const username = ref('周钜豪');
const email = ref('zhangsan@example.com');
const phoneNumber = ref('138-0000-0000');
const address = ref('翻斗花园6号楼');
</script>

<style scoped>
.personal-info {
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  max-width: 400px;
  margin: 0 auto;
  background-color: #f9f9f9;
}

h2 {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #ddd;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: bold;
  color: #333;
}

.value {
  color: #666;
}
</style>
