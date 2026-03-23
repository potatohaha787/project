<template>
  <div class="heritage-portal">
    <Header />



    <div class="page-banner" :style="{ backgroundImage: `url(${BgImage})` }">
      <div class="banner-overlay"></div>
      <div class="banner-content">
        <h1>香山非遗</h1>
        <p class="banner-title">Xiangshan Intangible Cultural Heritage</p>
        <p>大音希声，大象无形。品味中山千年的民间绝学</p>
      </div>
    </div>

    <section class="intro-section">
      <div class="intro-container">
        <div class="intro-text">
          <h2 class="section-heading">国家历史文化名城</h2>
          <p class="desc">
            中山，古称香山。这片土地不仅孕育了璀璨的近代商业与革命文明，更在漫长的岁月长河中，沉淀下了极其丰富的非物质文化遗产。
            <br /><br />
            从激昂的赛龙舟到婉转的三乡木偶戏，从精妙的红木雕刻到飘逸的黄圃飘色……它们是沉淀在民间的“活态”历史，承载着香山人民的匠心与温度。
          </p>
        </div>
        <div class="intro-image-wrapper">
          <img :src="CultureImg" class="intro-img" alt="文化传承" />
          <div class="img-border"></div>
        </div>
      </div>
    </section>

    <section class="catalog-section">
      <div class="catalog-wrapper">

        <div class="level-block" v-for="(levelGroup, gIndex) in heritageData" :key="gIndex">
          <div class="level-title-side">
            <h2 class="level-name">{{ levelGroup.level }}</h2>
            <div class="level-seal">{{ levelGroup.tag }}</div>
          </div>

          <div class="level-items-side">
            <div class="heritage-card pointer" v-for="(item, iIndex) in levelGroup.items" :key="iIndex"
              @click="goHeritageDetail(item.id)">
              <div class="watermark-num">0{{ iIndex + 1 }}</div>
              <div class="card-content">
                <h3 class="item-title">{{ item.title }}</h3>
                <p class="item-desc">{{ item.desc }}</p>
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import BgImage from '/@/assets/images/bg6.jpg'
import CultureImg from '/@/assets/images/culture.jpg'
// 【引入刚才写的接口】
import { listApi } from '/@/api/heritage'
import { useRouter } from 'vue-router/dist/vue-router' // 引入

const router = useRouter()
// 声明空的响应式数据
const heritageData = ref([])

onMounted(() => {
  // 页面加载时请求数据
  fetchHeritageData()
})

const fetchHeritageData = () => {
  listApi().then(res => {
    // res.data 是后端返回的扁平数组
    const rawData = res.data;

    // 我们需要把扁平的数据，按照 国家级、省级、市级 进行分组
    const groupedData = [];
    const levelOrder = ['国家级非物质文化遗产', '省级非物质文化遗产', '市级非物质文化遗产'];

    levelOrder.forEach(levelName => {
      // 过滤出当前级别的所有项目
      const items = rawData.filter(item => item.level === levelName);
      if (items.length > 0) {
        groupedData.push({
          level: levelName,
          tag: items[0].tag, // 标签名直接取第一项的
          // 映射字段名以适配原本的HTML模板
          items: items.map(i => ({
            id: i.id,
            title: i.title,
            desc: i.description
          }))
        });
      }
    });

    // 赋值给前端展示
    heritageData.value = groupedData;
  }).catch(err => {
    console.log("获取非遗数据失败", err);
  })
}

const goHeritageDetail = (id) => {
  // 使用 name 跳转，并通过 query 携带 ID (格式：/heritageDetail?id=xxx)
  router.push({ name: 'heritageDetail', query: { id: id } })
}
</script>

<style scoped lang="less">
/* --- 核心色彩与字体定义 --- */
@paper-white: #f7f6f2;
/* 宣纸白 */
@ink-black: #2c2827;
/* 墨黑 */
@vermilion: #bc332a;
/* 朱砂红 */
@accent-color: #c5b596;
/* 古铜金 */

