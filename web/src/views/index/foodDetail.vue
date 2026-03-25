<template>
  <div class="food-detail">
    <Header />

    <div v-if="imageUrl" class="detail-banner" :style="{ backgroundImage: `url(${imageUrl})` }">
      <div class="banner-overlay"></div>
      <div class="banner-content">
        <h1 class="main-title">{{ detailData.name }}</h1>
        <div class="divider"></div>
        <p class="subtitle">香山特色风味</p>
      </div>
    </div>

    <section class="main-section">
      <div class="content-wrapper">
        <div class="back-bar" @click="router.back()">
          <span class="icon">←</span>
          <span class="text">返回名录</span>
        </div>

        <article class="chronicle-article">
          <p class="desc">{{ detailData.description }}</p>

          <div class="separator-line"></div>

          <div class="rich-content" v-html="detailData.content"></div>
        </article>
      </div>
    </section>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router/dist/vue-router'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import { detailApi } from '/@/api/food'
import { BASE_URL } from '/@/store/constants'

const router = useRouter()
const route = useRoute()
const detailData = ref({})
const imageUrl = ref('')

onMounted(() => {
  const id = route.query.id
  if (id) {
    getDetail(id)
  } else {
    router.push({ name: 'food' })
  }
})

const getDetail = (id) => {
  detailApi({ id: id }).then(res => {
    detailData.value = res.data
    if (detailData.value.image) {
      imageUrl.value = BASE_URL + '/api/staticfiles/image/' + detailData.value.image
    } else {
      imageUrl.value = ''
    }
  }).catch(err => {
    console.log("获取详情失败", err);
  })
}
</script>

<style scoped lang="less">
/* 美食页暖色主题 */
@bg-color: #faf9f6;
@primary-orange: #ea580c;
/* 令人有食欲的橙红色 */
@dark-text: #1c1917;
@overlay-bg: rgba(28, 25, 23, 0.75);

.food-detail {
  background-color: @bg-color;
  color: @dark-text;
  font-family: "Noto Serif SC", "Songti SC", serif;
  overflow-x: hidden;
}

/* --- 1. 沉浸式美食大图 Banner --- */
.detail-banner {
  position: relative;
  min-height: 400px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #fff;

  /* 悬浮黑色遮罩，增强食欲感和神圣感 */
  .banner-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.6);
    /* 稍微深一点的遮罩，凸显高清图 */
  }

  .banner-content {
    position: relative;
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;

    .main-title {
      font-size: 68px;
      letter-spacing: 20px;
      margin: 0;
      color: #ffffff;
      font-weight: 800;
      text-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
      /* 加点投影，浮起主标题 */
    }

    .divider {
      width: 60px;
      height: 4px;
      background-color: @primary-orange;
      /* 换成更有非遗感觉的朱砂红 */
      margin: 0 auto;
      border-radius: 2px;
    }

    .subtitle {
      font-size: 18px;
      letter-spacing: 6px;
      color: #e0e0e0;
    }
  }
}

/* --- 2. 古籍刊本排版主内容区 --- */
.main-section {
  padding: 80px 0 140px;
}

.content-wrapper {
  max-width: 800px;
  /* 窄屏排版，适合沉浸式阅读 */
  margin: 0 auto;
  padding: 0 20px;
}

/* 极简返回栏 */
.back-bar {
  cursor: pointer;
  color: @primary-orange;
  /* 使用朱砂红 */
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 60px;
  opacity: 0.8;
  transition: opacity 0.3s;

  &:hover {
    opacity: 1;
    color: lighten(@primary-orange, 10%);
    /* hover 变淡 */
  }

  .icon {
    font-size: 18px;
  }
}

/* 文章核心样式 */
.chronicle-article {
  text-align: justify;
  line-height: 2;
  /* 增大行距，提升呼吸感 */

  /* 简介部分 */
  .desc {
    font-size: 18px;
    color: lighten(@dark-text, 10%);
    /* 简介变淡一点 */
    font-weight: 500;
    margin-bottom: 40px;
  }

  /* 古铜金丝分割线 */
  .separator-line {
    width: 60px;
    height: 1px;
    background-color: fade(@primary-orange, 30%);
    /* 使用淡化的朱砂红 */
    margin: 40px 0;
    opacity: 0.8;
  }

  /* 富文本正文样式适配 (极简主义) */
  .rich-content {
    color: @dark-text;
    font-size: 16px;
    font-family: sans-serif;
    /* 正文切换为无衬线，更易阅读 */

    /* 深度选择器适配 v-html 生成的 HTML */
    :deep(p) {
      margin-bottom: 24px;
    }

    :deep(h3) {
      font-family: "Noto Serif SC", serif;
      font-size: 20px;
      font-weight: 400;
      color: @dark-text;
      margin: 40px 0 20px;
      position: relative;
      padding-left: 15px;

      /* 章节标题左侧加一道朱砂红竖线 */
      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 5px;
        bottom: 5px;
        width: 3px;
        background-color: @primary-orange;
        /* 使用朱砂红 */
      }
    }
  }
}
</style>