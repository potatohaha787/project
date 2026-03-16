<template>
  <div class="portal">
    <Header />

    <section class="hero-banner">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">伟人故里 · 魅力中山</h1>
        <p class="hero-subtitle">探寻孙中山故居的厚重，体验岭南水乡的温婉，品味地道香山美食</p>
        <div class="hero-actions">
          <button class="btn-primary" @click="scrollToTimeline">开启旅程</button>
        </div>
      </div>
    </section>

    <section class="history-section" id="history-timeline">

      <div class="section-header">
        <h2 class="section-title">香山纪事 · 岁月长卷</h2>
        <p class="section-desc">走过近千年的风雨历程，感受这座城市的沧桑与辉煌</p>
        <div class="more-btn" @click="goToFullHistory">
          查看完整时间线 <span class="arrow">&rarr;</span>
        </div>
      </div>

      <div class="timeline-wrapper">
        <div class="timeline-track">
          <div class="timeline-item" v-for="(item, index) in historyList.slice(0, 4)" :key="index">
            <div class="timeline-dot"></div>
            <div class="timeline-stem"></div>

            <div class="timeline-content" @click="openDetail(item)">
              <div class="timeline-year">{{ item.year }}</div>
              <h3 class="timeline-event-title">{{ item.title }}</h3>
              <div class="timeline-image-wrapper">
                <img :src="item.image" :alt="item.title" class="timeline-img">
              </div>
              <p class="timeline-brief">{{ item.brief }}</p>
              <span class="read-more">查看详情 &rarr;</span>
            </div>
          </div>
        </div>
      </div>
    </section>




    <a-modal v-model:visible="detailVisible" :title="null" :footer="null" :width="680" :centered="true"
      class="history-modal">
      <div v-if="currentHistory" class="modal-body">
        <div class="modal-year-badge">{{ currentHistory.year }}</div>
        <h2 class="modal-title">{{ currentHistory.title }}</h2>
        <div class="modal-image-box">
          <img :src="currentHistory.image" :alt="currentHistory.title">
        </div>
        <div class="modal-text">
          <p v-for="(paragraph, idx) in currentHistory.detail" :key="idx">{{ paragraph }}</p>
        </div>
      </div>
    </a-modal>


    <Story />
    <div class="portal-content" id="portal-main-content">
      <Content />
    </div>

    <Footer />
  </div>
</template>

<script>
import Header from '/@/views/index/components/header.vue'
import Story from '/@/views/index/components/story.vue'
import Footer from '/@/views/index/components/footer.vue'
import Content from '/@/views/index/components/content.vue'
// 1. 引入刚才写好的 API 请求接口和常量
import { listApi } from '/@/api/history'
import { BASE_URL } from "/@/store/constants"

export default {
  components: { Footer, Header, Story, Content },
  data() {
    return {
      detailVisible: false,
      currentHistory: null,
      // 2. 清空写死的数据，变成空数组
      historyList: []
    }
  },
  // 3. 页面加载时调用接口拿数据
  mounted() {
    this.getHistoryData();
  },
  methods: {
    // 4. 新增获取数据库历史数据的方法
    getHistoryData() {
      listApi({}).then(res => {
        if (res.code === 200) {
          let dbData = res.data;

          // 对后台返回的数据进行预处理
          dbData.forEach(item => {
            // A. 处理图片路径：如果是本地上传的图片(没有http开头)，则拼接上后台服务器的地址
            if (item.image && !item.image.startsWith('http')) {
              item.image = BASE_URL + '/api/staticfiles/image/' + item.image;
            }

            // B. 处理详细内容：把 JSON 字符串转成数组，用于弹窗多段落显示
            if (item.detail && typeof item.detail === 'string') {
              try {
                item.detail = JSON.parse(item.detail);
              } catch (e) {
                item.detail = [item.detail];
              }
            } else if (!item.detail) {
              item.detail = [];
            }
          });

          // C. 按照之前设置的 sort_order 从小到大排序 (可选，保证首页和内页顺序一致)
          dbData.sort((a, b) => {
            return (a.sortOrder || 0) - (b.sortOrder || 0);
          });

          // 赋值给页面变量
          // 注意：模板里写了 historyList.slice(0, 4)，所以这里哪怕存入几十条，页面也只会显示前4条！
          this.historyList = dbData;
        }
      }).catch(err => {
        console.error("获取首页香山纪事数据失败：", err);
      });
    },

    scrollToTimeline() {
      const el = document.getElementById('history-timeline');
      if (el) { window.scrollTo({ top: el.offsetTop - 64, behavior: 'smooth' }); }
    },
    openDetail(item) {
      this.currentHistory = item;
      this.detailVisible = true;
    },
    // 跳转到完整的历史时间线页面
    goToFullHistory() {
      this.$router.push({ name: 'history' });
    }
  }
}
</script>

<style scoped lang="less">
@primary-color: #0b6a65;
@accent-color: #d29841;
@bg-gray: #f3f4f6;

.hero-banner {
  position: relative;
  height: 95vh;
  min-height: 600px;
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
    background: linear-gradient(rgba(0, 0, 0, 0.2), rgba(49, 44, 44, 0.7));
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -1px;
    left: 0;
    width: 100%;
    height: 250px;
    z-index: 5;
    pointer-events: none;
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
    color: @accent-color;
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
      background: @primary-color;
      color: #fff;
      border: 2px solid @primary-color;

      &:hover {
        background: transparent;
        border-color: #fff;
      }
    }
  }
}

