<template>
  <section class="story-section">
    <div class="section-header">
      <h2 class="section-title">香山人物 · 时代回响</h2>
      <p class="section-desc">倾听历史回声，走近从香山走向世界的杰出先驱</p>
      <div class="more-btn" @click="$router.push({ name: 'celebrity' })">
        查看更多人物 <span class="arrow">&rarr;</span>
      </div>
    </div>

    <div class="story-layout">
      <div class="featured-story" v-if="featuredStory" @click="openStory(featuredStory)">
        <img :src="featuredStory.image" alt="主推人物" class="featured-img">
        <div class="featured-overlay">
          <div class="quote" v-if="featuredStory.tags">“{{ featuredStory.tags }}”</div>
          <h3 class="name">{{ featuredStory.title }}</h3>
          <p class="brief">{{ featuredStory.brief }}</p>
        </div>
      </div>
      <div class="featured-story skeleton-box" v-else></div>

      <div class="side-stories" v-if="sideStories.length > 0">
        <div class="story-item" v-for="(item, index) in sideStories" :key="index" @click="openStory(item)">
          <div class="story-img-box">
            <img :src="item.image" alt="人物图" class="story-img">
          </div>
          <div class="story-info">
            <h4 class="story-name">{{ item.title }}</h4>
            <p class="story-tags">{{ item.tags }}</p>
            <p class="story-desc">{{ item.brief }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
// 引入写好的API和基础图片路径配置
import { listApi } from '/@/api/celebrity'
import { BASE_URL } from "/@/store/constants"

const router = useRouter()

// 定义响应式变量存放数据
const featuredStory = ref(null)
const sideStories = ref([])

// 获取数据库人物数据
const getCelebrityData = () => {
  listApi({}).then(res => {
    if (res.code === 200) {
      let dbData = res.data || [];

      // 预处理图片路径：加上服务器前缀
      dbData.forEach(item => {
        if (item.image && !item.image.startsWith('http')) {
          item.image = BASE_URL + '/api/staticfiles/image/' + item.image;
        }
      });

      // 拆分数据：第 1 个作为主推，第 2~4 个作为右侧列表
      if (dbData.length > 0) {
        featuredStory.value = dbData[0];
        sideStories.value = dbData.slice(1, 4);
      }
    }
  }).catch(err => {
    console.error("获取名人故事数据失败：", err);
  });
}

onMounted(() => {
  getCelebrityData()
})

const openStory = (story) => {
  router.push({
    name: 'celebrityDetail',
    params: {
      id: story?.id || ''
    }
  })
}
</script>

<style scoped lang="less">
/* === 这里的样式保持上一条消息的完全不变 === */
@primary-color: #0b6a65;
@accent-color: #c59d5f;

.story-section {
  padding: 80px 20px;
  background-color: #ffffff;
  position: relative;

  .section-header {
    max-width: 1200px;
    margin: 0 auto 40px;
    position: relative;
    text-align: center;

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
    }

    .more-btn {
      position: absolute;
      right: 0;
      bottom: 0;
      font-size: 15px;
      color: @primary-color;
      font-weight: 600;
      cursor: pointer;
      display: flex;
      align-items: center;
      transition: all 0.3s;

      .arrow {
        margin-left: 6px;
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

  .story-layout {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    gap: 30px;
    height: 500px;
  }

  .featured-story {
    flex: 6;
    position: relative;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);

    &.skeleton-box {
      background-color: #f1f5f9;
    }

    .featured-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.8s ease;
    }

    .featured-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      width: 100%;
      padding: 60px 40px 40px;
      background: linear-gradient(to top, rgba(11, 106, 101, 0.95), transparent);
      color: #fff;
      display: flex;
      flex-direction: column;
      justify-content: flex-end;
    }

    .quote {
      font-family: 'Noto Serif SC', serif;
      font-size: 24px;
      color: @accent-color;
      font-weight: 700;
      margin-bottom: 12px;
      font-style: italic;
    }

    .name {
      font-size: 36px;
      font-weight: 700;
      margin-bottom: 12px;
      letter-spacing: 2px;
    }

    .brief {
      font-size: 15px;
      line-height: 1.6;
      opacity: 0.9;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    &:hover .featured-img {
      transform: scale(1.05);
    }
  }

  .side-stories {
    flex: 4;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .story-item {
    display: flex;
    align-items: center;
    height: calc((100% - 40px) / 3);
    background: #f8fafc;
    border-radius: 12px;
    padding: 16px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid transparent;

    &:hover {
      background: #ffffff;
      border-color: @accent-color;
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
      transform: translateX(-5px);
    }

    .story-img-box {
      width: 100px;
      height: 100%;
      border-radius: 8px;
      overflow: hidden;
      flex-shrink: 0;
      margin-right: 20px;

      .story-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .story-info {
      flex: 1;
      overflow: hidden;

      .story-name {
        font-family: 'Noto Serif SC', serif;
        font-size: 20px;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 6px;
      }

      .story-tags {
        font-size: 12px;
        color: @accent-color;
        margin-bottom: 10px;
        font-weight: 500;
      }

      .story-desc {
        font-size: 13px;
        color: #64748b;
        line-height: 1.5;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
    }
  }
}

@media (max-width: 768px) {
  .story-section .story-layout {
    flex-direction: column;
    height: auto;
  }

  .story-section .featured-story {
    height: 400px;
  }

  .story-section .side-stories {
    gap: 16px;
  }

  .story-section .story-item {
    height: 140px;
  }
}
</style>