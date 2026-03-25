<template>
  <div class="guide-page">
    <Header />

    <div class="guide-hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">探索香山，分享每一次心动</h1>
        <p class="hero-subtitle">全网最全的中山游记攻略与本地圈子</p>
        <div class="search-box">
          <a-input-search v-model:value="searchQuery" placeholder="搜索游记、景点、或者是你关心的旅行问题..." size="large"
            enter-button="搜 索" @search="onSearch" />
        </div>
      </div>
    </div>

    <section class="community-section">
      <div class="community-container">
        <a-row :gutter="32">

          <a-col :xs="24" :lg="16">
            <div class="section-header">
              <h2>精选游记</h2>
              <a-button type="link" class="more-btn">发布我的游记 &rarr;</a-button>
            </div>

            <div class="guide-grid">
              <div class="guide-card" v-for="item in guideList" :key="item.id">
                <div class="card-cover">
                  <img :src="item.cover" :alt="item.title" />
                  <div class="location-tag">📍 {{ item.location }}</div>
                </div>
                <div class="card-body">
                  <h3 class="guide-title">{{ item.title }}</h3>
                  <p class="guide-brief">{{ item.brief }}</p>
                  <div class="card-footer">
                    <div class="author-info">
                      <a-avatar :src="item.avatar" size="small" />
                      <span class="author-name">{{ item.author }}</span>
                    </div>
                    <div class="meta-info">
                      <span>👁 {{ item.views }}</span>
                      <span>❤️ {{ item.likes }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </a-col>

          <a-col :xs="24" :lg="8">
            <div class="section-header">
              <h2>热门讨论</h2>
              <a-button type="link" class="more-btn">去交流吧 &rarr;</a-button>
            </div>

            <div class="forum-box">
              <div class="forum-item" v-for="topic in forumList" :key="topic.id">
                <div class="topic-main">
                  <span class="topic-tag" :class="topic.type">{{ topic.tag }}</span>
                  <h4 class="topic-title">{{ topic.title }}</h4>
                </div>
                <p class="topic-desc">{{ topic.desc }}</p>
                <div class="topic-meta">
                  <span class="reply-count">💬 {{ topic.replies }} 条回复</span>
                  <span class="time">{{ topic.time }}</span>
                </div>
              </div>
            </div>

            <div class="ai-poster" @click="openAiChat">
              <div class="ai-poster-content">
                <h3>💡 懒得做攻略？</h3>
                <p>唤醒 AI 伴游，一键生成专属你的中山行程计划！</p>
                <a-button type="primary" shape="round">呼叫 AI 助手</a-button>
              </div>
            </div>
          </a-col>

        </a-row>
      </div>
    </section>

    <div class="ai-fab" @click="openAiChat">
      <div class="fab-icon">✨</div>
      <div class="fab-text">AI 伴游</div>
    </div>

    <a-drawer v-model:visible="aiDrawerVisible" title="✨ 香山 AI 智能伴游" placement="right" :width="400"
      :bodyStyle="{ padding: 0, display: 'flex', flexDirection: 'column', background: '#f8fafc' }">
      <div class="chat-container" ref="chatContainer">
        <div v-for="(msg, index) in chatList" :key="index"
          :class="['chat-bubble-wrapper', msg.role === 'ai' ? 'ai-msg' : 'user-msg']">
          <a-avatar v-if="msg.role === 'ai'" class="chat-avatar" style="background-color: #0ea5e9;">AI</a-avatar>

          <div class="chat-bubble" v-html="msg.content"></div>

          <a-avatar v-if="msg.role === 'user'" class="chat-avatar" src="https://joeschmoe.io/api/v1/random" />
        </div>
        <div v-if="isAiTyping" class="chat-bubble-wrapper ai-msg">
          <a-avatar class="chat-avatar" style="background-color: #0ea5e9;">AI</a-avatar>
          <div class="chat-bubble typing-indicator">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>

      <div class="chat-input-area">
        <div class="quick-prompts">
          <span @click="sendQuickPrompt('帮我规划中山一日游')">中山一日游</span>
          <span @click="sendQuickPrompt('崖口村怎么去？')">崖口村交通</span>
          <span @click="sendQuickPrompt('推荐几家地道早茶')">早茶推荐</span>
        </div>
        <div class="input-wrapper">
          <a-input v-model:value="aiInput" placeholder="告诉 AI 你的旅行想法..." @pressEnter="sendAiMessage" />
          <a-button type="primary" @click="sendAiMessage" :loading="isAiTyping">发送</a-button>
        </div>
      </div>
    </a-drawer>

    <Footer />
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import ImgTourism from '/@/assets/images/tourism.jpg'
import ImgPlace from '/@/assets/images/place.jpg'
import ImgBg2 from '/@/assets/images/bg2.jpg'

// --- 基础状态 ---
const searchQuery = ref('')
const onSearch = () => {
  console.log("搜索:", searchQuery.value)
  // TODO: 对接后台搜索接口
}

// --- 模拟游记数据 ---
const guideList = ref([
  {
    id: 1, title: '周末逃离城市计划：沉醉在崖口村的金色稻浪', brief: '不想走远，就去中山崖口村吧。这里有绝美的海边日落，还有成片成片的稻田...', cover: ImgTourism, location: '南朗崖口村', author: '旅行体验师', avatar: 'https://joeschmoe.io/api/v1/jess', views: 3420, likes: 256
  },
  {
    id: 2, title: '跟着伟人足迹，重走翠亨村（附详细打卡路线）', brief: '作为中山人不可不知的历史，第一次来中山必去的孙中山故居及周边徒步指南。', cover: ImgPlace, location: '翠亨新区', author: '历史寻踪客', avatar: 'https://joeschmoe.io/api/v1/jon', views: 8901, likes: 1024
  },
  {
    id: 3, title: '本地土著吐血整理：石岐老街藏着的地道美食地图', brief: '拒绝网红店！这篇攻略带你钻进石岐的巷子，寻找街坊从小吃到大的宝藏味道。', cover: ImgBg2, location: '石岐老街', author: '贪吃的小胖', avatar: 'https://joeschmoe.io/api/v1/jia', views: 12050, likes: 3200
  }
])

// --- 模拟论坛数据 ---
const forumList = ref([
  { id: 1, type: 'tag-ask', tag: '求助', title: '国庆带父母去中山，住哪个区比较方便？', desc: '父母年纪大了不方便走太多路，希望周围有早茶店，去景点也近...', replies: 24, time: '2小时前' },
  { id: 2, type: 'tag-share', tag: '分享', title: '刚避雷！千万别在这个时候去紫马岭公园！', desc: '给大家提个醒，周末下午四点多停车位完全爆满，建议大家早上早点去。', replies: 89, time: '5小时前' },
  { id: 3, type: 'tag-mate', tag: '约伴', title: '这周末有一起去五桂山大尖山徒步的吗？', desc: '本人女，有一定户外经验，求被捡或者组队，目前已有2人。', replies: 12, time: '昨天' },
  { id: 4, type: 'tag-ask', tag: '求助', title: '除了石岐乳鸽，还有什么能带走做特产的？', desc: '想带点伴手礼回去给同事，咀香园杏仁饼买过了，求推荐别的。', replies: 45, time: '昨天' }
])

// --- AI 助手相关逻辑 ---
const aiDrawerVisible = ref(false)
const aiInput = ref('')
const isAiTyping = ref(false)
const chatContainer = ref(null)

const chatList = ref([
  { role: 'ai', content: '你好！我是你的香山 AI 智能伴游助手。你可以让我帮你规划行程、寻找美食，或者解答任何关于中山旅游的问题。🎒' }
])

const openAiChat = () => {
  aiDrawerVisible.value = true
}

const sendQuickPrompt = (text) => {
  aiInput.value = text
  sendAiMessage()
}

// ⭐ 核心：发送消息并对接 AI 接口的地方
const sendAiMessage = async () => {
  if (!aiInput.value.trim()) return

  const userText = aiInput.value.trim()
  chatList.value.push({ role: 'user', content: userText })
  aiInput.value = ''
  isAiTyping.value = true
  scrollToBottom()

  // -------------------------------------------------------------
  // TODO: 在这里发起你的 Axios 请求，对接后端的 AI 接口
  // const res = await axios.post('/api/ai/chat', { prompt: userText })
  // const aiResponse = res.data.reply
  // -------------------------------------------------------------

  // 这里用 setTimeout 模拟 AI 思考和回复的过程
  setTimeout(() => {
    isAiTyping.value = false

    // 模拟一段流式或者富文本的 AI 回复
    let fakeReply = `好的，关于“<b>${userText}</b>”，我为您提供以下建议：<br/><br/>中山是一座充满魅力的城市，建议您可以从孙中山故居出发，感受历史底蕴；中午前往石岐品尝正宗乳鸽；下午漫步岐江边，享受慢时光。<br/><br/>需要我为您生成详细的时间排期表吗？`

    chatList.value.push({ role: 'ai', content: fakeReply })
    scrollToBottom()
  }, 1500)
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}
</script>

<style scoped lang="less">
/* --- 全局主题与色彩 --- */
@bg-color: #f1f5f9;
@primary-cyan: #0ea5e9;
/* 旅游清新蓝绿调 */
@dark-text: #1e293b;
@light-text: #64748b;

.guide-page {
  background-color: @bg-color;
  min-height: 100vh;
}

/* --- 1. Hero Banner --- */
.guide-hero {
  position: relative;
  padding: 160px 20px 100px;
  background-image: url(/@/assets/images/tourism.jpg);
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;

  .hero-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(15, 23, 42, 0.6);
    /* 深色半透明遮罩 */
  }

  .hero-content {
    position: relative;
    z-index: 2;
    max-width: 800px;
    width: 100%;

    .hero-title {
      font-size: 48px;
      font-weight: 800;
      color: #fff;
      margin-bottom: 16px;
      letter-spacing: 2px;
    }

    .hero-subtitle {
      font-size: 18px;
      color: #cbd5e1;
      margin-bottom: 40px;
    }

    .search-box {
      max-width: 600px;
      margin: 0 auto;

      /* 覆盖 Antd 搜索框默认的主题色为蓝绿色 */
      :deep(.ant-input-search-button) {
        background-color: @primary-cyan;
        border-color: @primary-cyan;
      }
    }
  }
}

