<template>
  <div id="app">
    <nav class="navbar">
      <!-- Left side with Logo and Left links -->
      <div class="navbar-left">
        <!-- Logo Section -->
        <div class="logo">
          <img src="./assets/2.png" alt="Logo" class="logo-image" />
        </div>
        
        <!-- Navigation Links -->
        <div class="navbar-links">
          <router-link to="/" class="navbar-item" active-class="active-link">首页</router-link>
          <router-link to="/project-square" class="navbar-item" active-class="active-link">项目广场</router-link>
          <router-link to="/ai-assistant" class="navbar-item" active-class="active-link">AI助手</router-link>
          <router-link to="/ai-learning" class="navbar-item" active-class="active-link">AI学习</router-link>
        </div>
      </div>
      
      <!-- Right side: "个人信息" as a clickable logo -->
      <div class="navbar-right">
        <router-link to="/personal-info" class="navbar-item">
          <img src="./assets/客户头像.png" alt="个人信息" class="personal-info-logo" />
        </router-link>
      </div>
    </nav>

    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style scoped>
/* Prevents any margin collapse issues */
#app {
  padding: 0;
  margin: 0;
  font-family: 'Arial', sans-serif;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f4f4f4;
  transition: background-color 0.3s ease;
}

/* Navbar Styling */
.navbar {
  display: flex;
  justify-content: space-between; /* Ensures space between left and right sections */
  align-items: center; /* Center items vertically */
  background-color: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  padding: 15px 20px;
  width: 100%;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

/* Left Group: Logo and Links */
.navbar-left {
  display: flex;
  align-items: center;
  justify-content: flex-start; /* Align to the left */
  width: 100%;
  max-width: 800px; /* Limit width of left section */
  flex-grow: 1;
}

/* Logo Styling - move logo to the right */
.logo {
  margin-left: 100px; /* Increase space to move the logo further right */
}

.logo-image {
  width: 40px; /* Adjust logo size */
  height: auto;
}

/* Navbar Links Styling (Left side items) */
.navbar-links {
  display: flex;
  justify-content: space-between; /* Distribute items evenly */
  width: 100%; /* Allow the links container to take up available space */
}

/* Navbar Item Styling */
.navbar-item {
  text-decoration: none;
  color: #555;
  margin: 0 15px; /* Set some margin between items */
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.5px;
  position: relative;
  transition: color 0.3s ease, transform 0.3s ease, box-shadow 0.3s ease;
}

.navbar-item:hover {
  color: #007BFF;
  transform: translateY(-2px);
}

.navbar-item::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 0;
  height: 2px;
  background-color: #007BFF;
  transition: width 0.3s ease;
}

.navbar-item:hover::after {
  width: 100%;
}

/* Active Link Styling */
.active-link {
  color: #0056b3;
  font-weight: bold;
  text-decoration: underline;
  text-decoration-color: #1a73e8;
}

/* Right Group: "个人信息" as logo */
.navbar-right {
  display: flex;
  justify-content: center; /* Center the logo */
  flex-grow: 1; /* Ensure right section fills the remaining space */
  margin-left: auto; /* Push it to the far right */
}

/* Personal Info Logo Styling */
.personal-info-logo {
  width: 40px; /* Set logo size */
  height: auto;
  cursor: pointer; /* Change cursor to pointer to indicate clickability */
}

/* Page content area */
router-view {
  margin-top: 80px; /* Ensure the content isn't hidden under the navbar */
  width: 100%;
  padding: 40px;
  max-width: 1200px;
  background-color: #fff;
  border-radius: 15px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

router-view:hover {
  box-shadow: 0 4px 40px rgba(0, 0, 0, 0.2);
}

/* Smooth Scroll Effects */
html {
  scroll-behavior: smooth;
}

/* Responsive Design for smaller screens */
@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    align-items: center;
    padding: 10px 20px;
  }

  .navbar-item {
    margin: 10px 0;
  }

  router-view {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .navbar-item {
    font-size: 16px;
    margin: 8px 0;
  }

  router-view {
    padding: 15px;
  }
}
</style>
