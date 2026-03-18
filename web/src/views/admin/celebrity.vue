<template>
  <div class="admin-celebrity">
    <div class="operation-bar">
      <a-button type="primary" @click="handleAdd">新增人物</a-button>
    </div>

    <a-table :dataSource="dataList" :columns="columns" rowKey="id" bordered>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'image'">
          <img :src="getImageUrl(record.image)" alt="avatar"
            style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px;" />
        </template>
        <template v-else-if="column.key === 'operation'">
          <span>
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除该人物吗?" ok-text="是" cancel-text="否" @confirm="handleDelete(record.id)">
              <a href="#">删除</a>
            </a-popconfirm>
          </span>
        </template>
      </template>
    </a-table>

    <a-modal v-model:visible="modalVisible" :title="isEdit ? '编辑人物信息' : '新增人物'" @ok="handleOk" okText="保存"
      cancelText="取消" width="700px">
      <a-form layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="姓名/标题" required>
              <a-input v-model:value="formData.title" placeholder="例如: 孙中山" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="人物身份/标签">
              <a-input v-model:value="formData.tags" placeholder="例如: 中国民主革命的伟大先驱" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="出生日期">
              <a-input v-model:value="formData.birthDate" placeholder="例如: 1866年11月12日" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="逝世日期">
              <a-input v-model:value="formData.deathDate" placeholder="例如: 1925年3月12日" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="出生地">
          <a-input v-model:value="formData.birthplace" placeholder="例如: 广东省香山县翠亨村" />
        </a-form-item>

        <a-form-item label="主要成就">
          <a-textarea v-model:value="formData.achievements" :rows="2" placeholder="例如: 领导辛亥革命，推翻封建帝制" />
        </a-form-item>
        <a-form-item label="人物图片">
          <a-upload-dragger name="file" accept="image/*" :multiple="false" :before-upload="beforeUpload">
            <p class="ant-upload-drag-icon">
              <template v-if="formData.imageUrl">
                <img :src="formData.imageUrl" style="max-width: 100%; max-height: 120px; object-fit: cover;" />
              </template>
              <template v-else>
                <div style="font-size: 32px; color: #1890ff;">+</div>
              </template>
            </p>
            <p class="ant-upload-text">点击或拖拽上传图片</p>
          </a-upload-dragger>
        </a-form-item>

        <a-form-item label="简短描述 (Brief)">
          <a-textarea v-model:value="formData.brief" :rows="3" placeholder="请输入前台卡片显示的简短介绍" />
        </a-form-item>

        <a-form-item label="详细生平 (Detail)">
          <a-textarea v-model:value="formData.detail" :rows="5" placeholder="请输入详情页展示的完整文字" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { listApi, createApi, updateApi, deleteApi } from '/@/api/celebrity';
import { BASE_URL } from '/@/store/constants';

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '图片', dataIndex: 'image', key: 'image', width: 80, align: 'center' },
  { title: '姓名', dataIndex: 'title', key: 'title', width: 120 },
  { title: '标签', dataIndex: 'tags', key: 'tags', width: 180 },
  { title: '出生地', dataIndex: 'birthplace', key: 'birthplace', width: 150 }, // 表格也展示一下出生地
  { title: '简介', dataIndex: 'brief', key: 'brief', ellipsis: true },
  { title: '操作', dataIndex: 'action', key: 'operation', width: 140, align: 'center', fixed: 'right' }
];

const dataList = ref([]);
const modalVisible = ref(false);
const isEdit = ref(false);

const formData = reactive({
  id: undefined,
  title: '',
  brief: '',
  tags: '',
  detail: '',
  birthplace: '',   // 新增
  birthDate: '',    // 新增
  deathDate: '',    // 新增
  achievements: '', // 新增
  imageFile: null as File | null,
  imageUrl: ''
});

const loadData = () => {
  listApi({}).then((res: any) => {
    if (res.code === 200) dataList.value = res.data;
  });
};

onMounted(() => {
  loadData();
});

const getImageUrl = (image: string) => {
  if (image && !image.startsWith('http')) {
    return BASE_URL + '/api/staticfiles/image/' + image;
  }
  return image || '';
};

const beforeUpload = (file: File) => {
  formData.imageFile = file;
  formData.imageUrl = URL.createObjectURL(file);
  return false;
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: undefined, title: '', brief: '', tags: '', detail: '',
    birthplace: '', birthDate: '', deathDate: '', achievements: '', // 重置新增字段
    imageFile: null, imageUrl: ''
  });
  modalVisible.value = true;
};

const handleEdit = (record: any) => {
  isEdit.value = true;
  Object.assign(formData, record);
  formData.imageFile = null;
  formData.imageUrl = record.image ? getImageUrl(record.image) : '';
  modalVisible.value = true;
};

const handleDelete = (id: number) => {
  deleteApi({ ids: id }).then((res: any) => {
    if (res.code === 200) { message.success('删除成功'); loadData(); }
  });
};

const handleOk = () => {
  if (!formData.title) return message.warning('请输入人物姓名');

  const submitData = new FormData();
  if (formData.id) submitData.append('id', String(formData.id));
  if (formData.title) submitData.append('title', formData.title);
  if (formData.brief) submitData.append('brief', formData.brief);
  if (formData.tags) submitData.append('tags', formData.tags);
  if (formData.detail) submitData.append('detail', formData.detail);

  // 👇 将新增的四个字段附带提交给后端
  if (formData.birthplace) submitData.append('birthplace', formData.birthplace);
  if (formData.birthDate) submitData.append('birthDate', formData.birthDate);
  if (formData.deathDate) submitData.append('deathDate', formData.deathDate);
  if (formData.achievements) submitData.append('achievements', formData.achievements);

  if (formData.imageFile) submitData.append('imageFile', formData.imageFile);

  const targetApi = isEdit.value ? updateApi : createApi;
  targetApi(submitData).then((res: any) => {
    if (res.code === 200) {
      message.success(isEdit.value ? '更新成功' : '新增成功');
      modalVisible.value = false;
      loadData();
    } else {
      message.error(res.msg || '操作失败');
    }
  });
};
</script>

<style scoped lang="less">
.admin-celebrity {
  background: #fff;
  padding: 24px;
  border-radius: 4px;
  height: 100%;
}

.operation-bar {
  margin-bottom: 16px;
}
</style>