<template>
  <div class="detail">
    <Header />

    <div class="detail-content">
      <div class="detail-content-top">
        <div class="thing-infos-view">
          <div class="thing-img-box">
            <img :src="detailData.cover" alt="景区封面" />
          </div>

          <div class="thing-info-box">
            <div class="thing-state">
              <span class="state-badge">开放中</span>
              <span class="state-text">景区当前状态正常</span>
            </div>

            <h1 class="thing-name">{{ detailData.title }}</h1>

            <div class="price-box">
              <span class="a-price-symbol">¥</span>
              <span class="a-price">{{ detailData.price }}</span>
              <span class="price-unit">/ 人起</span>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <span class="label">分类</span>
                <span class="value">{{ detailData.classification_title }}</span>
              </div>
              <div class="info-item">
                <span class="label">级别</span>
                <span class="value">{{ detailData.level }}级景区</span>
              </div>
              <div class="info-item full-width">
                <span class="label">地址</span>
                <span class="value">{{ detailData.address }}</span>
              </div>
            </div>

            <div class="action-row">
              <div class="action-btn" @click="addToWish()">
                <img :src="WantIcon" class="icon" />
                <span class="text">加入心愿单 ({{ detailData.wishCount }})</span>
              </div>
              <div class="action-btn" @click="collect()">
                <img :src="RecommendIcon" class="icon" />
                <span class="text">收藏 ({{ detailData.collectCount }})</span>
              </div>
              <div class="action-btn" @click="share()">
                <img :src="ShareIcon" class="icon" />
                <span class="text">分享</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-content-bottom">

        <div class="main-content">
          <div class="order-view main-tab">
            <div class="tab-list">
              <span class="tab" :class="selectTabIndex === index ? 'tab-select' : ''" v-for="(item, index) in tabData"
                :key="index" @click="selectTab(index)">
                {{ item }}
              </span>
              <span :style="{ left: tabUnderLeft + 'px' }" class="tab-underline"></span>
            </div>
          </div>

          <div class="thing-intro" :class="selectTabIndex <= 0 ? '' : 'hide'">
            <p class="text">{{ detailData.description }}</p>
          </div>

          <div class="thing-comment" :class="selectTabIndex > 0 ? '' : 'hide'">
            <div class="publish flex-view">
              <img :src="AvatarIcon" class="mine-img">
              <input placeholder="对这个景区有什么看法？说点什么..." class="content-input" ref="commentRef" @keyup.enter="sendComment">
              <button class="send-btn" @click="sendComment()">发布</button>
            </div>

            <div class="tab-view flex-view">
              <div class="count-text">共 {{ commentData.length }} 条评论</div>
              <div class="tab-box flex-view" v-if="commentData.length > 0">
                <span :class="sortIndex === 0 ? 'tab-select' : ''" @click="sortCommentList('recent')">最新</span>
                <div class="line"></div>
                <span :class="sortIndex === 1 ? 'tab-select' : ''" @click="sortCommentList('hot')">热门</span>
              </div>
            </div>

            <div class="comments-list">
              <div class="comment-item" v-for="item in commentData" :key="item.id">
                <div class="flex-item flex-view">
                  <img :src="item.avatar || AvatarIcon" class="avator">
                  <div class="person">
                    <div class="name">{{ item.nickname || item.username || '匿名用户' }}</div>
                    <div class="time">{{ item.commentTime }}</div>
                  </div>
                  <div class="float-right hover-btn" @click="like(item)">
                    <span>👍 推荐</span>
                    <span class="num">{{ item.likeCount }}</span>
                  </div>
                </div>
                <p class="comment-content">{{ item.content }}</p>
              </div>

              <div class="infinite-loading-container" v-if="commentData.length === 0">
                <div class="no-results">
                  <p>还没有人评论，快来抢沙发吧~</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="sidebar recommend">
          <div class="title">热门推荐</div>
          <div class="things">
            <div class="thing-item" v-for="item in recommendData" :key="item.id" @click="handleDetail(item)">
              <div class="img-view">
                <img :src="item.cover" alt="推荐图片">
              </div>
              <div class="info-view">
                <h3 class="thing-name">{{ item.title.substring(0, 15) }}<span v-if="item.title.length > 15">...</span>
                </h3>
                <div class="price-tag">{{ item.level }}级 · ¥{{ item.price }}</div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { message } from "ant-design-vue";
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import WantIcon from '/@/assets/images/want-read-hover.svg';
import RecommendIcon from '/@/assets/images/recommend-hover.svg';
import ShareIcon from '/@/assets/images/share-icon.svg';
import AvatarIcon from '/@/assets/images/avatar.jpg';
import {
  detailApi,
  listApi as listThingList,
} from '/@/api/thing'
import { listThingCommentsApi, createApi as createCommentApi, likeApi } from '/@/api/comment'
import { wishApi } from '/@/api/thingWish'
import { collectApi } from '/@/api/thingCollect'
import { BASE_URL } from "/@/store/constants";
import { useRoute, useRouter } from "vue-router/dist/vue-router";
import { useUserStore } from "/@/store";
import { getFormatTime } from "/@/utils";
import { ref, onMounted } from 'vue';

