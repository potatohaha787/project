<template>
  <div class="zhongshan-home">
    <header :class="['main-bar-view', { 'is-scrolled': isScrolled }]">
      <div class="logo" @click="$router.push({name:'portal'})">
        <img v-if="logoImage" :src="logoImage" class="logo-icon">
        <span class="logo-text">中山文旅信息平台</span>
      </div>

      <nav class="main-nav hidden-sm">
        <a class="nav-item active">首页</a>
        <a class="nav-item">景点探索</a>
        <a class="nav-item">非遗文化</a>
        <a class="nav-item">香山美食</a>
        <a class="nav-item">游记攻略</a>
      </nav>

      <div class="right-view">
        <div class="search-entry">
          <img :src="SearchIcon" class="search-icon">
          <input placeholder="搜索中山景点/美食/文化..." ref="keywordRef" @keyup.enter="search" />
        </div>

        <div class="right-icon" @click="msgVisible=true">
          <img :src="MessageIcon" class="icon-filter">
          <span class="msg-point" v-if="msgData.length > 0"></span>
        </div>

        <template v-if="userStore.user_token">
          <a-dropdown>
            <a class="ant-dropdown-link" @click="e => e.preventDefault()">
              <img :src="AvatarIcon" class="self-img" >
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item><a @click="goUserCenter('orderView')">订单中心</a></a-menu-item>
                <a-menu-item><a @click="goUserCenter('userInfoEditView')">个人设置</a></a-menu-item>
                <a-menu-item><a @click="quit()">退出登录</a></a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </template>
        <template v-else>
          <button class="login-btn hidden-sm" @click="goLogin()">登录 / 注册</button>
        </template>
      </div>
    </header>

    <section class="hero-banner">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">伟人故里 · 魅力中山</h1>
        <p class="hero-subtitle">探寻孙中山故居的厚重，体验岭南水乡的温婉，品味地道香山美食</p>
        <div class="hero-actions">
          <button class="btn-primary">开启旅程</button>
          <button class="btn-secondary">查看热门路线</button>
        </div>
      </div>
    </section>

    <a-drawer
        title="我的消息"
        placement="right"
        :closable="true"
        :maskClosable="true"
        :visible="msgVisible"
        @close="onClose"
    >
      <a-spin :spinning="loading" style="min-height: 200px;">
        <div class="list-content">
          <div class="notification-item flex-view" v-for="item in msgData" :key="item.id">
            <div class="content-box">
              <div class="header">
                <span class="title-txt">{{item.title}}</span>
                <span class="time">{{ item.create_time }}</span>
              </div>
              <div class="content">
                <p>{{ item.content }}</p>
              </div>
            </div>
          </div>
          <div v-if="msgData.length === 0" class="empty-msg">暂无最新消息</div>
        </div>
      </a-spin>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
// 假设这是你原本的依赖路径
import { listApi } from '/@/api/notice'
import { useUserStore } from "/@/store";

import logoImage from '/@/assets/images/logo1.png';
import SearchIcon from '/@/assets/images/search-icon.svg';
import AvatarIcon from '/@/assets/images/avatar.jpg';
import MessageIcon from '/@/assets/images/message-icon.svg';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const keywordRef = ref();
let loading = ref(false);
let msgVisible = ref(false);
let msgData = ref([] as any);
let isScrolled = ref(false); // 控制导航栏吸顶变色

