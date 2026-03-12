<template>
  <div class="page-container">
    <div class="history-banner">
      <div class="overlay">
        <div class="text-wrapper">
          <span class="tag">【 国家重点文物保护单位 】</span>
          <h1 class="history-title">孙中山故居纪念馆</h1>
          <p class="history-desc">
            始于翠亨，见证共和。<br/>
            踏入这座融合中西风格的红楼，品读香山少年的壮志雄心，领略伟人忧国忧民的情怀。
          </p>
          <div class="action-group">
            <button class="cultural-btn primary-btn">开启寻根之旅</button>
            <button class="cultural-btn ghost-btn">查看全景导览</button>
          </div>
        </div>
      </div>
    </div>

    <div class="content">
      <div class="content-left">
        <div class="left-search-item">
          <h4>文化印记</h4>
          <a-tree :tree-data="contentData.cData" :selected-keys="contentData.selectedKeys" @select="onSelect"
                  style="min-height: 220px;">
          </a-tree>
        </div>
        <div class="left-search-item"><h4>非遗与特色</h4>
          <div class="tag-view tag-flex-view">
              <span class="tag" :class="{'tag-select': contentData.selectTagId===item.id}"
                    v-for="item in contentData.tagData" :key="item.id"
                    @click="clickTag(item.id)">{{ item.title }}</span>
          </div>
        </div>
      </div>
      
      <div class="content-right">
        <div class="top-select-view flex-view">
          <div class="order-view">
            <span class="title"></span>
            <span class="tab"
                  :class="contentData.selectTabIndex===index? 'tab-select':''"
                  v-for="(item,index) in contentData.tabData"
                  :key="index"
                  @click="selectTab(index)">
              {{ item }}
            </span>
            <span :style="{left: contentData.tabUnderLeft + 'px'}" class="tab-underline"></span>
          </div>
        </div>
        <a-spin :spinning="contentData.loading" style="min-height: 200px;">
          <div class="pc-thing-list flex-view">
            <div v-for="item in contentData.pageData" :key="item.id" @click="handleDetail(item)"
                 class="thing-item item-column-3">
              <div class="img-view">
                <img :src="item.cover">
              </div>
              <div class="info-view">
                <h3 class="thing-name">{{ item.title.substring(0, 12) }}</h3>
                <span class="a-price-wrapper">
                  <span class="a-price">评分 {{ item.level }}级 | ￥{{ item.price }} | 余票：{{item.repertory}}</span>
                </span>
              </div>
            </div>
            <div v-if="contentData.pageData.length <= 0 && !contentData.loading" class="no-data">暂无历史文旅数据</div>
          </div>
        </a-spin>
        <div class="page-view">
          <a-pagination v-model="contentData.page" size="small" @change="changePage" :hideOnSinglePage="true"
                        :defaultPageSize="contentData.pageSize" :total="contentData.total" :showSizeChanger="false"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {listApi as listClassificationList} from '/@/api/classification'
