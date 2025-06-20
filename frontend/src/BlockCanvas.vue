<template>
  <div class="block-canvas bg-gray-50 rounded-xl shadow-lg p-6 relative overflow-hidden fullscreen-canvas" :style="canvasStyle" @mousedown="startCanvasDrag">
    <div class="canvas-content" :style="canvasContentStyle">
      <!-- 问题块（中心，可拖动，左问号右问题） -->
      <div
        v-if="question"
        class="block-item absolute border-2 border-primary rounded-xl shadow-md flex items-center font-medium draggable question-block"
        :style="blockStyle(blocks[0], 'question')"
        @mousedown="startDrag($event, 0)"
      >
        <span class="question-icon text-2xl mr-4 text-primary">❓</span>
        <span class="question-content break-words">{{ question }}</span>
      </div>
      <!-- 关键词块（复杂卡片样式，可拖动） -->
      <div
        v-if="keywords && keywords.length > 0"
        class="block-item keyword-card absolute draggable"
        :style="blockStyle(blocks[1], 'keyword')"
        @mousedown="startDrag($event, 1)"
      >
        <div class="card-header flex items-center justify-between">
          <div class="card-title flex items-center">
            <span class="text-amber-500 mr-2">💡</span>
            <span>{{ question }}</span>
          </div>
          <div class="card-actions">
            <button @click.stop="emitKeywordExpand(blocks[1])" class="p-1 hover:bg-amber-100 rounded-full transition-colors">
              <span class="text-amber-500">➕</span>
            </button>
          </div>
        </div>
        <div class="card-divider my-2"></div>
        <div class="card-group">
          <button
            v-for="(kw, idx) in keywords"
            :key="idx"
            class="keyword-btn flex items-center justify-center"
            :style="{ width: '100%' }"
            @click="onKeywordClick(kw, idx)"
          >
            <span class="mr-2">#</span>
            <span>{{ kw }}</span>
          </button>
        </div>
      </div>
      <!-- 文本块（可拖动，父子连线，标题和正文分框） -->
      <div
        v-for="(block, idx) in textBlocks"
        :key="block.id"
        class="block-item text-block absolute draggable"
        :style="blockStyle(block, 'text')"
        @mousedown="startDrag($event, idx + 2)"
        @mouseover="hoverIdx = idx + 2"
        @mouseleave="hoverIdx = null"
        @click="selectBlock(idx + 2)"
      >
        <div class="text-block-header flex items-center justify-between bg-emerald-50 p-2 rounded-t-lg">
          <div class="text-block-title font-medium">{{ block.keyword }} 介绍</div>
          <div class="block-actions">
            <button @click.stop="emitTextEdit(block)" class="p-1 hover:bg-emerald-100 rounded-full transition-colors">
              <span class="text-emerald-500">✏️</span>
            </button>
          </div>
        </div>
        <div class="text-block-content-box bg-white p-3 rounded-b-lg overflow-auto" :style="{ maxHeight: contentHeight }">
          <div class="text-block-content text-gray-700" v-html="renderMarkdown(block.intro)"></div>
        </div>
        <!-- 右侧总结按钮 -->
        <div v-if="hoverIdx === idx + 2" class="text-func-btn summary-btn" @click.stop="emitTextSummary(block)">
          <span title="生成总结" class="text-emerald-500">📝</span>
        </div>
        <!-- 下方关键词按钮 -->
        <div v-if="hoverIdx === idx + 2" class="text-func-btn keyword-btn-func" @click.stop="emitTextKeywords(block)">
          <span title="生成关键词" class="text-emerald-500">#️⃣</span>
        </div>
      </div>
      <!-- 关键词块扩展渲染（如由文本块生成） -->
      <div
        v-for="(block, idx) in keywordBlocks"
        :key="block.id"
        class="block-item keyword-card absolute draggable"
        :style="blockStyle(block, 'keyword')"
        @mousedown="startDrag($event, idx + 2 + textBlocks.length)"
        @mouseenter="hoverIdx = idx + 2 + textBlocks.length"
        @mouseleave="hoverIdx = null"
      >
        <div class="card-header flex items-center justify-between">
          <div class="card-title flex items-center">
            <span class="text-amber-500 mr-2">🔍</span>
            <span>{{ block.sourceKeyword || 'AI关键词' }}</span>
          </div>
          <div class="card-actions">
            <button @click.stop="emitKeywordCollapse(block)" class="p-1 hover:bg-amber-100 rounded-full transition-colors">
              <span class="text-amber-500">-</span>
            </button>
          </div>
        </div>
        <div class="card-divider my-2"></div>
        <div class="card-group">
          <button
            v-for="(kw, kidx) in block.keywords"
            :key="kidx"
            class="keyword-btn flex items-center justify-center"
            :style="{ width: '100%' }"
            @click="onKeywordClick(kw, kidx, block.id)"
          >
            <span class="mr-2">#</span>
            <span>{{ kw }}</span>
          </button>
        </div>
      </div>
      <!-- SVG连线（所有关系，曲线） -->
      <svg :width="canvasConfig.width" :height="canvasConfig.height" class="absolute top-0 left-0 pointer-events-none" style="z-index:1">
        <path
          v-for="(rel, idx) in relations"
          :key="'rel-line-' + idx"
          :d="getCurvePath(rel)"
          :stroke="getRelationColor(rel)"
          stroke-width="2"
          fill="none"
          style="pointer-events:none"
          :class="{ 'animate-dash': rel.animated }"
        />
      </svg>
    </div>
  </div>