/* --- 2. 社区主体内容区 --- */
.community-section {
  padding: 40px 20px 100px;
}

.community-container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h2 {
    font-size: 24px;
    font-weight: 700;
    color: @dark-text;
    margin: 0;
  }

  .more-btn {
    color: @primary-cyan;
    font-weight: 500;
  }
}

/* 游记卡片网格 */
.guide-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.guide-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);

    .card-cover img {
      transform: scale(1.05);
    }
  }

  .card-cover {
    position: relative;
    height: 200px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }

    .location-tag {
      position: absolute;
      bottom: 12px;
      left: 12px;
      background: rgba(0, 0, 0, 0.6);
      color: #fff;
      padding: 4px 10px;
      border-radius: 20px;
      font-size: 12px;
      backdrop-filter: blur(4px);
    }
  }

  .card-body {
    padding: 20px;

    .guide-title {
      font-size: 18px;
      font-weight: 600;
      color: @dark-text;
      margin-bottom: 8px;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .guide-brief {
      font-size: 13px;
      color: @light-text;
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-top: 1px solid #f1f5f9;
      padding-top: 12px;

      .author-info {
        display: flex;
        align-items: center;
        gap: 8px;

        .author-name {
          font-size: 13px;
          color: #475569;
        }
      }

      .meta-info {
        display: flex;
        gap: 12px;
        font-size: 12px;
        color: #94a3b8;
      }
    }
  }
}

