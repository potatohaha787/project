<template>
  <div :class="['main-bar-view', { 'is-home-transparent': isHome && !isScrolled, 'is-solid': !isHome || isScrolled }]">
    <div class="logo">
      <img :src="logoImage" @click="$router.push({ name: 'portal' })">
      <span class="logo-text">中山文旅</span>
    </div>

    <nav class="main-nav hidden-sm">
      <a class="nav-item" :class="{ 'active': route.name === 'portal' }"
        @click="$router.push({ name: 'portal' })">首页</a>
      <a class="nav-item" :class="{ 'active': route.name === 'history' }" @click="goUserCenter('history')">香山纪事</a>
      <a class="nav-item" :class="{ 'active': route.name === 'celebrity' || route.name === 'celebrityDetail' }"
        @click="goUserCenter('celebrity')">名人故事</a>
      <a class="nav-item" :class="{ 'active': route.name === 'scenic' }" @click="goUserCenter('scenic')">景点探索</a>
      <a class="nav-item" :class="{ 'active': route.name === 'heritage' || route.name === 'heritageDetail' }"
        @click="goUserCenter('heritage')">非遗文化</a>
      <a class="nav-item" :class="{ 'active': route.name === 'food' }" @click="goUserCenter('food')">香山美食</a>
      <a class="nav-item" :class="{ 'active': route.name === 'guide' }" @click="goUserCenter('guide')">游记攻略</a>
    </nav>

    <div class="search-entry">
      <img :src="SearchIcon" class="search-icon">
      <input v-model="keyword" placeholder="搜索中山景点" @keyup.enter="search" />
    </div>

    <div class="right-view">
      <template v-if="userStore.user_token">
        <a-dropdown>
          <a class="ant-dropdown-link" @click="e => e.preventDefault()">
            <img :src="userAvatar" class="self-img">
          </a>
          <template #overlay>
            <a-menu>
              <a-menu-item><a @click="goUserCenter('userInfoEditView')">个人设置</a></a-menu-item>
              <a-menu-item><a @click="quit()">退出</a></a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </template>
      <template v-else>
        <button class="login btn hidden-sm" @click="goLogin()">登录 / 注册</button>
      </template>

      <div class="right-icon" @click="msgVisible = true">
        <img :src="MessageIcon" class="icon-filter">
        <span class="msg-point" v-if="msgData.length > 0"></span>
      </div>

      <a-drawer title="我的消息" placement="right" :closable="true" :maskClosable="true" :visible="msgVisible"
        @close="onClose">
        <div class="msg-drawer-list">
          <div v-if="msgData.length === 0" style="text-align: center; color: #94a3b8; padding-top: 20px;">
            暂无消息
          </div>
          <div class="msg-item" v-for="item in msgData" :key="item.id">
            <div class="msg-item-header">
              <span class="msg-item-title" :class="item.type">{{ item.title }}</span>
              <span class="msg-item-time">{{ item.time }}</span>
            </div>
            <div class="msg-item-content">{{ item.content }}</div>
          </div>
        </div>
      </a-drawer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { listApi } from '/@/api/notice';
import { get } from '/@/utils/http/axios';
import { useUserStore } from "/@/store";
import logoImage from '/@/assets/images/logo1.png';
import SearchIcon from '/@/assets/images/search-icon.svg';
import AvatarIcon from '/@/assets/images/avatar.jpg';
import MessageIcon from '/@/assets/images/message-icon.svg';
import { BASE_URL } from '/@/store/constants';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const keyword = ref();
let loading = ref(false);
let msgVisible = ref(false);
let msgData = ref([] as any);
let isScrolled = ref(false);

const isHome = computed(() => route.name === 'portal');
const userAvatar = ref(AvatarIcon);

const getMessageList = async () => {
  const userId = userStore.user_id;
  if (!userId) return;

  try {
    // 1. 获取系统通知
    const resNotice = await listApi({});
    const notices = (resNotice.data || []).map((item: any) => ({
      id: 'notice_' + item.id,
      title: item.title || '系统通知',
      content: item.content,
      time: item.createTime,
      type: 'notice'
    }));

    // 2. 获取别人对我文章的回复
    const resComments = await get({ url: '/api/postComment/myMessages', params: { userId: userId } });
    const comments = (resComments.data || []).map((item: any) => ({
      id: 'comment_' + item.id,
      title: '收到的回复',
      content: `${item.nickname || item.username || '热心网友'} 评论了你的文章《${item.title}》`,
      time: item.commentTime,
      type: 'comment'
    }));

    // 3. 合并并按时间倒序
    const combined = [...notices, ...comments];
    combined.sort((a: any, b: any) => new Date(b.time).getTime() - new Date(a.time).getTime());

    msgData.value = combined;
  } catch (err) {
    console.error('获取消息失败', err);
  }
};