</template>

<script>
import MarkdownIt from 'markdown-it';
const md = new MarkdownIt({ breaks: true });
let blockId = 1;
export default {
  name: 'BlockCanvas',
  props: {
    question: String,
    keywords: Array,
    textBlocks: Array, // 由父组件传入，包含 {id, keyword, intro, x, y}
    keywordBlocks: Array, // 新增，扩展关键词块
    relations: { type: Array, default: () => [] } // [{sourceId, targetId, type, animated}]
  },
  data() {
    return {
      canvasConfig: {
        width: window.innerWidth,
        height: window.innerHeight
      },
      minBlockWidth: 260,
      minBlockHeight: 60,
      dragging: false,
      dragIdx: null,
      dragOffset: { x: 0, y: 0 },
      blocks: [], // [{x, y}] 问题块、关键词块
      velocities: [], // 用于避让动效
      hoverIdx: null,
      selectedBlock: null,
      contentHeight: 'auto',
      isDraggingCanvas: false,
      canvasDragStart: { x: 0, y: 0 },
      canvasOffset: { x: 0, y: 0 }
    };
  },
  computed: {
    canvasContentStyle() {
      return {
        transform: `translate(${this.canvasOffset.x}px, ${this.canvasOffset.y}px)`
      };
    }
  },
  watch: {
    question: {
      immediate: true,
      handler(val) {
        this.initBlocks();
      }
    },
    keywords: {
      immediate: true,
      handler(val) {
        this.initBlocks();
      }
    },
    textBlocks: {
      deep: true,
      handler(blocks) {
        blocks.forEach(block => {
          if (!block.id) block.id = blockId++;
        });
      }
    },
    keywordBlocks: {
      deep: true,
      handler(blocks) {
        blocks.forEach(block => {
          if (!block.id) block.id = blockId++;
        });
      }
    }
  },
  mounted() {
    window.addEventListener('resize', this.onResize);
    this.startAvoidanceLoop();
    this.setupAnimatedRelations();
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.onResize);
    cancelAnimationFrame(this.avoidanceFrame);
  },
  methods: {
    renderMarkdown(text) {
      return md.render(text || '');
    },
    onResize() {
      this.canvasConfig.width = window.innerWidth;
      this.canvasConfig.height = window.innerHeight;
      this.initBlocks();
    },
    initBlocks() {
      // 问题块居中，关键词块在右侧
      this.blocks = [
        { x: this.canvasConfig.width/2 - this.minBlockWidth/2, y: this.canvasConfig.height/2 - this.minBlockHeight/2 },
        { x: this.canvasConfig.width/2 + this.minBlockWidth, y: this.canvasConfig.height/2 - this.minBlockHeight/2 }
      ];
      this.velocities = [ {x:0,y:0}, {x:0,y:0} ];
    },
    getBlockWidth(type, block) {
      if (type === 'text' && block) {
        // 文本块宽度自适应，最大为1.5倍
        const text = (block.intro || '') + (block.keyword || '');
        const len = Math.max(text.length, 10);
        const px = Math.min(this.minBlockWidth * 1.5, this.minBlockWidth + len * 8);
        return px;
      }
      return this.minBlockWidth;
    },
    getBlockHeight(type, block) {
      if (type === 'text' && block) {
        // 精确计算文本内容高度，避免不必要的滚动条
        const tempDiv = document.createElement('div');
        tempDiv.style.position = 'absolute';
        tempDiv.style.visibility = 'hidden';
        // 减去内边距以获得准确的内容区域宽度
        const contentWidth = this.getBlockWidth('text', block) - 24; // p-3 = 12px * 2
        tempDiv.style.width = `${contentWidth}px`;
        // 应用与实际内容相同的样式
        tempDiv.className = 'text-block-content text-gray-700'; 
        tempDiv.innerHTML = this.renderMarkdown(block.intro || '');
        
        document.body.appendChild(tempDiv);
        const contentHeight = tempDiv.offsetHeight;
        document.body.removeChild(tempDiv);

        const headerHeight = 50; // 估算标题栏高度
        const verticalPadding = 24; // p-3
        const totalHeight = contentHeight + headerHeight + verticalPadding;
        
        return Math.max(this.minBlockHeight, totalHeight);
      }
      if (type === 'keyword') {
        // 关键词块高度：标题(40) + 分割线(16) + 关键词按钮数量 * 按钮高度(32) + 间距(24)
        const keywordCount = this.keywords?.length || 1;
        const buttonHeight = 32; // 每个按钮的高度
        const titleHeight = 40; // 标题区域高度
        const dividerHeight = 16; // 分割线高度
        const padding = 24; // 上下内边距
        return Math.max(this.minBlockHeight, titleHeight + dividerHeight + (keywordCount * buttonHeight) + padding);
      }
      return this.minBlockHeight;
    },
    blockStyle(block, type) {
      const width = this.getBlockWidth(type, block);
      const height = this.getBlockHeight(type, block);
      
      const isSelected = this.selectedBlock === (type === 'question' || type === 'keyword' ? 
        (type === 'question' ? 0 : 1) : 
        this.textBlocks.findIndex(tb => tb.id === block.id) + 2);

      let currentBlock = null;
      if (this.dragIdx !== null) {
        currentBlock = this.getDragBlock();
      }
      const isDraggingThis = this.dragging && block === currentBlock;
      
      return {
        left: block.x + 'px',
        top: block.y + 'px',
        width: width + 'px',
        height: height + 'px',
        zIndex: isSelected ? 30 : (isDraggingThis ? 40 : 10),
        transition: isDraggingThis ? 'none' : 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        boxShadow: isSelected ? '0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)' : 
                               '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        transform: isSelected ? 'scale(1.02)' : 'scale(1)'
      };
    },
    selectBlock(idx) {
      this.selectedBlock = idx;
      // 取消其他块的选中状态
      this.$nextTick(() => {
        const allBlocks = document.querySelectorAll('.block-item');
        allBlocks.forEach((block, i) => {
          block.classList.toggle('active', i === idx);
        });
      });
    },
    onKeywordClick(kw, idx, sourceBlockId = null) {
      // 计算新文本块初始位置（关键词块右侧，避免碰撞）
      let baseX, baseY;
      
      if (sourceBlockId) {
        // 扩展关键词块：找到源块位置
        const sourceBlock = this.keywordBlocks.find(b => b.id === sourceBlockId);
        if (sourceBlock) {
          baseX = sourceBlock.x + this.getBlockWidth('keyword') + 40;
          baseY = sourceBlock.y + idx * (this.minBlockHeight + 20);
        } else {
          // 如果找不到源块，使用默认位置
          baseX = this.blocks[1].x + this.getBlockWidth('keyword') + 40;
          baseY = this.blocks[1].y + idx * (this.minBlockHeight + 20);
        }
      } else {
        // 主关键词块
        baseX = this.blocks[1].x + this.getBlockWidth('keyword') + 40;
        baseY = this.blocks[1].y + idx * (this.minBlockHeight + 20);
      }
      
      // 简单防碰撞：如有重叠则下移
      while (this.textBlocks.some(tb => Math.abs(tb.x - baseX) < this.getBlockWidth('text', tb) && Math.abs(tb.y - baseY) < this.getBlockHeight('text', tb))) {
        baseY += this.minBlockHeight + 20;
      }
      
      // 传递源块ID，用于建立关系
      this.$emit('keyword-click', kw, { x: baseX, y: baseY }, sourceBlockId);
    },
    emitKeywordExpand(blockStyle) {
      this.$emit('keyword-expand', blockStyle);
    },
    emitKeywordCollapse(block) {
      this.$emit('keyword-collapse', block);
    },
    emitTextEdit(block) {
      this.$emit('text-edit', block);
    },
    startDrag(e, idx) {
      this.dragging = true;
      this.dragIdx = idx;
      const block = this.getDragBlock();
      this.dragOffset = {
        x: e.clientX - block.x,
        y: e.clientY - block.y
      };
      
      document.addEventListener('mousemove', this.onDrag);
      document.addEventListener('mouseup', this.stopDrag);
    },
    onDrag(e) {
      if (!this.dragging) return;
      const block = this.getDragBlock(); // 封装获取当前块的逻辑
      // 实时计算位置（去掉防抖/延迟）
      block.x = e.clientX - this.dragOffset.x;
      block.y = e.clientY - this.dragOffset.y;
      // 实时限制画布边界（立即生效）
      this.limitBlockBounds(block);
    },
    stopDrag() {
      this.dragging = false;
      this.dragIdx = null;
      
      document.removeEventListener('mousemove', this.onDrag);
      document.removeEventListener('mouseup', this.stopDrag);
    },
    startCanvasDrag(e) {
      // 只有直接点击画布背景时才触发拖动
      if (e.target !== e.currentTarget) return;

      this.isDraggingCanvas = true;
      this.canvasDragStart = {
        x: e.clientX,
        y: e.clientY
      };
      
      document.addEventListener('mousemove', this.onCanvasDrag);
      document.addEventListener('mouseup', this.stopCanvasDrag);
    },
    onCanvasDrag(e) {
      if (!this.isDraggingCanvas) return;
      const dx = e.clientX - this.canvasDragStart.x;
      const dy = e.clientY - this.canvasDragStart.y;
      
      // 更新画布偏移量
      this.canvasOffset.x += dx;
      this.canvasOffset.y += dy;
      
      // 重置拖动起始点
      this.canvasDragStart.x = e.clientX;
      this.canvasDragStart.y = e.clientY;
    },
    stopCanvasDrag() {
      this.isDraggingCanvas = false;
      document.removeEventListener('mousemove', this.onCanvasDrag);
      document.removeEventListener('mouseup', this.stopCanvasDrag);
    },
    startAvoidanceLoop() {
      const step = () => {
        this.avoidOverlap();
        this.avoidanceFrame = requestAnimationFrame(step);
      };
      this.avoidanceFrame = requestAnimationFrame(step);
    },
    avoidOverlap() {
      // 所有块（blocks+textBlocks+keywordBlocks）两两检测，未被拖动的块自动避让
      const allBlocks = [
        ...this.blocks.map((b, i) => ({ ...b, _idx: i, _type: i < 2 ? 'main' : 'text' })),
        ...this.textBlocks.map((b, i) => ({ ...b, _idx: i + 2, _type: 'text' })),
        ...this.keywordBlocks.map((b, i) => ({ ...b, _idx: i + 2 + this.textBlocks.length, _type: 'keyword' }))
      ];
      
      const velocities = Array(allBlocks.length).fill(0).map(() => ({ x: 0, y: 0 }));
      const repel = 2; // 斥力系数（大幅提升）
      const damping = 0.9; // 阻尼
      
      for (let i = 0; i < allBlocks.length; i++) {
        if (this.dragging && this.dragIdx === i) continue; // 被拖动块不自动避让
        
        let b1 = allBlocks[i];
        let blockType = b1._type === 'main' ? (b1._idx === 0 ? 'question' : 'keyword') : 'text';
        let w1 = this.getBlockWidth(blockType, b1);
        let h1 = this.getBlockHeight(blockType, b1);
        
        for (let j = 0; j < allBlocks.length; j++) {
          if (i === j) continue;
          
          let b2 = allBlocks[j];
          blockType = b2._type === 'main' ? (b2._idx === 0 ? 'question' : 'keyword') : 'text';
          let w2 = this.getBlockWidth(blockType, b2);
          let h2 = this.getBlockHeight(blockType, b2);
          
          // 检查重叠
          if (
            b1.x < b2.x + w2 &&
            b1.x + w1 > b2.x &&
            b1.y < b2.y + h2 &&
            b1.y + h1 > b2.y
          ) {
            // 计算中心距离
            let dx = (b1.x + w1/2) - (b2.x + w2/2);
            let dy = (b1.y + h1/2) - (b2.y + h2/2);
            let dist = Math.sqrt(dx*dx + dy*dy) || 1;
            
            // 斥力与距离成反比，距离越近推力越大
            let force = repel * (w1 + h1 + w2 + h2) / dist;
            velocities[i].x += (dx / dist) * force;
            velocities[i].y += (dy / dist) * force;
          }
        }
      }
      
      // 应用速度和阻尼
      for (let i = 0; i < allBlocks.length; i++) {
        if (this.dragging && this.dragIdx === i) continue;
        
        let b = allBlocks[i];
        let v = velocities[i];
        v.x *= damping;
        v.y *= damping;
        
        b.x += v.x;
        b.y += v.y;
        
        // 限制在画布内
        let blockType = b._type === 'main' ? (b._idx === 0 ? 'question' : 'keyword') : 'text';
        let w = this.getBlockWidth(blockType, b);
        let h = this.getBlockHeight(blockType, b);
        
        b.x = Math.max(0, Math.min(this.canvasConfig.width - w - this.canvasOffset.x, b.x));
        b.y = Math.max(0, Math.min(this.canvasConfig.height - h - this.canvasOffset.y, b.y));
        
        // 写回原数据
        if (b._idx < 2) {
          this.blocks[b._idx].x = b.x;
          this.blocks[b._idx].y = b.y;
        } else if (b._idx < 2 + this.textBlocks.length) {
          this.textBlocks[b._idx - 2].x = b.x;
          this.textBlocks[b._idx - 2].y = b.y;
        } else {
          this.keywordBlocks[b._idx - 2 - this.textBlocks.length].x = b.x;
          this.keywordBlocks[b._idx - 2 - this.textBlocks.length].y = b.y;
        }
      }
    },
    emitTextSummary(block) {
      // 计算新总结块位置（右侧）
      const pos = { x: block.x + this.getBlockWidth('text', block) + 40, y: block.y };
      this.$emit('text-summary', { block, pos });
    },
    emitTextKeywords(block) {
      // 计算新关键词块位置（下方）
      const pos = { x: block.x, y: block.y + this.getBlockHeight('text', block) + 40 };
      this.$emit('text-keywords', { block, pos });
    },
    // 通过ID获取块的完整数据（位置、尺寸、类型）
    getBlockDataById(id) {
      // 问题块
      if (id === 0) {
        const block = this.blocks[0];
        return {
          ...block,
          width: this.getBlockWidth('question', block),
          height: this.getBlockHeight('question', block),
          type: 'question'
        };
      }
      // 主关键词块
      if (id === 1) {
        const block = this.blocks[1];
        return {
          ...block,
          width: this.getBlockWidth('keyword', block),
          height: this.getBlockHeight('keyword', block),
          type: 'keyword'
        };
      }
      // 文本块
      const tb = this.textBlocks.find(b => b.id === id);
      if (tb) {
        return {
          ...tb,
          width: this.getBlockWidth('text', tb),
          height: this.getBlockHeight('text', tb),
          type: 'text'
        };
      }
      // 扩展关键词块
      const kb = this.keywordBlocks.find(b => b.id === id);
      if (kb) {
        return {
          ...kb,
          width: this.getBlockWidth('keyword', kb),
          height: this.getBlockHeight('keyword', kb),
          type: 'keyword'
        };
      }
      return null;
    },
    // 为块计算4个锚点
    getAnchorPoints(blockData) {
      if (!blockData) return [];
      const { x, y, width, height } = blockData;
      const anchorOffset = 10; // 锚点离块边缘的距离
      return [
        { x: x + width / 2, y: y - anchorOffset, pos: 'top' }, // 上
        { x: x + width + anchorOffset, y: y + height / 2, pos: 'right' }, // 右
        { x: x + width / 2, y: y + height + anchorOffset, pos: 'bottom' }, // 下
        { x: x - anchorOffset, y: y + height / 2, pos: 'left' } // 左
      ];
    },
    // 获取关系曲线路径
    getCurvePath(rel) {
      const sourceBlock = this.getBlockDataById(rel.sourceId);
      const targetBlock = this.getBlockDataById(rel.targetId);

      if (!sourceBlock || !targetBlock) return '';

      const sourceAnchors = this.getAnchorPoints(sourceBlock);
      const targetAnchors = this.getAnchorPoints(targetBlock);

      let minDistance = Infinity;
      let closestPair = { from: null, to: null };

      // 找到距离最近的一对锚点
      for (const from of sourceAnchors) {
        for (const to of targetAnchors) {
          const dist = Math.sqrt(Math.pow(from.x - to.x, 2) + Math.pow(from.y - to.y, 2));
          if (dist < minDistance) {
            minDistance = dist;
            closestPair = { from, to };
          }
        }
      }

      const { from, to } = closestPair;
      if (!from || !to) return '';
      
      // 贝塞尔控制点：同时考虑横向和纵向偏移，使曲线更自然
      const dx = to.x - from.x;
      const dy = to.y - from.y;
      const curveFactor = 0.3;
      
      let cx1, cy1, cx2, cy2;

      // 根据锚点位置智能调整控制点
      if (from.pos === 'left' || from.pos === 'right') {
        cx1 = from.x + dx * curveFactor;
        cy1 = from.y;
      } else {
        cx1 = from.x;
        cy1 = from.y + dy * curveFactor;
      }

      if (to.pos === 'left' || to.pos === 'right') {
        cx2 = to.x - dx * curveFactor;
        cy2 = to.y;
      } else {
        cx2 = to.x;
        cy2 = to.y - dy * curveFactor;
      }
      
      return `M${from.x},${from.y} C${cx1},${cy1} ${cx2},${cy2} ${to.x},${to.y}`;
    },
    // 关系颜色
    getRelationColor(rel) {
      if (rel.type === 'parent') return '#10B981';
      if (rel.type === 'related') return '#4F46E5';
      if (rel.type === 'summary') return '#e67c23';
      return '#aaa';
    },
    setupAnimatedRelations() {
      // 为新关系添加动画效果
      this.relations.forEach(rel => {
        if (!rel.animated) {
          rel.animated = true;
          setTimeout(() => {
            rel.animated = false;
          }, 2000);
        }
      });
    },
    // 2. 新增：获取当前拖拽的块
    getDragBlock() {
      if (this.dragIdx < 2) {
        return this.blocks[this.dragIdx];
      } else if (this.dragIdx < 2 + this.textBlocks.length) {
        return this.textBlocks[this.dragIdx - 2];
      } else {
        return this.keywordBlocks[this.dragIdx - 2 - this.textBlocks.length];
      }
    },
    // 3. 新增：实时限制块在画布内
    limitBlockBounds(block) {
      const type = this.dragIdx < 2 
        ? (this.dragIdx === 0 ? 'question' : 'keyword') 
        : 'text';
      const width = this.getBlockWidth(type, block);
      const height = this.getBlockHeight(type, block);
      // 立即修正位置
      block.x = Math.max(0, Math.min(this.canvasConfig.width - width - this.canvasOffset.x, block.x));
      block.y = Math.max(0, Math.min(this.canvasConfig.height - height - this.canvasOffset.y, block.y));
    }
  }
};
</script>

