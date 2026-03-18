<template>
  <div class="explore-page">
    <Header />

    <section class="explore-hero">
      <div class="hero-content">
        <h1 class="page-title">景点探索</h1>
        <p class="page-subtitle">Discover Xiangshan</p>
        <p class="header-desc">
          从历史古迹到自然风光，漫步中山的大街小巷，感受这座城市的独特脉动与人文气息。
        </p>
      </div>
    </section>

    <section class="category-section">
      <div class="category-tabs">
        <div class="tab-item" :class="{ 'active': activeClassId === '' }" @click="switchCategory('')">全部</div>
        <div class="tab-item" v-for="c in classList" :key="c.id" :class="{ 'active': activeClassId === String(c.id) }"
          @click="switchCategory(String(c.id))">
          {{ c.title }}
        </div>
      </div>
    </section>

    <section class="thing-grid-container">
      <div class="thing-grid" v-if="thingList.length > 0">
        <div class="thing-card" v-for="item in thingList" :key="item.id" @click="goToDetail(item.id)">
          <div class="card-cover">
            <img :src="item.imageUrl" :alt="item.title" class="cover-img" />
            <div class="level-badge" v-if="item.level">{{ item.level }}景区</div>
          </div>

          <div class="card-info">
            <h3 class="thing-title">{{ item.title }}</h3>

            <div class="info-row">
              <span class="location"><i class="icon-location">📍</i> {{ item.address || '中山市' }}</span>
            </div>

            <p class="thing-desc">{{ item.description || '暂无简介' }}</p>

            <div class="card-footer">
              <div class="price-box">
                <span v-if="item.price && item.price > 0">
                  <span class="currency">¥</span>
                  <span class="price">{{ item.price }}</span>
                  <span class="unit">起</span>
                </span>
                <span v-else class="free">免费开放</span>
              </div>
              <button class="book-btn">查看详情</button>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-state" v-else>
        <div class="empty-icon">🍃</div>
        <p>该分类下暂无景点数据，请去后台添加吧~</p>
      </div>
    </section>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'

// 测试封面图
import defaultCover from '/@/assets/images/tourism.jpg'

const router = useRouter()

const classList = ref<any[]>([])
const thingList = ref<any[]>([])
const activeClassId = ref<string>('')

// 静态测试数据 (Mock Data)
const mockClassList = [
  { id: '1', title: '科考探险' },
  { id: '2', title: '文学艺术' },
  { id: '3', title: '民俗风情' },
  { id: '4', title: '历史古迹' },
  { id: '5', title: '观光游览' }
]

const mockThingList = [
  { id: '101', title: '孙中山故居纪念馆', classId: '4', address: '中山市南朗镇翠亨村', description: '全国重点文物保护单位，展示孙中山先生的生平事迹。', imageUrl: defaultCover, level: '5A', price: 0 },
  { id: '102', title: '中山影视城', classId: '2', address: '中山市南朗镇', description: '集旅游观光、爱国主义教育和影视拍摄等于一体的大型综合性旅游景区。', imageUrl: defaultCover, level: '4A', price: 65 },
  { id: '103', title: '詹园', classId: '3', address: '中山市南区北台村', description: '目前岭南地区最大的私家古典园林，建筑以苏杭园林为基调。', imageUrl: defaultCover, level: '4A', price: 60 },
  { id: '104', title: '紫马岭公园', classId: '5', address: '中山市东区', description: '天工与人力交融，游憩共生态并重的大型郊野公园。', imageUrl: defaultCover, level: '', price: 0 },
  { id: '105', title: '五桂山自然风景区', classId: '1', address: '中山市五桂山镇', description: '主峰海拔531米，为中山市最高峰。适合徒步探险。', imageUrl: defaultCover, level: '自然保护区', price: 0 },
  { id: '106', title: '崖口村', classId: '3', address: '中山市南朗镇', description: '体验地道岭南水乡民俗和品尝海鲜的好去处。', imageUrl: defaultCover, level: '美丽乡村', price: 0 },
  { id: '107', title: '岐江夜游', classId: '5', address: '中山市石岐区', description: '乘船游览岐江河，欣赏两岸璀璨的城市夜景与历史遗迹。', imageUrl: defaultCover, level: '', price: 50 },
  { id: '108', title: '郑观应故居', classId: '4', address: '中山市三乡镇雍陌村', description: '中国近代早期维新思想家郑观应的出生地。', imageUrl: defaultCover, level: '文物保护', price: 0 }
]

