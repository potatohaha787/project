<template>
  <div class="travelogue-detail-page">
    <Header />

    <div class="article-hero" :style="{ backgroundImage: `url(${article.cover})` }">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <div class="hero-meta">
          <a-tag color="rgba(255, 255, 255, 0.2)" class="location-tag">
            📍 {{ article.location }}
          </a-tag>
          <span class="publish-time">{{ article.time }}</span>
        </div>
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="hero-stats">
          <span><i class="iconfont icon-eye"></i> {{ article.views }} 阅读</span>
          <span class="divider">|</span>
          <span><i class="iconfont icon-heart"></i> {{ article.likes }} 喜欢</span>
        </div>
      </div>
    </div>

    <div class="detail-container">
      <a-row :gutter="40">

        <a-col :xs="24" :lg="17">
          <div class="article-main-card">

            <div class="article-body" v-html="article.content"></div>

            <div class="article-tags">
              <span class="tag-item" v-for="tag in article.tags" :key="tag"># {{ tag }}</span>
            </div>

            <div class="interaction-bar">
              <div class="action-btn" :class="{ active: isLiked }" @click="toggleLike">
                <div class="icon-circle"><i class="iconfont icon-heart"></i></div>
                <span>{{ isLiked ? '已喜欢' : '喜欢' }} ({{ article.likes + (isLiked ? 1 : 0) }})</span>
              </div>
              <div class="action-btn" :class="{ active: isCollected }" @click="toggleCollect">
                <div class="icon-circle"><i class="iconfont icon-star"></i></div>
                <span>{{ isCollected ? '已收藏' : '收藏' }}</span>
              </div>
              <div class="action-btn share-btn">
                <div class="icon-circle"><i class="iconfont icon-share"></i></div>
                <span>分享</span>
              </div>
            </div>
          </div>

          <div class="comments-card">
            <h3 class="section-title">游记留言 <span>({{ comments.length }})</span></h3>

            <div class="comment-input-box">
              <a-avatar src="https://joeschmoe.io/api/v1/random" :size="40" />
              <div class="input-wrapper">
                <a-textarea v-model:value="commentText" placeholder="留下你的足迹或向作者提问..." :rows="3" />
                <div class="input-actions">
                  <a-button type="primary" class="submit-btn" @click="submitComment"
                    :loading="isSubmitting">发表留言</a-button>
                </div>
              </div>
            </div>

            <div class="comments-list">
              <div class="comment-item" v-for="comment in comments" :key="comment.id">
                <a-avatar :src="comment.avatar" :size="40" />
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="c-name">{{ comment.author }}</span>
                    <span class="c-time">{{ comment.time }}</span>
                  </div>
                  <p class="c-text">{{ comment.content }}</p>
                  <div class="c-actions">
                    <span class="action">回复</span>
                    <span class="action">👍 {{ comment.likes }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </a-col>

        <a-col :xs="24" :lg="7">
          <div class="sidebar-wrapper">
            <div class="related-card">
              <h3 class="sidebar-title">更多香山探索</h3>
              <div class="related-list">
                <div class="related-item" v-for="item in relatedArticles" :key="item.id">
                  <div class="r-cover">
                    <img :src="item.cover" alt="" />
                  </div>
                  <div class="r-info">
                    <h4 class="r-title">{{ item.title }}</h4>
                    <span class="r-meta"><i class="iconfont icon-eye"></i> {{ item.views }}</span>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </a-col>

      </a-row>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import { message } from 'ant-design-vue'

// 引入真实的 API 和 全局常量
import { getPostDetailApi, getPostListApi } from '/@/api/post'
import { BASE_URL } from "/@/store/constants"

const route = useRoute()
const router = useRouter()

// 交互状态
const isLiked = ref(false)
const isCollected = ref(false)
const isFollowed = ref(false)
const isSubmitting = ref(false)
const commentText = ref('')

// 定义游记详细数据（先给一个空结构防止模板渲染报错）
const article = ref({
  id: '',
  title: '',
  cover: '',
  location: '',
  time: '',
  views: 0,
  likes: 0,
  author: '',
  authorAvatar: '',
  authorDesc: '',
  tags: [],
  content: ''
})

const comments = ref([]) // 如果你有评论接口，可仿照游记逻辑请求，此处先置空或保留你的模拟数据
const relatedArticles = ref([]) // 相关游记列表

// 1. 获取游记详情数据
const loadArticleDetail = () => {
  const id = route.query.id
  if (!id) {
    message.error('参数错误：缺少游记ID')
    return
  }

  getPostDetailApi({ id: id }).then((res) => {
    if (res.code === 200) {
      let data = res.data

      // 处理封面图路径
      let coverUrl = data.cover || ''
      if (coverUrl && !coverUrl.startsWith('http')) {
        coverUrl = BASE_URL + '/api/staticfiles/image/' + coverUrl
      }

      // 处理作者头像路径
      let avatarUrl = data.authorAvatar || ''
      if (avatarUrl && !avatarUrl.startsWith('http')) {
        avatarUrl = BASE_URL + '/api/staticfiles/avatar/' + avatarUrl
      }

      // 将数据库字段映射到前端所需的数据结构
      article.value = {
        id: data.id,
        title: data.title,
        cover: coverUrl,
        location: data.location || '未知地点',
        time: data.createTime,
        views: data.pv || 0,                 // 数据库的 pv 映射为 views
        likes: data.likeCount || 0,          // 数据库的 likeCount 映射为 likes
        author: data.authorName || '匿名用户',
        authorAvatar: avatarUrl || 'https://joeschmoe.io/api/v1/random', // 默认头像
        authorDesc: '该用户很懒，什么都没写~',
        tags: data.tags || [],
        content: data.content || '<p>暂无内容</p>'
      }
    } else {
      message.error(res.msg || '获取游记详情失败')
    }
  }).catch((err) => {
    console.error('获取详情异常', err)
  })
}

// 2. 获取右侧“相关游记”推荐 (调用列表接口获取几条 guide 数据)
const loadRelatedArticles = () => {
  // 请求游记类型的数据，并在前端截取前3个作为推荐
  getPostListApi({ type: 'guide' }).then((res) => {
    if (res.code === 200) {
      let list = res.data || []
      // 过滤掉当前正在看的这篇，并取前 3 篇
      list = list.filter(item => String(item.id) !== String(route.query.id)).slice(0, 3)

      relatedArticles.value = list.map(item => {
        let coverUrl = item.cover || ''
        if (coverUrl && !coverUrl.startsWith('http')) {
          coverUrl = BASE_URL + '/api/staticfiles/image/' + coverUrl
        }
        return {
          id: item.id,
          title: item.title,
          views: item.pv || 0,
          cover: coverUrl
        }
      })
    }
  })
}

// 切换喜欢/收藏
const toggleLike = () => {
  isLiked.value = !isLiked.value;
  if (isLiked.value) message.success('点赞成功！')
  // TODO: 后期可以调用点赞后端的 API
}

const toggleCollect = () => {
  isCollected.value = !isCollected.value;
  if (isCollected.value) message.success('已加入收藏夹！')
  // TODO: 后期可以调用收藏后端的 API
}

// 发表评论（模拟交互）
const submitComment = () => {
  if (!commentText.value.trim()) { message.warning('写点什么再发表吧~'); return }
  isSubmitting.value = true
  setTimeout(() => {
    comments.value.unshift({
      id: Date.now(),
      author: '当前用户',
      avatar: 'https://joeschmoe.io/api/v1/random',
      time: '刚刚',
      content: commentText.value,
      likes: 0
    })
    commentText.value = ''
    isSubmitting.value = false
    message.success('留言成功！')
    // TODO: 后期替换为真实的提交评论 API
  }, 800)
}

// 页面挂载时请求数据
onMounted(() => {
  loadArticleDetail()
  loadRelatedArticles()
})
</script>

<style scoped lang="less">
@bg-color: #f4f6f9;
@zs-green: #064e3b;
/* 深翠绿 */
@zs-yellow: #d97706;
/* 琥珀黄 */
@dark-text: #1e293b;
@light-text: #64748b;

.travelogue-detail-page {
  background-color: @bg-color;
  min-height: 100vh;
  padding-bottom: 100px;
}

/* --- 1. 沉浸式宽幅头图 --- */
.article-hero {
  position: relative;
  height: 500px;
  /* 宽幅大图，视觉冲击力强 */
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  /* 视差滚动效果 */
  display: flex;
  align-items: flex-end;
  padding-bottom: 60px;

  .hero-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    /* 从上到下渐变，底部较深以突出文字 */
    background: linear-gradient(to bottom, rgba(6, 40, 30, 0.1) 0%, rgba(6, 40, 30, 0.8) 100%);
  }

  .hero-content {
    position: relative;
    z-index: 2;
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
    padding: 0 20px;
    color: #fff;

    .hero-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 16px;

      .location-tag {
        border: 1px solid rgba(255, 255, 255, 0.4);
        border-radius: 20px;
        padding: 4px 12px;
        font-size: 13px;
        backdrop-filter: blur(4px);
      }

      .publish-time {
        font-size: 14px;
        opacity: 0.8;
      }
    }

    .article-title {
      font-size: 44px;
      font-weight: 800;
      color: #fff;
      margin-bottom: 20px;
      line-height: 1.3;
      text-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
      max-width: 900px;
    }

    .hero-stats {
      font-size: 14px;
      opacity: 0.9;
      display: flex;
      gap: 12px;
      align-items: center;

      .divider {
        opacity: 0.5;
      }
    }
  }
}