import {listApi as listTagList} from '/@/api/tag'
import {listApi as listThingList} from '/@/api/thing'
import {BASE_URL} from "/@/store/constants";
import {useUserStore} from "/@/store";
import { reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const userStore = useUserStore()
const router = useRouter();

const contentData = reactive({
  selectX: 0,
  selectTagId: -1,
  cData: [],
  selectedKeys: [],
  tagData: [],
  loading: false,

  tabData: ['最新史料', '最热打卡', '文旅推荐'],
  selectTabIndex: 0,
  tabUnderLeft: 12,

  thingData: [],
  pageData: [],

  page: 1,
  total: 0,
  pageSize: 12,
})

onMounted(() => {
  initSider()
  getThingList({})
})

const initSider = () => {
  contentData.cData.push({key:'-1', title:'全部香山记忆'})
  listClassificationList().then(res => {
    res.data.forEach(item=>{
      item.key = item.id
      contentData.cData.push(item)
    })
  })
  listTagList().then(res => {
    contentData.tagData = res.data
  })
}

const getSelectedKey = () => {
  if (contentData.selectedKeys.length > 0) {
    return contentData.selectedKeys[0]
  } else {
    return -1
  }
}
const onSelect = (selectedKeys) => {
  contentData.selectedKeys = selectedKeys
  if (contentData.selectedKeys.length > 0) {
    getThingList({c: getSelectedKey()})
  }
  contentData.selectTagId = -1
}
const clickTag = (index) => {
  contentData.selectedKeys = []
  contentData.selectTagId = index
  getThingList({tag: contentData.selectTagId})
}

const selectTab = (index) => {
  contentData.selectTabIndex = index
  contentData.tabUnderLeft = 12 + 65 * index // 调整了下划线滑动的距离
  let sort = (index === 0 ? 'recent' : index === 1 ? 'hot' : 'recommend')
  const data = {sort: sort}
  if (contentData.selectTagId !== -1) {
    data['tag'] = contentData.selectTagId
  } else {
    data['c'] = getSelectedKey()
  }
  getThingList(data)
}

const handleDetail = (item) => {
  let text = router.resolve({name: 'detail', query: {id: item.id}})
  window.open(text.href, '_blank')
}

const changePage = (page) => {
  contentData.page = page
  let start = (contentData.page - 1) * contentData.pageSize
  contentData.pageData = contentData.thingData.slice(start, start + contentData.pageSize)
}

const getThingList = (data) => {
  contentData.loading = true
  listThingList(data).then(res => {
    contentData.loading = false
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' +  item.cover
      }
    })
    contentData.thingData = res.data
    contentData.total = contentData.thingData.length
    changePage(1)
  }).catch(err => {
    console.log(err)
    contentData.loading = false
  })
}
</script>

<style scoped lang="less">
.page-container {
  background-color: #F5F5DC; /* 全局注入宣纸米白 */
  min-height: 100vh;
  padding-bottom: 60px;
}

