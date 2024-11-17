<template>
  <div class="personal-info">
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <div class="info-item">
        <label>用户名：</label>
        <span>{{ username }}</span>
      </div>
      
      <div class="info-item">
        <label>邮箱：</label>
        <span>{{ email }}</span>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'PersonalInfo',
  data() {
    return {
      username: localStorage.getItem('username') || '未登录',
      email: localStorage.getItem('email') || '未设置'
    }
  }
}
</script>

<style scoped>
.personal-info {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.info-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item {
  margin: 20px 0;
  display: flex;
  align-items: center;
}

.info-item label {
  width: 80px;
  color: #606266;
}

.info-item span {
  color: #303133;
  flex: 1;
}
</style>
