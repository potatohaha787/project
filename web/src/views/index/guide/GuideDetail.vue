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

            <div class="author-card">
              <div class="author-bg"></div>
              <div class="author-info">
                <a-avatar :src="article.authorAvatar" :size="72" class="author-avatar" />
                <h3 class="author-name">{{ article.author }}</h3>
                <p class="author-desc">{{ article.authorDesc }}</p>
                <div class="author-stats">
                  <div class="stat"><span class="num">12</span><span class="label">游记</span></div>
                  <div class="stat"><span class="num">3.4k</span><span class="label">获赞</span></div>
                  <div class="stat"><span class="num">890</span><span class="label">粉丝</span></div>
                </div>
                <a-button type="primary" block size="large" class="follow-btn" :class="{ followed: isFollowed }"
                  @click="isFollowed = !isFollowed">
                  {{ isFollowed ? '已关注' : '+ 关注作者' }}
                </a-button>
              </div>
            </div>

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
import { ref } from 'vue'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import { message } from 'ant-design-vue'

// 交互状态
const isLiked = ref(false)
const isCollected = ref(false)
const isFollowed = ref(false)
const isSubmitting = ref(false)
const commentText = ref('')

// 切换喜欢/收藏
const toggleLike = () => { isLiked.value = !isLiked.value; if (isLiked.value) message.success('点赞成功！') }
const toggleCollect = () => { isCollected.value = !isCollected.value; if (isCollected.value) message.success('已加入收藏夹！') }

// 模拟游记详细数据
const article = ref({
  title: '周末逃离城市计划：沉醉在崖口村的金色稻浪与绝美日落',
  cover: 'https://images.unsplash.com/photo-1506012787146-f92b2d7d6d96?q=80&w=2069&auto=format&fit=crop', // 替换为您本地的高清宽图
  location: '南朗崖口村',
  time: '2023-10-24 14:30',
  views: '3.4w',
  likes: 1280,
  author: '旅行体验师_阿杰',
  authorAvatar: 'https://joeschmoe.io/api/v1/jess',
  authorDesc: '走遍大江南北，偏爱岭南烟火。中山土著，带你玩转地道香山。',
  tags: ['周末游', '摄影打卡', '海边日落', '小众秘境'],
  content: `
    <p class="lead-text">如果你问我中山秋天最美的地方在哪里，我会毫不犹豫地告诉你：去崖口吧。</p>
    <p>不想走远，就在中山周边寻一处清静。南朗崖口村，一个背靠五桂山、面向珠江口的小渔村，藏着中山最迷人的金色稻浪和最温柔的海边日落。</p>
    <h3>📍 第一站：千亩稻田，宫崎骏的夏天</h3>
    <p>下午三点左右到达崖口，这时候的光线已经开始变得柔和。租一辆小电驴或者自行车，穿梭在平整的村道上，两旁是金灿灿的稻田，微风吹过，稻浪翻滚，仿佛直接闯入了宫崎骏的动画世界。</p>
    <img src="https://images.unsplash.com/photo-1589148413645-8c7e0bde224a?q=80&w=1000&auto=format&fit=crop" alt="稻田" />
    <span class="img-caption">微风拂过金色的稻浪，是秋天最美的馈赠</span>
    <p><strong>拍照小Tips：</strong>建议穿浅色系（白色、米色）或者碎花裙，和金色的稻田形成反差，特别出片。走到田埂中间的小路上，用长焦镜头压缩背景，氛围感拉满！</p>
    
    <h3>📍 第二站：崖口海鲜街与绝美日落</h3>
    <p>逛完稻田，差不多五点半，直接前往海边。崖口的海滩虽然没有白沙，但长长的海岸堤坝和错落的渔船，别有一番渔村风情。</p>
    <p>找一家靠海的煲仔饭店，点上一份招牌的“崖口煲仔饭”和几盘刚刚打捞上来的小海鲜。当夕阳的余晖将整个海面染成橘红色时，吃着热腾腾的煲仔饭，吹着海风，所有的班味都在这一刻烟消云散。</p>
    <img src="https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=1000&auto=format&fit=crop" alt="海边日落" />
    
    <h3>💡 实用攻略信息</h3>
    <ul>
      <li><strong>🚗 交通：</strong>自驾导航至“南朗崖口村”，村内有停车场。如果是公共交通，可以坐城轨到“南朗站”，再打车前往。</li>
      <li><strong>⏰ 最佳时间：</strong>看稻田建议10月下旬到11月中旬；看日落一定要提前查好天气，晴天的傍晚最美。</li>
      <li><strong>🍜 美食推荐：</strong>崖口云吞、海鲜煲仔饭、崖口特色咸茶。</li>
    </ul>
    <p>不需要太多的计划，在一个周末的午后，去崖口吹吹风吧，中山的慢生活，都在这片海与田之间了。</p>
  `
})

