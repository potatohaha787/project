<template>
  <div class="publish-page">
    <Header />

    <div class="publish-container">
      <div class="page-header">
        <a-button type="link" class="back-btn" @click="goBack">
          &larr; 返回社区
        </a-button>
        <h1 class="page-title">撰写游记</h1>
        <p class="page-subtitle">分享你在香山的每一次心动与探索</p>
      </div>

      <div class="form-card">
        <a-form :model="formState" layout="vertical" @finish="onSubmit">

          <a-form-item name="cover" class="cover-upload-item">
            <a-upload-dragger v-model:fileList="fileList" name="file" :multiple="false" :before-upload="beforeUpload"
              class="custom-dragger">
              <p class="ant-upload-drag-icon">
                <span class="upload-icon">📸</span>
              </p>
              <p class="ant-upload-text">点击或将图片拖拽到这里上传封面</p>
              <p class="ant-upload-hint">建议尺寸 16:9，高清精美的图片能吸引更多人阅读哦</p>
            </a-upload-dragger>
          </a-form-item>

          <a-form-item name="title" :rules="[{ required: true, message: '请填写游记标题！' }]">
            <a-input v-model:value="formState.title" placeholder="给你的游记起个响亮的标题吧 (最多40字)" size="large" :maxlength="40"
              class="title-input" />
          </a-form-item>

          <a-form-item name="content" :rules="[{ required: true, message: '请填写游记正文！' }]">
            <a-textarea v-model:value="formState.content" placeholder="分享你的行程、美景、美食或者避坑指南..."
              :auto-size="{ minRows: 12, maxRows: 20 }" class="content-input" />
          </a-form-item>

          <a-row :gutter="24">
            <a-col :xs="24" :sm="12">
              <a-form-item label="📍 打卡地点" name="location">
                <a-input v-model:value="formState.location" placeholder="例如：孙中山故居 / 崖口村" size="large" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12">
              <a-form-item label="🏷️ 游记标签" name="tags">
                <a-select v-model:value="formState.tags" mode="multiple" placeholder="选择或输入标签 (最多3个)" size="large"
                  :maxTagCount="3" :options="tagOptions" />
              </a-form-item>
            </a-col>
          </a-row>

          <div class="form-actions">
            <a-button size="large" @click="saveDraft" class="draft-btn">保存草稿</a-button>
            <a-button type="primary" size="large" html-type="submit" class="submit-btn" :loading="isSubmitting">
              发布游记
            </a-button>
          </div>
        </a-form>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import { message } from 'ant-design-vue'

// 引入发布游记的真实接口
import { createPostApi } from '/@/api/post'

const router = useRouter()
const isSubmitting = ref(false)

// 表单数据绑定
const formState = reactive({
  title: '',
  content: '',
  location: '',
  tags: []
})

// 上传封面列表
const fileList = ref([])

// 模拟预设标签
const tagOptions = ref([
  { value: '小众秘境', label: '小众秘境' },
  { value: '周末游', label: '周末游' },
  { value: '地道美食', label: '地道美食' },
  { value: '亲子遛娃', label: '亲子遛娃' },
  { value: '历史人文', label: '历史人文' },
  { value: '徒步户外', label: '徒步户外' }
])

// 返回上一页
const goBack = () => {
  router.back()
}

// 阻止图片的自动上传，将其暂存在 fileList 中等待表单一起提交
const beforeUpload = (file) => {
  // 因为限制了单张封面，所以每次选择新图片直接覆盖旧的
  fileList.value = [file]
  return false // false 代表阻止 Ant Design Vue 的默认网络上传
}

// 保存草稿模拟
const saveDraft = () => {
  message.info('已保存到草稿箱')
}