<style scoped>
:root {
  --primary: #4F46E5;
  --primary-light: #F3F4F6;
  --emerald: #10B981;
  --amber: #F59E0B;
}

.block-canvas {
  min-height: 100vh;
  min-width: 100vw;
  width: 100vw;
  height: 100vh;
  box-sizing: border-box;
  position: fixed;
  left: 0;
  top: 0;
  overflow: auto;
  background-image: radial-gradient(#e2e8f0 1px, transparent 1px);
  background-size: 20px 20px;
}
.fullscreen-canvas {
  width: 100vw !important;
  height: 100vh !important;
  min-width: 100vw !important;
  min-height: 100vh !important;
  position: fixed !important;
  left: 0;
  top: 0;
  border-radius: 0 !important;
  box-shadow: none !important;
}
.block-item {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: grab;
  background: white;
  border-radius: 12px;
  padding: 16px;
  user-select: none;
  text-align: center;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  animation: fadeIn 0.5s ease-out;
  will-change: transform, box-shadow;
  z-index: 10;
}
.block-item:active {
  cursor: grabbing;
}
.block-item:hover, .block-item.active, .block-item:focus, .block-item:focus-visible {
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  z-index: 20;
}
.text-block {
  background: #f0fdf4;
  border: 2px solid var(--emerald);
}
.text-block:hover, .text-block.active {
  border-color: #065f46;
  background: #dcfce7;
}
.keyword-card {
  background: #fef3c7;
  border: 2px solid var(--amber);
  padding: 12px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  min-height: fit-content;
}
.keyword-card:hover, .keyword-card.active {
  border-color: #b45309;
  background: #fde68a;
}
.question-block {
  background: var(--primary-light);
  border: 2px solid var(--primary);
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
}
.question-block:hover, .question-block.active {
  border-color: #3730a3;
  background: #e5e7eb;
}
.question-icon {
  font-size: 1.8rem;
  color: var(--primary);
}
.question-content {
  font-size: 1.1rem;
  text-align: left;
  font-weight: 500;
  color: #1f2937;
  flex-grow: 1;
}
.keyword-card {
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(245, 158, 11, 0.1), 0 2px 4px -1px rgba(245, 158, 11, 0.06);
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  padding: 0;
}
.card-title {
  font-weight: 600;
  color: var(--amber);
  font-size: 0.9rem;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-actions button {
  border: none;
  background: transparent;
  outline: none;
  flex-shrink: 0;
}
.card-divider {
  border-bottom: 1px solid currentColor;
  opacity: 0.5;
  margin: 8px 0;
}
.card-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 100%;
}
.keyword-btn {
  background: white;
  color: #92400e;
  border: 1px solid #eab308;
  border-radius: 6px;
  padding: 8px 10px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  font-size: 0.9rem;
}
.keyword-btn:hover {
  background: #fef3c7;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(245, 158, 11, 0.1), 0 2px 4px -1px rgba(245, 158, 11, 0.06);
}
.keyword-btn:active {
  transform: translateY(0);
}
.text-block {
  box-shadow: 0 4px 6px -1px rgba(16, 185, 129, 0.1), 0 2px 4px -1px rgba(16, 185, 129, 0.06);
}
.text-block-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.text-block-title {
  text-align: left;
  font-weight: 600;
  font-size: 1rem;
  color: #065f46;
}
.text-block-content-box {
  border-radius: 8px;
  box-shadow: inset 0 2px 4px 0 rgba(0, 0, 0, 0.05);
  flex-grow: 1;
  overflow-y: auto;
}
.text-block-content {
  text-align: left;
  word-break: break-all;
  font-size: 0.95rem;
  color: #4b5563;
  line-height: 1.5;
}
.text-func-btn {
  position: absolute;
  width: 36px;
  height: 36px;
  background: white;
  border: 2px solid currentColor;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  color: currentColor;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.2s;
  z-index: 100;
  will-change: transform, box-shadow;
}
.summary-btn {
  right: -18px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--emerald);
  border-color: var(--emerald);
}
.keyword-btn-func {
  left: 50%;
  bottom: -18px;
  transform: translateX(-50%);
  color: var(--emerald);
  border-color: var(--emerald);
}
.text-func-btn:hover {
  background: currentColor;
  color: white;
  transform: scale(1.1);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}
.animate-dash {
  stroke-dasharray: 1000;
  stroke-dashoffset: 1000;
  animation: dash 1.5s ease-in-out forwards;
}
@keyframes dash {
  to {
    stroke-dashoffset: 0;
  }
}
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
/* 4. 修复连线层级：强制svg在最底层 */
svg {
  z-index: 1 !important; /* 卡片默认z-index≥10，确保连线在底层 */
}
.canvas-content {
  position: absolute;
  width: 100%;
  height: 100%;
  transition: transform 0.1s linear;
}
</style> 