<template>
  <div class="celebrity-gallery-page">
    <Header />

    <div class="page-banner">
      <div class="banner-overlay"></div>
      <div class="banner-content">
        <h1>香山人物</h1>
        <p class="banner-title">Figures of Xiangshan</p>
        <p>寻访香山历史，致敬时代先驱——在这片海滨沃土，诞生了无数改变中国历史的杰出人物。</p>
      </div>
    </div>

    <section class="gallery-grid-container">
      <div class="gallery-grid">
        <div class="celebrity-card" v-for="(item, index) in celebrities" :key="item.id || index">
          <div class="card-image-box">
            <img :src="item.imageUrl" :alt="item.title" class="card-img" />
          </div>
          <div class="card-info">
            <p class="card-tag">{{ item.tags }}</p>
            <h3 class="card-name">{{ item.title }}</h3>
            <p class="card-desc">{{ item.brief }}</p>
            <div class="card-actions">
              <span class="action-icon achievement-icon">
                <i class="icon-medal"></i>
                成就
              </span>
              <span class="action-icon detail-icon">
                <i class="icon-details"></i>
                详情
              </span>
              <router-link :to="{ name: 'celebrityDetail', params: { id: item.id } }" class="detail-btn">
                了解详情
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <div v-if="celebrities.length === 0" style="text-align: center; padding: 40px; color: #94a3b8;">
        正在加载人物数据或暂无数据...
      </div>
    </section>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'

// 👇 引入后台接口和常量配置
import { listApi } from '/@/api/celebrity'
import { BASE_URL } from "/@/store/constants"

// 响应式数据，存放数据库返回的人物列表
const celebrities = ref<any[]>([])

// 获取数据库数据的方法
const getCelebrityData = () => {
  listApi({}).then((res: any) => {
    if (res.code === 200) {
      let dbData = res.data || [];

      // 遍历数据，处理图片路径
      dbData.forEach((item: any) => {
        if (item.image && !item.image.startsWith('http')) {
          item.imageUrl = BASE_URL + '/api/staticfiles/image/' + item.image;
        } else {
          item.imageUrl = item.image || ''; // 防止空图报错
        }
      });

      // 赋值给页面变量
      celebrities.value = dbData;
    }
  }).catch((err) => {
    console.error("获取名人故事数据失败：", err);
  });
}

// 页面挂载时调用
onMounted(() => {
  getCelebrityData()
})
</script>

<style scoped lang="less">
// 延续首页的配色方案
@primary-color: #0b6a65; // 岩石绿
@accent-color: #c59d5f; // 暖金
@bg-color: #f8fafc; // 暖白背景
@text-color: #1f2937; // 深灰文字

.celebrity-gallery-page {
  background-color: @bg-color;
  color: @text-color;
  min-height: 100vh;
}

.page-banner {
  position: relative;
  padding: 140px 20px 80px;
  background-image: url(/@/assets/images/bg5.jpg);
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

.gallery-grid-container {
  padding: 80px 20px;
  background-color: #fff;
  border-radius: 40px 40px 0 0;
  margin-top: -40px;
  position: relative;
  z-index: 10;
  min-height: 500px;
  /* 保证加载时也有一定高度 */
}

.gallery-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.celebrity-card {
  background-color: #fff;
  border: 1px solid rgba(11, 106, 101, 0.1);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 16px 36px rgba(0, 0, 0, 0.1);
    border-color: @accent-color;
  }

  .card-image-box {
    width: 100%;
    height: 300px;
    overflow: hidden;

    .card-img {
      width: 100%;
      height: 100%;
      object-fit: cover; // 统一裁剪
      object-position: top;
      transition: transform 0.8s ease;
    }
  }

  &:hover .card-img {
    transform: scale(1.05);
  }

  .card-info {
    padding: 24px;
    display: flex;
    flex-direction: column;
    flex: 1;
    /* 让内容区撑开，把按钮挤到底部 */

    .card-tag {
      font-size: 12px;
      color: @accent-color;
      margin-bottom: 8px;
      font-weight: 600;
      /* 处理标签过长换行问题 */
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .card-name {
      font-size: 24px;
      margin-bottom: 14px;
      font-family: 'Noto Serif SC', serif;
      color: @primary-color;
    }

    .card-desc {
      font-size: 14px;
      color: #64748b;
      line-height: 1.6;
      margin-bottom: 24px;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-actions {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-top: auto;
      /* 自动推到最底部 */

      .action-icon {
        font-size: 12px;
        color: #94a3b8;
        display: flex;
        align-items: center;

        i {
          margin-right: 4px;
        }
      }

      .detail-btn {
        margin-left: auto;
        font-size: 14px;
        color: @accent-color;
        font-weight: 600;
        cursor: pointer;
        transition: color 0.3s ease;
        text-decoration: none;
        /* 移除 a 标签下划线 */

        &:hover {
          color: @primary-color;
        }
      }
    }
  }
}

// 适配不同屏幕尺寸
@media (max-width: 1024px) {
  .gallery-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .gallery-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .gallery-grid {
    grid-template-columns: repeat(1, 1fr);
  }
}
</style>