const loadClassifications = () => { classList.value = mockClassList; }

const loadThings = () => {
  if (activeClassId.value === '') {
    thingList.value = mockThingList;
  } else {
    thingList.value = mockThingList.filter(item => item.classId === activeClassId.value);
  }
}

const switchCategory = (id: string) => {
  activeClassId.value = id
  loadThings()
}

const goToDetail = (id: string) => {
  router.push({ name: 'detail', query: { id: id } })
}

onMounted(() => {
  loadClassifications()
  loadThings()
})
</script>

<style scoped lang="less">
/* --- 样式完全保持与之前一致 --- */
@primary-color: #0b6a65;
@accent-color: #c59d5f;
@bg-color: #f4f7f6;
@text-main: #1f2937;
@text-sub: #6b7280;

.explore-page {
  background-color: @bg-color;
  min-height: 100vh;
}

.explore-hero {
  background: linear-gradient(rgba(11, 106, 101, 0.85), rgba(11, 106, 101, 0.95)), url('/@/assets/images/tourism.jpg') center/cover;
  padding: 100px 20px 80px;
  color: #fff;
  text-align: center;

  .hero-content {
    max-width: 800px;
    margin: 0 auto;
  }

  .page-title {
    font-size: 46px;
    margin-bottom: 12px;
    font-family: 'Noto Serif SC', serif;
    letter-spacing: 4px;
  }

  .page-subtitle {
    font-size: 18px;
    color: @accent-color;
    margin-bottom: 24px;
    letter-spacing: 2px;
    text-transform: uppercase;
  }

  .header-desc {
    font-size: 16px;
    line-height: 1.8;
    opacity: 0.9;
  }
}

.category-section {
  max-width: 1200px;
  margin: -30px auto 40px;
  position: relative;
  z-index: 10;
  padding: 0 20px;

  .category-tabs {
    background: #fff;
    border-radius: 50px;
    padding: 10px 20px;
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);

    .tab-item {
      padding: 10px 24px;
      border-radius: 30px;
      font-size: 15px;
      color: @text-sub;
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 500;

      &:hover {
        color: @primary-color;
        background: rgba(11, 106, 101, 0.05);
      }

      &.active {
        background: @primary-color;
        color: #fff;
        box-shadow: 0 4px 12px rgba(11, 106, 101, 0.3);
      }
    }
  }
}

.thing-grid-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 80px;

  .thing-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30px;
  }
}

.thing-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);

    .cover-img {
      transform: scale(1.06);
    }
  }

  .card-cover {
    height: 240px;
    overflow: hidden;
    position: relative;

    .cover-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.6s ease;
    }

    .level-badge {
      position: absolute;
      top: 16px;
      left: 16px;
      background: rgba(0, 0, 0, 0.6);
      color: #fff;
      backdrop-filter: blur(4px);
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: bold;
      border: 1px solid rgba(255, 255, 255, 0.2);
    }
  }

  .card-info {
    padding: 24px;
    display: flex;
    flex-direction: column;
    flex: 1;

    .thing-title {
      font-size: 20px;
      color: @text-main;
      margin-bottom: 10px;
      font-weight: 600;
    }

    .info-row {
      margin-bottom: 12px;
      font-size: 13px;
      color: #9ca3af;

      .icon-location {
        margin-right: 4px;
      }
    }

    .thing-desc {
      font-size: 14px;
      color: @text-sub;
      line-height: 1.6;
      margin-bottom: 24px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-footer {
      margin-top: auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-top: 1px solid #f3f4f6;
      padding-top: 16px;

      .price-box {
        color: #ef4444;

        .currency {
          font-size: 14px;
        }

        .price {
          font-size: 22px;
          font-weight: bold;
          margin: 0 2px;
        }

        .unit {
          font-size: 12px;
          color: #9ca3af;
        }

        .free {
          color: @primary-color;
          font-weight: bold;
          font-size: 16px;
        }
      }

      .book-btn {
        background: @accent-color;
        color: #fff;
        border: none;
        padding: 8px 20px;
        border-radius: 20px;
        font-size: 14px;
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: darken(@accent-color, 10%);
        }
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #9ca3af;

  .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
    opacity: 0.5;
  }
}

@media (max-width: 1024px) {
  .thing-grid-container .thing-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .thing-grid-container .thing-grid {
    grid-template-columns: 1fr;
  }

  .category-tabs {
    border-radius: 12px !important;
  }
}
</style>