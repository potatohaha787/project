<template>
  <div class="search-page">
    <Header />

    <div class="search-container">
      <div class="decoration-leaf"></div>

      <div class="search-header">
        <div class="title-wrapper">
          <span class="sub-title">EXPLORE ZHONGSHAN</span>
          <h1>探索中山</h1>
          <div class="line"></div>
        </div>
        <p class="search-meta">
          为您找到关于 <span>“{{ currentKeyword }}”</span> 的文旅灵感，共 <span>{{ resultList.length }}</span> 处
        </p>
      </div>

      <div v-if="loading" class="status-box">
        <a-spin size="large" tip="正在翻阅中山文旅画卷..." />
      </div>

      <div v-else-if="resultList.length === 0" class="status-box empty">
        <div class="empty-icon">🍃</div>
        <p>暂未发现相关足迹，建议尝试搜索“孙中山故居”或“石岐”</p>
      </div>

      <div v-else class="result-grid">
        <div class="result-card" v-for="item in resultList" :key="item.id" @click="goToDetail(item.id)">
          <div class="card-cover">
            <img :src="item.cover ? (BASE_URL + '/api/staticfiles/image/' + item.cover) : '/images/place.jpg'"
              alt="封面" />
            <div class="card-tag" v-if="item.tags">{{ item.tags }}</div>
          </div>
          <div class="card-info">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-desc">{{ item.description || '在这里，遇见中山的过去与未来。' }}</p>
            <div class="card-bottom">
              <div class="price-box">
                <span class="unit" v-if="item.price > 0">￥</span>
                <span class="price" :class="{ free: item.price <= 0 }">{{ item.price > 0 ? item.price : '免费开放' }}</span>
              </div>
              <span class="location"><i class="iconfont icon-location"></i> {{ item.address || '中山市' }}</span>
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
// 定义中山文旅专属色系
@zs-green: #064e3b; // 深翠绿
@zs-accent: #d97706; // 琥珀橙
@zs-bg: #f8f9fa;

.search-page {
  background-color: @zs-bg;
  background-image: radial-gradient(#e5e7eb 1px, transparent 1px);
  background-size: 40px 40px; // 微弱的点状底纹增加纸质感
  min-height: 100vh;
  position: relative;
}

.search-container {
  max-width: 1240px;
  margin: 0 auto;
  padding: 140px 20px 100px;
  position: relative;
  z-index: 1;
}

// 装饰性的叶子图标或形状
.decoration-leaf {
  position: absolute;
  top: 100px;
  right: 20px;
  width: 150px;
  height: 150px;
  background: url('https://api.iconify.design/ri:leaf-fill.svg?color=%23064e3b&alpha=0.05') no-repeat;
  z-index: -1;
}

.search-header {
  margin-bottom: 60px;
  text-align: left;

  .title-wrapper {
    position: relative;
    margin-bottom: 15px;

    .sub-title {
      font-size: 12px;
      letter-spacing: 4px;
      color: @zs-accent;
      font-weight: bold;
      display: block;
      margin-bottom: 4px;
    }

    h1 {
      font-size: 38px;
      font-family: "PingFang SC", "Microsoft YaHei", serif;
      font-weight: 800;
      color: #111827;
      margin: 0;
    }

    .line {
      width: 60px;
      height: 4px;
      background: @zs-green;
      margin-top: 10px;
      border-radius: 2px;
    }
  }

  .search-meta {
    font-size: 16px;
    color: #6b7280;

    span {
      color: @zs-green;
      font-weight: 600;
      border-bottom: 1px solid @zs-green;
      margin: 0 4px;
    }
  }
}

/* 结果网格优化 */
.result-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 32px;
}

.result-card {
  background: #ffffff;
  border-radius: 4px; // 更加硬朗、高级的微圆角
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  border: 1px solid #eee;
  position: relative;

  &:hover {
    transform: translateY(-10px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
    border-color: @zs-green;
  }

  .card-cover {
    height: 220px;
    overflow: hidden;
    position: relative;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.8s cubic-bezier(0.165, 0.84, 0.44, 1);
    }

    .card-tag {
      position: absolute;
      top: 12px;
      left: 12px;
      background: rgba(255, 255, 255, 0.9);
      padding: 4px 12px;
      font-size: 12px;
      color: @zs-green;
      font-weight: bold;
      backdrop-filter: blur(4px);
    }
  }

  &:hover .card-cover img {
    transform: scale(1.1);
  }

  .card-info {
    padding: 24px;

    .card-title {
      font-size: 20px;
      font-weight: 700;
      color: #1a1a1a;
      margin-bottom: 12px;
    }

    .card-desc {
      font-size: 14px;
      color: #666;
      line-height: 1.6;
      margin-bottom: 20px;
      height: auto; // 取消固定高度，让内容根据行数自适应
      display: -webkit-box; // 必须结合 box 使用
      -webkit-box-orient: vertical; // 设置伸缩盒子的子元素排列方式
      -webkit-line-clamp: 3; // 限制显示的行数
      overflow: hidden; // 超出部分隐藏
      text-overflow: ellipsis; // 溢出显示省略号
      word-break: break-all;
    }

    .card-bottom {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 16px;
      border-top: 1px dashed #eee;

      .price-box {
        .unit {
          font-size: 14px;
          color: @zs-accent;
        }

        .price {
          font-size: 20px;
          color: @zs-accent;
          font-weight: 800;

          &.free {
            color: @zs-green;
            font-size: 15px;
            letter-spacing: 1px;
          }
        }
      }

      .location {
        font-size: 13px;
        color: #999;
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}

// 状态箱
.status-box {
  background: white;
  border-radius: 12px;
  padding: 100px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  &.empty .empty-icon {
    font-size: 64px;
    margin-bottom: 20px;
  }

  p {
    color: #9ca3af;
  }
}
</style>