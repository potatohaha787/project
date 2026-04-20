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

        <div class="hero-author" style="display: flex; align-items: center; gap: 12px; margin-bottom: 20px;">
          <a-avatar :src="article.authorAvatar" :size="36" style="border: 2px solid #fff;" />
          <span class="author-name" style="font-size: 15px; font-weight: 500;">{{ article.author }}</span>
        </div>
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
                <div class="icon-circle">
                  <img :src="WantIcon" class="action-icon" />
                </div>
                <span>{{ isLiked ? '已喜欢' : '喜欢' }} ({{ article.likes }})</span>
              </div>
              <div class="action-btn" :class="{ active: isCollected }" @click="toggleCollect">
                <div class="icon-circle">
                  <img :src="RecommendIcon" class="action-icon" />
                </div>
                <span>{{ isCollected ? '已收藏' : '收藏' }}</span>
              </div>
              <div class="action-btn share-btn">
                <div class="icon-circle">
                  <img :src="ShareIcon" class="action-icon" />
                </div>
                <span>分享</span>
              </div>
            </div>
          </div>

          <div class="comments-card">
            <h3 class="section-title">游记留言 <span>({{ comments.length }})</span></h3>

            <div class="comment-input-box">
              <a-avatar :src="currentUserAvatar" :size="40" />
              <div class="input-wrapper">
                <a-textarea ref="commentInputRef" v-model:value="commentText" placeholder="留下你的足迹或向作者提问..." :rows="3" />
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
                    <span class="action" @click="handleReply(comment)">回复</span>
                    <span class="action"
                      :style="{ color: comment.hasLiked ? '#d97706' : '', fontWeight: comment.hasLiked ? 'bold' : 'normal' }"
                      @click="handleLikeComment(comment)">
                      {{ comment.hasLiked ? '已赞' : '👍' }} {{ comment.likes }}
                    </span>
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
                <div class="related-item" v-for="item in relatedArticles" :key="item.id" @click="goToDetail(item.id)">
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
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import { message } from 'ant-design-vue'
import WantIcon from '/@/assets/images/want-read-hover.svg';
import RecommendIcon from '/@/assets/images/recommend-hover.svg';
import ShareIcon from '/@/assets/images/share-icon.svg';

import { getPostDetailApi, getPostListApi, likePostApi, cancelLikePostApi, collectPostApi, cancelCollectPostApi } from '/@/api/post'
import { BASE_URL } from "/@/store/constants"
import { createPostCommentApi, listPostCommentsApi, likePostCommentApi, cancelLikePostCommentApi } from '/@/api/comment'

const route = useRoute()
const router = useRouter()

// 交互状态
const isLiked = ref(false)
const isCollected = ref(false)
const isFollowed = ref(false)
const isSubmitting = ref(false)
const commentText = ref('') // 输入框绑定的内容
const commentInputRef = ref(null)

// 当前登录用户的头像
const currentUserAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')

// 定义游记详细数据
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

const comments = ref([]) // 评论列表
const relatedArticles = ref([]) // 相关游记列表

onMounted(() => {
  loadPageData()

  const savedAvatar = localStorage.getItem('user_avatar') || localStorage.getItem('avatar');
  if (savedAvatar) {
    if (!savedAvatar.startsWith('http')) {
      currentUserAvatar.value = BASE_URL + '/api/staticfiles/avatar/' + savedAvatar;
    } else {
      currentUserAvatar.value = savedAvatar;
    }
  }
})

const fetchComments = async (id) => {
  try {
    const res = await listPostCommentsApi({ postId: id })
    if (res.code === 200) {
      const currentUserId = localStorage.getItem('user_id') || 'guest'
      const likedRecords = JSON.parse(localStorage.getItem(`liked_comments_${currentUserId}`) || '[]')

      comments.value = res.data.map(item => {
        let avatarUrl = item.avatar || item.userAvatar || item.authorAvatar || '';
        if (avatarUrl && !avatarUrl.startsWith('http')) {
          avatarUrl = BASE_URL + '/api/staticfiles/avatar/' + avatarUrl;
        }

        return {
          id: item.id,
          avatar: avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
          author: item.nickname || item.username || item.userId || '未知用户',
          time: item.commentTime || '刚刚',
          content: item.content,
          likes: item.likeCount || 0,
          hasLiked: likedRecords.includes(item.id)
        }
      })
    }
  } catch (error) {
    console.error('获取评论失败', error)
  }
}

