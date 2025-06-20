<!-- components/AuthLayout.vue -->
<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="w-full max-w-md space-y-8 bg-white p-6 rounded-lg shadow-md">
      <div>
        <h2 class="mt-6 text-center text-3xl font-bold text-gray-900">
          {{ isLogin ? '登录' : '注册' }}
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          {{ isLogin ? '欢迎回来' : '创建新账号' }}
        </p>
      </div>
      <form class="space-y-6" @submit.prevent="handleSubmit">
        <div>
          <label for="email" class="block text-sm font-medium text-gray-700">邮箱</label>
          <input 
            id="email" 
            v-model="form.email" 
            type="email" 
            required 
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary"
          >
        </div>
        <div>
          <label for="password" class="block text-sm font-medium text-gray-700">密码</label>
          <input 
            id="password" 
            v-model="form.password" 
            type="password" 
            required 
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary"
          >
        </div>
        <div>
          <button 
            type="submit" 
            class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-white bg-primary hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
          >
            {{ isLogin ? '登录' : '注册' }}
          </button>
        </div>
      </form>
      <div class="text-center">
        <a 
          href="#" 
          class="font-medium text-primary hover:text-primary/90"
          @click="toggleAuthMode"
        >
          {{ isLogin ? '去注册' : '已有账号？登录' }}
        </a>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLogin: true,
      form: { email: '', password: '' }
    }
  },
  methods: {
    toggleAuthMode() {
      this.isLogin = !this.isLogin;
      this.form = { email: '', password: '' };
    },
    handleSubmit() {
      // 调用登录/注册逻辑
      console.log('提交表单:', this.form);
    }
  }
}
</script>

<style scoped>
/* 可自定义主题色 */
:root { --primary: #4F46E5; }
</style> 