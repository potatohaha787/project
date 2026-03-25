<template>
  <div class="explore-container">
    <div class="explore-layout">
      <div class="left-title-area">
        <div class="vertical-title-container">
          <span class="sub-title">Explore the Charming Xiangshan</span>
          <h2 class="main-title">
            玩转魅力香山
            <span class="deco-triangle">▼</span>
          </h2>

        </div>
      </div>

      <div class="right-cards-area">
        <div class="category-card" v-for="(card, index) in cards" :key="index" @click="goToPage(card.routeName)"
          :style="{ backgroundImage: `url(${card.bg})` }">
          <div class="card-overlay" :style="{ backgroundColor: card.overlayColor }"></div>

          <div class="card-content">
            <div class="top-content">
              <div class="card-icon" v-html="card.icon"></div>
              <h3 class="card-title">{{ card.title }}</h3>
              <span class="card-link">了解更多 &rarr;</span>
            </div>

            <div class="bottom-content">
              <span class="en-text">{{ card.en }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import bgImage1 from '/@/assets/images/bg.jpg'
import bgImage2 from '/@/assets/images/culture.jpg'
import bgImage3 from '/@/assets/images/food.jpg'
import bgImage4 from '/@/assets/images/place.jpg'

const router = useRouter()

// 跳转函数
const goToPage = (name) => {
  if (name) {
    router.push({ name: name })
  }
}

// 卡片数据配置 (加入了 routeName 字段对应我们之前配置好的路由名)
const cards = [
  {
    id: '1',
    title: '景点探索',
    en: 'Scenic',
    bg: bgImage1,
    routeName: 'scenic',
    overlayColor: 'rgba(105, 128, 105, 0.85)', // 复古绿
    icon: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>`
  },
  {
    id: '2',
    title: '非遗文化',
    en: 'Culture',
    bg: bgImage2,
    routeName: 'heritage',
    overlayColor: 'rgba(176, 126, 68, 0.85)', // 琥珀黄
    icon: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="1.5"><polyline points="20 12 20 22 4 22 4 12"></polyline><rect x="2" y="7" width="20" height="5"></rect><line x1="12" y1="22" x2="12" y2="7"></line><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"></path><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"></path></svg>`
  },
  {
    id: '3',
    title: '香山美食',
    en: 'Food',
    bg: bgImage3,
    routeName: 'food',
    overlayColor: 'rgba(76, 134, 141, 0.85)', // 黛蓝
    icon: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M3 3h18v4H3z"></path><path d="M4 7l-1 14h18l-1-14"></path><path d="M9 7v14"></path><path d="M15 7v14"></path></svg>`
  },
  {
    id: '4',
    title: '游记攻略',
    en: 'Tourism',
    bg: bgImage4,
    routeName: 'guide',
    overlayColor: 'rgba(142, 105, 140, 0.85)', // 墨青
    icon: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>`
  }
]
</script>


<style scoped lang="less">
.explore-container {
  background-color: #F8F7F3;
  /* 宣纸质感的米白色背景 */
  padding: 80px 0 100px;
  min-height: 700px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.explore-layout {
  display: flex;
  gap: 60px;
  max-width: 1200px;
  width: 100%;
}

/* === 左侧标题区域 === */
.left-title-area {
  position: relative;
  width: 140px;
  height: 480px;
  display: flex;
  justify-content: flex-end;
  color: rgba(142, 105, 140, 0.85);
}

.vertical-title-container {
  writing-mode: vertical-rl;
  text-orientation: mixed;
  height: 100%;
}

.main-title {
  font-family: 'Noto Serif SC', 'STZhongsong', serif;
  font-size: 36px;
  font-weight: 700;
  color: #2c2c2c;
  letter-spacing: 15px;
  margin-right: 20px;
  display: flex;
  align-items: center;

  .deco-triangle {
    font-size: 14px;
    color: #a09586;
    margin-top: 16px;
  }
}

.sub-title {
  font-family: 'Times New Roman', Times, serif;
  font-size: 12px;
  color: #666;
  letter-spacing: 4px;
  opacity: 0.8;
}


/* === 右侧卡片区域 === */
.right-cards-area {
  display: flex;
  gap: 20px;
  flex: 1;
}

.category-card {
  position: relative;
  flex: 1;
  height: 420px;
  background-size: cover;
  background-position: center;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s ease;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);

  /* 核心：奇偶数错落排列 */
  &:nth-child(even) {
    margin-top: 60px;
  }

  &:hover {
    transform: translateY(-10px);
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);

    .card-overlay {
      opacity: 0.95;
    }

    .card-link {
      transform: translateX(5px);
    }
  }
}

.card-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  transition: opacity 0.4s ease;
}

.card-content {
  position: relative;
  z-index: 2;
  padding: 36px 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
  color: #fff;
  box-sizing: border-box;
}

.top-content {
  .card-icon {
    margin-bottom: 24px;
    opacity: 0.9;
  }

  .card-title {
    font-family: 'Noto Serif SC', 'STZhongsong', serif;
    font-size: 22px;
    font-weight: 600;
    margin: 0 0 16px 0;
    letter-spacing: 1px;
  }

  .card-link {
    font-size: 12px;
    opacity: 0.7;
    font-family: sans-serif;
    display: inline-block;
    transition: transform 0.3s ease;
  }
}

.bottom-content {
  margin-top: auto;
  /* 将底部英文挤到底部 */

  .en-text {
    font-family: 'Times New Roman', serif;
    font-size: 24px;
    opacity: 0.8;
    letter-spacing: 1px;
  }
}
</style>