/* 历史长卷 Banner 样式 */
.history-banner {
  width: 100%;
  height: 450px;
  margin-top: 64px; /* 避开Header高度 */
  background: linear-gradient(to right, rgba(50, 20, 10, 0.85), rgba(160, 82, 45, 0.2)), 
              url('/images/bg2.jpg') no-repeat center/cover; /* 建议将故居原图替换为 public/images/bg2.jpg 或你的实图 */
  position: relative;
  
  .overlay {
    height: 100%;
    display: flex;
    align-items: center;
    padding-left: 10%;
  }

  .text-wrapper {
    color: #F5F5DC;
    
    .tag {
      background: #A0522D;
      padding: 6px 16px;
      font-size: 14px;
      letter-spacing: 2px;
      border-radius: 4px;
    }
    
    .history-title {
      font-size: 56px;
      margin: 24px 0 16px;
      font-family: 'STZhongsong', 'SimSun', serif;
      color: #FFF;
      text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
    }
    
    .history-desc {
      max-width: 550px;
      font-size: 18px;
      line-height: 1.8;
      margin-bottom: 36px;
      opacity: 0.95;
      text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
    }

    .action-group {
      display: flex;
      gap: 20px;
      
      .cultural-btn {
        padding: 12px 32px;
        font-size: 16px;
        border-radius: 30px;
        cursor: pointer;
        transition: all 0.3s;
        border: none;
      }
      
      .primary-btn {
        background: #A0522D;
        color: #FFF;
        &:hover { background: #8B4513; }
      }
      
      .ghost-btn {
        background: transparent;
        border: 2px solid #F5F5DC;
        color: #F5F5DC;
        &:hover {
          background: #F5F5DC;
          color: #A0522D;
        }
      }
    }
  }
}

.content {
  display: flex;
  flex-direction: row;
  width: 1100px;
  margin: 40px auto 0; /* 调整间距 */
}

.content-left {
  width: 220px;
  margin-right: 32px;
}

.left-search-item {
  overflow: hidden;
  border-bottom: 1px dashed #D2B48C; /* 分割线改为具有复古感的虚线 */
  margin-top: 24px;
  padding-bottom: 24px;
}

h4 {
  color: #4A3A35;
  font-family: 'STZhongsong', 'SimSun', serif;
  font-weight: bold;
  font-size: 18px;
  line-height: 24px;
  height: 24px;
}

.tag-view {
  flex-wrap: wrap;
  margin-top: 12px;
}

.tag-flex-view {
  display: flex;
}

.tag {
  background: transparent;
  border: 1px solid #C0A080;
  box-sizing: border-box;
  border-radius: 4px; /* 改为方形标签更显传统 */
  height: 24px;
  line-height: 22px;
  padding: 0 10px;
  margin: 8px 8px 0 0;
  cursor: pointer;
  font-size: 13px;
  color: #5C4A42;
  transition: all 0.3s ease;
}

.tag:hover, .tag-select {
  background: #A0522D;
  color: #F5F5DC;
  border: 1px solid #A0522D;
}

.content-right {
  flex: 1;
  padding-top: 12px;

  .top-select-view {
    justify-content: space-between;
    align-items: center;
    height: 40px;
    line-height: 40px;

    .order-view {
      position: relative;
      color: #6C5C54;
      font-size: 16px;
      font-family: 'STZhongsong', 'SimSun', serif;

      .tab {
        color: #998A83;
        margin-right: 32px;
        cursor: pointer;
        transition: color 0.3s;
      }

      .tab-select {
        color: #A0522D;
        font-weight: bold;
      }

      .tab-underline {
        position: absolute;
        bottom: 0;
        left: 12px;
        width: 64px; /* 延长下划线 */
        height: 3px;
        background: #A0522D; /* 主色调下划线 */
        transition: left 0.3s;
      }
    }
  }

  .pc-thing-list {
    flex-wrap: wrap;

    .thing-item {
      min-width: 255px;
      max-width: 255px;
      position: relative;
      flex: 1;
      margin-right: 20px;
      height: fit-content;
      margin-top: 26px;
      margin-bottom: 36px;
      cursor: pointer;
      background: #FFFDFC;
      border-radius: 6px;
      box-shadow: 0 4px 12px rgba(160, 82, 45, 0.08); /* 暖色阴影 */
      transition: all 0.4s ease;

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 12px 24px rgba(160, 82, 45, 0.15);
      }

      .img-view {
        height: 180px;
        width: 100%;
        overflow: hidden;
        border-radius: 6px 6px 0 0;

        img {
          height: 100%;
          width: 100%;
          object-fit: cover;
          /* 核心交互：复古黑白滤镜，悬停恢复生机 */
          filter: sepia(0.5) contrast(1.1) brightness(0.9);
          transition: transform 0.6s ease, filter 0.6s ease;
        }
      }

      &:hover .img-view img {
        transform: scale(1.05);
        filter: sepia(0) contrast(1) brightness(1); /* 鼠标移入变回现代彩色 */
      }

      .info-view {
        padding: 16px;
        border-top: 3px solid #EADDD7;

        .thing-name {
          line-height: 24px;
          margin-top: 0;
          color: #4A3A35 !important;
          font-size: 16px !important;
          font-weight: bold !important;
          font-family: 'STZhongsong', 'SimSun', serif;
        }

        .a-price-wrapper {
          display: block;
          margin-top: 10px;
          color: #8B4513;
          font-size: 13px;
          border-top: 1px dashed #EADDD7;
          padding-top: 10px;
        }
      }
    }

    .no-data {
      height: 200px;
      line-height: 200px;
      text-align: center;
      width: 100%;
      font-size: 16px;
      color: #A0522D;
    }
  }

  .page-view {
    width: 100%;
    text-align: center;
    margin-top: 48px;
  }
}
</style>