.heritage-portal {
  background-color: @paper-white;
  font-family: "Noto Serif SC", "Songti SC", "STZhongsong", serif;
  color: @ink-black;
  overflow-x: hidden;
}

.page-banner {
  position: relative;
  padding: 140px 20px 80px;
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
    background: rgba(0, 0, 0, 0.65);
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
      /* 修复：将未定义的 @accent-color 改为白色或古铜金 @accent-color */
      color: #c59d5f;
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

/* --- 2. 序言区 --- */
.intro-section {
  padding: 100px 0;
  max-width: 1100px;
  margin: 0 auto;
}

.intro-container {
  display: flex;
  align-items: center;
  gap: 100px;
}

.intro-text {
  flex: 1;
  position: relative;

  &::before {
    content: "「";
    position: absolute;
    top: -40px;
    left: -30px;
    font-size: 80px;
    color: @accent-color;
    opacity: 0.3;
  }

  .section-heading {
    font-size: 36px;
    font-weight: normal;
    margin-bottom: 30px;
    color: @ink-black;
  }

  .desc {
    font-size: 16px;
    line-height: 2.2;
    color: #555;
    text-align: justify;
    font-family: sans-serif;
  }
}

.intro-image-wrapper {
  flex: 1;
  position: relative;

  .intro-img {
    width: 100%;
    height: 440px;
    object-fit: cover;
    z-index: 2;
    position: relative;
  }

  .img-border {
    position: absolute;
    top: 20px;
    left: 20px;
    width: 100%;
    height: 100%;
    border: 1px solid @accent-color;
    z-index: 1;
  }
}

/* --- 3. 博物馆图录风格分类排版 --- */
.catalog-section {
  padding: 40px 0 140px;
  background: #ffffff;
}

.catalog-wrapper {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 20px;
}

.level-block {
  display: flex;
  margin-bottom: 100px;
  border-bottom: 1px solid #f0eee5;
  padding-bottom: 80px;

  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }
}

/* 左侧：古典竖排标题区 */
.level-title-side {
  width: 160px;
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding-right: 40px;
  border-right: 1px dashed @accent-color;
  margin-right: 60px;

  .level-name {
    writing-mode: vertical-rl;
    font-size: 32px;
    font-weight: normal;
    letter-spacing: 12px;
    color: @ink-black;
    margin: 0;
  }

  .level-seal {
    writing-mode: vertical-rl;
    background: @vermilion;
    color: #fff;
    padding: 16px 6px;
    font-size: 14px;
    letter-spacing: 4px;
    border-radius: 2px;
    margin-top: 10px;
  }
}

/* 右侧：卡片网格区 */
.level-items-side {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
}

/* 无图高级卡片设计 */
.heritage-card {
  position: relative;
  padding: 30px;
  background: #fdfdfc;
  border: 1px solid #eae8df;
  transition: all 0.4s ease;
  cursor: default;
  overflow: hidden;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.04);
    border-color: @accent-color;

    .item-title {
      color: @vermilion;
    }

    .watermark-num {
      transform: scale(1.1) translate(5px, 5px);
      color: #e8e4d3;
    }
  }

  /* 巨型水印数字 */
  .watermark-num {
    position: absolute;
    right: 10px;
    bottom: -15px;
    font-size: 100px;
    font-weight: bold;
    color: #f2f0e6;
    z-index: 0;
    transition: all 0.5s ease;
    font-family: Arial, Helvetica, sans-serif;
    user-select: none;
  }

  .card-content {
    position: relative;
    z-index: 1;
  }

  .item-title {
    font-size: 22px;
    font-weight: normal;
    color: @ink-black;
    margin: 0 0 16px 0;
    transition: color 0.3s;
    position: relative;
    padding-bottom: 12px;

    &::after {
      content: "";
      position: absolute;
      bottom: 0;
      left: 0;
      width: 30px;
      height: 2px;
      background: @accent-color;
    }
  }

  .item-desc {
    font-family: sans-serif;
    font-size: 15px;
    color: #666;
    line-height: 1.8;
    margin: 0;
    text-align: justify;
  }
}
</style>