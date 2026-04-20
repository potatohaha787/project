<template>
  <div class="content-list">
    <div class="list-title">个人设置</div>
    <a-spin :spinning="loading" style="min-height: 200px;">
      <div class="list-content">
        <div class="edit-view">

          <div class="item flex-view">
            <div class="label">头像</div>
            <div class="right-box avatar-box flex-view">
              <div class="avatar-wrapper">
                <img v-if="tData.form && tData.form.avatar" :src="tData.form.avatar" class="avatar">
                <img v-else :src="AvatarIcon" class="avatar">
              </div>
              <div class="change-tips flex-view">
                <a-upload name="file" accept="image/*" :multiple="false" :before-upload="beforeUpload"
                  :showUploadList="false">
                  <label class="upload-btn">更换头像</label>
                </a-upload>
                <p class="tip">支持 GIF、PNG、JPEG 格式，建议尺寸 200x200 PX，小于 4 MB</p>
              </div>
            </div>
          </div>

          <div class="item flex-view">
            <div class="label">昵称</div>
            <div class="right-box">
              <input type="text" v-model="tData.form.nickname" placeholder="请输入您的昵称" maxlength="20" class="input-dom">
              <p class="tip">支持中英文，长度不能超过 20 个字符</p>
            </div>
          </div>

          <div class="item flex-view">
            <div class="label">手机号</div>
            <div class="right-box">
              <input type="text" v-model="tData.form.mobile" placeholder="请输入您的手机号" maxlength="11" class="input-dom">
            </div>
          </div>

          <div class="item flex-view">
            <div class="label">邮箱</div>
            <div class="right-box">
              <input type="text" v-model="tData.form.email" placeholder="请输入您的常用邮箱" maxlength="100" class="input-dom">
            </div>
          </div>

          <div class="item flex-view">
            <div class="label">个人简介</div>
            <div class="right-box">
              <textarea v-model="tData.form.description" placeholder="向大家介绍一下你自己，分享你的旅行态度..." maxlength="200"
                class="intro"></textarea>
              <p class="tip">还能输入 {{ 200 - (tData.form.description?.length || 0) }} 个字</p>
            </div>
          </div>

          <div class="action-row">
            <button class="save-btn" @click="submit()">保存修改</button>
          </div>

        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { message } from "ant-design-vue";
import { detailApi, updateUserInfoApi } from '/@/api/user'
import { BASE_URL } from "/@/store/constants";
import { useUserStore } from "/@/store";
import AvatarIcon from '/@/assets/images/avatar.jpg'

const router = useRouter();
const userStore = useUserStore();

let loading = ref(false)
let tData = reactive({
  form: {
    avatar: undefined,
    avatarFile: undefined,
    nickname: undefined,
    email: undefined,
    mobile: undefined,
    description: undefined,
  }
})

onMounted(() => {
  getUserInfo()
})

const beforeUpload = (file) => {
  // 改文件名
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6)
  const copyFile = new File([file], fileName)
  console.log(copyFile)
  tData.form.avatarFile = copyFile

  // 本地预览头像
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = (e) => {
    tData.form.avatar = e.target.result
  }

  return false
}

const getUserInfo = () => {
  loading.value = true
  let userId = userStore.user_id
  detailApi({ userId: userId }).then(res => {
    tData.form = res.data
    if (tData.form.avatar) {
      tData.form.avatar = BASE_URL + '/api/staticfiles/avatar/' + tData.form.avatar
    }
    loading.value = false
  }).catch(err => {
    console.log(err)
    loading.value = false
  })
}

const submit = () => {
  let formData = new FormData()
  let userId = userStore.user_id
  formData.append('id', userId)
  if (tData.form.avatarFile) {
    formData.append('avatarFile', tData.form.avatarFile)
  }
  if (tData.form.nickname) {
    formData.append('nickname', tData.form.nickname)
  }
  if (tData.form.email) {
    formData.append('email', tData.form.email)
  }
  if (tData.form.mobile) {
    formData.append('mobile', tData.form.mobile)
  }
  if (tData.form.description) {
    formData.append('description', tData.form.description)
  }
  updateUserInfoApi(formData).then(res => {
    message.success('保存成功')
    getUserInfo() // 更新右侧自己
    // 发送全局事件，通知头部数据已更新
    window.dispatchEvent(new CustomEvent('userInfoUpdated'))
  }).catch(err => {
    console.log(err)
  })
}
</script>

