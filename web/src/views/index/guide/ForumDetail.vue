<template>
  <div class="forum-detail-page">
    <Header />

    <div class="detail-container">
      <a-row :gutter="32">

        <a-col :xs="24" :lg="17">

          <div class="topic-main-card">

            <div class="topic-header">
              <a-button type="link" class="back-btn" @click="goBack">
                &larr; 返回社区交流
              </a-button>

              <div class="title-area">
                <a-tag :class="['topic-tag', topicDetail.type]">{{ topicDetail.tag }}</a-tag>
                <h1 class="topic-title">
                  {{ topicDetail.title }}
                  <span class="highlight-dot">.</span>
                </h1>
              </div>

              <div class="author-meta">
                <div class="author-info">
                  <a-avatar :src="topicDetail.avatar" size="large" />
                  <div class="author-text">
                    <span class="author-name">{{ topicDetail.author }}</span>
                    <span class="publish-time">发表于 {{ topicDetail.time }}</span>
                  </div>
                </div>
                <div class="interaction-meta">
                  <span>👁 {{ topicDetail.views }} 阅读</span>
                  <a-button type="text" class="like-btn">❤️ {{ topicDetail.likes }} 点赞</a-button>
                </div>
              </div>
            </div>

            <div class="topic-content">
              <p v-html="topicDetail.content"></p>
            </div>
          </div>

          <div class="reply-section-card">
            <div class="reply-header">
              <h3>全部回复 <span class="reply-count">({{ replyList.length }})</span></h3>
            </div>

            <a-list class="reply-list" item-layout="horizontal" :data-source="replyList">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-comment>
                    <template #avatar>
                      <a-avatar :src="item.avatar" />
                    </template>
                    <template #author>
                      <a class="reply-author-name">{{ item.author }}</a>
                      <span class="reply-time">{{ item.time }}</span>
                    </template>
                    <template #content>
                      <p class="reply-content-text">{{ item.content }}</p>
                    </template>
                    <template #actions>
                      <span key="comment-basic-reply-to" class="action-item">回复</span>
                      <span key="comment-basic-like" class="action-item">👍 {{ item.likes }}</span>
                    </template>
                  </a-comment>
                </a-list-item>
              </template>
            </a-list>
          </div>

          <div class="reply-input-card">
            <a-comment>
              <template #avatar>
                <a-avatar src="https://joeschmoe.io/api/v1/random" />
              </template>
              <template #content>
                <a-form-item>
                  <a-textarea v-model:value="replyValue" placeholder="分享你的看法，友善交流哦..." :rows="4"
                    class="reply-textarea" />
                </a-form-item>
                <a-form-item class="input-actions">
                  <a-button html-type="submit" :loading="isSubmitting" type="primary" @click="handleSubmitReply"
                    class="submit-reply-btn">
                    提交回复
                  </a-button>
                </a-form-item>
              </template>
            </a-comment>
          </div>
        </a-col>

        <a-col :xs="24" :lg="7">
          <div class="sidebar-card hot-topics-card">
            <div class="sidebar-header">
              <h3><i class="iconfont icon-hot"></i> 热门讨论</h3>
            </div>
            <div class="hot-topics-list">
              <div class="hot-topic-item" v-for="(hotTopic, index) in relatedTopics" :key="hotTopic.id"
                @click="goToDetail(hotTopic.id)">
                <div class="topic-ranking" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
                <div class="topic-info">
                  <h4 class="hot-topic-title">{{ hotTopic.title }}</h4>
                  <div class="hot-topic-meta">
                    <a-tag :class="['topic-tag-sm', hotTopic.type]" size="small">{{ hotTopic.tag }}</a-tag>
                    <span class="reply-count">💬 {{ hotTopic.replies }} 回复</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="sidebar-card rules-card">
            <div class="sidebar-header">
              <h3>🛡️ 香山交流守则</h3>
            </div>
            <div class="rules-content">
              <p>1. 尊重他人，友善交流，严禁人身攻击。</p>
              <p>2. 请勿发布虚假、违法或侵权信息。</p>
              <p>3. 鼓励分享真实的中山旅行体验与攻略。</p>
              <p>4. 遇纠纷请保持冷静，使用举报功能。</p>
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
import { getPostDetailApi, getPostListApi } from '/@/api/post'
import { BASE_URL } from '/@/store/constants'

