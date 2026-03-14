<template>
  <div class="history-page">
    <Header />

    <div class="page-banner">
      <div class="banner-overlay"></div>
      <div class="banner-content">
        <h1>香山纪事 · 完整长卷</h1>
        <p>跨越近千年的时光，探索中山的辉煌历程</p>
      </div>
    </div>

    <section class="full-history-section">
      <div class="snake-timeline-wrapper">

        <div v-for="(row, rIndex) in chunkedHistory" :key="rIndex" class="timeline-row"
          :class="rIndex % 2 === 0 ? 'row-even' : 'row-odd'">
          <div class="row-track"></div>

          <div v-if="rIndex < chunkedHistory.length - 1" class="u-turn"></div>

          <div class="timeline-item" v-for="item in row" :key="item.year">
            <div class="timeline-dot"></div>

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

    <Footer />
  </div>
</template>

<script>
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'

export default {
  components: { Footer, Header },
  data() {
    return {
      detailVisible: false,
      currentHistory: null,
      itemsPerRow: 3, // 默认每行展示 3 个事件
      resizeTimer: null,
      historyList: [
        {
          year: '1152年', title: '香山立县',
          brief: '南宋绍兴二十二年，正式设立香山县，开启了独立行政建制的历史序幕。',
          image: 'https://images.unsplash.com/photo-1588668214407-6ea9a6d8c272?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
          detail: ['南宋绍兴二十二年（1152年），由东莞县划出香山镇，正式设立香山县。在此之前，这里曾是盛产海盐和香木的海岛。', '立县标志着香山地区在政治、经济、文化上迈入了新的发展阶段。香山之名，来源于境内五桂山“奇花异卉，神仙茶隔水闻香”。']
        },
        {
          year: '1866年', title: '伟人诞生',
          brief: '中国民主革命的伟大先驱孙中山先生在香山县翠亨村诞生。',
          image: 'https://images.unsplash.com/photo-1583884826131-063989c44d18?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
          detail: ['1866年11月12日，孙中山先生出生于广东省香山县（今中山市）南朗镇翠亨村。', '作为中国近代民主革命的伟大先行者，他的“天下为公”精神至今仍激励着无数中山人。']
        },
        {
          year: '1925年', title: '纪念伟人，更名中山',
          brief: '为纪念在此诞生的伟大先驱孙中山先生，香山县易名为中山县。',
          image: 'https://images.unsplash.com/photo-1541872703-74c5e44368f9?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
          detail: ['1925年4月15日，为了纪念刚刚在北京逝世的中国民主革命伟大先驱孙中山先生，广州大元帅府下令将香山县更名为中山县。', '中山市不仅是伟人的故乡，更是近代中国走向共和、向世界开放的先锋门户。']
        },
        {
          year: '1988年', title: '升格地级市',
          brief: '中山正式升格为地级市，跻身“广东四小虎”，经济社会发展步入快车道。',
          image: 'https://images.unsplash.com/photo-1582269438706-e7c65ba443c5?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
          detail: ['1988年1月，中山升格为地级市。乘着改革开放的春风，中山市大力发展乡镇企业，创造了闻名全国的“中山模式”。', '在此期间，中山与东莞、南海、顺德并称为“广东四小虎”。']
        },
        {
          year: '1997年', title: '荣获联合国人居奖',
          brief: '凭借卓越的生态环境与城市治理，中山市成为国内首批获此殊荣的城市。',
          image: 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
          detail: ['1997年，中山市以“注重环境保护，城市绿化、净化、美化成绩卓著”为由，荣获该年度的“联合国人居环境奖”。', '这一奖项标志着中山成功兼顾了生态文明与人居环境的建设，确立了其“宜居城市”的金字招牌。']
        },
        {
          year: '2019年', title: '大湾区重要节点',
          brief: '《粤港澳大湾区发展规划纲要》正式发布，中山被明确定位为重要节点城市。',
          image: 'https://images.unsplash.com/photo-1555899434-94d1368aa7af?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
          detail: ['2019年2月，中共中央、国务院印发了《粤港澳大湾区发展规划纲要》，中山市迎来了历史性的发展新机遇。', '作为珠江口西岸的重要节点城市，中山致力于打造成为大湾区的精品文旅城市。']
        },
        {
          year: '2024年', title: '深中通道通车',
          brief: '超级工程深中通道建成通车，大湾区“一小时生活圈”核心格局形成。',
          image: 'https://images.unsplash.com/photo-1561081498-8ddc0eb09e20?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
          detail: ['深中通道是集“桥、岛、隧、水下互通”于一体的跨海集群工程。通车后，从中山到深圳的车程由2小时缩短至不到30分钟。', '这标志着中山正式迈入大湾区发展的核心舞台。']
        }
      ]
    }
  },
  computed: {
    // 核心算法：将一维数组根据屏幕大小切割成二维数组（每行N个）
    chunkedHistory() {
      const chunks = [];
      for (let i = 0; i < this.historyList.length; i += this.itemsPerRow) {
        chunks.push(this.historyList.slice(i, i + this.itemsPerRow));
      }
      return chunks;
    }
  },
  mounted() {
    this.handleResize();
    window.addEventListener('resize', this.onResize);
    window.scrollTo(0, 0);
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.onResize);
  },
  methods: {
    openDetail(item) {
      this.currentHistory = item;
      this.detailVisible = true;
    },
    // 防抖处理窗口变化
    onResize() {
      if (this.resizeTimer) clearTimeout(this.resizeTimer);
      this.resizeTimer = setTimeout(() => {
        this.handleResize();
      }, 100);
    },
    // 响应式重排算法
    handleResize() {
      const width = window.innerWidth;
      if (width <= 768) {
        this.itemsPerRow = 1; // 手机排1列
      } else if (width <= 1024) {
        this.itemsPerRow = 2; // 平板排2列
      } else {
        this.itemsPerRow = 3; // 电脑排3列
      }
    }
  }
}
</script>

