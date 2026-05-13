<template>
  <div class="login-container">
    <div class="bg-overlay"></div>

    <div class="login-box">
      <div class="brand-side hidden-mobile">
        <div class="brand-content">
          <img :src="LogoIcon" alt="logo" class="brand-logo">
          <h1 class="brand-title">探索香山之美</h1>
          <p class="brand-slogan">登录您的账号，开启中山文旅沉浸式体验之旅，发现更多人文与风景。</p>
        </div>
      </div>

      <div class="form-side">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>请输入您的账号密码</p>
        </div>

        <div class="login-tab">
          <div class="tab-item active">
            <span>登录</span>
            <span class="tab-line"></span>
          </div>
        </div>

        <div class="form-body">
          <div class="input-group">
            <div class="input-icon-wrapper">
              <img :src="MailIcon" class="input-icon">
            </div>
            <div class="input-content">
              <input placeholder="请输入注册账号" v-model="pageData.loginForm.username" type="text" class="zs-input"
                @keyup.enter="handleLogin">
            </div>
          </div>

          <div class="input-group">
            <div class="input-icon-wrapper">
              <img :src="PwdIcon" class="input-icon">
            </div>
            <div class="input-content">
              <input placeholder="请输入密码" v-model="pageData.loginForm.password" type="password" class="zs-input"
                @keyup.enter="handleLogin">
            </div>
          </div>

          <button class="zs-btn primary-btn" @click="handleLogin">登 录</button>

          <div class="form-footer">
            <a @click="handleCreateUser" class="link-btn">注册新账号</a>
            <router-link to="/adminLogin" class="link-btn">管理员登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '/@/store';
import { message } from "ant-design-vue";
import LogoIcon from '/@/assets/images/logo1.png';
import MailIcon from '/@/assets/images/mail-icon.svg';
import PwdIcon from '/@/assets/images/pwd-icon.svg';

const router = useRouter();
const userStore = useUserStore();

const pageData = reactive({
  loginForm: {
    username: '',
    password: ''
  }
})

const handleLogin = () => {
  if (!pageData.loginForm.username) {
    message.warning('请输入账号')
    return
  }
  if (!pageData.loginForm.password) {
    message.warning('请输入密码')
    return
  }

  userStore.login({
    username: pageData.loginForm.username,
    password: pageData.loginForm.password
  }).then(res => {
    loginSuccess()
  }).catch(err => {
    message.warn(err.msg || '登录失败')
  })
}

const handleCreateUser = () => {
  router.push({ name: 'register' })
}

const loginSuccess = () => {
  router.push({ name: 'portal' })
  message.success('登录成功，欢迎回来！')
}
</script>

<style scoped lang="less">
/* 中山文旅主题色 */
@zs-green: #0b6a65;
@zs-green-hover: #09504c;
@zs-yellow: #c59d5f;
@text-main: #1e293b;
@text-light: #64748b;

.login-container {
  background-image: url('../../assets/images/bg2.jpg');
  background-size: cover;
  background-position: center;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
}

.login-box {
  position: relative;
  z-index: 10;
  display: flex;
  width: 900px;
  height: 520px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

/* 左侧品牌区 */
.brand-side {
  flex: 1;
  background: linear-gradient(135deg, @zs-green 0%, #064e3b 100%);
  color: #fff;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 60%);
    pointer-events: none;
  }

  .brand-content {
    position: relative;
    z-index: 2;
  }

  .brand-logo {
    width: 64px;
    height: 64px;
    margin-bottom: 32px;
  }

  .brand-title {
    font-size: 36px;
    font-weight: 700;
    color: #fff;
    margin-bottom: 20px;
    font-family: 'Noto Serif SC', serif;
  }

  .brand-slogan {
    font-size: 16px;
    line-height: 1.8;
    color: rgba(255, 255, 255, 0.8);
  }
}

/* 右侧表单区 */
.form-side {
  width: 440px;
  padding: 50px 50px;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.form-header {
  margin-bottom: 30px;

  h2 {
    font-size: 28px;
    font-weight: 600;
    color: @text-main;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 14px;
    color: @text-light;
    margin: 0;
  }
}

.login-tab {
  display: flex;
  border-bottom: 1px solid #e2e8f0;
  margin-bottom: 30px;

  .tab-item {
    padding: 0 10px 12px;
    font-size: 16px;
    color: @text-light;
    position: relative;

    &.active {
      color: @zs-green;
      font-weight: 600;

      .tab-line {
        width: 100%;
      }
    }

    .tab-line {
      position: absolute;
      bottom: -1px;
      left: 0;
      width: 0;
      height: 3px;
      background: @zs-green;
      border-radius: 3px;
      transition: width 0.3s ease;
    }
  }
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.input-group {
  display: flex;
  align-items: center;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 0 16px;
  height: 50px;
  transition: all 0.3s;

  &:focus-within {
    border-color: @zs-green;
    background: #fff;
    box-shadow: 0 0 0 3px rgba(11, 106, 101, 0.1);
  }

  .input-icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;

    .input-icon {
      width: 24px;
    }
  }

  .input-content {
    flex: 1;

    .zs-input {
      width: 100%;
      height: 100%;
      border: none;
      background: transparent;
      outline: none;
      font-size: 15px;
      color: @text-main;

      &::placeholder {
        color: #94a3b8;
      }
    }
  }
}

.zs-btn {
  height: 50px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  outline: none;
  transition: all 0.3s;
  margin-top: 8px;

  &.primary-btn {
    background: @zs-green;
    color: #fff;

    &:hover {
      background: @zs-green-hover;
      box-shadow: 0 6px 15px rgba(11, 106, 101, 0.2);
      transform: translateY(-1px);
    }

    &:active {
      transform: translateY(1px);
    }
  }
}

.form-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;

  .link-btn {
    font-size: 14px;
    color: @text-light;
    cursor: pointer;
    transition: color 0.3s;
    text-decoration: none;
    /* 去除可能出现的下划线 */

    &:hover {
      color: @zs-yellow;
    }
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .login-box {
    width: 90%;
    height: auto;
    flex-direction: column;
  }

  .hidden-mobile {
    display: none;
  }

  .form-side {
    width: 100%;
    padding: 40px 30px;
  }
}
</style>