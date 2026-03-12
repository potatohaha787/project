<template>
  <div class="portal">
    <Header />

    <section class="hero-banner">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">伟人故里 · 魅力中山</h1>
        <p class="hero-subtitle">探寻孙中山故居的厚重，体验岭南水乡的温婉，品味地道香山美食</p>
        <div class="hero-actions">
          <button class="btn-primary" @click="scrollToContent">开启旅程</button>
        </div>
      </div>
    </section>

    <div class="portal-content" id="portal-main-content">
      <Content />
    </div>

    <Footer />
  </div>
</template>

<script>
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import Content from '/@/views/index/components/content.vue'

export default {
  components: {
    Footer,
    Header,
    Content
  },
  data() {
    return {}
  },
  methods: {
    scrollToContent() {
      const el = document.getElementById('portal-main-content');
      if (el) {
        window.scrollTo({
          top: el.offsetTop - 64,
          behavior: 'smooth'
        });
      }
    }
  }
}
</script>

<style scoped lang="less">
/* === 沉浸式 Hero Banner === */
.hero-banner {
  position: relative;
  height: 85vh;
  /* 占据屏幕85%高度 */
  min-height: 500px;
  background-image: url(/@/assets/images/bg.jpg);
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
    background: linear-gradient(rgba(0, 0, 0, 0.2), rgba(49, 44, 44, 0.6));
  }

  .hero-content {
    position: relative;
    z-index: 10;
    color: #fff;
    padding: 0 20px;
    animation: fadeInDown 1.2s ease-out;
  }

  .hero-title {
    font-size: 56px;
    font-weight: 700;
    font-family: 'Noto Serif SC', 'SimSun', serif;
    margin-bottom: 24px;
    letter-spacing: 4px;
    color: #c59d5f;
  }

  .hero-subtitle {
    font-size: 20px;
    margin-bottom: 48px;
    font-weight: 300;
    letter-spacing: 2px;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  }

  .hero-actions {
    display: flex;
    justify-content: center;

    .btn-primary {
      padding: 14px 40px;
      font-size: 16px;
      border-radius: 30px;
      cursor: pointer;
      transition: all 0.3s;
      font-weight: 500;
      background: #0b6a65;
      color: #fff;
      border: 2px solid #0b6a65;
      box-shadow: 0 4px 12px rgba(11, 106, 101, 0.4);

      &:hover {
        background: transparent;
        border-color: #fff;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
      }
    }
  }
}

/* 让内容区有一定的顶部间距和白色背景 */
.portal-content {
  position: relative;
  background-color: #f8fafc;
  min-height: 500px;
  z-index: 10;
  /* 可以在这里添加一些上边距或者圆角让过度更自然 */
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>