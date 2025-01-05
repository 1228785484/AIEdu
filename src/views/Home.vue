<template>
  <div class="home-container">
    <div class="welcome-section">
      <h1>欢迎进入首页</h1>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage'
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
}

.welcome-section {
  text-align: center;
}

.welcome-section h1 {
  color: #2c3e50;
  font-size: 2.5rem;
  margin: 0;
}
</style>