// 🌟 1. 引入评论的真实 API
import { createApi, listThingCommentsApi } from '/@/api/comment'

const route = useRoute()
const router = useRouter()

// 1. 对应模板的帖子详情数据
const topicDetail = ref({
  type: '',
  tag: '加载中...',
  title: '加载中...',
  avatar: 'https://joeschmoe.io/api/v1/random',
  author: '...',
  time: '',
  views: 0,
  likes: 0,
  content: ''
})

// 2. 对应模板的评论和交互状态
const replyList = ref([])
const replyValue = ref('')
const isSubmitting = ref(false)
const relatedTopics = ref([])

// 返回上一页
const goBack = () => {
  router.back()
}

const goToDetail = (id) => {
  router.push({ name: 'ForumDetail', query: { id: id } })
}

// 根据 ID 从数据库获取详情
const fetchDetail = async (id) => {
  try {
    const res = await getPostDetailApi({ id: id })
    if (res.code === 200 && res.data) {
      const data = res.data
      let typeClass = 'tag-share'
      let tagText = '分享'
      if (data.type === 'ask') { typeClass = 'tag-ask'; tagText = '求助' }
      if (data.type === 'mate') { typeClass = 'tag-mate'; tagText = '结伴' }

      topicDetail.value = {
        type: typeClass,
        tag: tagText,
        title: data.title,
        avatar: data.authorAvatar || 'https://joeschmoe.io/api/v1/random',
        author: data.authorName || '香山体验师',
        time: data.createTime || '刚刚',
        views: data.pv || 0,
        likes: data.likeCount || 0,
        content: data.content || '<p>暂无内容</p>'
      }
    } else {
      message.error(res.msg || '获取详情失败')
    }
  } catch (error) {
    console.error(error)
    message.error('获取详情异常')
  }
}

// 🌟 2. 新增：从数据库获取当前游记的真实评论列表
const fetchComments = async (id) => {
  try {
    // 借用 thingId 来存放游记/帖子的 ID
    const res = await listThingCommentsApi({ thingId: id, order: 'recent' })
    if (res.code === 200) {
      replyList.value = res.data.map(item => ({
        id: item.id,
        avatar: 'https://joeschmoe.io/api/v1/random', // 如果后端评论实体有头像可以用 item.avatar
        author: item.username || '热心网友',
        time: item.commentTime || '刚刚',
        content: item.content,
        likes: item.likeCount || 0
      }))
    }
  } catch (error) {
    console.error('获取评论失败', error)
  }
}

// 获取右侧热门讨论列表
const fetchRelatedTopics = async () => {
  try {
    const res = await getPostListApi()
    if (res.code === 200 && res.data) {
      relatedTopics.value = res.data
        .filter(item => item.type !== 'guide' && String(item.id) !== String(route.query.id))
        .slice(0, 5)
        .map(item => {
          let typeClass = 'tag-share'
          let tagText = '分享'
          if (item.type === 'ask') { typeClass = 'tag-ask'; tagText = '求助' }
          if (item.type === 'mate') { typeClass = 'tag-mate'; tagText = '结伴' }
          return {
            id: item.id,
            title: item.title,
            type: typeClass,
            tag: tagText,
            replies: item.likeCount || 0
          }
        })
    }
  } catch (error) {
    console.error(error)
  }
}

