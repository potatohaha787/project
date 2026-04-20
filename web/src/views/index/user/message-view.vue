<template>
  <div class="content-list">
    <div class="list-title">我的消息</div>
    <a-spin :spinning="loading" style="min-height: 200px;">
      <div class="list-content">
        <div class="notification-view">
          <div class="list">
            <div v-if="msgData.length === 0" style="text-align: center; color: #94a3b8; padding: 40px 0;">
              暂无任何消息
            </div>

            <div class="notification-item flex-view" v-for="item in msgData" :key="item.id">
              <img :src="item.avatar" class="avatar">
              <div class="content-box">
                <div class="header">
                  <span class="title-txt">{{ item.title }}</span>
                  <span class="time">{{ item.time }}</span>
                </div>
                <div class="content">
                  <p>{{ item.content }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listApi } from '/@/api/notice'
// 导入 axios get 方法用来请求新增的接口
import { get } from '/@/utils/http/axios'
// 获取当前登录用户
import { useUserStore } from "/@/store"
import { BASE_URL } from "/@/store/constants"

// 引入默认头像
import defaultAvatar from '/@/assets/images/avatar.jpg'

const loading = ref(false)
const msgData = ref([])
const userStore = useUserStore()

onMounted(() => {
  getMessageList()
})

const getMessageList = async () => {
  loading.value = true
  let userId = userStore.user_id

  try {
    // 1. 获取官方系统通知
    const resNotice = await listApi()
    let notices = (resNotice.data || []).map(item => ({
      id: 'notice_' + item.id,
      title: item.title || '系统通知',
      content: item.content,
      time: item.createTime,
      // 系统通知使用你之前的蓝色铃铛 Base64 图片
      avatar: 'data:image/png;base64,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'
    }))

    // 2. 获取别人对我文章的评论
    let comments = []
    if (userId) {
      const resComments = await get({ url: '/api/postComment/myMessages', params: { userId: userId } })
      comments = (resComments.data || []).map(item => {
        // 处理真实的头像拼接
        let avatarUrl = item.avatar || ''
        if (avatarUrl && !avatarUrl.startsWith('http')) {
          avatarUrl = BASE_URL + '/api/staticfiles/avatar/' + avatarUrl
        }
        return {
          id: 'comment_' + item.id,
          title: `${item.nickname || item.username || '热心网友'} 评论了你的文章《${item.title || '未知'}》`,
          content: item.content,
          time: item.commentTime,
          avatar: avatarUrl || defaultAvatar
        }
      })
    }

    // 3. 将系统通知与用户评论合并，并按时间降序排序（最新在最上）
    let combinedData = [...notices, ...comments]
    combinedData.sort((a, b) => new Date(b.time).getTime() - new Date(a.time).getTime())

    msgData.value = combinedData
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="less">
/* 样式部分保持你的原样即可 */
progress {
  vertical-align: baseline;
}

.flex-view {
  display: flex;
}

input,
textarea {
  outline: none;
  border-style: none;
}

button {
  padding: 0;
}

.content-list {
  flex: 1;

  .list-title {
    color: #152844;
    font-weight: 600;
    font-size: 18px;
    height: 48px;
    margin-bottom: 4px;
    border-bottom: 1px solid #cedce4;
  }
}

.notification-item {
  padding-top: 16px;

  .avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    margin-right: 12px;
    object-fit: cover;
  }

  .content-box {
    flex: 1;
    border-bottom: 1px dashed #e9e9e9;
    padding: 4px 0 16px;
  }

  .header {
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .title-txt {
    color: #315c9e;
    font-weight: 600;
    font-size: 14px;
  }

  .time {
    color: #94a3b8;
    font-size: 12px;
  }

  .content {
    margin-top: 4px;
    color: #484848;
    font-size: 14px;
    line-height: 22px;
    background: #f8fafc;
    padding: 8px 12px;
    border-radius: 6px;
  }
}
</style>