onMounted(() => {
  getMessageList();
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50;
};

const getMessageList = () => {
  loading.value = true;
  listApi({}).then(res => {
    msgData.value = res.data || [];
    loading.value = false;
  }).catch(err => {
    console.log(err);
    loading.value = false;
  });
};

const search = () => {
  const keyword = keywordRef.value.value;
  if (route.name === 'search') {
    router.push({ name: 'search', query: { keyword: keyword } });
  } else {
    let text = router.resolve({ name: 'search', query: { keyword: keyword } });
    window.open(text.href, '_blank');
  }
};

const goLogin = () => router.push({ name: 'login' });
const goUserCenter = (menuName) => router.push({ name: menuName });
const quit = () => {
  userStore.logout().then(() => {
    router.push({ name: 'portal' });
  });
};
const onClose = () => { msgVisible.value = false; };
</script>

<style scoped lang="less">
/* === 全局与变量 === */
@primary-color: #0b6a65; /* 岭南青 */
@accent-color: #c59d5f; /* 香山金 */
@text-dark: #1f2937;
@text-light: #ffffff;

.zhongshan-home {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  min-height: 100vh;
  background-color: #f8fafc;
}

/* === 顶部导航栏 === */
.main-bar-view {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 72px;
  padding: 0 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 999;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(to bottom, rgba(0,0,0,0.6) 0%, rgba(0,0,0,0) 100%);
  color: @text-light;
  
  /* 滚动后的样式 */
  &.is-scrolled {
    background: #ffffff;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    color: @text-dark;
    height: 64px;

    .logo-text { color: @primary-color; }
    .nav-item { color: @text-dark; }
    .nav-item:hover, .nav-item.active { color: @primary-color; }
    .search-entry { background: #f1f5f9; }
    .search-entry input { color: @text-dark; }
    .icon-filter { filter: invert(0); } /* 如果图标是白色的，可以通过滤镜变黑，具体视素材而定 */
  }
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  
  .logo-icon {
    width: 36px;
    height: 36px;
    margin-right: 12px;
  }
  
  .logo-text {
    font-size: 22px;
    font-weight: 600;
    font-family: 'Noto Serif SC', 'SimSun', serif; /* 增加文化气息的衬线体 */
    letter-spacing: 2px;
  }
}

.main-nav {
  display: flex;
  gap: 32px;
  font-size: 16px;
  font-weight: 500;
  
  .nav-item {
    cursor: pointer;
    position: relative;
    padding: 8px 0;
    transition: color 0.3s;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      width: 0;
      height: 2px;
      background-color: @accent-color;
      transition: all 0.3s;
      transform: translateX(-50%);
    }
    
    &:hover::after, &.active::after {
      width: 100%;
    }
  }
}

.right-view {
  display: flex;
  align-items: center;
  gap: 24px;
}

/* 搜索框 */
.search-entry {
  display: flex;
  align-items: center;
  width: 260px;
  height: 38px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(4px);
  border-radius: 20px;
  padding: 0 16px;
  transition: all 0.3s;

  &:focus-within {
    background: #ffffff;
    box-shadow: 0 0 0 2px rgba(11, 106, 101, 0.2);
    input { color: @text-dark; }
  }

  .search-icon {
    width: 16px;
    margin-right: 8px;
    opacity: 0.8;
  }
  
  input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    color: inherit;
    font-size: 14px;
    
    &::placeholder {
      color: inherit;
      opacity: 0.7;
    }
  }
}

/* 图标与头像 */
.right-icon {
  position: relative;
  cursor: pointer;
  
  img {
    width: 24px;
    height: 24px;
    transition: transform 0.2s;
    &:hover { transform: scale(1.1); }
  }
  
  .msg-point {
    position: absolute;
    top: -2px;
    right: -4px;
    width: 8px;
    height: 8px;
    background: #ef4444; /* 红色提示点 */
    border-radius: 50%;
    border: 2px solid #fff;
  }
}

.self-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.login-btn {
  background: @primary-color;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 8px 24px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s, transform 0.2s;
  
  &:hover {
    background: darken(@primary-color, 10%);
    transform: translateY(-1px);
  }
}

/* === 沉浸式 Hero Banner === */
.hero-banner {
  position: relative;
  height: 80vh; /* 占据屏幕80%高度 */
  min-height: 600px;
  /* 请替换为实际的中山风景图（如孙中山故居、岐江等） */
  background-image: url('/@/assets/images/bg.jpg'); 
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
    background: linear-gradient(rgba(0,0,0,0.3), rgba(0,0,0,0.6));
  }
  
  .hero-content {
    position: relative;
    z-index: 10;
    color: #fff;
    padding: 0 20px;
    animation: fadeInDown 1s ease-out;
  }
  
  .hero-title {
    font-size: 56px;
    font-weight: 700;
    font-family: 'Noto Serif SC', serif;
    margin-bottom: 24px;
    letter-spacing: 4px;
    text-shadow: 0 4px 12px rgba(0,0,0,0.5);
  }
  
  .hero-subtitle {
    font-size: 20px;
    margin-bottom: 48px;
    font-weight: 300;
    letter-spacing: 2px;
    text-shadow: 0 2px 8px rgba(0,0,0,0.5);
  }
  
  .hero-actions {
    display: flex;
    gap: 24px;
    justify-content: center;
    
    button {
      padding: 14px 36px;
      font-size: 16px;
      border-radius: 30px;
      cursor: pointer;
      transition: all 0.3s;
      font-weight: 500;
    }
    
    .btn-primary {
      background: @primary-color;
      color: #fff;
      border: 2px solid @primary-color;
      &:hover { background: transparent; border-color: #fff; }
    }
    
    .btn-secondary {
      background: transparent;
      color: #fff;
      border: 2px solid #fff;
      backdrop-filter: blur(4px);
      &:hover { background: #fff; color: @text-dark; }
    }
  }
}

/* === 消息列表样式优化 === */
.notification-item {
  padding: 16px 0;
  border-bottom: 1px dashed #e2e8f0;
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    
    .title-txt {
      color: @primary-color;
      font-weight: 600;
      font-size: 15px;
    }
    .time {
      color: #94a3b8;
      font-size: 12px;
    }
  }
  
  .content {
    color: #475569;
    font-size: 14px;
    line-height: 1.6;
  }
}

.empty-msg {
  text-align: center;
  color: #94a3b8;
  padding: 40px 0;
}

/* 动画 */
@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-30px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式调整 */
@media (max-width: 992px) {
  .hidden-sm { display: none !important; }
  .main-bar-view { padding: 0 20px; }
  .hero-title { font-size: 40px; }
  .hero-subtitle { font-size: 16px; }
}
</style>