const router = useRouter()
const route = useRoute()
const userStore = useUserStore();

let thingId = ref('')
let detailData = ref({})
let tabUnderLeft = ref(12)
let tabData = ref(['详情简介', '游客评论'])
let selectTabIndex = ref(0)

let commentData = ref([])
let recommendData = ref([])
let sortIndex = ref(0)
let order = ref('recent')

let commentRef = ref()

onMounted(() => {
  thingId.value = route.query.id.trim()
  getThingDetail()
  getRecommendThing()
  getCommentList()
})

const selectTab = (index) => {
  selectTabIndex.value = index
  tabUnderLeft.value = 12 + 104 * index // 调整了指示器滑动的距离，适应更宽的字
}

const getThingDetail = () => {
  detailApi({ id: thingId.value }).then(res => {
    detailData.value = res.data
    detailData.value.cover = BASE_URL + '/api/staticfiles/image/' + detailData.value.cover
  }).catch(err => {
    message.error('获取详情失败')
  })
}

const addToWish = () => {
  let userId = userStore.user_id
  if (userId) {
    wishApi({ thingId: thingId.value, userId: userId }).then(res => {
      message.success(res.msg)
      getThingDetail()
    }).catch(err => {
      console.log('操作失败')
    })
  } else {
    message.warn('请先登录')
  }
}

const collect = () => {
  let userId = userStore.user_id
  if (userId) {
    collectApi({ thingId: thingId.value, userId: userId }).then(res => {
      message.success(res.msg)
      getThingDetail()
    }).catch(err => {
      console.log('收藏失败')
    })
  } else {
    message.warn('请先登录')
  }
}

const share = () => {
  let content = '分享一个非常棒的景区：' + detailData.value.title + ' ' + window.location.href
  let shareHref = 'http://service.weibo.com/share/share.php?title=' + encodeURIComponent(content)
  window.open(shareHref)
}

const getRecommendThing = () => {
  listThingList({ sort: 'recommend' }).then(res => {
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
      }
    })
    recommendData.value = res.data.slice(0, 5)
  }).catch(err => {
    console.log(err)
  })
}

const handleDetail = (item) => {
  let text = router.resolve({ name: 'detail', query: { id: item.id } })
  window.open(text.href, '_blank')
}

const sendComment = () => {
  let text = commentRef.value.value.trim()
  if (text.length <= 0) {
    message.warn('评论内容不能为空')
    return
  }
  let userId = userStore.user_id
  if (userId) {
    // ✅ 核心修复：使用 URLSearchParams 将数据转换为后端支持的表单格式
    const formData = new URLSearchParams()
    formData.append('content', text)
    formData.append('thingId', thingId.value)
    formData.append('userId', userId)

    // 发送格式化后的 formData
    createCommentApi(formData).then(res => {
      message.success('评论发布成功')
      commentRef.value.value = ''
      getCommentList() // 刷新评论列表
    }).catch(err => {
      console.log(err)
      message.error('评论发布失败')
    })
  } else {
    message.warn('请先登录！')
    router.push({ name: 'login' })
  }
}

const like = (item) => {
  // 1. 获取当前登录用户的 ID
  let currentUserId = userStore.user_id;
  if (!currentUserId) {
    message.warn('请先登录后再点赞！');
    return;
  }

  // 2. 限制不能给自己点赞 (判断当前登录用户ID是否等于评论作者的ID)
  if (item.userId === currentUserId) {
    message.warn('不能给自己的评论点赞哦！');
    return;
  }

  // 3. 从本地缓存获取当前用户已经点赞过的评论ID列表
  let storageKey = 'likedComments_' + currentUserId;
  let likedList = JSON.parse(localStorage.getItem(storageKey) || '[]');

  // 4. 判断是否已经点过赞
  if (likedList.includes(item.id)) {
    message.warn('您已经推荐过这条评论啦，不能重复点赞！');
    return;
  }

  // 5. 发送点赞请求到后端
  likeApi({ id: item.id }).then(res => {
    // 点赞成功后，将该评论ID存入当前用户的专属缓存中
    likedList.push(item.id);
    localStorage.setItem(storageKey, JSON.stringify(likedList));

    message.success('推荐成功！');
    getCommentList(); // 刷新评论列表
  }).catch(err => {
    console.log(err);
  });
}