<style scoped lang="less">
/* --- 主题变量 --- */
@zs-green: #0b6a65;
@zs-green-hover: #09504c;
@text-main: #1e293b;
@text-light: #64748b;
@border-color: #e2e8f0;
@bg-light: #f8fafc;

input,
textarea {
  border-style: none;
  outline: none;
  margin: 0;
  padding: 0;
}

.flex-view {
  display: flex;
}

.content-list {
  flex: 1;
  background: #ffffff;
  border-radius: 16px;
  padding: 32px 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);

  .list-title {
    color: @text-main;
    font-weight: 700;
    font-size: 22px;
    padding-bottom: 20px;
    margin-bottom: 32px;
    border-bottom: 1px solid @border-color;
  }

  .edit-view {
    max-width: 700px;
    /* 限制表单最大宽度，避免太散 */

    .item {
      align-items: flex-start;
      /* 顶部对齐，适应多行文本和头像 */
      margin-bottom: 32px;

      .label {
        width: 100px;
        color: @text-main;
        font-weight: 600;
        font-size: 15px;
        padding-top: 12px;
        /* 与输入框内文字对齐 */
        flex-shrink: 0;
      }

      .right-box {
        flex: 1;
        width: 100%;
      }

      /* 头像区域样式 */
      .avatar-box {
        align-items: center;
        background: @bg-light;
        padding: 20px;
        border-radius: 12px;
        border: 1px dashed @border-color;
      }

      .avatar-wrapper {
        width: 72px;
        height: 72px;
        border-radius: 50%;
        overflow: hidden;
        margin-right: 24px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        flex-shrink: 0;

        .avatar {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .change-tips {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;

        .upload-btn {
          display: inline-block;
          background: rgba(11, 106, 101, 0.08);
          color: @zs-green;
          font-size: 13px;
          font-weight: 600;
          padding: 6px 16px;
          border-radius: 20px;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            background: @zs-green;
            color: #ffffff;
            transform: translateY(-1px);
          }
        }
      }

      /* 提示文字 */
      .tip {
        color: @text-light;
        font-size: 12px;
        margin: 8px 0 0 4px;
      }

      /* 通用输入框样式 */
      .input-dom {
        width: 100%;
        background: @bg-light;
        border: 1px solid @border-color;
        border-radius: 8px;
        height: 46px;
        font-size: 15px;
        color: @text-main;
        padding: 0 16px;
        transition: all 0.3s ease;

        &::placeholder {
          color: #94a3b8;
        }

        &:focus {
          background: #ffffff;
          border-color: @zs-green;
          box-shadow: 0 0 0 3px rgba(11, 106, 101, 0.1);
        }
      }

      /* 文本域特殊样式 */
      .intro {
        width: 100%;
        background: @bg-light;
        border: 1px solid @border-color;
        border-radius: 8px;
        min-height: 120px;
        font-size: 15px;
        color: @text-main;
        padding: 16px;
        resize: vertical;
        line-height: 1.6;
        transition: all 0.3s ease;

        &::placeholder {
          color: #94a3b8;
        }

        &:focus {
          background: #ffffff;
          border-color: @zs-green;
          box-shadow: 0 0 0 3px rgba(11, 106, 101, 0.1);
        }
      }
    }

    /* 按钮区域 */
    .action-row {
      margin-top: 40px;
      margin-left: 100px;
      /* 与 label 对齐 */

      .save-btn {
        background: @zs-green;
        color: #ffffff;
        font-size: 15px;
        font-weight: 600;
        height: 44px;
        padding: 0 40px;
        border-radius: 22px;
        /* 药丸形状 */
        border: none;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 4px 12px rgba(11, 106, 101, 0.2);

        &:hover {
          background: @zs-green-hover;
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(11, 106, 101, 0.3);
        }

        &:active {
          transform: translateY(0);
        }
      }
    }
  }
}
</style>