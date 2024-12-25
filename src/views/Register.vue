<template>
  <div class="register-container">
    <div class="register-card">
      <h2>创建账号</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>用户名</label>
          <div class="input-wrapper">
            <input v-model="registerForm.username" type="text" required>
          </div>
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <div class="input-wrapper">
            <input
              v-model="registerForm.email"
              type="email"
              required
              @input="validateEmail"
              :class="{ error: emailError }"
            >
            <span class="email-error" v-if="emailError">{{ emailError }}</span>
          </div>
        </div>
        <div class="form-group verification-code">
          <label>验证码</label>
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
          <label>密码</label>
          <div class="input-wrapper">
            <input
              v-model="registerForm.password"
              type="password"
              required
              @input="validatePassword"
            >
          </div>
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <div class="input-wrapper">
            <input
              v-model="registerForm.confirmPassword"
              type="password"
              required
              @input="validatePassword"
            >
            <span class="password-error" v-if="passwordError">{{ passwordError }}</span>
          </div>
        </div>
        <div class="form-buttons">
          <button type="submit" class="submit-btn">注册</button>
          <div class="login-link">
            已有账号？ <router-link to="/login">立即登录</router-link>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';

export default {
  name: 'RegisterPage',
  data() {
    return {
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
  methods: {
    async sendVerificationCode() {
      if (!this.registerForm.email) {
        ElMessage.warning('请先输入邮箱地址');
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
          
          ElMessage.success('验证码已发送到您的邮箱');
        } else {
          ElMessage.error('验证码发送失败，请稍后重试');
        }
      } catch (error) {
        console.error('发送验证码错误:', error);
        ElMessage.error('发送验证码过程发生错误');
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
      if (this.emailCheckTimeout) {
        clearTimeout(this.emailCheckTimeout);
      }
      
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.registerForm.email)) {
        this.emailError = '请输入有效的邮箱地址';
        return;
      }
      
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
      }, 500);
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
        ElMessage.warning('请输入验证码');
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
          ElMessage.success('注册成功！');
          this.$router.push('/login');
        } else {
          const data = await response.json();
          ElMessage.error(data.message || '注册失败，请稍后重试');
        }
      } catch (error) {
        console.error('注册错误:', error);
        ElMessage.error('注册过程发生错误');
      }
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  padding: 20px;
}

.register-card {
  background: white;
  padding: 2.5rem;
  border-radius: 16px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.register-card h2 {
  color: #2c3e50;
  text-align: center;
  margin-bottom: 2rem;
  font-size: 1.8rem;
}

/* 复用登录页面的其他样式 */
.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  color: #5c6c7c;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.input-wrapper {
  position: relative;
}

.input-wrapper input {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 2px solid #e8ecf2;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
}

.input-wrapper input:focus {
  outline: none;
  border-color: #7fb2f0;
  box-shadow: 0 0 0 3px rgba(127, 178, 240, 0.1);
}

.input-wrapper input.error {
  border-color: #ff4d4f;
}

.email-error, .password-error {
  color: #ff4d4f;
  font-size: 0.8rem;
  margin-top: 0.3rem;
}

.code-input-group {
  display: flex;
  gap: 1rem;
}

.code-input-group input {
  flex: 1;
}

.send-code-btn {
  padding: 0 1rem;
  background: #7fb2f0;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.send-code-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.form-buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 2rem;
}

.submit-btn {
  width: 100%;
  padding: 0.8rem;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #7fb2f0 0%, #4a90e2 100%);
  color: white;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(74, 144, 226, 0.2);
}

.login-link {
  text-align: center;
  color: #5c6c7c;
  font-size: 0.9rem;
}

.login-link a {
  color: #4a90e2;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}
</style> 