const getCommentList = () => {
  listThingCommentsApi({ thingId: thingId.value, order: order.value }).then(res => {
    res.data.forEach(item => {
      item.commentTime = getFormatTime(item.commentTime, true)

      // 👇 新增：处理头像真实路径的拼接
      if (item.avatar && !item.avatar.startsWith('http')) {
        item.avatar = BASE_URL + '/api/staticfiles/avatar/' + item.avatar
      }
    })
    commentData.value = res.data
  }).catch(err => {
    console.log(err)
  })
}

const sortCommentList = (sortType) => {
  sortIndex.value = sortType === 'recent' ? 0 : 1
  order.value = sortType
  getCommentList()
}
</script>

<style scoped lang="less">
/* 全局基础设置 */
.detail {
  background: #f7f9fa;
  /* 更柔和的高级灰背景 */
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.hide {
  display: none !important;
}

.flex-view {
  display: flex;
}

.detail-content {
  width: 1200px;
  margin: 32px auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ===== 顶部卡片 (Hero Section) ===== */
.detail-content-top {
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.04);
  padding: 32px;
  margin-top: 40px;
}

.thing-infos-view {
  display: flex;
  align-items: stretch;
  gap: 40px;

  /* 封面图 */
  .thing-img-box {
    flex: 0 0 460px;
    height: 320px;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;

      &:hover {
        transform: scale(1.03);
      }
    }
  }

  /* 核心信息区 */
  .thing-info-box {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .thing-state {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      .state-badge {
        background: #e8f3ff;
        color: #1677ff;
        padding: 4px 12px;
        border-radius: 20px;
        font-size: 13px;
        font-weight: 600;
      }

      .state-text {
        font-size: 13px;
        color: #86868b;
      }
    }

    .thing-name {
      font-size: 32px;
      font-weight: 700;
      color: #1d1d1f;
      line-height: 1.3;
      margin: 0 0 16px 0;
    }

    .price-box {
      margin-bottom: 24px;
      color: #FF5A5F;
      display: flex;
      align-items: baseline;

      .a-price-symbol {
        font-size: 20px;
        font-weight: 600;
        margin-right: 2px;
      }

      .a-price {
        font-size: 36px;
        font-weight: 700;
      }

      .price-unit {
        font-size: 14px;
        color: #86868b;
        margin-left: 8px;
        font-weight: 400;
      }
    }

    /* 网格化信息展示 */
    .info-grid {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      margin-bottom: 32px;
      background: #fbfbfd;
      padding: 20px;
      border-radius: 12px;

      .info-item {
        display: flex;
        flex-direction: column;
        gap: 6px;
        min-width: 120px;

        &.full-width {
          width: 100%;
        }

        .label {
          font-size: 13px;
          color: #86868b;
        }

        .value {
          font-size: 15px;
          color: #1d1d1f;
          font-weight: 500;
        }
      }
    }

    /* 操作按钮行 */
    .action-row {
      display: flex;
      gap: 16px;
      margin-top: auto;

      .action-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        background: #f2f2f7;
        padding: 10px 20px;
        border-radius: 30px;
        cursor: pointer;
        transition: all 0.2s ease;

        .icon {
          width: 20px;
          height: 20px;
        }

        .text {
          font-size: 14px;
          font-weight: 600;
          color: #1d1d1f;
        }

        &:hover {
          background: #e5e5ea;
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }
      }
    }
  }
}

/* ===== 底部内容区 (Left: Main, Right: Sidebar) ===== */
.detail-content-bottom {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

/* 左侧主容器 */
.main-content {
  flex: 1;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.04);
  padding: 32px;
  min-height: 500px;

  /* 自定义时尚 Tab */
  .main-tab {
    border-bottom: 1px solid #f0f0f0;
    margin-bottom: 32px;

    .tab-list {
      position: relative;
      display: flex;
      gap: 40px;
    }

    .tab {
      font-size: 18px;
      font-weight: 500;
      color: #86868b;
      padding-bottom: 16px;
      cursor: pointer;
      transition: color 0.3s ease;

      &.tab-select {
        color: #1d1d1f;
        font-weight: 600;
      }

      &:hover {
        color: #1d1d1f;
      }
    }

    .tab-underline {
      position: absolute;
      bottom: -1px;
      width: 48px;
      height: 4px;
      background: #1d1d1f;
      border-radius: 2px;
      transition: left 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    }
  }

  /* 简介排版 */
  .thing-intro {
    .text {
      font-size: 16px;
      line-height: 1.8;
      color: #333333;
      white-space: pre-wrap;
      text-align: justify;
    }
  }
}

