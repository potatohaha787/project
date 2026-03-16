<template>
  <div class="celebrity-detail-page">
    <Header />

    <section class="detail-hero" v-if="detail">
      <img class="hero-bg" :src="detail.image" :alt="detail.title" />
      <div class="hero-mask"></div>
      <div class="hero-content">
        <p class="tag" v-if="detail.tags">{{ detail.tags }}</p>
        <h1>{{ detail.title }}</h1>
        <p class="brief">{{ detail.brief }}</p>
      </div>
    </section>

    <section class="detail-content" v-if="detail">
      <h2>人物介绍</h2>
      <p v-for="(p, idx) in detailParagraphs" :key="idx">{{ p }}</p>
    </section>

    <section class="detail-content" v-else>
      <h2>未找到对应人物</h2>
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
import { BASE_URL } from '/@/store/constants'
import { detailApi } from '/@/api/celebrity'

const route = useRoute()
const detail = ref<any>(null)

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
  // 确保 id 是单个字符串类型，避免 TS 报错标红
  const id = (route.params.id || route.query.id) as string
  if (!id) return

  const res = await detailApi({ id })
  if (res.code === 200 && res.data) {
    const item = res.data
    if (item.image && !item.image.startsWith('http')) {
      item.image = BASE_URL + '/api/staticfiles/image/' + item.image
    }
    detail.value = item
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
    position: absolute;
    inset: 0;
    background: linear-gradient(0deg, rgba(0, 0, 0, .6), rgba(0, 0, 0, .25));
  }

  .hero-content {
    position: absolute;
    left: 50%;
    bottom: 56px;
    transform: translateX(-50%);
    width: min(1100px, calc(100% - 40px));
    color: #fff;

    .tag {
      font-size: 14px;
      opacity: .9;
    }

    h1 {
      font-size: 46px;
      margin: 8px 0 12px;
    }

    .brief {
      max-width: 860px;
      line-height: 1.8;
      font-size: 16px;
    }
  }
}

.detail-content {
  width: min(1100px, calc(100% - 40px));
  margin: 36px auto 60px;
  background: #fff;
  border-radius: 12px;
  padding: 30px 34px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);

  h2 {
    margin-bottom: 14px;
  }

  p {
    line-height: 1.9;
    color: #374151;
    margin-bottom: 12px;
  }
}
</style>