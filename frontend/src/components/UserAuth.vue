<template>
  <div>
    <!-- 用户信息显示 -->
    <div v-if="isLoggedIn" class="flex items-center gap-4">
      <span class="text-gray-700">欢迎，{{ userInfo.username }}</span>
      <button @click="logout" class="text-gray-600 hover:text-red-600 transition-colors border border-gray-300 px-4 py-2 rounded-lg bg-white shadow-sm">
        登出
      </button>
    </div>
    
    <!-- 登录注册按钮 -->
    <div v-else class="flex gap-4 mb-4 z-50 relative">
      <button @click="showLogin = true" class="text-gray-600 hover:text-blue-600 transition-colors border border-gray-300 px-4 py-2 rounded-lg bg-white shadow-sm">登录</button>
      <button @click="showRegister = true" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow transition-all duration-300 transform hover:scale-105 font-medium">注册</button>
    </div>
    
    <!-- 登录模态框 -->
    <div v-if="showLogin" class="fixed inset-0 bg-black/30 flex items-center justify-center z-50 backdrop-blur-sm">
      <div class="bg-white rounded-xl shadow-2xl p-8 w-full max-w-md mx-4">
        <div class="text-center mb-6">
          <h3 class="text-2xl font-bold text-gray-900 mb-2">登录</h3>
        </div>
        <form @submit.prevent="login" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
            <input 
              v-model="loginForm.username" 
              class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
              required 
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
            <input 
              v-model="loginForm.password" 
              type="password" 
              class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
              required 
            />
          </div>
          <div v-if="loginError" class="text-red-500 text-sm">{{ loginError }}</div>
          <div class="flex justify-between items-center pt-4">
            <button 
              type="button" 
              @click="showLogin = false" 
              class="px-6 py-2 rounded-lg bg-gray-200 hover:bg-gray-300 transition-colors"
            >
              取消
            </button>
            <button 
              type="submit" 
              :disabled="loginLoading"
              class="px-6 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition-all duration-300 disabled:opacity-50"
            >
              <span v-if="loginLoading">登录中...</span>
              <span v-else>登录</span>
            </button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 注册模态框 -->
    <div v-if="showRegister" class="fixed inset-0 bg-black/30 flex items-center justify-center z-50 backdrop-blur-sm">
      <div class="bg-white rounded-xl shadow-2xl p-8 w-full max-w-md mx-4">
        <div class="text-center mb-6">
          <h3 class="text-2xl font-bold text-gray-900 mb-2">注册</h3>
        </div>
        <form @submit.prevent="register" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
            <input 
              v-model="registerForm.username" 
              class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
              required 
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
            <input 
              v-model="registerForm.email" 
              type="email" 
              class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
              required 
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
            <input 
              v-model="registerForm.password" 
              type="password" 
              class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
              required 
            />
          </div>
          <div v-if="registerError" class="text-red-500 text-sm">{{ registerError }}</div>
          <div class="flex justify-between items-center pt-4">
            <button 
              type="button" 
              @click="showRegister = false" 
              class="px-6 py-2 rounded-lg bg-gray-200 hover:bg-gray-300 transition-colors"
            >
              取消
            </button>
            <button 
              type="submit" 
              :disabled="registerLoading"
              class="px-6 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition-all duration-300 disabled:opacity-50"
            >
              <span v-if="registerLoading">注册中...</span>
              <span v-else>注册</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import authApi from '../api/auth';

export default {
  name: 'UserAuth',
  data() {
    return {
      isLoggedIn: false,
      userInfo: { username: '' },
      showLogin: false,
      showRegister: false,
      loginForm: { username: '', password: '' },
      registerForm: { username: '', email: '', password: '' },
      loginLoading: false,
      registerLoading: false,
      loginError: '',
      registerError: ''
    };
  },
  methods: {
    async login() {
      this.loginError = '';
      this.loginLoading = true;
      
      try {
        const response = await authApi.login({
          username: this.loginForm.username,
          password: this.loginForm.password
        });
        
        // 保存token和用户信息
        localStorage.setItem('token', response.data.token);
        const userData = {
          username: response.data.username,
          token: response.data.token
        };
        localStorage.setItem('user', JSON.stringify(userData));
        
        this.isLoggedIn = true;
        this.userInfo = userData;
        this.showLogin = false;
        this.loginForm = { username: '', password: '' };
        
        // 触发登录事件
        this.$emit('login', userData);
        
      } catch (error) {
        console.error('登录失败:', error);
        this.loginError = error.response?.data || '登录失败，请检查用户名和密码';
      } finally {
        this.loginLoading = false;
      }
    },
    
    async register() {
      this.registerError = '';
      this.registerLoading = true;
      
      try {
        const response = await authApi.register({
          username: this.registerForm.username,
          email: this.registerForm.email,
          password: this.registerForm.password
        });
        
        // 注册成功后自动登录
        this.showRegister = false;
        this.showLogin = true;
        this.registerForm = { username: '', email: '', password: '' };
        
        // 可以显示成功消息
        alert('注册成功！请登录');
        
      } catch (error) {
        console.error('注册失败:', error);
        this.registerError = error.response?.data || '注册失败，请稍后重试';
      } finally {
        this.registerLoading = false;
      }
    },
    
    logout() {
      // 清除本地存储
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      
      // 更新状态
      this.isLoggedIn = false;
      this.userInfo = { username: '' };
      
      // 触发登出事件
      this.$emit('logout');
    }
  },
  
  mounted() {
    // 检查是否已登录
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');
    if (token && user) {
      try {
        const userData = JSON.parse(user);
        this.isLoggedIn = true;
        this.userInfo = userData;
      } catch (e) {
        console.error('解析用户信息失败:', e);
        this.logout();
      }
    }
  }
};
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { 
    opacity: 0; 
    transform: scale(0.95) translateY(-10px);
  }
  to { 
    opacity: 1; 
    transform: scale(1) translateY(0);
  }
}
</style> 