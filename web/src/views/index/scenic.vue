<template>
  <div class="explore-page">
    <Header />

    <div class="page-banner">
      <div class="banner-overlay"></div>
      <div class="banner-content">
        <h1>景点探索</h1>
        <p class="banner-title">Discover Xiangshan</p>
        <p>从历史古迹到自然风光，漫步中山的大街小巷，感受这座城市的独特脉动与人文气息。</p>
      </div>
    </div>

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
            <div class="level-badge" v-if="item.level">{{ item.level }}</div>
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

// 👇 重点1：解封真实接口的引入
import { listApi as getClassificationList } from '/@/api/classification'
import { listApi as getThingList } from '/@/api/thing'
import { BASE_URL } from "/@/store/constants"

const router = useRouter()

const classList = ref<any[]>([])
const thingList = ref<any[]>([])
const activeClassId = ref<string>('')

// 1. 从数据库加载真实的分类数据
const loadClassifications = () => {
  getClassificationList({}).then((res: any) => {
    if (res.code === 200) {
      classList.value = res.data || []
    }
  })
}

// 2. 从数据库加载真实的景区数据
const loadThings = () => {
  const params: any = {}
  // 如果选中了某个分类，就把分类ID传给后端过滤
  if (activeClassId.value !== '') {
    params.c = activeClassId.value
  }

  getThingList(params).then((res: any) => {
    if (res.code === 200) {
      let data = res.data || [];
      // 处理图片路径，将数据库字段映射为前端展示需要的格式
      data.forEach((item: any) => {
        // 图片路径处理
        if (item.cover && !item.cover.startsWith('http')) {
          item.imageUrl = BASE_URL + '/api/staticfiles/image/' + item.cover;
        } else {
          item.imageUrl = item.cover || '';
        }

        // 数据库叫 location，模板里原本写的是 address，这里做个桥接
        item.address = item.location;
      });
      thingList.value = data;
    }
  }).catch((err) => {
    console.error("获取景点失败", err);
  })
}

// 3. 点击切换分类时，重新请求后端
const switchCategory = (id: string) => {
  activeClassId.value = id
  loadThings()
}

// 4. 跳转详情页
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

.page-banner {
  position: relative;
  padding: 140px 20px 80px;
  background-image: url(/@/assets/images/bg2.jpg);
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
    background: rgba(0, 0, 0, 0.50);
  }

  .banner-content {
    position: relative;
    z-index: 2;

    .banner-title {
      font-size: 13px;
      margin: -20px 0 30px;
    }

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