// 模拟评论
const comments = ref([
  { id: 1, author: '广州周末游', avatar: 'https://joeschmoe.io/api/v1/jon', time: '2天前', content: '被博主种草了！请问这个时候去稻子收割了吗？准备这周末带娃过去。', likes: 12 },
  { id: 2, author: '贪吃的小胖', avatar: 'https://joeschmoe.io/api/v1/jia', time: '1天前', content: '崖口煲仔饭是真的绝！特别是那个黄鳝煲仔饭，加上锅巴，一口下去太满足了！', likes: 8 }
])

// 发表评论
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
  }, 800)
}

// 模拟相关推荐
const relatedArticles = ref([
  { id: 1, title: '重走伟人故里，翠亨村的一日人文漫步', views: '1.2w', cover: 'https://images.unsplash.com/photo-1543083652-32a76f2fc700?q=80&w=300&auto=format&fit=crop' },
  { id: 2, title: '本地土著吐血整理：石岐老街地道美食地图', views: '8900', cover: 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?q=80&w=300&auto=format&fit=crop' },
  { id: 3, title: '紫马岭公园秋日赏花指南，周末野餐好去处', views: '5420', cover: 'https://images.unsplash.com/photo-1465146344425-f00d5f5c8f07?q=80&w=300&auto=format&fit=crop' }
])
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

/* 作者名片 */
.author-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  text-align: center;

  .author-bg {
    height: 80px;
    background: linear-gradient(135deg, @zs-green 0%, darken(@zs-green, 10%) 100%);
  }

  .author-info {
    padding: 0 24px 24px;

    .author-avatar {
      border: 4px solid #fff;
      margin-top: -36px;
      margin-bottom: 12px;
      background: #fff;
    }

    .author-name {
      font-size: 18px;
      font-weight: bold;
      color: @dark-text;
      margin-bottom: 8px;
    }

    .author-desc {
      font-size: 13px;
      color: @light-text;
      line-height: 1.6;
      margin-bottom: 20px;
    }

    .author-stats {
      display: flex;
      justify-content: center;
      gap: 24px;
      margin-bottom: 24px;
      padding-bottom: 24px;
      border-bottom: 1px solid #f1f5f9;

      .stat {
        display: flex;
        flex-direction: column;

        .num {
          font-size: 16px;
          font-weight: bold;
          color: @dark-text;
        }

        .label {
          font-size: 12px;
          color: #94a3b8;
          margin-top: 4px;
        }
      }
    }

    .follow-btn {
      background: @zs-yellow;
      border-color: @zs-yellow;
      font-weight: bold;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(217, 119, 6, 0.2);

      &:hover {
        background: lighten(@zs-yellow, 5%);
        border-color: lighten(@zs-yellow, 5%);
        transform: translateY(-1px);
      }

      &.followed {
        background: #f1f5f9;
        border-color: #f1f5f9;
        color: @light-text;
        box-shadow: none;
      }
    }
  }
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