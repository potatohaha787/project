<template>
  <div class="forum-list-page">
    <Header />

    <div class="forum-hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">香山茶馆 <span class="highlight-dot">.</span> 交流吧</h1>
        <p class="hero-subtitle">在这里，聊聊中山的那些事儿，寻找志同道合的旅伴</p>
      </div>
    </div>

    <div class="forum-container">
      <a-row :gutter="32">

        <a-col :xs="24" :lg="17">
          <div class="forum-main-card">

            <div class="forum-toolbar">
              <div class="filter-tabs">
                <span v-for="tab in tabs" :key="tab.key" :class="['tab-item', { active: currentTab === tab.key }]"
                  @click="switchTab(tab.key)">
                  {{ tab.name }}
                </span>
              </div>

              <a-button type="primary" class="publish-btn-mobile" @click="showPublishModal">
                + 发帖
              </a-button>
            </div>

            <div class="topic-list">
              <div class="topic-item" v-for="topic in topicList" :key="topic.id" @click="goToDetail(topic.id)">
                <div class="topic-avatar">
                  <a-avatar :src="topic.avatar" :size="48" />
                </div>

                <div class="topic-content">
                  <div class="topic-title-wrapper">
                    <a-tag :class="['topic-tag', topic.type]">{{ topic.tag }}</a-tag>
                    <h3 class="topic-title">{{ topic.title }}</h3>
                  </div>
                  <p class="topic-desc">{{ topic.desc }}</p>

                  <div class="topic-meta">
                    <span class="author-name">{{ topic.author }}</span>
                    <span class="divider">|</span>
                    <span class="publish-time">{{ topic.time }}</span>
                    <span class="divider">|</span>
                    <span class="last-reply" v-if="topic.lastReply">最后回复: {{ topic.lastReply }}</span>
                  </div>
                </div>

                <div class="topic-stats">
                  <div class="stat-item">
                    <span class="stat-num">{{ topic.replies }}</span>
                    <span class="stat-label">回复</span>
                  </div>
                  <div class="stat-item views">
                    <span class="stat-num">{{ topic.views }}</span>
                    <span class="stat-label">浏览</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="pagination-wrapper">
              <a-pagination v-model:current="currentPage" :total="50" show-less-items />
            </div>
          </div>
        </a-col>

        <a-col :xs="24" :lg="7">

          <a-button type="primary" class="publish-btn-desktop" block size="large" @click="showPublishModal">
            <i class="iconfont icon-write"></i> 发起新讨论
          </a-button>

          <div class="sidebar-card tags-card">
            <div class="sidebar-header">
              <h3>🏷️ 热门标签</h3>
            </div>
            <div class="tags-cloud">
              <span class="cloud-tag">石岐乳鸽</span>
              <span class="cloud-tag">周末遛娃</span>
              <span class="cloud-tag">五桂山徒步</span>
              <span class="cloud-tag">崖口日落</span>
              <span class="cloud-tag">孙中山故居</span>
              <span class="cloud-tag">岐江公园</span>
              <span class="cloud-tag">中山温泉</span>
            </div>
          </div>

          <div class="sidebar-card active-users-card">
            <div class="sidebar-header">
              <h3>🔥 活跃吧友</h3>
            </div>
            <div class="user-list">
              <div class="user-item" v-for="i in 5" :key="i">
                <a-avatar :src="`https://joeschmoe.io/api/v1/user${i}`" />
                <div class="user-info">
                  <span class="u-name">香山达人 0{{ i }}</span>
                  <span class="u-desc">发布了 {{ i * 12 }} 篇内容</span>
                </div>
              </div>
            </div>
          </div>

        </a-col>
      </a-row>
    </div>

    <a-modal v-model:visible="isPublishModalVisible" title="发起新讨论" :footer="null" :width="640"
      class="custom-publish-modal" destroyOnClose>
      <div class="modal-subtitle">分享你的中山足迹，或是向吧友求助</div>

      <a-form :model="publishForm" layout="vertical" @finish="handlePublishSubmit">
        <a-form-item name="type" :rules="[{ required: true, message: '请选择讨论分类' }]">
          <a-radio-group v-model:value="publishForm.type" class="type-radio-group">
            <a-radio-button value="ask">🙋 求助问答</a-radio-button>
            <a-radio-button value="share">💡 经验分享</a-radio-button>
            <a-radio-button value="mate">🤝 结伴同游</a-radio-button>
          </a-radio-group>
        </a-form-item>

        <a-form-item name="title" :rules="[{ required: true, message: '请输入讨论标题' }]">
          <a-input v-model:value="publishForm.title" placeholder="一句话概括你的问题或分享 (最多40字)..." size="large" :maxlength="40"
            class="modal-input" />
        </a-form-item>

        <a-form-item name="content" :rules="[{ required: true, message: '请输入讨论内容' }]">
          <a-textarea v-model:value="publishForm.content" placeholder="详细描述你的内容，友善的交流会得到更多回复哦..." :rows="6"
            class="modal-textarea" />
        </a-form-item>

        <a-form-item name="images">
          <a-upload v-model:file-list="publishForm.images" action="/api/upload" list-type="picture-card"
            @preview="handlePreview">
            <div v-if="publishForm.images.length < 9">
              <div class="upload-icon">📸</div>
              <div style="margin-top: 8px; font-size: 12px; color: #94a3b8;">添加图片</div>
            </div>
          </a-upload>
        </a-form-item>

        <div class="modal-footer-actions">
          <a-button size="large" @click="isPublishModalVisible = false" class="cancel-btn">取消</a-button>
          <a-button type="primary" size="large" html-type="submit" :loading="isPublishing" class="submit-btn">
            发布讨论
          </a-button>
        </div>
      </a-form>
    </a-modal>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
