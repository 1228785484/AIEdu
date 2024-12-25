<template>
  <div class="login-container">
    <!-- 背景动画元素 -->
    <div class="background-animation">
      <div class="grid"></div>
      <div class="circles">
        <div v-for="i in 10" :key="i" class="circle"></div>
      </div>
    </div>

    <div class="login-content">
      <div class="login-left">
        <div class="brand-info">
          <div class="logo-container">
            <img src="@/assets/2.png" alt="Logo" class="logo">
            <div class="logo-glow"></div>
          </div>
          <h1>AI Education Platform</h1>
          <p class="slogan">探索 AI 教育的无限可能</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Monitor /></el-icon>
              <span>智能学习助手</span>
            </div>
            <div class="feature-item">
              <el-icon><Connection /></el-icon>
              <span>实时互动教学</span>
            </div>
            <div class="feature-item">
              <el-icon><DataAnalysis /></el-icon>
              <span>个性化学习分析</span>
            </div>
          </div>
        </div>
      </div>

      <div class="login-card">
        <div class="card-header">
          <h2>欢迎回来</h2>
          <p class="subtitle">登录以开启您的智慧学习之旅</p>
        </div>
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label>用户名</label>
            <div class="input-wrapper">
              <el-input 
                v-model="loginForm.username" 
                placeholder="请输入用户名"
                :prefix-icon="User"
                :disabled="isLoading">
              </el-input>
            </div>
          </div>
          <div class="form-group">
            <label>密码</label>
            <div class="input-wrapper">
              <el-input 
                v-model="loginForm.password" 
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                :disabled="isLoading">
                <template #suffix>
                  <el-icon 
                    class="password-toggle"
                    @click="showPassword = !showPassword">
                    <View v-if="showPassword"/>
                    <Hide v-else/>
                  </el-icon>
                </template>
              </el-input>
            </div>
          </div>
          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <a href="#" class="forgot-link">忘记密码？</a>
          </div>
          <el-button 
            type="primary" 
            native-type="submit"
            :loading="isLoading"
            class="submit-btn">
            <span class="btn-content">
              {{ isLoading ? '登录中...' : '登录' }}
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </span>
          </el-button>
          <div class="register-link">
            还没有账号？ <router-link to="/register">立即注册</router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, View, Hide, Monitor, Connection, DataAnalysis, ArrowRight } from '@element-plus/icons-vue'

function parseJwt(token) {
  if (!token) return null;
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join(''));
  return JSON.parse(jsonPayload);
}

