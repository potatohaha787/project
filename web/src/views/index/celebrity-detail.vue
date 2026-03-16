<template>
  <div class="celebrity-detail-container">
    <div class="top-bar">
      <div class="back-btn" @click="$router.back()">
        <span class="arrow">&larr;</span> 返回上一页
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <p>正在加载人物故事...</p>
    </div>

    <div v-else-if="celebrity" class="detail-content">
      <div class="header-section">
        <div class="image-wrapper">
          <img :src="celebrity.image" :alt="celebrity.title" class="avatar" />
        </div>
        <div class="info-wrapper">
          <div class="tags" v-if="celebrity.tags">
            <span class="tag" v-for="(tag, index) in celebrity.tags.split(',')" :key="index">
              {{ tag }}
            </span>
          </div>
          <h1 class="name">{{ celebrity.title }}</h1>
          <div class="divider"></div>
          <p class="brief">{{ celebrity.brief }}</p>
        </div>
      </div>

      <div class="story-section">
        <h2 class="section-title">人物生平</h2>
        <div class="rich-text-content" v-html="celebrity.description || celebrity.brief"></div>
      </div>
    </div>

    <div v-else class="empty-state">
      <p>未找到该人物信息</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
// 核心修改 1：不引入 detailApi，而是复用现有的 listApi
import { listApi } from '/@/api/celebrity'
import { BASE_URL } from '/@/store/constants'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const celebrity = ref(null)

const fetchDetail = () => {
  const targetId = route.query.id
  if (!targetId) {
    loading.value = false
    return
  }

  // 核心修改 2：请求列表数据，然后在前端进行筛选
  listApi({}).then(res => {
    if (res.code === 200 && res.data) {
      const dataList = res.data

      // 在数组中找到 id 匹配的人物
      const foundItem = dataList.find(item => String(item.id) === String(targetId))

      if (foundItem) {
        // 处理图片路径
        if (foundItem.image && !foundItem.image.startsWith('http')) {
          foundItem.image = BASE_URL + '/api/staticfiles/image/' + foundItem.image
        }
        celebrity.value = foundItem
      }
    }
  }).catch(err => {
    console.error('获取详情失败:', err)
  }).finally(() => {
    loading.value = false
  })
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped lang="less">
@primary-color: #0b6a65;
@accent-color: #c59d5f;

.celebrity-detail-container {
  min-height: 100vh;
  background-color: #fcfbf9;
  /* 宣纸底色 */
  padding: 40px 20px 100px;
  font-family: 'Noto Serif SC', serif;
}

.top-bar {
  max-width: 1000px;
  margin: 0 auto 30px;

  .back-btn {
    display: inline-flex;
    align-items: center;
    font-size: 15px;
    color: #666;
    cursor: pointer;
    transition: color 0.3s;
    font-family: sans-serif;

    .arrow {
      margin-right: 6px;
      font-size: 18px;
      transition: transform 0.3s;
    }

    &:hover {
      color: @primary-color;

      .arrow {
        transform: translateX(-4px);
      }
    }
  }
}

.detail-content {
  max-width: 1000px;
  margin: 0 auto;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* 头部排版 */
.header-section {
  display: flex;
  padding: 60px;
  background: linear-gradient(135deg, #ffffff 0%, #f4f6f5 100%);
  gap: 50px;
  align-items: center;
  border-bottom: 1px solid #eaeaea;

  .image-wrapper {
    flex-shrink: 0;
    width: 280px;
    height: 380px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 15px 30px rgba(11, 106, 101, 0.15);

    .avatar {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .info-wrapper {
    flex: 1;

    .tags {
      margin-bottom: 16px;

      .tag {
        display: inline-block;
        font-size: 13px;
        color: @accent-color;
        border: 1px solid @accent-color;
        padding: 4px 12px;
        border-radius: 20px;
        margin-right: 10px;
        font-weight: 500;
      }
    }

    .name {
      font-size: 42px;
      color: #1f2937;
      font-weight: 700;
      letter-spacing: 4px;
      margin: 0 0 20px 0;
    }

    .divider {
      width: 60px;
      height: 4px;
      background-color: @primary-color;
      margin-bottom: 24px;
    }

    .brief {
      font-size: 16px;
      color: #4b5563;
      line-height: 1.8;
      text-align: justify;
    }
  }
}

/* 正文排版 */
.story-section {
  padding: 60px;

  .section-title {
    font-size: 24px;
    color: @primary-color;
    margin-bottom: 30px;
    display: flex;
    align-items: center;

    &::before {
      content: '';
      display: inline-block;
      width: 6px;
      height: 24px;
      background-color: @accent-color;
      margin-right: 12px;
    }
  }

  .rich-text-content {
    font-size: 16px;
    color: #333;
    line-height: 2;
    text-align: justify;
    font-family: sans-serif;

    /* 针对可能存在的 p 标签缩进 */
    :deep(p) {
      margin-bottom: 20px;
      text-indent: 2em;
    }
  }
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 100px 0;
  font-size: 18px;
  color: #999;
}

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    padding: 30px 20px;

    .image-wrapper {
      width: 100%;
      height: auto;
      aspect-ratio: 3/4;
    }
  }

  .story-section {
    padding: 30px 20px;
  }
}
</style>