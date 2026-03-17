<template>
  <div class="celebrity-detail-page">
    <Header />

    <section class="detail-hero" v-if="detail">
      <img class="hero-bg" :src="detail.image" :alt="detail.title" />
      <div class="hero-mask"></div>

      <div class="hero-content">
        <div class="hero-portrait" v-if="detail.portrait">
          <img :src="detail.portrait" alt="人物肖像" class="portrait-img" />
        </div>

        <div class="hero-text">
          <p class="tag" v-if="detail.tags">{{ detail.tags }}</p>
          <h1>{{ detail.title }}</h1>
          <p class="brief">{{ detail.brief }}</p>
        </div>
      </div>
    </section>

    <section class="detail-content" v-if="detail">
      <div class="detail-body">
        <div class="text-info">
          <div class="info-block">
            <h2>基本信息</h2>
            <p><strong>出生年月：</strong> {{ detail.birth || '暂无数据' }}</p>
          </div>

          <div class="info-block" v-if="detail.achievements">
            <h2>主要成就</h2>
            <p class="achievements-text">{{ detail.achievements }}</p>
          </div>

          <div class="info-block">
            <h2>人物介绍</h2>
            <p v-for="(p, idx) in detailParagraphs" :key="idx" class="intro-p">{{ p }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="detail-content" v-else>
      <h2>加载中或未找到对应人物</h2>
      <p>请返回首页重新选择人物卡片。</p>
    </section>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'

// 【关键修改 1】：在这里主动引入首页的背景图（假设是 bg.jpg，如果是另一张可以换成 bg2.jpg）
import homeBgImage from '/@/assets/images/bg2.jpg'

const route = useRoute()
const detail = ref<any>(null)

// 处理详情文本的换行/数组
const detailParagraphs = computed(() => {
  const text = detail.value?.detail
  if (!text) return []
  try {
    if (typeof text === 'string') {
      const parsed = JSON.parse(text)
      return Array.isArray(parsed) ? parsed : [text]
    }
  } catch (e) {
    return [text]
  }
  return Array.isArray(text) ? text : [text]
})

const loadDetail = async () => {
  // ======== 用于单独查看和调试样式的假数据 ========
  detail.value = {
    title: '孙中山',
    tags: '民主革命先行者',
    brief: '孙中山（1866年—1925年），名文，字载之，号日新，又号逸仙。他是中国近代民族民主主义革命的开拓者，中国民主革命伟大先行者。',
    image: homeBgImage,
    birth: '1866年11月12日',
    achievements: '1. 领导辛亥革命，推翻封建帝制\n2. 提出“三民主义”\n3. 创立中华民国',
    portrait: 'https://p1.ssl.qhimg.com/t013cdd5510e2f19b29.jpg',
    detail: JSON.stringify([
      "孙中山先生高扬反对封建专制统治的斗争旗帜，提出民族、民权、民生的三民主义政治纲领。",
      "他率先发出“振兴中华”的呐喊，希望将中国建设成为独立、民主、富强的现代化国家。",
      "在他的领导下，1911年爆发的辛亥革命，结束了在中国延续几千年的君主专制制度，为中国的进步打开了闸门。"
    ])
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="less">
.celebrity-detail-page {
  background: #f3f4f6;
  min-height: 100vh;
}

.detail-hero {
  position: relative;
  height: 62vh;
  min-height: 420px;
  overflow: hidden;

  .hero-bg {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .hero-mask {
    display: flex;
    position: absolute;
    inset: 0;
    background: linear-gradient(0deg, rgba(0, 0, 0, .8), rgba(0, 0, 0, .2));
  }

  /* ======== 核心修改：左右 Flex 布局 ======== */
  .hero-content {
    position: absolute;
    left: 45%;
    bottom: 56px;
    transform: translateX(-50%);
    color: #fff;
    display: flex;
    align-items: flex-end;
    gap: 40px;

    .hero-portrait {
      width: 220px;
      flex-shrink: 0; //防止图片被压缩 
      border-radius: 8px;
      overflow: hidden;
      border: 2px solid rgba(255, 255, 255, 0.2);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);

      .portrait-img {
        width: 100%;
        height: auto;
        display: block;
      }
    }

    .hero-text {
      flex: 1;

      .tag {
        font-size: 14px;
        opacity: .9;
        background: #c59d5f;
        display: inline-block;
        padding: 4px 12px;
        border-radius: 4px;
        margin-bottom: 12px;
      }

      h1 {
        color: #c59d5f;
        font-size: 46px;
        margin: 0 0 16px;
        font-family: 'Noto Serif SC', serif;
      }

      .brief {
        max-width: 860px;
        line-height: 1.8;
        font-size: 16px;
        opacity: 0.95;
      }
    }
  }
}

.detail-content {
  width: calc(100% - 40px);
  max-width: 1100px;
  margin: -30px auto 60px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 10;

  .detail-body {
    display: flex;
    gap: 40px;
    align-items: flex-start;

    .text-info {
      flex: 1;

      .info-block {
        margin-bottom: 40px;
        padding-bottom: 30px;
        border-bottom: 1px dashed #e5e7eb;

        &:last-child {
          border-bottom: none;
          margin-bottom: 0;
          padding-bottom: 0;
        }
      }

      h2 {
        font-size: 22px;
        color: #0b6a65;
        margin-bottom: 16px;
        font-family: 'Noto Serif SC', serif;
        position: relative;
        padding-left: 14px;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 4px;
          bottom: 4px;
          width: 4px;
          background-color: #c59d5f;
          border-radius: 2px;
        }
      }

      p {
        font-size: 16px;
        color: #374151;
        line-height: 1.8;
      }

      .achievements-text {
        white-space: pre-wrap;
        background: #f8fafc;
        padding: 16px;
        border-radius: 8px;
        color: #475569;
      }

      .intro-p {
        text-indent: 2em;
        margin-bottom: 14px;
      }
    }
  }
}
</style>