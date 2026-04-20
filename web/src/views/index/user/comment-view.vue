<template>
  <div class="content-list">
    <div class="list-title">我的评论</div>
    <div class="list-content">
      <div class="comment-view">
        <a-spin :spinning="loading" style="min-height: 200px;">
          <div class="comment-list">
            <div class="comment-item flex-view" v-for="item in commentData">
              <img :src="item.cover" class="avatar">
              <div class="infos">
                <div class="name flex-view">
                  <h3> 评论了</h3>
                  <h3 @click="handleClickTitle(item)" style="color: #0b6a65; cursor: pointer; margin-left: 8px;"
                    title="点击查看详情">
                    《{{ item.title }}》
                  </h3>
                </div>
                <div class="time">{{ getFormatTime(item.commentTime, true) }}</div>
                <div class="content">{{ item.content }}</div>
              </div>
            </div>
          </div>
        </a-spin>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from "/@/store";
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'

// 导入景区评论 API
import { listUserCommentsApi } from '/@/api/comment'
// 导入自定义 request 发起对新增接口的调用
import { get } from '/@/utils/http/axios'
import { BASE_URL } from "/@/store/constants";
import { getFormatTime } from '/@/utils'

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false)
const commentData = ref([])

onMounted(() => {
  getCommentList()
})

const handleClickTitle = (record) => {
  if (record.isPost) {
    // 如果是游记/帖子，跳转到游记详情
    let text = router.resolve({ name: 'GuideDetail', query: { id: record.thingId } })
    window.open(text.href, '_blank')
  } else {
    // 否则跳转到景区详情
    let text = router.resolve({ name: 'detail', query: { id: record.thingId } })
    window.open(text.href, '_blank')
  }
}

const getCommentList = async () => {
  loading.value = true
  let userId = userStore.user_id

  try {
    // 1. 获取景区评论
    const resThing = await listUserCommentsApi({ userId: userId })
    let thingComments = resThing.data.map(item => {
      return {
        ...item,
        cover: BASE_URL + '/api/staticfiles/image/' + item.cover,
        isPost: false // 标记为非游记
      }
    })

    // 2. 获取游记评论 (调用刚才在后端新增的接口)
    const resPost = await get({ url: '/api/postComment/userList', params: { userId: userId } })
    let postComments = resPost.data.map(item => {
      // 游记封面可能存的是相对路径或者绝对路径，需要兼容处理
      let coverUrl = item.cover || ''
      if (coverUrl && !coverUrl.startsWith('http')) {
        coverUrl = BASE_URL + '/api/staticfiles/image/' + coverUrl
      }
      return {
        ...item,
        thingId: item.postId, // 统一将 postId 映射为 thingId，方便前端共用点击跳转逻辑
        cover: coverUrl,
        isPost: true // 标记为游记
      }
    })

    // 3. 合并两组数据，并按评论时间降序排序
    let combinedData = [...thingComments, ...postComments]
    combinedData.sort((a, b) => {
      return new Date(b.commentTime) - new Date(a.commentTime)
    })

    commentData.value = combinedData

  } catch (err) {
    message.error(err.msg || '获取评论异常')
  } finally {
    loading.value = false
  }
}

</script>
<style scoped lang="less">
.flex-view {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

.content-list {
  flex: 1;

  .list-title {
    color: #152844;
    font-weight: 600;
    font-size: 18px;
    //line-height: 24px;
    height: 48px;
    margin-bottom: 4px;
    border-bottom: 1px solid #cedce4;
  }
}

.comment-view {
  overflow: hidden;

  .comment-list {
    margin: 8px auto;
  }

  .comment-item {
    padding: 15px 0;

    .avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      margin-right: 8px;
    }

    .infos {
      position: relative;
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
    }

    .name {
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      cursor: pointer;
    }

    h3 {
      color: #152844;
      font-weight: 600;
      font-size: 14px;
      margin: 0;
    }

    .traingle {
      width: 0;
      height: 0;
      border-left: 6px solid #c3c9d5;
      border-right: 0;
      border-top: 4px solid transparent;
      border-bottom: 4px solid transparent;
      margin: 0 12px;
    }

    .time {
      color: #5f77a6;
      font-size: 12px;
      line-height: 16px;
      height: 16px;
      margin: 2px 0 8px;
    }

    .content {
      color: #484848;
      font-size: 14px;
      line-height: 22px;
      padding-right: 30px;
    }
  }
}
</style>