const loadArticleDetail = () => {
  const id = route.query.id
  if (!id) return

  // 获取初始点赞和收藏状态
  const currentUserId = localStorage.getItem('user_id')
  if (currentUserId) {
    const collectedPosts = JSON.parse(localStorage.getItem(`collected_posts_${currentUserId}`) || '[]')
    const likedPosts = JSON.parse(localStorage.getItem(`liked_posts_${currentUserId}`) || '[]')
    isCollected.value = collectedPosts.includes(String(id))
    isLiked.value = likedPosts.includes(String(id))
  }

  getPostDetailApi({ id: id }).then((res) => {
    if (res.code === 200) {
      let data = res.data
      let coverUrl = data.cover || ''
      if (coverUrl && !coverUrl.startsWith('http')) {
        coverUrl = BASE_URL + '/api/staticfiles/image/' + coverUrl
      }

      let avatarUrl = data.authorAvatar || ''
      if (avatarUrl && !avatarUrl.startsWith('http')) {
        avatarUrl = BASE_URL + '/api/staticfiles/avatar/' + avatarUrl
      }

      article.value = {
        id: data.id,
        title: data.title,
        cover: coverUrl,
        location: data.location || '未知地点',
        time: data.createTime,
        views: data.pv || 0,
        likes: data.likeCount || 0,
        author: data.authorName || '匿名用户',
        authorAvatar: avatarUrl || 'https://joeschmoe.io/api/v1/random',
        authorDesc: '该用户很懒，什么都没写~',
        tags: data.tags || [],
        content: data.content || '<p>暂无内容</p>'
      }
    }
  }).catch((err) => {
    console.error('获取详情异常', err)
  })
}