// 通用页面加载逻辑
const loadPageData = () => {
  const id = route.query.id
  if (id) {
    fetchDetail(id)
    fetchRelatedTopics()
    fetchComments(id) // 🌟 3. 页面加载时拉取真实评论
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } else {
    message.warning('参数错误：缺少帖子ID')
  }
}

onMounted(() => {
  loadPageData()
})

watch(
  () => route.query.id,
  (newId, oldId) => {
    if (newId && newId !== oldId && route.name === 'ForumDetail') {
      loadPageData()
    }
  }
)

// 🌟 4. 彻底重写：将真实数据提交到后端数据库
const handleSubmitReply = async () => {
  if (!replyValue.value.trim()) {
    message.warning('请输入回复内容')
    return
  }

  // 必须登录才能评论
  const currentUserId = localStorage.getItem('user_id')
  if (!currentUserId) {
    message.warning('请先登录后再回复哦！')
    return
  }

  isSubmitting.value = true

  // 组装表单数据发送给后端
  const formData = new FormData()
  formData.append('content', replyValue.value.trim())
  formData.append('thingId', route.query.id) // 把游记ID存入 thingId 字段关联起来
  formData.append('userId', currentUserId)

  try {
    const res = await createApi(formData)
    if (res.code === 200) {
      message.success('回复发表成功！')
      replyValue.value = ''
      // 🌟 提交成功后，重新拉取一次评论列表刷新页面
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
</script>

<style scoped lang="less">
/* --- 沿用中山文旅主题色 --- */
@bg-color: #f1f5f9;
@zs-green: #064e3b;
/* 深翠绿 */
@zs-yellow: #d97706;
/* 琥珀黄 */
@dark-text: #1e293b;
@light-text: #64748b;

.forum-detail-page {
  background-color: @bg-color;
  min-height: 100vh;
  padding-bottom: 80px;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 120px 20px 40px;
}

/* 帖子主卡片样式 */
.topic-main-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.03);
  margin-bottom: 24px;
}

.topic-header {
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 24px;
  margin-bottom: 24px;

  .back-btn {
    color: @light-text;
    padding: 0;
    font-size: 14px;
    margin-bottom: 16px;

    &:hover {
      color: @zs-green;
    }
  }

  .title-area {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;

    .topic-tag {
      font-size: 13px;
      font-weight: bold;
      padding: 3px 8px;
      border-radius: 4px;

      &.tag-ask {
        background: #e0f2fe;
        color: #0284c7;
      }

      &.tag-share {
        background: lighten(@zs-green, 60%);
        color: @zs-green;
      }

      &.tag-mate {
        background: #fef3c7;
        color: @zs-yellow;
      }
    }

    .topic-title {
      font-size: 28px;
      font-weight: 800;
      color: @dark-text;
      margin: 0;
      letter-spacing: 1px;

      .highlight-dot {
        color: @zs-yellow;
        /* 使用琥珀黄作为点缀 */
      }
    }
  }

  .author-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;

    .author-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .author-name {
        font-size: 16px;
        color: @dark-text;
        font-weight: 600;
        display: block;
      }

      .publish-time {
        font-size: 13px;
        color: @light-text;
      }
    }

    .interaction-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      font-size: 13px;
      color: @light-text;

      .like-btn {
        color: @light-text;
        font-size: 13px;
        padding: 0;

        &:hover {
          color: #ef4444;
          /* 点赞悬浮红色 */
          background-color: transparent;
        }
      }
    }
  }
}

.topic-content {
  font-size: 16px;
  line-height: 1.8;
  color: #334155;
}

/* 回复列表卡片样式 */
.reply-section-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.03);
  margin-bottom: 24px;

  .reply-header {
    margin-bottom: 24px;

    h3 {
      font-size: 18px;
      font-weight: 700;
      color: @dark-text;
      margin: 0;

      .reply-count {
        color: @light-text;
        font-weight: normal;
        margin-left: 4px;
      }
    }
  }
}

