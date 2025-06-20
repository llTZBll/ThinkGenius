<template>
  <div class="bg-gray-50 min-h-screen font-sans">
    <!-- 加载动画 -->
    <div v-if="loadingCanvas" class="fixed inset-0 flex items-center justify-center z-50 bg-gray-50/80 backdrop-blur-sm">
      <div class="bg-white p-8 rounded-xl shadow-2xl flex flex-col items-center animate-fade-in">
        <div class="w-20 h-20 border-4 border-primary border-t-transparent rounded-full animate-spin mb-4"></div>
        <h3 class="text-xl font-bold text-gray-800 mb-2">AI生成中...</h3>
        <p class="text-gray-500">正在分析您的输入并生成关键词网络</p>
      </div>
    </div>
    
    <!-- 导航栏 -->
    <header class="bg-white shadow-sm fixed top-0 left-0 right-0 z-40 transition-all duration-300">
      <div class="container mx-auto px-4 py-3 flex justify-between items-center">
        <button v-if="!showCanvas" @click="openPanel" class="bg-primary hover:bg-primary/90 text-white px-4 py-2 rounded-lg shadow flex items-center transition-all duration-300 transform hover:scale-105">
          <i class="fa fa-magic mr-2"></i>
          <span>AI生成</span>
        </button>
        <h1 class="text-xl font-bold text-neutral flex items-center">
          <i class="fa fa-lightbulb-o text-primary mr-2"></i>
          AI与思维导图的完美结合
        </h1>
        <UserAuth @login="onLogin" @logout="onLogout" />
      </div>
    </header>

    <!-- 首页内容 -->
    <main v-if="!showCanvas" class="container mx-auto px-4 pt-24 pb-16">
      <!-- 介绍区域 -->
      <div class="text-center mb-12 animate-fade-in-up">
        <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-neutral mb-4 tracking-tight">探索AI生成赋能头脑风暴</h2>
        <div class="max-w-2xl mx-auto">
          <p class="text-gray-600 mb-6 leading-relaxed">
            输入任何主题或关键词，AI将为您生成相关的概念网络，帮助您发现新的关联和创意方向。
          </p>
          <div class="relative max-w-2xl mx-auto">
            <input 
              v-model="prompt" 
              type="text" 
              placeholder="输入关键词或主题..." 
              class="w-full px-5 py-4 rounded-xl border border-gray-300 focus:ring-3 focus:ring-primary/30 focus:border-primary shadow-sm transition-all duration-300 text-lg"
              @keyup.enter="generateKeywords"
            >
            <button 
              @click="generateKeywords" 
              :disabled="loading" 
              class="absolute right-2 top-1/2 -translate-y-1/2 bg-primary hover:bg-primary/90 text-white px-6 py-2.5 rounded-lg shadow-lg transition-all duration-300 transform hover:scale-105 flex items-center"
            >
              <i v-if="loading" class="fa fa-spinner fa-spin mr-2"></i>
              <i v-else class="fa fa-rocket mr-2"></i>
              <span>生成</span>
            </button>
          </div>
          <div v-if="aiError" class="text-red-500 text-sm mt-2 flex items-center justify-center">
            <i class="fa fa-exclamation-circle mr-1"></i>
            <span>{{ aiError }}</span>
          </div>
        </div>
      </div>

      <!-- 生成的关键词预览 -->
      <div v-if="keywords.length > 0" class="max-w-3xl mx-auto mb-12 animate-fade-in-up" style="animation-delay: 0.2s">
        <div class="bg-white rounded-xl shadow-md p-6 transform transition-all hover:shadow-xl">
          <h3 class="text-lg font-bold mb-4 text-neutral flex items-center">
            <i class="fa fa-tags text-primary mr-2"></i>
            AI生成的关键词
          </h3>
          <div class="flex flex-wrap gap-3">
            <span 
              v-for="(kw, idx) in keywords" 
              :key="idx" 
              class="bg-primary/10 text-primary px-4 py-2 rounded-full text-sm font-medium transition-all hover:bg-primary hover:text-white cursor-pointer"
              @click="handleKeywordPreviewClick(kw)"
            >
              {{ kw }}
            </span>
          </div>
        </div>
      </div>

      <!-- 功能卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-12">
        <div 
          v-for="(card, index) in cards" 
          :key="index" 
          class="bg-white rounded-xl shadow-md overflow-hidden transition-all hover:shadow-xl hover:-translate-y-2 duration-300 transform hover:scale-105 animate-fade-in-up"
          :style="{animationDelay: `${index * 0.1}s`}"
        >
          <div :class="card.bgClass" class="h-48 flex items-center justify-center">
            <i :class="card.iconClass"></i>
          </div>
          <div class="p-6">
            <h3 class="text-xl font-semibold mb-2 text-neutral flex items-center">
              <i :class="`${card.iconClass} text-primary mr-2 text-lg`"></i>
              {{ card.title }}
            </h3>
            <p class="text-gray-600 leading-relaxed">
              {{ card.description }}
            </p>
          </div>
        </div>
      </div>
    </main>

    <!-- 画布视图 -->
    <section v-if="showCanvas" class="container mx-auto px-4 pt-24 pb-16">
      <button 
        @click="backToHome" 
        class="mb-4 bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded-lg flex items-center transition-all duration-300 transform hover:scale-105"
      >
        <i class="fa fa-arrow-left mr-2"></i>
        返回首页
      </button>
      <BlockCanvas
        :question="canvasData.question"
        :keywords="canvasData.keywords"
        :textBlocks="textBlocks"
        :keywordBlocks="keywordBlocks"
        :relations="relations"
        @keyword-click="handleKeywordClick"
        @text-summary="handleTextBlockSummary"
        @text-keywords="handleTextBlockKeywords"
        @keyword-expand="handleKeywordExpand"
        @keyword-collapse="handleKeywordCollapse"
        @text-edit="handleTextEdit"
      />
    </section>

    <!-- 页脚 -->
    <footer class="bg-neutral text-white py-8">
      <div class="container mx-auto px-4">
        <div class="flex flex-col md:flex-row justify-between items-center">
          <div class="mb-4 md:mb-0">
            <h2 class="text-xl font-bold flex items-center">
              <i class="fa fa-lightbulb-o text-primary mr-2"></i>
              AI关键词生成器
            </h2>
            <p class="text-gray-400 mt-1">由AI驱动的关键词拓展工具</p>
          </div>
          <div class="flex space-x-4">
            <a href="#" class="text-gray-400 hover:text-white transition-colors duration-300 transform hover:scale-110">
              <i class="fa fa-github text-xl"></i>
            </a>
            <a href="#" class="text-gray-400 hover:text-white transition-colors duration-300 transform hover:scale-110">
              <i class="fa fa-twitter text-xl"></i>
            </a>
            <a href="#" class="text-gray-400 hover:text-white transition-colors duration-300 transform hover:scale-110">
              <i class="fa fa-linkedin text-xl"></i>
            </a>
          </div>
        </div>
        <div class="border-t border-gray-700 mt-6 pt-6 text-center text-gray-400">
          <p>© 2025 AI关键词生成器. 保留所有权利.</p>
        </div>
      </div>
    </footer>

    <!-- 社交媒体链接 -->
    <div class="fixed bottom-6 right-6 flex flex-col gap-4 z-50">
      <a 
        href="https://github.com/your-repo" 
        target="_blank"
        class="w-12 h-12 bg-white rounded-full shadow-lg flex items-center justify-center text-gray-800 hover:bg-gray-100 transition-all duration-300 transform hover:scale-110"
        title="GitHub"
      >
        <i class="fa fa-github text-2xl"></i>
      </a>
      <a 
        href="https://space.bilibili.com/your-space" 
        target="_blank"
        class="w-12 h-12 bg-white rounded-full shadow-lg flex items-center justify-center text-blue-500 hover:bg-gray-100 transition-all duration-300 transform hover:scale-110"
        title="Bilibili"
      >
        <svg class="w-6 h-6" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M896 128H128v768h768V128zm-96 128-64-32-64-32-64 32-160 80v320l64 32 64 32 64-32 160-80V256zm-128 64v192l-96 48-96-48V320l96-48 96 48zM224 512V320l64-32v256l-64-32zm-32-256h64v64h-64v-64zm0 128h64v64h-64v-64zm0 128h64v64h-64v-64zm0 128h64v64h-64v-64z"></path></svg>
      </a>
      <a 
        href="#"
        class="w-12 h-12 bg-white rounded-full shadow-lg flex items-center justify-center text-green-500 hover:bg-gray-100 transition-all duration-300 transform hover:scale-110"
        title="WeChat"
      >
        <i class="fa fa-weixin text-2xl"></i>
      </a>
    </div>
  </div>