// 引入刚刚写好的 API
import { getPostListApi, createPostApi } from '/@/api/post'
import { BASE_URL } from '/@/store/constants' // 如果您的图片路径需要拼接 BASE_URL

const router = useRouter()

// --- 列表获取逻辑 ---
const topicList = ref([])
const currentTab = ref('all')
const currentPage = ref(1)

// 核心：从数据库获取列表数据
const fetchPostList = async () => {
  try {
    // 构造请求参数，如果是 all 则不传 type
    const queryParams = {
      type: currentTab.value === 'all' ? '' : currentTab.value
    }
    const res = await getPostListApi(queryParams)

    // 后端返回的数据映射给前端
    // 注意：我们将后端的 createTime 时间戳格式化，并将 content 剥离 HTML 标签作为简短描述(desc)
    topicList.value = res.data.map(item => ({
      id: item.id,
      type: item.type === 'guide' ? 'tag-share' : (item.type === 'ask' ? 'tag-ask' : 'tag-mate'),
      tag: item.type === 'guide' ? '分享' : (item.type === 'ask' ? '求助' : '结伴'),
      title: item.title,
      // 去掉HTML标签，截取前80个字作为列表简介
      desc: item.content ? item.content.replace(/<[^>]+>/g, '').substring(0, 80) + '...' : '',
      // 如果没有头像，给个默认头像
      avatar: item.authorAvatar || 'https://joeschmoe.io/api/v1/random',
      author: item.authorName || '香山吧友',
      // 这里简易处理时间，实际项目中建议用 dayjs 格式化时间戳
      time: new Date(Number(item.createTime)).toLocaleDateString(),
      views: item.pv || 0,
      replies: item.likeCount || 0 // 暂时用点赞数代替回复数占位
    }))
  } catch (error) {
    console.error("获取列表失败:", error)
  }
}

// 监听 Tab 切换，重新请求数据
watch(currentTab, () => {
  fetchPostList()
})

// 页面加载时执行一次查询
onMounted(() => {
  fetchPostList()
})

// 切换选项卡
const switchTab = (key) => {
  currentTab.value = key
}

// 跳转到详情页 (将真实 ID 传过去)
const goToDetail = (id) => {
  router.push({ path: '/forum-detail', query: { id: id } })
}

// --- 以下为：发布弹窗逻辑改造 ---
const isPublishModalVisible = ref(false)
const isPublishing = ref(false)
const publishForm = ref({ type: 'ask', title: '', content: '', images: [] })

const showPublishModal = () => { isPublishModalVisible.value = true }

