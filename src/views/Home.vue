<template>
  <div class="home-container">
    <div v-if="!isLoggedIn">
      <h1>欢迎来到AI教育平台</h1>
      <div class="auth-buttons">
        <button @click="showLoginModal = true" class="auth-button login-btn">登录</button>
        <button @click="showRegisterModal = true" class="auth-button register-btn">注册</button>
      </div>

      <!-- 登录模态框 -->
      <div v-if="showLoginModal" class="modal">
        <div class="modal-content">
          <h2>登录</h2>
          <form @submit.prevent="handleLogin">
            <div class="form-group">
              <label>用户名:</label>
              <input v-model="loginForm.username" type="text" required>
            </div>
            <div class="form-group">
              <label>密码:</label>
              <input v-model="loginForm.password" type="password" required>
            </div>
            <div class="modal-buttons">
              <button type="submit" class="submit-btn">登录</button>
              <button type="button" @click="showLoginModal = false" class="cancel-btn">取消</button>
            </div>
          </form>
        </div>
      </div>

      <!-- 注册模态框 -->
      <div v-if="showRegisterModal" class="modal">
        <div class="modal-content">
          <h2>注册</h2>
          <form @submit.prevent="handleRegister">
            <div class="form-group">
              <label>用户名:</label>
              <input v-model="registerForm.username" type="text" required>
            </div>
            <div class="form-group">
              <label>邮箱:</label>
              <input
                v-model="registerForm.email"
                type="email"
                required
                @input="validateEmail"
                :class="{ error: emailError }"
              >
              <span class="email-error" v-if="emailError">{{ emailError }}</span>
            </div>
            <div class="form-group verification-code">
              <label>验证码:</label>
              <div class="code-input-group">
                <input v-model="registerForm.verificationCode" type="text" required>
                <button type="button"
                        @click="sendVerificationCode"
                        :disabled="cooldown > 0"
                        class="send-code-btn">
                  {{ cooldown > 0 ? `${cooldown}秒后重试` : '发送验证码' }}
                </button>
              </div>
            </div>
            <div class="form-group">
              <label>密码:</label>
              <input
                v-model="registerForm.password"
                type="password"
                required
                @input="validatePassword"
              >
            </div>
            <div class="form-group">
              <label>确认密码:</label>
              <input
                v-model="registerForm.confirmPassword"
                type="password"
                required
                @input="validatePassword"
              >
              <span class="password-error" v-if="passwordError">{{ passwordError }}</span>
            </div>
            <div class="modal-buttons">
              <button type="submit" class="submit-btn">注册</button>
              <button type="button" @click="showRegisterModal = false" class="cancel-btn">取消</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 已登录状态 -->
    <div v-else class="welcome-content">
      <h1>欢迎进入首页</h1>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';

export default {
  name: 'HomePage',
  data() {
    return {
      showLoginModal: false,
      showRegisterModal: false,
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        email: '',
        userid: '',
        password: '',
        confirmPassword: '',
        verificationCode: ''
      },
      cooldown: 0,
      passwordError: '',
      emailError: '',
      emailCheckTimeout: null
    }
  },
  computed: {
    isLoggedIn() {
      return localStorage.getItem('token') !== null
    }
  },
  methods: {
    async handleLogin() {
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
          
          ElMessage.success('登录成功');
          this.showLoginModal = false;
          this.$router.go(0); // 刷新页面以更新状态
        } else {
          ElMessage.error(data.message || '登录失败');
        }
      } catch (error) {
        ElMessage.error('登录失败：' + error.message);
      }
    },
    
    async sendVerificationCode() {
      if (!this.registerForm.email) {
        alert('请先输入邮箱地址');
        return;
      }
      
      try {
        const response = await fetch('http://localhost:8008/api/auth/send-code', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ email: this.registerForm.email })
        });
        
        if (response.ok) {
          this.cooldown = 60;
          const timer = setInterval(() => {
            this.cooldown--;
            if (this.cooldown <= 0) {
              clearInterval(timer);
            }
          }, 1000);
          
          alert('验证码已发送到您的邮箱，如未收到，请耐心等待或再发送一次');
        } else {
          alert('验证码发送失败，请稍后重试');
        }
      } catch (error) {
        console.error('发送验证码错误:', error);
        alert('发送验证码过程发生错误');
      }
    },
    
    validatePassword() {
      if (this.registerForm.confirmPassword) {
        if (this.registerForm.password !== this.registerForm.confirmPassword) {
          this.passwordError = '两次输入的密码不一致';
        } else {
          this.passwordError = '';
        }
      }
    },
    
    async validateEmail() {
      // 清除之前的定时器
      if (this.emailCheckTimeout) {
        clearTimeout(this.emailCheckTimeout);
      }
      
      // 邮箱格式验证
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.registerForm.email)) {
        this.emailError = '请输入有效的邮箱地址';
        return;
      }
      
      // 设置新的定时器（防抖）
      this.emailCheckTimeout = setTimeout(async () => {
        try {
          const response = await fetch(
            `http://localhost:8008/api/auth/check-email?email=${encodeURIComponent(this.registerForm.email)}`,
            {
              method: 'GET',
              headers: {
                'Content-Type': 'application/json'
              }
            }
          );
          
          const data = await response.json();
          if (data.exists) {
            this.emailError = '该邮箱已被注册';
          } else {
            this.emailError = '';
          }
        } catch (error) {
          console.error('检查邮箱错误:', error);
        }
      }, 500); // 500ms 的防抖延迟
    },
    
    async handleRegister() {
      if (this.emailError) {
        ElMessage.error(this.emailError);
        return;
      }
      
      if (this.passwordError) {
        ElMessage.error('两次输入的密码不一致');
        return;
      }
      
      if (!this.registerForm.verificationCode) {
        alert('请输入验证码');
        return;
      }
      
      try {
        const response = await fetch('http://localhost:8008/api/auth/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.registerForm)
        });
        
        if (response.ok) {
          alert('注册成功!');
          this.showRegisterModal = false;
          this.showLoginModal = true;
        } else {
          const data = await response.json();
          alert(data.message || '注册失败，请稍后重试');
        }
      } catch (error) {
        console.error('注册错误:', error);
        alert('注册过程发生错误');
      }
    }
  }
}
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 60px);
  padding: 20px;
  background-color: #f5f7fa;
}

.welcome-content {
  text-align: center;
}

.welcome-content h1 {
  color: #409EFF;
  font-size: 2.5em;
  margin-bottom: 20px;
}

.auth-buttons {
  margin-top: 30px;
}

.auth-button {
  padding: 12px 24px;
  margin: 0 10px;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn {
  background-color: #4CAF50;
  color: white;
}

.register-btn {
  background-color: #2196F3;
  color: white;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  width: 400px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.submit-btn, .cancel-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn {
  background-color: #4CAF50;
  color: white;
}

.cancel-btn {
  background-color: #f44336;
  color: white;
}

.verification-code .code-input-group {
  display: flex;
  gap: 10px;
}

.send-code-btn {
  padding: 8px 15px;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.send-code-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.password-error {
  color: #f44336;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}

.form-group input.error {
  border-color: #f44336;
}

.email-error {
  color: #f44336;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}
</style>