// 确保仅有一个 onMounted，处理消息请求、滚动事件和头像加载
onMounted(() => {
  if (userStore.user_token) {
    getMessageList();
  }
  window.addEventListener('scroll', handleScroll);

  // 读取并拼接真实头像
  const savedAvatar = localStorage.getItem('user_avatar') || localStorage.getItem('avatar');
  if (savedAvatar) {
    if (!savedAvatar.startsWith('http')) {
      userAvatar.value = BASE_URL + '/api/staticfiles/avatar/' + savedAvatar;
    } else {
      userAvatar.value = savedAvatar;
    }
  }
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50;
};

const search = () => {
  if (keyword.value && keyword.value.trim()) {
    router.push({
      name: 'search',
      query: { keyword: keyword.value }
    });
  }
};
const goLogin = () => { router.push({ name: 'login' }) };
// 跳转页面的通用方法
const goUserCenter = (menuName: string) => { router.push({ name: menuName }) };
const quit = () => { userStore.logout().then(res => { router.push({ name: 'portal' }) }) };
const onClose = () => { msgVisible.value = false; };
</script>

<style scoped lang="less">
@primary-color: #0b6a65;
@accent-color: #c59d5f;

//固定定位的导航栏容器
.main-bar-view {
  position: fixed;
  top: 0;
  left: 0;
  height: 64px;
  width: 100%;
  padding: 0 48px;
  z-index: 999;
  display: flex;
  flex-direction: row;
  align-items: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* === 状态1：首页顶部的透明模式 === */
.is-home-transparent {
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0) 100%);
  color: #fff;
  border-bottom: none;

  .logo-text {
    color: #fff;
  }

  .nav-item {
    color: #fff;
  }

  .search-entry {
    background: rgba(255, 255, 255, 0.2);

    input {
      color: #fff;
    }

    input::placeholder {
      color: rgba(255, 255, 255, 0.8);
    }
  }

  .icon-filter {
    filter: brightness(0) invert(1);
  }
}

/* === 状态2：滚动后或在其他页面的实心模式 === */
.is-solid {
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  color: #152844;

  .logo-text {
    color: @primary-color;
  }

  .nav-item {
    color: #152844;
  }

  .nav-item:hover,
  .nav-item.active {
    color: @primary-color;
  }

  .search-entry {
    background: #ecf3fc;

    input {
      color: #000;
    }
  }

  .icon-filter {
    filter: none;
  }
}

/* 以下是基础排版样式 */
.logo {
  display: flex;
  align-items: center;
  margin-right: 48px;

  img {
    width: 32px;
    height: 32px;
    cursor: pointer;
    margin-right: 8px;
  }

  .logo-text {
    font-size: 20px;
    font-weight: 600;
    font-family: 'Noto Serif SC', serif;
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

    &:hover::after,
    &.active::after {
      width: 100%;
    }
  }
}

.search-entry {
  position: relative;
  margin-left: 130px;
  width: 300px;
  height: 36px;
  border-radius: 18px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  transition: all 0.3s;

  .search-icon {
    width: 16px;
    margin-right: 8px;
  }

  input {
    flex: 1;
    border: none;
    outline: none;
    background: transparent;
    font-size: 14px;
  }
}

.right-view {
  flex: 1;
  display: flex;
  flex-direction: row;
  gap: 24px;
  justify-content: flex-end;
  align-items: center;

  .self-img {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    cursor: pointer;
  }

  .right-icon {
    position: relative;
    width: 24px;
    cursor: pointer;
  }

  .msg-point {
    position: absolute;
    right: -4px;
    top: -2px;
    width: 8px;
    height: 8px;
    background: #ef4444;
    border-radius: 50%;
    border: 2px solid #fff;
  }

  .btn {
    background: @primary-color;
    font-size: 14px;
    color: #fff;
    border-radius: 32px;
    width: 88px;
    height: 32px;
    text-align: center;
    border: none;
    cursor: pointer;
  }
}

/* 消息弹窗内的样式 */
.msg-drawer-list {
  .msg-item {
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    .msg-item-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 4px;

      .msg-item-title {
        font-weight: 600;
        font-size: 14px;

        &.notice {
          color: #1677ff;
        }

        &.comment {
          color: #d97706;
        }
      }

      .msg-item-time {
        font-size: 12px;
        color: #94a3b8;
      }
    }

    .msg-item-content {
      font-size: 13px;
      color: #475569;
      line-height: 1.5;
    }
  }
}
</style>