const loadRelatedArticles = () => {
  getPostListApi({ type: 'guide' }).then((res) => {
    if (res.code === 200) {
      let list = res.data || []
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

const handleReply = (comment) => {
  commentText.value = `回复 @${comment.author} : `
  const inputBox = document.querySelector('.comment-input-box')
  if (inputBox) {
    const offsetTop = inputBox.getBoundingClientRect().top + window.scrollY - 100
    window.scrollTo({
      top: offsetTop,
      behavior: 'smooth'
    })
  }
  setTimeout(() => {
    if (commentInputRef.value) {
      commentInputRef.value.focus()
    }
  }, 300)
}

const submitComment = async () => {
  if (!commentText.value.trim()) {
    message.warning('请输入回复内容')
    return
  }

  const currentUserId = localStorage.getItem('user_id')
  if (!currentUserId) {
    message.warning('请先登录后再回复哦！')
    return
  }

  isSubmitting.value = true

  const requestParams = new URLSearchParams()
  requestParams.append('content', commentText.value.trim())
  requestParams.append('postId', route.query.id)
  requestParams.append('userId', currentUserId)

  try {
    const res = await createPostCommentApi(requestParams)
    if (res.code === 200) {
      message.success('回复发表成功！')
      commentText.value = ''
      fetchComments(route.query.id)
    } else {
      message.error(res.msg || '回复失败')
    }
  } catch (error) {
    console.error(error)
    message.error('网络异常，发表失败')
  } finally {
    isSubmitting.value = false
  }
}

// 点赞/取消点赞评论
const handleLikeComment = async (comment) => {
  const currentUserId = localStorage.getItem('user_id') || 'guest'
  const likeRecordKey = `liked_comments_${currentUserId}`
  let likedRecords = JSON.parse(localStorage.getItem(likeRecordKey) || '[]')

  // ✅ 核心修复：使用 URLSearchParams 解决 400 报错
  const formData = new URLSearchParams()
  formData.append('id', comment.id)

  if (comment.hasLiked) {
    try {
      const res = await cancelLikePostCommentApi(formData)
      if (res.code === 200) {
        comment.likes -= 1
        comment.hasLiked = false
        likedRecords = likedRecords.filter(id => id !== comment.id)
        localStorage.setItem(likeRecordKey, JSON.stringify(likedRecords))
      }
    } catch (error) {
      console.error(error)
    }
  } else {
    try {
      const res = await likePostCommentApi(formData)
      if (res.code === 200) {
        comment.likes += 1
        comment.hasLiked = true
        likedRecords.push(comment.id)
        localStorage.setItem(likeRecordKey, JSON.stringify(likedRecords))
      }
    } catch (error) {
      console.error(error)
    }
  }
}

// 游记文章本身的喜欢
const toggleLike = async () => {
  const currentUserId = localStorage.getItem('user_id') || 'guest'
  const likeRecordKey = `liked_posts_${currentUserId}`
  let likedPosts = JSON.parse(localStorage.getItem(likeRecordKey) || '[]')
  const postId = route.query.id

  // ✅ 核心修复：使用 URLSearchParams
  const formData = new URLSearchParams()
  formData.append('id', postId)

  if (isLiked.value) {
    // 取消点赞
    const res = await cancelLikePostApi(formData)
    if (res.code === 200) {
      isLiked.value = false
      article.value.likes -= 1
      likedPosts = likedPosts.filter(id => id !== postId)
      localStorage.setItem(likeRecordKey, JSON.stringify(likedPosts))
    }
  } else {
    // 点赞
    const res = await likePostApi(formData)
    if (res.code === 200) {
      isLiked.value = true
      article.value.likes += 1
      likedPosts.push(postId)
      localStorage.setItem(likeRecordKey, JSON.stringify(likedPosts))
      message.success('点赞成功！', 1)
    }
  }
}

// 游记文章的收藏
const toggleCollect = async () => {
  const currentUserId = localStorage.getItem('user_id')
  if (!currentUserId) {
    message.warn('请先登录！')
    return
  }

  const collectRecordKey = `collected_posts_${currentUserId}`
  let collectedPosts = JSON.parse(localStorage.getItem(collectRecordKey) || '[]')
  const postId = route.query.id

  // ✅ 核心修复：同时把 id(游记ID) 和 userId 传给后端
  const formData = new URLSearchParams()
  formData.append('id', postId)
  formData.append('userId', currentUserId) // 👈 必须传 userId，后端才能存进表里

  if (isCollected.value) {
    const res = await cancelCollectPostApi(formData)
    if (res.code === 200) {
      isCollected.value = false
      collectedPosts = collectedPosts.filter(id => id !== postId)
      localStorage.setItem(collectRecordKey, JSON.stringify(collectedPosts))
      message.success('已取消收藏')
    }
  } else {
    const res = await collectPostApi(formData)
    if (res.code === 200) {
      isCollected.value = true
      collectedPosts.push(postId)
      localStorage.setItem(collectRecordKey, JSON.stringify(collectedPosts))
      message.success('已加入收藏夹！', 1)
    }
  }
}

const goToDetail = (id) => {
  router.push({ query: { id: id } })
}

const loadPageData = () => {
  const id = route.query.id
  if (id) {
    loadArticleDetail()
    loadRelatedArticles()
    fetchComments(id)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

watch(() => route.query.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    loadPageData()
  }
})
</script>

<style scoped lang="less">
@bg-color: #f4f6f9;
@zs-green: #064e3b;
@zs-yellow: #d97706;
@dark-text: #1e293b;
@light-text: #64748b;

.travelogue-detail-page {
  background-color: @bg-color;
  min-height: 100vh;
  padding-bottom: 100px;
}

.article-hero {
  position: relative;
  height: 500px;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  align-items: flex-end;
  padding-bottom: 60px;

  .hero-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
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
  position: relative;
  z-index: 10;
  padding: 0 20px;
}

.article-main-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
}

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

/* 修复了嵌套的 interaction-bar 样式 */
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
      transition: all 0.3s;

      .action-icon {
        width: 26px;
        height: 26px;
        object-fit: contain;
        transition: transform 0.3s;
      }
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

        /* 当激活时，使用滤镜让 SVG 图标变成琥珀黄色 */
        .action-icon {
          filter: sepia(1) saturate(100%) hue-rotate(5deg) brightness(0.9) contrast(1.2);
        }
      }
    }
  }
}

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

.sidebar-wrapper {
  position: sticky;
  top: 100px;
}

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