// 核心：提交发布真实逻辑（直接写入数据库）
const onSubmit = async (values) => {
  isSubmitting.value = true

  try {
    // 1. 获取当前登录用户的 ID
    const userInfoStr = localStorage.getItem('user_id')
    if (!currentUserId) {
      message.warning('请先登录后再发布游记哦！')
      isSubmitting.value = false
      return
    }

    if (fileList.value.length === 0) {
      message.warning('请上传一张游记封面！')
      isSubmitting.value = false
      return
    }

    // 2. 组装发给后端的 FormData 数据
    const formData = new FormData()
    formData.append('type', 'guide') // 强制指定类型为游记
    formData.append('title', values.title)
    formData.append('userId', currentUserId)

    // 如果游记表没有专门的 location 和 tags 字段，我们可以巧妙地把它们拼接到正文后面
    let finalContent = values.content
    if (values.location) {
      finalContent += `<br/><br/>📍 <b>打卡地点：</b>${values.location}`
    }
    if (values.tags && values.tags.length > 0) {
      finalContent += `<br/>🏷️ <b>游记标签：</b>${values.tags.join('、')}`
    }
    formData.append('content', finalContent)

    // 3. 将之前拦截暂存的图片实体提取出来放进表单
    // Ant Design Vue 的 file 对象真实文件藏在 originFileObj 属性里
    const rawFile = fileList.value[0].originFileObj || fileList.value[0]
    formData.append('imageFile', rawFile)

    // 4. 调用后端接口直接入库
    await createPostApi(formData)

    message.success('🎉 游记发布成功！已直接入库。')

    // 发布成功后清空表单并跳转回交流吧列表页 (路由名字根据您实际配置的调整)
    router.push({ name: 'ForumList' })

  } catch (error) {
    console.error("游记发布失败:", error)
    message.error('发布失败，请检查网络或联系管理员重试')
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

.publish-page {
  background-color: @bg-color;
  min-height: 100vh;
  padding-bottom: 80px;
}

.publish-container {
  max-width: 900px;
  /* 沉浸式写作不需要太宽 */
  margin: 0 auto;
  padding: 120px 20px 40px;
}

/* 页面头部 */
.page-header {
  margin-bottom: 30px;
  text-align: center;
  position: relative;

  .back-btn {
    position: absolute;
    left: 0;
    top: 0;
    color: @light-text;
    padding: 0;
    font-size: 15px;

    &:hover {
      color: @zs-green;
    }
  }

  .page-title {
    font-size: 32px;
    font-weight: 800;
    color: @dark-text;
    margin-bottom: 8px;
  }

  .page-subtitle {
    font-size: 15px;
    color: @light-text;
  }
}

/* 表单卡片 */
.form-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
}

/* 覆盖上传组件样式 */
.cover-upload-item {
  margin-bottom: 30px;
}

.custom-dragger {
  :deep(.ant-upload) {
    background-color: #f8fafc;
    border: 2px dashed #e2e8f0;
    border-radius: 12px;
    transition: all 0.3s;

    &:hover {
      border-color: @zs-green;
      background-color: #f0fdf4;
    }
  }

  .upload-icon {
    font-size: 40px;
    opacity: 0.8;
  }

  .ant-upload-text {
    font-size: 16px;
    color: @dark-text;
    font-weight: 600;
    margin-top: 10px;
  }

  .ant-upload-hint {
    color: @light-text;
    font-size: 13px;
  }
}

/* 覆盖输入框样式 */
.title-input {
  font-size: 20px;
  font-weight: bold;
  border: none;
  border-bottom: 2px solid #e2e8f0;
  border-radius: 0;
  padding: 10px 0;
  box-shadow: none !important;

  &:focus {
    border-bottom-color: @zs-green;
  }

  &::placeholder {
    font-weight: normal;
    color: #cbd5e1;
  }
}

.content-input {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  font-size: 15px;
  line-height: 1.8;
  resize: none;

  &:focus {
    border-color: @zs-green;
    box-shadow: 0 0 0 2px rgba(6, 78, 59, 0.1);
    background-color: #fff;
  }
}

/* 覆盖 Antd 输入框和选择器的主题色 */
:deep(.ant-input-affix-wrapper),
:deep(.ant-select-selector),
:deep(.ant-input) {

  &:focus,
  &-focused {
    border-color: @zs-green !important;
    box-shadow: 0 0 0 2px rgba(6, 78, 59, 0.1) !important;
  }
}

:deep(.ant-select-item-option-selected:not(.ant-select-item-option-disabled)) {
  background-color: rgba(6, 78, 59, 0.1);
  color: @zs-green;
  font-weight: bold;
}

/* 底部按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #f1f5f9;

  .draft-btn {
    border-color: #cbd5e1;
    color: @light-text;
    border-radius: 8px;

    &:hover {
      color: @zs-yellow;
      border-color: @zs-yellow;
    }
  }

  .submit-btn {
    background-color: @zs-green;
    border-color: @zs-green;
    border-radius: 8px;
    font-weight: bold;
    padding: 0 32px;

    &:hover {
      background-color: lighten(@zs-green, 5%);
      border-color: lighten(@zs-green, 5%);
    }
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .publish-container {
    padding: 100px 16px 20px;
  }

  .form-card {
    padding: 24px 16px;
  }

  .page-header .back-btn {
    position: relative;
    display: inline-block;
    margin-bottom: 10px;
  }
}
</style>