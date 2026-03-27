<template>
  <div class="food-page">
    <Header />

    <header class="food-hero">
      <div class="hero-content">
        <h1 class="hero-title">舌尖上的<span class="highlight">香山</span></h1>
        <p class="hero-subtitle">TASTE OF ZHONGSHAN</p>
        <div class="divider"></div>
        <p class="hero-desc">
          “食在广东，味在中山”。从石岐乳鸽的脆皮多汁，到东升脆肉鲩的爽脆弹牙，
          中山美食以其独特的水乡风味与广府精髓，征服了无数食客的味蕾。
          在这里，每一道菜都是一段历史，每一口鲜都是乡愁。
        </p>
      </div>
    </header>

    <section class="bento-section">
      <div class="bento-grid">

        <div v-for="(item, index) in foodList" :key="index" :class="['bento-card', item.gridClass]"
          @click="goFoodDetail(item.id)">
          <img :src="item.image" :alt="item.name" class="food-img" />

          <div class="food-overlay">
            <div class="food-info">
              <h2 class="food-name">{{ item.name }}</h2>
              <p class="food-desc">{{ item.description }}</p>
              <div class="food-tag">中山特色名菜</div>
            </div>
          </div>
        </div>

      </div>
    </section>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import { listApi } from '/@/api/food'
import { BASE_URL } from '/@/store/constants'
import { useRouter } from 'vue-router/dist/vue-router'

// 初始化为空数组
const foodList = ref([])
const router = useRouter()

onMounted(() => {
  getFoodData()
})

const getFoodData = () => {
  listApi().then(res => {
    if (res.code === 200) {
      let dbData = res.data;

      // 遍历数据，拼接完整的图片路径
      dbData.forEach(item => {
        if (item.image && !item.image.startsWith('http')) {
          item.image = BASE_URL + '/api/staticfiles/image/' + item.image;
        }
      });

      // 赋值给前端展示
      foodList.value = dbData;
    }
  }).catch(err => {
    console.error("获取美食数据失败", err);
  })
}

// 跳转到美食详情页
const goFoodDetail = (id) => {
  // 使用 name 跳转，并通过 query 携带 ID (格式：/foodDetail?id=xxx)
  router.push({ name: 'foodDetail', query: { id: id } })
}

</script>

<style scoped lang="less">
/* 美食页暖色主题 */
@bg-color: #faf9f6;
@accent-color: #c59d5f;
/* 让人有食欲的橙红色 */
@white-text: #ffffff;
@overlay-bg: rgba(28, 25, 23, 0.75);

.food-page {
  background-color: @bg-color;
  min-height: 100vh;
}

/* --- 1. 杂志风格头部 --- */
.food-hero {
  padding: 160px 20px 80px;
  /* 设置背景图片 */
  background-image: url(/@/assets/images/foods.jpg);
  background-size: cover;
  /* 核心：让图片等比例缩放，百分百铺满整个区域 */
  background-position: center;
  /* 核心：让图片始终居中显示 */
  background-repeat: no-repeat;
  /* 核心：确保图片不重复拼贴 */
  text-align: center;
  position: relative;
  overflow: hidden;

  /* 添加一个全屏的半透明纯白遮罩，防止花哨的背景图导致深色文字看不清 */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(40, 40, 40, 0.70);
    /* 85%透明度的白底，你可以根据图片深浅自己微调 0.85 这个数值 */
    z-index: 1;
  }

  .hero-content {
    max-width: 800px;
    margin: 0 auto;
    position: relative;
    z-index: 2;
    /* 必须加上层级，确保文字悬浮在遮罩层之上 */
  }

  .hero-title {
    font-family: 'Noto Serif SC', serif;
    font-size: 56px;
    font-weight: 800;
    color: @white-text;
    letter-spacing: 4px;
    margin-bottom: 10px;

    .highlight {
      color: @accent-color;
    }
  }

  .hero-subtitle {
    font-size: 16px;
    letter-spacing: 8px;
    color: @white-text;
    font-weight: 600;
    margin-bottom: 24px;
  }

  .divider {
    width: 60px;
    height: 4px;
    background-color: @accent-color;
    margin: 0 auto 30px;
    border-radius: 2px;
  }

  .hero-desc {
    font-size: 16px;
    line-height: 2;
    color: @white-text;
    text-align: justify;
    text-align-last: center;
  }
}

/* --- 2. 便当盒网格 (Bento Grid) --- */
.bento-section {
  padding: 40px 20px 120px;
}

.bento-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  /* 定义 4 列基础网格 */
  grid-template-columns: repeat(4, 1fr);
  /* 定义基础行高 */
  grid-auto-rows: 260px;
  gap: 24px;
}

/* 动态跨行跨列类名 */
.col-span-2 {
  grid-column: span 2;
}

.col-span-1 {
  grid-column: span 1;
}

.row-span-2 {
  grid-row: span 2;
}

.row-span-1 {
  grid-row: span 1;
}

/* 卡片基础样式 */
.bento-card {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  /* 悬浮动效提速 */
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(234, 88, 12, 0.15);

    .food-img {
      transform: scale(1.08);
    }

    .food-overlay {
      opacity: 1;
      background: @overlay-bg;
    }

    .food-info {
      transform: translateY(0);
      opacity: 1;
    }
  }
}

.food-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
  display: block;
}

/* 悬浮遮罩层 */
.food-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.6) 0%, transparent 50%);
  opacity: 0.8;
  transition: all 0.4s ease;
  display: flex;
  align-items: flex-end;
  padding: 30px;
}

/* 遮罩内的文本内容 */
.food-info {
  color: #fff;
  transform: translateY(20px);
  opacity: 0.9;
  transition: all 0.4s ease;

  .food-name {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 8px;
    color: #fff;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  }

  .food-desc {
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 16px;
    color: #f5f5f4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .food-tag {
    display: inline-block;
    padding: 4px 12px;
    background: @accent-color;
    color: #fff;
    font-size: 12px;
    font-weight: bold;
    border-radius: 20px;
    letter-spacing: 1px;
  }
}

/* 响应式：屏幕变小时改变网格结构 */
@media (max-width: 992px) {
  .bento-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .col-span-2 {
    grid-column: span 2;
  }

  .col-span-1 {
    grid-column: span 1;
  }
}

@media (max-width: 576px) {
  .bento-grid {
    grid-template-columns: 1fr;
    grid-auto-rows: 220px;
  }

  .col-span-2,
  .col-span-1 {
    grid-column: span 1;
  }

  .row-span-2 {
    grid-row: span 1;
  }
}
</style>