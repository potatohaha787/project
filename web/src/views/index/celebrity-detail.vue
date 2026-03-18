<template>
  <div class="celebrity-detail-page">
    <Header />

    <section class="detail-hero" v-if="detail">
      <img class="hero-bg" :src="detail.heroBg" :alt="detail.title" />
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
            <p><strong>出生地：</strong> {{ detail.birthplace || '暂无数据' }}</p>
            <p><strong>出生日期：</strong> {{ detail.birthDate || '暂无数据' }}</p>
            <p v-if="detail.deathDate"><strong>逝世日期：</strong> {{ detail.deathDate }}</p>
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

    <section class="detail-content" v-else-if="!loading">
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
import homeBgImage from '/@/assets/images/bg4.jpg' // 默认背景图

// 引入写好的API和基础图片路径配置
import { listApi } from '/@/api/celebrity'
import { BASE_URL } from "/@/store/constants"

const route = useRoute()
const detail = ref<any>(null)
const loading = ref(true)

// 处理详情文本的换行 (支持 JSON 数组格式或普通的 \n 换行格式)
const detailParagraphs = computed(() => {
  const text = detail.value?.detail
  if (!text) return []
  try {
    if (typeof text === 'string') {
      const parsed = JSON.parse(text)
      if (Array.isArray(parsed)) return parsed
    }
  } catch (e) {
    // 解析 JSON 失败说明是普通字符串，按回车符 \n 切割成多段
    return text.split('\n').filter((p: string) => p.trim() !== '')
  }
  return Array.isArray(text) ? text : [text]
})

// 读取数据库人物详情
const loadDetail = () => {
  const id = route.params.id;
  if (!id) {
    loading.value = false;
    return;
  }

  // 获取所有人列表，然后本地筛选出这一个 (因为数据量小，这样最方便)
  listApi({}).then((res: any) => {
    if (res.code === 200) {
      const dbData = res.data || [];
      // 匹配当前路由传递的 ID
      const currentPerson = dbData.find((item: any) => String(item.id) === String(id));

      if (currentPerson) {
        // 处理头像图片路径
        if (currentPerson.image && !currentPerson.image.startsWith('http')) {
          currentPerson.portrait = BASE_URL + '/api/staticfiles/image/' + currentPerson.image;
        } else {
          currentPerson.portrait = currentPerson.image;
        }

        // 统一设置背景图
        currentPerson.heroBg = homeBgImage;

        // 赋值给页面变量
        detail.value = currentPerson;
      }
    }
  }).catch(err => {
    console.error("获取名人故事数据失败：", err);
  }).finally(() => {
    loading.value = false;
  });
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="less">
/* === 样式部分保持你原来的完全不变 === */
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
      height: 300px;
      /* 👇 新增：给头像一个固定的高度，约等于 3:4 的标准肖像比例 */
      flex-shrink: 0;
      border-radius: 8px;
      overflow: hidden;
      border: 2px solid rgba(255, 255, 255, 0.2);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);

      .portrait-img {
        width: 100%;
        height: 100%;
        /* 👇 修改：让图片高度完全填满上面的 300px 父容器 */
        object-fit: cover;
        /* 👇 核心魔法：保证图片绝对不会被拉伸变形，多出的部分会自动隐藏！ */
        object-position: top;
        /* 👇 新增：裁剪时尽量保留人物头部（靠上对齐） */
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
        margin-bottom: 5px;
        padding-bottom: 20px;
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