/* 覆盖 Antd Reply List 和 Comment 样式 */
:deep(.ant-list-item) {
  padding: 16px 0;
  border-bottom: 1px solid #f1f5f9;

  &:last-child {
    border-bottom: none;
  }
}

:deep(.ant-comment-content-author-name) {
  .reply-author-name {
    color: @dark-text;
    font-weight: 600;

    &:hover {
      color: @zs-green;
    }
  }
}

:deep(.ant-comment-content-author-time) {
  .reply-time {
    color: #94a3b8;
    font-size: 12px;
  }
}

:deep(.ant-comment-content-detail) {
  .reply-content-text {
    font-size: 14px;
    color: #475569;
    line-height: 1.6;
    margin-top: 4px;
  }
}

:deep(.ant-comment-actions) {
  margin-top: 8px;

  .action-item {
    font-size: 12px;
    color: #94a3b8;
    margin-right: 16px;
    cursor: pointer;

    &:hover {
      color: @zs-green;
    }
  }
}

/* 发表回复卡片样式 */
.reply-input-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.03);

  .reply-textarea {
    background-color: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 16px;
    resize: none;

    &:focus {
      border-color: @zs-green;
      box-shadow: 0 0 0 2px rgba(6, 78, 59, 0.1);
      background-color: #fff;
    }
  }

  .input-actions {
    margin-bottom: 0;
    text-align: right;
  }

  .submit-reply-btn {
    background-color: @zs-green;
    border-color: @zs-green;
    border-radius: 8px;
    font-weight: bold;

    &:hover {
      background-color: lighten(@zs-green, 5%);
      border-color: lighten(@zs-green, 5%);
    }
  }
}

/* 侧边栏卡片共同样式 */
.sidebar-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.03);
  margin-bottom: 24px;

  .sidebar-header {
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f1f5f9;

    h3 {
      font-size: 17px;
      font-weight: 700;
      color: @dark-text;
      margin: 0;
      display: flex;
      align-items: center;
      gap: 8px;

      .iconfont {
        color: @zs-yellow;
        font-size: 18px;
      }
    }
  }
}

/* 侧边栏：热门讨论列表 */
.hot-topic-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #eee;
  cursor: pointer;

  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }

  &:hover .hot-topic-title {
    color: @zs-green;
  }

  .topic-ranking {
    flex-shrink: 0;
    width: 22px;
    height: 22px;
    background: #f1f5f9;
    color: #94a3b8;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: bold;

    /* 热门排名前三的高亮 */
    &.rank-1 {
      background: @zs-yellow;
      color: #fff;
    }

    &.rank-2 {
      background: rgba(217, 119, 6, 0.6);
      color: #fff;
    }

    &.rank-3 {
      background: rgba(217, 119, 6, 0.3);
      color: @zs-yellow;
    }
  }

  .topic-info {
    .hot-topic-title {
      font-size: 14px;
      color: @dark-text;
      font-weight: 600;
      margin-bottom: 4px;
      transition: color 0.3s;
    }

    .hot-topic-meta {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 12px;
      color: #94a3b8;

      .topic-tag-sm {
        font-size: 11px;
        padding: 1px 5px;

        &.tag-ask {
          background: #e0f2fe;
          color: #0284c7;
        }

        &.tag-share {
          background: lighten(@zs-green, 60%);
          color: @zs-green;
        }

        &.tag-mate {
          background: #fef3c7;
          color: @zs-yellow;
        }
      }
    }
  }
}

/* 侧边栏：交流守则 */
.rules-content {
  font-size: 13px;
  color: @light-text;
  line-height: 1.8;

  p {
    margin-bottom: 8px;
  }
}

/* 适配移动端 */
@media (max-width: 992px) {
  .detail-container {
    padding-top: 100px;
  }

  .topic-main-card,
  .reply-section-card,
  .reply-input-card {
    padding: 24px;
  }

  .topic-header .title-area {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .topic-header .title-area .topic-title {
    font-size: 24px;
  }
}
</style>