/* === 横向S型时间轴 (首页部分) === */
.history-section {
  margin-top: 0;
  padding: 60px 0 60px;
  background-color: @bg-gray;

  position: relative;
  z-index: 10;

  .section-header {
    max-width: 1200px;
    margin: 0 auto 40px;
    position: relative;
    text-align: center;
    z-index: 2;

    .section-title {
      font-size: 36px;
      color: #1f2937;
      font-weight: 600;
      font-family: 'Noto Serif SC', serif;
      margin-bottom: 12px;
      letter-spacing: 2px;
    }

    .section-desc {
      font-size: 15px;
      color: #4b5563;
      font-weight: 500;
      letter-spacing: 1px;
    }

    /* 新增：绝对定位在右侧的【查看更多】按钮 */
    .more-btn {
      position: absolute;
      right: 20px;
      bottom: 0px;
      font-size: 15px;
      color: @primary-color;
      font-weight: 600;
      cursor: pointer;
      display: flex;
      align-items: center;
      transition: all 0.3s;

      .arrow {
        margin-left: 6px;
        font-size: 18px;
        transition: transform 0.3s;
      }

      &:hover {
        color: @accent-color;

        .arrow {
          transform: translateX(5px);
        }
      }
    }
  }
}

.timeline-wrapper {
  width: 100%;
  overflow-x: auto;
  padding: 385px 20px 20px;
  display: flex;
  justify-content: flex-start;
  scroll-behavior: smooth;
}

.timeline-wrapper::-webkit-scrollbar {
  height: 6px;
}

.timeline-wrapper::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.timeline-track {
  position: relative;
  height: 2px;
  background: #d1d5db;
  display: flex;
  flex-direction: row;
  align-items: center;
  min-width: max-content;
  margin: 0 auto;
  padding: 0 40px;
}

.timeline-item {
  position: relative;
  width: 250px;
  margin: 0 30px;
}

.timeline-dot {
  position: absolute;
  top: -6px;
  left: 50%;
  transform: translateX(-50%);
  width: 14px;
  height: 14px;
  background: @bg-gray;
  border: 3px solid @accent-color;
  border-radius: 50%;
  z-index: 2;
}

.timeline-stem {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 2px;
  height: 30px;
  background: #d1d5db;
  z-index: 1;
  bottom: 0;
}

.timeline-content {
  position: absolute;
  left: 0;
  height: 350px;
  width: 250px;
  background: #ffffff;
  border-radius: 12px;
  padding: 18px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s ease;
  box-sizing: border-box;

  bottom: 30px;
  /* 新增这一行：将卡片固定在竖线正上方 */

  &:hover {
    border-color: @primary-color;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
    transform: translateY(-5px);
    /* 新增：把之前 .item-top 里的悬浮动画移到这里 */

    .timeline-img {
      transform: scale(1.05);
    }
  }

  .timeline-year {
    font-size: 28px;
    font-weight: 700;
    color: @accent-color;
    font-family: 'Times New Roman', serif;
    line-height: 1;
    margin-bottom: 8px;
  }

  .timeline-event-title {
    font-size: 18px;
    color: #1f2937;
    font-weight: 600;
    margin-bottom: 12px;
  }

  .timeline-image-wrapper {
    width: 100%;
    height: 130px;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 12px;
    background: #f3f4f6;

    .timeline-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.6s ease;
    }
  }

  .timeline-brief {
    font-size: 13px;
    color: #6b7280;
    line-height: 1.6;
    overflow: hidden;
  }

  .read-more {
    //position: absolute;
    bottom: 18px;
    left: 18px;
    font-size: 13px;
    color: @primary-color;
    font-weight: 600;
    transition: all 0.3s;
  }
}


:deep(.history-modal) {
  .ant-modal-content {
    border-radius: 16px;
    overflow: hidden;
  }

  .ant-modal-close-x {
    font-size: 20px;
    color: #64748b;
  }
}

.modal-body {
  padding: 10px;

  .modal-year-badge {
    display: inline-block;
    padding: 4px 16px;
    background-color: fade(@accent-color, 15%);
    color: darken(@accent-color, 10%);
    font-weight: bold;
    border-radius: 20px;
    font-size: 16px;
    margin-bottom: 12px;
  }

  .modal-title {
    font-size: 28px;
    color: #152844;
    font-weight: 600;
    margin-bottom: 20px;
    font-family: 'Noto Serif SC', serif;
  }

  .modal-image-box {
    width: 100%;
    border-radius: 12px;
    overflow: hidden;
    margin-bottom: 24px;

    img {
      width: 100%;
      height: auto;
      max-height: 350px;
      object-fit: cover;
      display: block;
    }
  }

  .modal-text p {
    font-size: 16px;
    color: #475569;
    line-height: 1.8;
    margin-bottom: 12px;
    text-indent: 2em;
    text-align: justify;
  }
}

.portal-content {
  position: relative;
  background-color: #f8fafc;
  min-height: 500px;
  z-index: 10;
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

/* 手机端由于屏幕太小，把按钮放到标题正下方 */
@media (max-width: 768px) {
  .history-section .section-header .more-btn {
    position: static;
    justify-content: center;
    margin-top: 16px;
  }
}
</style>