<style scoped lang="less">
@primary-color: #0b6a65;
@accent-color: #c59d5f;
@bg-gray: #f3f4f6;
@track-color: #d1d5db;

.history-page {
  background-color: @bg-gray;
  min-height: 100vh;
}

.page-banner {
  position: relative;
  padding: 140px 20px 80px;
  background-image: url(/@/assets/images/bg.jpg);
  background-size: cover;
  background-position: center;
  text-align: center;
  color: #fff;

  .banner-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.65);
  }

  .banner-content {
    position: relative;
    z-index: 2;

    h1 {
      font-size: 42px;
      font-weight: bold;
      color: @accent-color;
      letter-spacing: 4px;
      font-family: 'Noto Serif SC', serif;
      margin-bottom: 12px;
    }

    p {
      font-size: 16px;
      color: #e5e7eb;
      letter-spacing: 2px;
    }
  }
}

.full-history-section {
  background-color: @bg-gray;
}

/* =========================================
   极致数学排版：S型蜿蜒时间轴 
========================================= */

.snake-timeline-wrapper {
  width: 100%;
  padding: 80px 20px 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 让整个时间轴在页面居中 */
  overflow: hidden;
}

/* 行容器基础 */
.timeline-row {
  display: flex;
  gap: 40px;
  /* 卡片之间的间距 */
  margin-bottom: 60px;
  /* 行与行之间的向下间距 */
  position: relative;
  /* 默认为PC端 3 列尺寸: (300*3) + (40*2) = 980px */
  width: 980px;
}

/* 核心一：奇偶行方向交替 */
.row-even {
  flex-direction: row;
}

/* 偶数行：从左往右排 */
.row-odd {
  flex-direction: row-reverse;
}

/* 奇数行：从右往左排 */


/* 核心二：横向轴线 */
.row-track {
  position: absolute;
  top: 15px;
  /* 与中心点的黄圈圆心对齐 */
  left: 150px;
  /* 从第一个卡片的正中心开始 */
  right: 150px;
  /* 到最后一个卡片的正中心结束 */
  height: 2px;
  background: @track-color;
  z-index: 1;
}

/* 核心三：精确计算的向下 U 型弯折线 */
.u-turn {
  position: absolute;
  top: 15px;
  width: 40px;
  /* 半径宽度 */
  /* 高度 = 100%容器高 + 60px向下间距 + 2px自身边框修正，完美触碰下一行轴线 */
  height: calc(100% + 60px + 2px);
  z-index: 1;
  box-sizing: border-box;
}

/* 偶数行：向右走到头，向下弯折 */
.row-even .u-turn {
  right: 110px;
  /* 150px(卡片中心) - 40px(半径) */
  border-top: 2px solid @track-color;
  border-right: 2px solid @track-color;
  border-bottom: 2px solid @track-color;
  border-radius: 0 40px 40px 0;
  /* 右侧圆角 */
}

/* 奇数行：向左走到头，向下弯折 */
.row-odd .u-turn {
  left: 110px;
  border-top: 2px solid @track-color;
  border-left: 2px solid @track-color;
  border-bottom: 2px solid @track-color;
  border-radius: 40px 0 0 40px;
  /* 左侧圆角 */
}

/* 核心四：单个卡片插槽 */
.timeline-item {
  width: 300px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 确保点和卡片中心对齐 */
  position: relative;
  z-index: 2;
  /* 盖在轴线之上 */
}

/* 金色节点 */
.timeline-dot {
  width: 16px;
  height: 16px;
  background: @bg-gray;
  border: 3px solid @accent-color;
  border-radius: 50%;
  margin-top: 8px;
  /* 配合顶部15px的轨道，使圆心完美咬合在轨道上 */
  margin-bottom: 24px;
  position: relative;
  z-index: 2;
  box-sizing: border-box;
}

/* 卡片内容样式保持你的极简风格 */
.timeline-content {
  width: 100%;
  background: #ffffff;
  border-radius: 12px;
  padding: 18px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;

  &:hover {
    border-color: @primary-color;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
    transform: translateY(-5px);

    .timeline-img {
      transform: scale(1.05);
    }

    .read-more {
      letter-spacing: 1px;
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
    margin-bottom: 12px;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .read-more {
    font-size: 13px;
    color: @primary-color;
    font-weight: 600;
    transition: all 0.3s;
  }
}

/* =========================================
   响应式宽度自适应 (完美支持各种屏幕) 
========================================= */
@media (max-width: 1024px) {

  /* 平板端 2 列尺寸: (300*2) + (40*1) = 640px */
  .timeline-row {
    width: 640px;
  }
}

@media (max-width: 768px) {

  /* 手机端 1 列尺寸: 300px */
  .timeline-row {
    width: 300px;
  }

  /* 手机由于只有单列，轨道线自动消隐，变成一条精美的垂中蛇形虚线 */
}

/* 弹窗代码 */
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
</style>