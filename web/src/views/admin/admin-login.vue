<template>
  <div class="login-container">
    <div class="bg-overlay"></div>
    <div class="login-box">
      <div class="brand-side hidden-mobile">
        <div class="brand-content">
          <img src="../../assets/images/logo1.png" alt="logo" class="brand-logo">
          <h1 class="brand-title">旅游景区管理系统</h1>
          <p class="brand-slogan">登录管理后台，高效管理景区资源，提升游客沉浸式体验。</p>
        </div>
      </div>

      <div class="form-side">
        <div class="form-header">
          <h2>管理后台</h2>
          <p>请输入您的管理员账号密码</p>
        </div>

        <div class="login-tab">
          <div class="tab-item active">
            <span>管理员登录</span>
            <span class="tab-line"></span>
          </div>
        </div>

        <div class="form-body">
          <div class="input-group">
            <div class="input-icon-wrapper">
              <img src="../../assets/images/mail-icon.svg" class="input-icon">
            </div>
            <div class="input-content">
              <input placeholder="请输入管理员账号" v-model="data.loginForm.username" type="text" class="zs-input"
                @keyup.enter="handleLogin">
            </div>
          </div>

          <div class="input-group">
            <div class="input-icon-wrapper">
              <img src="../../assets/images/pwd-icon.svg" class="input-icon">
            </div>
            <div class="input-content">
              <input placeholder="请输入密码" v-model="data.loginForm.password" type="password" class="zs-input"
                @keyup.enter="handleLogin">
            </div>
          </div>

          <button class="zs-btn primary-btn" @click="handleLogin" :disabled="loginBtn">
            {{ loginBtn ? '登 录 中...' : '登 录' }}
          </button>

          <div class="form-footer">
            <router-link to="/index/login" class="link-btn">返回游客登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '/@/store';
import { message } from "ant-design-vue";

const router = useRouter();
const userStore = useUserStore();
const loginBtn = ref<boolean>(false);

const data = reactive({
  loginForm: { username: '', password: '' }
});

const handleLogin = () => {
  if (!data.loginForm.username || !data.loginForm.password) {
    message.warning('请填写完整信息');
    return;
  }
  loginBtn.value = true;
  userStore.adminLogin({
    username: data.loginForm.username,
    password: data.loginForm.password
  }).then(() => {
    router.push({ path: '/admin' });
    message.success('管理员登录成功！');
  }).catch(err => {
    message.warn(err.msg || '登录失败');
  }).finally(() => {
    loginBtn.value = false;
  });
};
</script>

<style scoped lang="less">
/* 此处保留你之前的 CSS 样式，无需更改 */
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
}

.brand-logo {
  width: 64px;
  height: 64px;
  margin-bottom: 32px;
}

.brand-title {
  font-size: 36px;
  color: #fff;
  font-weight: 700;
  margin-bottom: 20px;
  font-family: 'Noto Serif SC', serif;
}

.brand-slogan {
  font-size: 16px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.8);
}

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
  }

  p {
    font-size: 14px;
    color: @text-light;
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
      transition: width 0.3s;
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

  &:focus-within {
    border-color: @zs-green;
    background: #fff;
  }

  .input-icon {
    width: 24px;
    margin-right: 12px;
  }

  .zs-input {
    width: 100%;
    border: none;
    outline: none;
    background: transparent;
  }
}

.zs-btn {
  height: 50px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.3s;

  &.primary-btn {
    background: @zs-green;
    color: #fff;

    &:hover {
      background: @zs-green-hover;
    }

    &:disabled {
      background: #94a3b8;
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
    text-decoration: none;

    &:hover {
      color: @zs-yellow;
    }
  }
}
</style>