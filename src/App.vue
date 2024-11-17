<template>
  <div class="app-container">
    <el-container class="main-container">
      <el-header>
        <div class="header-content">
          <img src="@/assets/2.png" alt="Logo" class="logo">
          <div class="nav-menu">
            <el-menu
              :default-active="activeIndex"
              mode="horizontal"
              router
              @select="handleSelect"
            >
              <el-menu-item index="/">首页</el-menu-item>
              <el-menu-item index="/project-square">项目广场</el-menu-item>
              <el-menu-item index="/ai-assistant">AI助手</el-menu-item>
              <el-menu-item index="/ai-learning">AI学习</el-menu-item>
            </el-menu>
          </div>
          <div class="user-info">
            <el-dropdown @command="handleCommand" trigger="click">
              <img 
                src="@/assets/客户头像.png" 
                alt="用户头像" 
                class="user-avatar"
              >
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="personal">个人信息</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'

export default {
  name: 'App',
  data() {
    return {
      activeIndex: '/',
      username: localStorage.getItem('username') || '未登录'
    }
  },
  computed: {
    isLoggedIn() {
      return localStorage.getItem('token') !== null
    }
  },
  methods: {
    handleSelect(key) {
      this.activeIndex = key
    },
    handleCommand(command) {
      if (command === 'logout') {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('email')
        this.username = '未登录'
        ElMessage.success('已退出登录')
        this.$router.push('/')
      } else if (command === 'personal') {
        this.$router.push('/personal-info')
      }
    }
  }
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.el-header {
  padding: 0;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12);
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  justify-content: space-between;
}

.logo {
  height: 40px;
  width: auto;
  margin-right: 20px;
}

.nav-menu {
  flex-grow: 1;
  margin-right: auto;
}

.nav-menu .el-menu {
  border-bottom: none;
}

.user-info {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  object-fit: cover;
}

.el-main {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px); /* 减去header的高度 */
  box-sizing: border-box;
}

/* 确保内容区域最小宽度 */
@media screen and (min-width: 1200px) {
  .el-main {
    padding: 20px calc((100% - 1200px) / 2);
  }
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .header-content {
    padding: 0 20px;
  }
  
  .el-main {
    padding: 10px;
  }
}
</style>