.detail-container {
  max-width: 1200px;
  margin: -40px auto 0;
  /* 负边距让内容区往上浮动，压住头图 */
  position: relative;
  z-index: 10;
  padding: 0 20px;
}

/* --- 2. 左侧：正文与互动区 --- */
.article-main-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
}

/* 杂志级排版正文 */
.article-body {
  font-size: 17px;
  color: #334155;
  line-height: 2;
  letter-spacing: 0.5px;

  :deep(p) {
    margin-bottom: 24px;
  }

  :deep(.lead-text) {
    font-size: 18px;
    color: @zs-green;
    font-weight: 600;
    padding-left: 16px;
    border-left: 4px solid @zs-yellow;
    background: #f8fafc;
    padding: 16px;
    border-radius: 0 8px 8px 0;
  }

  :deep(h3) {
    font-size: 22px;
    font-weight: bold;
    color: @dark-text;
    margin: 40px 0 20px;
    display: flex;
    align-items: center;
    gap: 8px;

    &::before {
      content: '';
      display: block;
      width: 6px;
      height: 22px;
      background: @zs-green;
      border-radius: 3px;
    }
  }

  :deep(img) {
    width: 100%;
    max-height: 600px;
    object-fit: cover;
    border-radius: 12px;
    margin: 10px 0;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  }

  :deep(.img-caption) {
    display: block;
    text-align: center;
    font-size: 13px;
    color: @light-text;
    margin-bottom: 24px;
  }

  :deep(ul) {
    background: #f8fafc;
    padding: 24px 24px 24px 40px;
    border-radius: 12px;

    li {
      margin-bottom: 12px;
      font-size: 16px;
    }
  }
}