</template>

<script>
import BlockCanvas from './BlockCanvas.vue';
import UserAuth from './components/UserAuth.vue';
import aiApi from './api/ai';

export default {
  components: { BlockCanvas, UserAuth },
  data() {
    return {
      prompt: '',
      keywords: [],
      loading: false,
      aiError: '',
      showCanvas: false,
      loadingCanvas: false,
      canvasData: { question: '', keywords: [] },
      textBlocks: [],
      keywordBlocks: [],
      relations: [],
      cards: [
        {
          title: '创意灵感',
          description: '输入主题，获取相关创意关键词和拓展想法',
          bgClass: 'bg-gradient-to-br from-primary/20 to-secondary/20',
          iconClass: 'fa fa-lightbulb-o text-6xl text-primary/60'
        },
        {
          title: '关键词研究',
          description: '拓展您的搜索词，发现更多相关主题和内容',
          bgClass: 'bg-gradient-to-br from-blue-200 to-purple-200',
          iconClass: 'fa fa-search text-6xl text-blue-500/60'
        },
        {
          title: '内容拓展',
          description: '深入挖掘主题，获取相关文本和内容建议',
          bgClass: 'bg-gradient-to-br from-green-200 to-teal-200',
          iconClass: 'fa fa-puzzle-piece text-6xl text-green-500/60'
        }
      ]
    }
  },
  methods: {
    openPanel() {
      // 聚焦输入框
      this.$nextTick(() => {
        document.querySelector('input').focus();
      });
    },
    
    // 生成关键词并显示画布
    async generateKeywords() {
      this.aiError = '';
      this.keywords = [];
      
      if (!this.prompt.trim()) {
        this.aiError = '请输入关键词或主题';
        return;
      }
      
      this.loading = true;
      this.loadingCanvas = true;
      
      try {
        const res = await aiApi.generateKeywords({ content: this.prompt });
        this.keywords = res.data.keywords || [];
        
        if (this.keywords.length === 0) {
          this.aiError = '未能生成相关关键词，请尝试其他输入';
          return;
        }
        
        this.canvasData = { question: this.prompt, keywords: this.keywords };
        this.textBlocks = [];
        this.keywordBlocks = [];
        this.relations = [];
        
        // 添加初始关系：问题到关键词
        this.relations.push({ sourceId: 0, targetId: 1, type: 'related', animated: true });
        
        // 延迟显示画布以展示加载动画
        setTimeout(() => {
          this.showCanvas = true;
          this.loadingCanvas = false;
        }, 800);
        
      } catch (e) {
        console.error('生成关键词失败:', e);
        this.aiError = e.message || '生成失败，请重试';
        this.loadingCanvas = false;
      } finally {
        this.loading = false;
      }
    },
    
    // 点击关键词预览时生成相关内容
    handleKeywordPreviewClick(keyword) {
      this.prompt = keyword;
      this.generateKeywords();
    },
    
    // 处理关键词点击事件（生成相关内容）
    async handleKeywordClick(keyword, pos, sourceBlockId = null) {
      const id = Date.now();
      const intro = '正在生成内容...';
      
      // 添加新文本块
      this.textBlocks.push({ id, keyword, intro, x: pos.x, y: pos.y });
      
      // 添加关系
      if (sourceBlockId) {
        // 扩展关键词块到新文本块
        this.relations.push({ sourceId: sourceBlockId, targetId: id, type: 'parent', animated: true });
      } else {
        // 主关键词块到新文本块
        this.relations.push({ sourceId: 1, targetId: id, type: 'parent', animated: true });
      }
      
      try {
        // 调用API生成内容
        const prompt = `请用100-250字、1-3段话简要介绍"${keyword}"。请直接生成介绍内容，不要包含对内容的分析、总结或评价。`;
        const res = await aiApi.analyze({ content: prompt });
        
        // 更新文本块内容
        const textBlockIndex = this.textBlocks.findIndex(block => block.id === id);
        if (textBlockIndex !== -1) {
          this.textBlocks[textBlockIndex] = {
            ...this.textBlocks[textBlockIndex],
            intro: res.data.analysis || res.data.summary || '暂无介绍'
          };
        }
        
      } catch (e) {
        console.error('生成内容失败:', e);
        // 更新文本块内容为错误信息
        const textBlockIndex = this.textBlocks.findIndex(block => block.id === id);
        if (textBlockIndex !== -1) {
          this.textBlocks[textBlockIndex] = {
            ...this.textBlocks[textBlockIndex],
            intro: '生成失败，请重试'
          };
        }
      }
    },
    
    // 处理文本块总结请求
    async handleTextBlockSummary({ block, pos }) {
      const id = Date.now();
      const intro = '正在生成总结...';
      
      // 添加新文本块（总结）
      this.textBlocks.push({ id, keyword: block.keyword + ' 总结', intro, x: pos.x, y: pos.y });
      
      // 添加关系（原文本块到总结块）
      this.relations.push({ sourceId: block.id, targetId: id, type: 'summary', animated: true });
      
      try {
        // 调用API生成总结
        const prompt = `请对以下内容进行简明总结，100-200字。请直接生成总结内容，不要包含对总结的分析或评价：\n${block.intro}`;
        const res = await aiApi.analyze({ content: prompt });
        
        // 更新总结内容
        const textBlockIndex = this.textBlocks.findIndex(b => b.id === id);
        if (textBlockIndex !== -1) {
          this.textBlocks[textBlockIndex] = {
            ...this.textBlocks[textBlockIndex],
            intro: res.data.analysis || res.data.summary || '暂无总结'
          };
        }
        
      } catch (e) {
        console.error('生成总结失败:', e);
        // 更新总结内容为错误信息
        const textBlockIndex = this.textBlocks.findIndex(b => b.id === id);
        if (textBlockIndex !== -1) {
          this.textBlocks[textBlockIndex] = {
            ...this.textBlocks[textBlockIndex],
            intro: '生成失败，请重试'
          };
        }
      }
    },
    
    // 处理文本块关键词提取请求
    async handleTextBlockKeywords({ block, pos }) {
      const id = Date.now();
      
      // 添加新关键词块
      this.keywordBlocks.push({ 
        id, 
        keywords: ['生成中...'], 
        x: pos.x, 
        y: pos.y,
        sourceKeyword: block.keyword
      });
      
      // 添加关系（原文本块到关键词块）
      this.relations.push({ sourceId: block.id, targetId: id, type: 'parent', animated: true });
      
      try {
        // 调用API生成关键词
        const prompt = `请为以下内容提取3-6个关键词，逗号分隔：\n${block.intro}`;
        const res = await aiApi.generateKeywords({ content: prompt });
        
        // 更新关键词
        const keywordBlockIndex = this.keywordBlocks.findIndex(b => b.id === id);
        if (keywordBlockIndex !== -1) {
          this.keywordBlocks[keywordBlockIndex] = {
            ...this.keywordBlocks[keywordBlockIndex],
            keywords: res.data.keywords || ['无关键词']
          };
        }
        
      } catch (e) {
        console.error('生成关键词失败:', e);
        // 更新关键词为错误信息
        const keywordBlockIndex = this.keywordBlocks.findIndex(b => b.id === id);
        if (keywordBlockIndex !== -1) {
          this.keywordBlocks[keywordBlockIndex] = {
            ...this.keywordBlocks[keywordBlockIndex],
            keywords: ['生成失败']
          };
        }
      }
    },
    
    // 处理关键词块展开请求
    async handleKeywordExpand(blockStyle) {
      // 这里可以实现关键词块展开功能，例如显示更多相关关键词
      console.log('Expand keyword block:', blockStyle);
    },
    
    // 处理关键词块折叠请求
    handleKeywordCollapse(block) {
      // 这里可以实现关键词块折叠功能
      console.log('Collapse keyword block:', block);
    },
    
    // 处理文本块编辑请求
    handleTextEdit(block) {
      // 这里可以实现文本块编辑功能，例如打开编辑模态框
      console.log('Edit text block:', block);
    },
    
    // 返回首页
    backToHome() {
      this.showCanvas = false;
      this.prompt = '';
      this.keywords = [];
      this.canvasData = { question: '', keywords: [] };
      this.textBlocks = [];
      this.keywordBlocks = [];
      this.relations = [];
    },
    
    // 登录回调
    onLogin(user) {
      console.log('User logged in:', user);
      // 可在此处处理登录后逻辑
    },
    
    // 登出回调
    onLogout() {
      console.log('User logged out');
      // 可在此处处理退出后逻辑
    }
  },
  
  // 生命周期钩子
  mounted() {
    // 添加动画类
    document.body.classList.add('animate-fade-in');
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zheng&display=swap');

body, html {
  font-family: 'Ma Shan Zheng', "STXingkai", "Xingkai SC", "KaiTi", "Kaiti SC", cursive;
}
</style>

<style scoped>
@tailwind base;
@tailwind components;
@tailwind utilities;

:root {
  --primary: #4F46E5;
  --secondary: #10B981;
  --neutral: #1F2937;
  --light: #F3F4F6;
}

/* 动画类 */
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s ease-out;
  opacity: 0;
  transform: translateY(20px);
  animation-fill-mode: forwards;
}

.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 自定义工具类 */
.bg-gradient-to-br {
  background-image: linear-gradient(to bottom right, var(--tw-gradient-stops));
}

.text-primary {
  color: var(--primary);
}

.bg-primary {
  background-color: var(--primary);
}

.hover:bg-primary:hover {
  background-color: var(--primary);
}

.text-secondary {
  color: var(--secondary);
}

.text-neutral {
  color: var(--neutral);
}

/* 卡片悬停效果 */
.card-hover {
  transition: all 0.3s ease;
}

.card-hover:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

/* 确保按钮层级高于画布 */
.fixed.bottom-4 {
  z-index: 9999; 
}
</style>