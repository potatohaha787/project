<template>
  <div class="heritage-detail">
    <Header />

    <div class="detail-banner">
      <div class="banner-overlay"></div>

      <div class="banner-content">
        <div class="level-badge">{{ detailData.level }}</div>
        <h1 class="main-title">{{ detailData.title }}</h1>
        <div class="seal">{{ detailData.tag }}</div>
      </div>
    </div>

    <section class="main-section">
      <div class="content-wrapper">
        <div class="back-bar" @click="router.back()">
          <span class="icon">←</span>
          <span class="text">展卷归里</span>
        </div>
        <article class="chronicle-article">
          <div v-if="imageUrl" class="article-image">
            <img :src="imageUrl" alt="非遗项目" />
          </div>
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
import { detailApi } from '/@/api/heritage'
import { BASE_URL } from '/@/store/constants'

const router = useRouter()
const route = useRoute()
const detailData = ref({})
const imageUrl = ref('')

onMounted(() => {
  // 从 URL 参数中获取 ID (?id=xxx)
  const id = route.query.id
  if (id) {
    getDetail(id)
  } else {
    // 没有 ID 则返回列表页
    router.push({ name: 'heritage' })
  }
})

const getDetail = (id) => {
  detailApi({ id: id }).then(res => {
    detailData.value = res.data
    if (detailData.value.image) {
      // 图片在后端存放在静态资源目录下，通常路径是：BASE_URL + '/api/staticfiles/image/' + 文件名
      imageUrl.value = BASE_URL + '/api/staticfiles/image/' + detailData.value.image
    } else {
      // 也可以在此处设一个默认图片
      imageUrl.value = ''
    }
  }).catch(err => {
    console.log("获取详情失败", err);
  })
}
</script>

<style scoped lang="less">
/* --- 核心色彩定义 --- */
@paper-white: #f7f6f2;
/* 宣纸白 */
@ink-black: #2c2827;
/* 墨黑 */
@vermilion: #bc332a;
/* 朱砂红 */
@gold: #c5b596;
/* 古铜金 */

.heritage-detail {
  background-color: @paper-white;
  color: @ink-black;
  font-family: "Noto Serif SC", "Songti SC", serif;
  overflow-x: hidden;
}

/* --- 1. 优化后的深色质感 Banner --- */
.detail-banner {
  position: relative;
  padding: 90px 20px 40px;
  background-image: url(/@/assets/images/bg6.jpg);
  background-size: cover;
  background-position: center;
  text-align: center;
  color: #fff;
}

/* 径向渐变遮罩层，实现中心聚焦和边缘淡出 */
.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.65);
}

/* 修复后的标准内容排版，全采用flexbox居中 */
.banner-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  width: 100%;
  height: 100%;

  .level-badge {
    font-size: 13px;
    border: 1px solid rgba(255, 255, 255, 0.4);
    padding: 4px 10px;
    letter-spacing: 2px;
    color: #eaeaea;
  }

  .main-title {
    font-size: 56px;
    letter-spacing: 12px;
    margin: 0;
    color: #ffffff;
    font-weight: 400;
  }

  .seal {
    writing-mode: horizontal-tb;
    font-size: 16px;
    letter-spacing: 4px;
    color: @vermilion;
    border: 2px solid @vermilion;
    padding: 5px 10px;
    border-radius: 2px;
    font-weight: bold;
    opacity: 0.9;
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
  color: @gold;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 60px;
  opacity: 0.8;
  transition: opacity 0.3s;

  &:hover {
    opacity: 1;
    color: @vermilion;
  }

  .icon {
    font-size: 18px;
  }
}

.article-image {
  margin-bottom: 60px;
  /* 和下方文字拉开呼吸距离 */
  position: relative;
  border-radius: 4px;
  /* 柔和一点 */
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  /* 极淡的投影，增加浮起感 */

  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border: 1px solid rgba(197, 181, 150, 0.3);
    /* 加上一道超淡的古铜金边框 */
    z-index: 2;
    pointer-events: none;
    /* 防止遮挡点击 */
  }

  img {
    width: 100%;
    height: auto;
    max-height: 500px;
    /* 限制一下，防止图片太长把页面撑坏 */
    object-fit: cover;
    /* 自动裁剪适配 */
    display: block;
    transition: transform 0.6s ease;
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
    color: #444;
    font-weight: 500;
    margin-bottom: 40px;
  }

  /* 古铜金丝分割线 */
  .separator-line {
    width: 60px;
    height: 1px;
    background-color: @gold;
    margin: 40px 0;
    opacity: 0.8;
  }

  /* 富文本正文样式适配 (极简主义) */
  .rich-content {
    color: @ink-black;
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
      color: @ink-black;
      margin: 40px 0 20px;
      position: relative;
      padding-left: 15px;

      /* 章节标题左侧加一道古铜金竖线 */
      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 5px;
        bottom: 5px;
        width: 3px;
        background-color: @gold;
      }
    }
  }
}
</style>