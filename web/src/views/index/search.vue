<template>
  <div class="search-page">
    <Header />

    <div class="search-container">
      <div class="search-header">
        <h1>搜索结果</h1>
        <p class="search-meta">关于 <span>“{{ currentKeyword }}”</span> 的搜索结果，共找到 <span>{{ resultList.length }}</span> 条内容
        </p>
      </div>

      <div v-if="loading" class="status-box">
        <a-spin size="large" tip="正在为您努力寻找..." />
      </div>

      <div v-else-if="resultList.length === 0" class="status-box empty">
        <img src="/@/assets/images/searchIcon.svg" alt="空" />
        <p>抱歉，没有找到相关内容，换个词试试吧？</p>
      </div>

      <div v-else class="result-grid">
        <div class="result-card" v-for="item in resultList" :key="item.id" @click="goToDetail(item.id)">
          <div class="card-cover">
            <img :src="item.cover ? (BASE_URL + '/api/staticfiles/image/' + item.cover) : '/images/place.jpg'"
              alt="封面" />
          </div>
          <div class="card-info">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-desc">{{ item.description || '暂无简介' }}</p>
            <div class="card-bottom">
              <span class="price" v-if="item.price > 0">￥{{ item.price }}</span>
              <span class="price free" v-else>免费</span>
              <span class="location">📍 {{ item.address || '中山市' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
// 这里引用你景区/项目的接口
import { listApi } from '/@/api/thing'
import { BASE_URL } from '/@/store/constants'

const route = useRoute()
const router = useRouter()

const currentKeyword = ref('')
const resultList = ref([])
const loading = ref(false)

// 发起后端搜索请求的方法
const fetchSearchResults = (keyword) => {
  currentKeyword.value = keyword
  loading.value = true

  // 调用你后台的接口，传入 keyword 参数进行模糊搜索
  listApi({ keyword: keyword }).then(res => {
    if (res.code === 200) {
      resultList.value = res.data
    }
  }).finally(() => {
    loading.value = false
  })
}

// 页面第一次加载时执行
onMounted(() => {
  const kw = route.query.keyword
  if (kw) {
    fetchSearchResults(kw)
  }
})

// 监听路由参数的变化 (解决当用户已经在搜索页，再次在头部输入新词搜索时页面不刷新的问题)
watch(
  () => route.query.keyword,
  (newKeyword) => {
    if (newKeyword) {
      fetchSearchResults(newKeyword)
    }
  }
)

// 跳转到详情页
const goToDetail = (id) => {
  router.push({ name: 'detail', query: { id: id } })
}
</script>

<style scoped lang="less">
@primary-color: #0b6a65;

.search-page {
  background-color: #f4f6f9;
  min-height: 100vh;
}

.search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 120px 20px 80px;
  min-height: 60vh;
}

.search-header {
  margin-bottom: 40px;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 20px;

  h1 {
    font-size: 32px;
    font-weight: bold;
    color: #1f2937;
    margin-bottom: 10px;
  }

  .search-meta {
    font-size: 16px;
    color: #6b7280;

    span {
      color: @primary-color;
      font-weight: bold;
      margin: 0 4px;
    }
  }
}

.status-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: #9ca3af;

  &.empty img {
    width: 120px;
    opacity: 0.5;
    margin-bottom: 20px;
  }
}

/* 搜索结果网格排版 */
.result-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.result-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  }

  .card-cover {
    height: 200px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s;
    }
  }

  &:hover .card-cover img {
    transform: scale(1.05);
  }

  .card-info {
    padding: 20px;

    .card-title {
      font-size: 18px;
      font-weight: 600;
      color: #1f2937;
      margin-bottom: 8px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .card-desc {
      font-size: 14px;
      color: #6b7280;
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-bottom {
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-top: 1px solid #f3f4f6;
      padding-top: 16px;

      .price {
        font-size: 18px;
        color: #ef4444;
        font-weight: bold;

        &.free {
          color: #10b981;
          font-size: 16px;
        }
      }

      .location {
        font-size: 12px;
        color: #9ca3af;
      }
    }
  }
}
</style>