export default {
  name: 'LoginPage',
  setup() {
    const showPassword = ref(false)
    return {
      showPassword,
      User,
      Lock,
      View,
      Hide,
      Monitor,
      Connection,
      DataAnalysis,
      ArrowRight
    }
  },
  data() {
    return {
      isLoading: false,
      rememberMe: false,
      loginForm: {
        username: '',
        password: ''
      }
    }
  },
  methods: {
    async handleLogin() {
      if (this.isLoading) return;
      
      this.isLoading = true;
      try {
        const response = await fetch('http://localhost:8008/api/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.loginForm),
        });

        const data = await response.json();
        if (response.ok) {
          localStorage.setItem('token', data.token);
          localStorage.setItem('username', data.username);
          localStorage.setItem('email', data.email);
          localStorage.setItem('userid', data.userid);
          
          if (this.rememberMe) {
            localStorage.setItem('rememberedUser', this.loginForm.username);
          } else {
            localStorage.removeItem('rememberedUser');
          }
          
          // 解析 JWT 获取角色信息
          const decodedToken = parseJwt(data.token);
          if (decodedToken && decodedToken.roles && decodedToken.roles.length > 0) {
            localStorage.setItem('roleName', decodedToken.roles[0]);
          }
          
          // 强制刷新页面以确保导航栏正确加载
          ElMessage.success('登录成功');
          await this.$router.push('/home');
          window.location.reload();
        } else {
          ElMessage.error(data.message || '登录失败');
        }
      } catch (error) {
        ElMessage.error('登录失败：' + error.message);
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: 
    linear-gradient(120deg, rgba(240,242,245,0.8) 0%, rgba(230,233,240,0.8) 100%),
    radial-gradient(circle at 50% 0%, #4a90e2 0%, transparent 75%),
    radial-gradient(circle at 100% 50%, #357abd 0%, transparent 50%),
    radial-gradient(circle at 0% 100%, #7fb2f0 0%, transparent 50%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 修改背景动画样式 */
.background-animation {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
}

.grid {
  position: absolute;
  width: 200%;
  height: 200%;
  background-image: 
    linear-gradient(rgba(74, 144, 226, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(74, 144, 226, 0.03) 1px, transparent 1px),
    radial-gradient(circle at 50% 50%, rgba(74, 144, 226, 0.05) 0%, transparent 100%);
  background-size: 20px 20px, 20px 20px, 600px 600px;
  background-position: center;
  transform: rotate(-45deg);
  animation: gridMove 60s linear infinite;
}

.circles {
  position: absolute;
  width: 100%;
  height: 100%;
}

.circle {
  position: absolute;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 20s infinite ease-in-out;
  backdrop-filter: blur(8px);
}

/* 添加更多装饰性圆形 */
.circle:nth-child(1) { width: 400px; height: 400px; top: -5%; left: -5%; animation-delay: 0s; }
.circle:nth-child(2) { width: 300px; height: 300px; top: 60%; right: -5%; animation-delay: -5s; }
.circle:nth-child(3) { width: 250px; height: 250px; bottom: -5%; left: 30%; animation-delay: -10s; }
.circle:nth-child(4) { width: 150px; height: 150px; top: 20%; right: 20%; animation-delay: -15s; }
.circle:nth-child(5) { width: 100px; height: 100px; bottom: 20%; left: 10%; animation-delay: -20s; }

/* 添加一些小点装饰 */
.login-container::before,
.login-container::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at center, rgba(74, 144, 226, 0.1) 0%, transparent 2px),
    radial-gradient(circle at center, rgba(74, 144, 226, 0.05) 0%, transparent 1px);
  background-size: 40px 40px, 20px 20px;
  animation: sparkle 4s linear infinite;
}

.login-container::after {
  transform: rotate(30deg);
  animation-delay: -2s;
}

@keyframes sparkle {
  0% { opacity: 0.5; }
  50% { opacity: 1; }
  100% { opacity: 0.5; }
}

/* 修改登录卡片的样式以配合新背景 */
.login-content {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  display: flex;
  width: 1100px;
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08),
              0 0 0 1px rgba(255, 255, 255, 0.8);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.login-left {
  flex: 1.2;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  padding: 60px;
  position: relative;
  overflow: hidden;
}

.logo-container {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 30px;
}

.logo {
  width: 100%;
  height: 100%;
  object-fit: contain;
  position: relative;
  z-index: 1;
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 140%;
  height: 140%;
  background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, transparent 70%);
  animation: glow 3s infinite ease-in-out;
}

.brand-info {
  text-align: center;
  color: white;
  position: relative;
  z-index: 1;
}

.brand-info h1 {
  font-size: 2.4rem;
  margin-bottom: 15px;
  font-weight: 600;
  text-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.slogan {
  font-size: 1.2rem;
  opacity: 0.9;
  margin-bottom: 40px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.1rem;
  opacity: 0.9;
  transition: transform 0.3s;
}

.feature-item:hover {
  transform: translateX(10px);
}

.login-card {
  flex: 1;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.card-header {
  margin-bottom: 40px;
}

.login-card h2 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 12px;
  font-weight: 600;
}

.subtitle {
  color: #7f8c8d;
  font-size: 1.1rem;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 0.9rem;
  font-weight: 500;
}

.input-wrapper :deep(.el-input__wrapper) {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  padding: 8px 12px;
}

.input-wrapper :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.2);
}

.password-toggle {
  cursor: pointer;
  color: #95a5a6;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
}

.forgot-link {
  color: #4a90e2;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s;
}

.forgot-link:hover {
  color: #357abd;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 1rem;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  border: none;
  border-radius: 8px;
  margin-bottom: 20px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.2);
}

.register-link {
  text-align: center;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.register-link a {
  color: #4a90e2;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

@keyframes gridMove {
  0% { transform: rotate(-45deg) translateY(0); }
  100% { transform: rotate(-45deg) translateY(-50%); }
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-20px, 20px); }
}

@keyframes glow {
  0%, 100% { opacity: 0.5; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.1); }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .login-content {
    width: 90%;
    max-width: 1000px;
  }
}

@media (max-width: 768px) {
  .login-content {
    flex-direction: column;
    max-width: 400px;
  }

  .login-left {
    padding: 40px 30px;
  }

  .login-card {
    padding: 40px 30px;
  }

  .feature-list {
    display: none;
  }
}
</style> 