// 核心：提交真实数据到后端
const handlePublishSubmit = async () => {
  isPublishing.value = true

  // 因为可能有图片，我们构造 FormData
  const formData = new FormData()
  formData.append('type', publishForm.value.type)
  formData.append('title', publishForm.value.title)
  formData.append('content', publishForm.value.content)
  // 假设用户登录了，应该传 userId，这里先写死 1 用于测试
  formData.append('userId', '1')

  // 处理上传的图片文件 (取第一张作为封面)
  if (publishForm.value.images && publishForm.value.images.length > 0) {
    formData.append('imageFile', publishForm.value.images[0].originFileObj)
  }

  try {
    await createPostApi(formData)
    message.success('🎉 讨论发布成功！')
    isPublishModalVisible.value = false

    // 清空表单并刷新列表
    publishForm.value = { type: 'ask', title: '', content: '', images: [] }
    fetchPostList() // 重新拉取最新数据
  } catch (error) {
    message.error('发布失败，请重试')
  } finally {
    isPublishing.value = false
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

.forum-list-page {
  background-color: @bg-color;
  min-height: 100vh;
  padding-bottom: 80px;
}

/* 顶部 Banner */
.forum-hero {
  position: relative;
  padding: 140px 20px 80px;
  background-image: url(/@/assets/images/tourism.jpg);
  background-size: cover;
  background-position: center;
  text-align: center;

  .hero-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(6, 30, 25, 0.7);
  }

  .hero-content {
    position: relative;
    z-index: 2;

    .hero-title {
      font-size: 42px;
      font-weight: 800;
      color: #fff;
      margin-bottom: 12px;
      letter-spacing: 2px;

      .highlight-dot {
        color: @zs-yellow;
      }
    }

    .hero-subtitle {
      font-size: 16px;
      color: #e2e8f0;
    }
  }
}

.forum-container {
  max-width: 1200px;
  margin: -40px auto 0;
  position: relative;
  z-index: 10;
  padding: 0 20px;
}

/* --- 左侧列表区 --- */
.forum-main-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  padding: 24px;
}

.forum-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #f1f5f9;
  padding-bottom: 16px;
  margin-bottom: 16px;

  .filter-tabs {
    display: flex;
    gap: 24px;

    .tab-item {
      font-size: 16px;
      font-weight: 600;
      color: @light-text;
      cursor: pointer;
      position: relative;
      padding-bottom: 8px;
      transition: color 0.3s;

      &:hover {
        color: @zs-green;
      }

      &.active {
        color: @zs-green;

        &::after {
          content: '';
          position: absolute;
          bottom: -18px;
          left: 0;
          width: 100%;
          height: 3px;
          background-color: @zs-yellow;
          border-radius: 2px;
        }
      }
    }
  }

  .publish-btn-mobile {
    display: none;
    background-color: @zs-green;
    border-color: @zs-green;
  }
}

/* 帖子列表 */
.topic-list {
  min-height: 500px;
}

.topic-item {
  display: flex;
  gap: 20px;
  padding: 24px 0;
  border-bottom: 1px dashed #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: #fcfdfd;
    padding-left: 12px;
    padding-right: 12px;
    margin-left: -12px;
    margin-right: -12px;
    border-radius: 8px;
    border-bottom-color: transparent;

    .topic-content .topic-title-wrapper .topic-title {
      color: @zs-green;
    }
  }

  .topic-avatar {
    flex-shrink: 0;
    padding-top: 4px;
  }

  .topic-content {
    flex: 1;
    min-width: 0;

    .topic-title-wrapper {
      display: flex;
      align-items: flex-start;
      gap: 10px;
      margin-bottom: 8px;

      .topic-tag {
        flex-shrink: 0;
        font-size: 12px;
        font-weight: bold;
        padding: 2px 6px;
        border-radius: 4px;
        margin-top: 2px;

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
        font-size: 18px;
        font-weight: 700;
        color: @dark-text;
        margin: 0;
        line-height: 1.4;
        transition: color 0.3s;
      }
    }

    .topic-desc {
      font-size: 14px;
      color: @light-text;
      line-height: 1.6;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .topic-meta {
      font-size: 12px;
      color: #94a3b8;
      display: flex;
      align-items: center;
      gap: 8px;

      .author-name {
        color: #64748b;
        font-weight: 500;
      }

      .divider {
        color: #e2e8f0;
      }
    }
  }

  .topic-stats {
    flex-shrink: 0;
    display: flex;
    gap: 16px;
    align-items: center;
    text-align: center;

    .stat-item {
      display: flex;
      flex-direction: column;
      justify-content: center;
      min-width: 48px;

      .stat-num {
        font-size: 16px;
        font-weight: bold;
        color: @zs-green;
      }

      .stat-label {
        font-size: 12px;
        color: #94a3b8;
        margin-top: 2px;
      }

      &.views .stat-num {
        color: #64748b;
      }
    }
  }
}