/* 热门讨论列表 */
.forum-box {
  background: #fff;
  border-radius: 16px;
  padding: 8px 20px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

.forum-item {
  padding: 16px 0;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: background 0.3s;

  &:last-child {
    border-bottom: none;
  }

  &:hover .topic-title {
    color: @primary-cyan;
  }

  .topic-main {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    margin-bottom: 8px;

    .topic-tag {
      flex-shrink: 0;
      font-size: 12px;
      padding: 2px 6px;
      border-radius: 4px;
      font-weight: bold;

      &.tag-ask {
        background: #e0f2fe;
        color: #0284c7;
      }

      &.tag-share {
        background: #dcfce7;
        color: #0369a1;
      }

      &.tag-mate {
        background: #fef3c7;
        color: #166534;
      }
    }

    .topic-title {
      margin: 0;
      font-size: 15px;
      font-weight: 600;
      color: @dark-text;
      line-height: 1.4;
      transition: color 0.3s;
    }
  }

  .topic-desc {
    font-size: 13px;
    color: @light-text;
    margin-bottom: 12px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .topic-meta {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #94a3b8;
  }
}

/* AI 引导海报 */
.ai-poster {
  background: linear-gradient(135deg, #38bdf8 0%, #0284c7 100%);
  border-radius: 16px;
  padding: 30px 20px;
  color: #fff;
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 20px rgba(2, 132, 199, 0.3);
  }

  h3 {
    color: #fff;
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 10px;
  }

  p {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 20px;
  }
}

/* --- 3. 悬浮 AI 按钮 --- */
.ai-fab {
  position: fixed;
  bottom: 40px;
  right: 40px;
  background: linear-gradient(135deg, #0ea5e9, #3b82f6);
  color: #fff;
  padding: 12px 24px;
  border-radius: 30px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 10px 25px rgba(14, 165, 233, 0.4);
  cursor: pointer;
  z-index: 1000;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

  &:hover {
    transform: scale(1.05) translateY(-5px);
    box-shadow: 0 15px 35px rgba(14, 165, 233, 0.5);
  }

  .fab-icon {
    font-size: 20px;
  }

  .fab-text {
    font-size: 16px;
    font-weight: bold;
    letter-spacing: 1px;
  }
}

/* --- 4. 抽屉内聊天 UI --- */
.chat-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chat-bubble-wrapper {
  display: flex;
  gap: 12px;
  max-width: 90%;

  &.user-msg {
    align-self: flex-end;
    flex-direction: row;

    .chat-bubble {
      background-color: @primary-cyan;
      color: #fff;
      border-radius: 16px 4px 16px 16px;
    }
  }

  &.ai-msg {
    align-self: flex-start;

    .chat-bubble {
      background-color: #fff;
      color: @dark-text;
      border-radius: 4px 16px 16px 16px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
      line-height: 1.6;
    }
  }
}

.chat-bubble {
  padding: 12px 16px;
  font-size: 14px;
  word-wrap: break-word;
}

/* 输入区 */
.chat-input-area {
  background: #fff;
  padding: 16px;
  border-top: 1px solid #e2e8f0;

  .quick-prompts {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;
    overflow-x: auto;

    span {
      white-space: nowrap;
      background: #f1f5f9;
      color: @primary-cyan;
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 12px;
      cursor: pointer;
      transition: background 0.2s;

      &:hover {
        background: #e0f2fe;
      }
    }
  }

  .input-wrapper {
    display: flex;
    gap: 8px;
  }
}

/* 打字加载动画 */
.typing-indicator {
  display: flex;
  gap: 4px;
  align-items: center;
  padding: 16px !important;

  span {
    display: block;
    width: 6px;
    height: 6px;
    background-color: #94a3b8;
    border-radius: 50%;
    animation: typing 1.4s infinite ease-in-out both;

    &:nth-child(1) {
      animation-delay: -0.32s;
    }

    &:nth-child(2) {
      animation-delay: -0.16s;
    }
  }
}

@keyframes typing {

  0%,
  80%,
  100% {
    transform: scale(0);
  }

  40% {
    transform: scale(1);
  }
}

@media (max-width: 992px) {
  .ai-fab {
    bottom: 20px;
    right: 20px;
  }
}
</style>