/* 评论区重构 */
.thing-comment {
  .publish {
    background: #fbfbfd;
    padding: 20px;
    border-radius: 16px;
    align-items: center;
    margin-bottom: 32px;

    .mine-img {
      width: 44px;
      height: 44px;
      border-radius: 50%;
      object-fit: cover;
      margin-right: 16px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .content-input {
      flex: 1;
      height: 48px;
      background: #ffffff;
      border: 1px solid #e5e5ea;
      border-radius: 24px;
      padding: 0 24px;
      font-size: 15px;
      color: #1d1d1f;
      transition: border-color 0.3s;

      &:focus {
        outline: none;
        border-color: #1677ff;
      }
    }

    .send-btn {
      margin-left: 16px;
      background: #1d1d1f;
      color: #ffffff;
      height: 48px;
      border-radius: 24px;
      padding: 0 32px;
      font-size: 15px;
      font-weight: 600;
      border: none;
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        background: #333336;
      }
    }
  }

  .tab-view {
    justify-content: space-between;
    margin-bottom: 24px;
    padding: 0 8px;

    .count-text {
      font-size: 15px;
      font-weight: 600;
      color: #1d1d1f;
    }

    .tab-box {
      font-size: 14px;
      color: #86868b;

      span {
        cursor: pointer;
        padding: 4px 8px;
        transition: color 0.2s;

        &.tab-select,
        &:hover {
          color: #1d1d1f;
          font-weight: 600;
        }
      }

      .line {
        width: 1px;
        height: 12px;
        background: #e5e5ea;
        margin: 0 8px;
      }
    }
  }

  .comments-list {
    .comment-item {
      padding: 24px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .flex-item {
        align-items: center;
        margin-bottom: 12px;

        .avator {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          margin-right: 12px;
        }

        .person {
          flex: 1;

          .name {
            font-size: 15px;
            font-weight: 600;
            color: #1d1d1f;
          }

          .time {
            font-size: 12px;
            color: #86868b;
            margin-top: 2px;
          }
        }

        .hover-btn {
          cursor: pointer;
          font-size: 13px;
          color: #86868b;
          background: #f2f2f7;
          padding: 6px 12px;
          border-radius: 16px;
          transition: all 0.2s;

          .num {
            margin-left: 4px;
            font-weight: 600;
          }

          &:hover {
            background: #e5e5ea;
            color: #1d1d1f;
          }
        }
      }

      .comment-content {
        margin-left: 52px;
        font-size: 15px;
        line-height: 1.6;
        color: #333333;
      }
    }

    .no-results {
      text-align: center;
      color: #86868b;
      padding: 60px 0;
      font-size: 15px;
    }
  }
}

/* 右侧侧边栏 (热门推荐) */
.sidebar {
  width: 320px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.04);
  padding: 24px;
  position: sticky;
  top: 24px;
  /* 随页面滚动时保持悬浮 */

  .title {
    font-size: 18px;
    font-weight: 600;
    color: #1d1d1f;
    margin-bottom: 20px;
  }

  .things {
    display: flex;
    flex-direction: column;
    gap: 20px;

    .thing-item {
      display: flex;
      gap: 16px;
      align-items: center;
      cursor: pointer;
      padding: 10px;
      margin: -10px;
      border-radius: 12px;
      transition: background 0.2s;

      &:hover {
        background: #fbfbfd;
      }

      .img-view {
        width: 80px;
        height: 80px;
        border-radius: 10px;
        overflow: hidden;
        flex-shrink: 0;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .info-view {
        display: flex;
        flex-direction: column;
        justify-content: center;

        .thing-name {
          font-size: 15px;
          font-weight: 600;
          color: #1d1d1f;
          line-height: 1.4;
          margin: 0 0 6px 0;
        }

        .price-tag {
          font-size: 13px;
          color: #FF5A5F;
          font-weight: 500;
        }
      }
    }
  }
}
</style>