.pagination-wrapper {
  margin-top: 30px;
  text-align: center;

  :deep(.ant-pagination-item-active) {
    border-color: @zs-green;

    a {
      color: @zs-green;
    }
  }
}

/* --- 右侧侧边栏 --- */
.publish-btn-desktop {
  background: linear-gradient(135deg, @zs-yellow, darken(@zs-yellow, 8%));
  border: none;
  height: 54px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 12px;
  margin-bottom: 24px;
  /* 阴影颜色同步调整为琥珀黄的透明色 */
  box-shadow: 0 8px 20px rgba(217, 119, 6, 0.25);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 25px rgba(217, 119, 6, 0.4);
    color: #fff;
    /* 确保悬停时文字也是白色 */
  }
}

.sidebar-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.03);
  margin-bottom: 24px;

  .sidebar-header {
    margin-bottom: 16px;

    h3 {
      font-size: 16px;
      font-weight: 700;
      color: @dark-text;
      margin: 0;
    }
  }
}



.tags-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  .cloud-tag {
    background: #f1f5f9;
    color: #475569;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: @zs-green;
      color: #fff;
    }
  }
}

.user-list {
  .user-item {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    cursor: pointer;

    &:last-child {
      margin-bottom: 0;
    }

    &:hover .u-name {
      color: @zs-green;
    }

    .user-info {
      display: flex;
      flex-direction: column;

      .u-name {
        font-size: 14px;
        font-weight: 600;
        color: @dark-text;
        transition: color 0.2s;
      }

      .u-desc {
        font-size: 12px;
        color: #94a3b8;
        margin-top: 2px;
      }
    }
  }
}

/* --- 🌟 弹窗定制样式 🌟 --- */
.custom-publish-modal {
  :deep(.ant-modal-content) {
    border-radius: 16px;
    padding: 24px;
  }

  :deep(.ant-modal-header) {
    border-bottom: none;
    padding-bottom: 8px;
  }

  :deep(.ant-modal-title) {
    font-size: 22px;
    font-weight: 800;
    color: @dark-text;
  }

  .modal-subtitle {
    color: @light-text;
    font-size: 14px;
    margin-bottom: 24px;
    margin-top: -10px;
  }

  /* 覆盖 Antd 单选按钮颜色为深翠绿 */
  .type-radio-group {
    display: flex;
    gap: 12px;

    :deep(.ant-radio-button-wrapper) {
      border-radius: 8px;
      border: 1px solid #e2e8f0;
      padding: 0 20px;

      &::before {
        display: none;
      }

      /* 去掉自带的连体竖线 */

      &:hover {
        color: @zs-green;
      }

      &-checked {
        background-color: rgba(6, 78, 59, 0.05);
        border-color: @zs-green;
        color: @zs-green;
        font-weight: bold;
        box-shadow: none !important;
      }
    }
  }

  /* 覆盖输入框选中状态 */
  .modal-input,
  .modal-textarea {
    border-radius: 8px;

    &:focus {
      border-color: @zs-green;
      box-shadow: 0 0 0 2px rgba(6, 78, 59, 0.1);
    }
  }

  /* 上传框小调整 */
  .upload-icon {
    font-size: 24px;
    opacity: 0.8;
  }

  /* 底部按钮 */
  .modal-footer-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid #f1f5f9;

    .cancel-btn {
      border-radius: 8px;

      &:hover {
        color: @zs-yellow;
        border-color: @zs-yellow;
      }
    }

    .submit-btn {
      border-radius: 8px;
      background-color: @zs-green;
      border-color: @zs-green;
      font-weight: bold;
      padding: 0 32px;

      &:hover {
        background-color: lighten(@zs-green, 5%);
        border-color: lighten(@zs-green, 5%);
      }
    }
  }
}

/* 移动端适配 */
@media (max-width: 992px) {
  .forum-container {
    margin-top: -20px;
  }

  .forum-toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;

    .publish-btn-mobile {
      display: block;
      width: 100%;
      border-radius: 8px;
    }
  }

  .publish-btn-desktop {
    display: none;
  }

  .topic-item {
    flex-direction: column;
    gap: 12px;
  }

  .topic-stats {
    justify-content: flex-start;
    margin-top: 8px;
    border-top: 1px dashed #eee;
    padding-top: 12px;
  }
}
</style>