.article-tags {
  margin-top: 40px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding-bottom: 30px;
  border-bottom: 1px dashed #e2e8f0;

  .tag-item {
    color: @zs-green;
    background: rgba(6, 78, 59, 0.05);
    padding: 6px 16px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.3s;
    cursor: pointer;

    &:hover {
      background: @zs-green;
      color: #fff;
    }
  }
}

/* 底部互动点赞条 */
.interaction-bar {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 30px;

  .action-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    color: @light-text;
    transition: all 0.3s;

    .icon-circle {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      background: #f1f5f9;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      transition: all 0.3s;
    }

    span {
      font-size: 14px;
      font-weight: 500;
    }

    &:hover {
      color: @dark-text;

      .icon-circle {
        background: #e2e8f0;
        transform: scale(1.05);
      }
    }

    &.active {
      color: @zs-yellow;

      .icon-circle {
        background: rgba(217, 119, 6, 0.1);
        color: @zs-yellow;
      }
    }

    &.share-btn:hover {
      color: @zs-green;

      .icon-circle {
        background: rgba(6, 78, 59, 0.1);
        color: @zs-green;
      }
    }
  }
}

/* 评论区 */
.comments-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.02);

  .section-title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 24px;

    span {
      color: @light-text;
      font-weight: normal;
      font-size: 16px;
    }
  }

  .comment-input-box {
    display: flex;
    gap: 16px;
    margin-bottom: 40px;

    .input-wrapper {
      flex: 1;

      :deep(.ant-input) {
        border-radius: 8px;
        background: #f8fafc;
        border-color: #e2e8f0;
        resize: none;

        &:focus {
          border-color: @zs-green;
          box-shadow: none;
          background: #fff;
        }
      }

      .input-actions {
        text-align: right;
        margin-top: 12px;

        .submit-btn {
          background: @zs-green;
          border-color: @zs-green;
          border-radius: 20px;
          padding: 0 24px;
        }
      }
    }
  }

  .comment-item {
    display: flex;
    gap: 16px;
    padding: 20px 0;
    border-bottom: 1px solid #f1f5f9;

    &:last-child {
      border-bottom: none;
      padding-bottom: 0;
    }

    .comment-content {
      flex: 1;

      .comment-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;

        .c-name {
          font-weight: 600;
          color: @dark-text;
        }

        .c-time {
          font-size: 12px;
          color: #94a3b8;
        }
      }

      .c-text {
        font-size: 15px;
        color: #475569;
        line-height: 1.6;
        margin-bottom: 12px;
      }

      .c-actions {
        font-size: 13px;
        color: #94a3b8;
        display: flex;
        gap: 16px;

        .action {
          cursor: pointer;

          &:hover {
            color: @zs-green;
          }
        }
      }
    }
  }
}

/* --- 3. 右侧：侧边栏 --- */
.sidebar-wrapper {
  position: sticky;
  top: 100px;
  /* 悬浮侧边栏，页面滚动时保持在视口内 */
}

/* 相关游记 */
.related-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.03);

  .sidebar-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 20px;
    border-left: 4px solid @zs-yellow;
    padding-left: 10px;
  }

  .related-item {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
    cursor: pointer;

    &:last-child {
      margin-bottom: 0;
    }

    &:hover .r-title {
      color: @zs-green;
    }

    .r-cover {
      width: 80px;
      height: 60px;
      flex-shrink: 0;
      border-radius: 6px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s;
      }
    }

    &:hover .r-cover img {
      transform: scale(1.1);
    }

    .r-info {
      display: flex;
      flex-direction: column;
      justify-content: space-between;

      .r-title {
        font-size: 14px;
        font-weight: 600;
        color: @dark-text;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        transition: color 0.3s;
      }

      .r-meta {
        font-size: 12px;
        color: #94a3b8;
      }
    }
  }
}

@media (max-width: 992px) {
  .article-hero {
    height: 350px;

    .hero-content .article-title {
      font-size: 30px;
    }
  }

  .detail-container {
    margin-top: -20px;
  }

  .article-main-card,
  .comments-card {
    padding: 20px;
  }

  .sidebar-wrapper {
    position: static;
    margin-top: 24px;
  }
}
</style>