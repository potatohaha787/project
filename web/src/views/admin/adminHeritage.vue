<template>
  <div>
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">新增</a-button>
          <a-button @click="handleBatchDelete">批量删除</a-button>
        </a-space>
      </div>
      <a-table size="middle" rowKey="id" :loading="data.loading" :columns="columns" :data-source="data.list"
        :row-selection="rowSelection">
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" @confirm="confirmDelete(record)">
                <a href="#">删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>
    </div>

    <a-modal :visible="modal.visile" :title="modal.title" @cancel="handleCancel" @ok="handleOk" width="700px">
      <a-form ref="myform" :label-col="{ style: { width: '80px' } }" :model="modal.form" :rules="modal.rules">
        <a-form-item label="名称" name="title">
          <a-input v-model:value="modal.form.title" placeholder="请输入非遗名称，如：小榄菊花会" />
        </a-form-item>
        <a-form-item label="级别" name="level">
          <a-input v-model:value="modal.form.level" placeholder="如：国家级非物质文化遗产" />
        </a-form-item>
        <a-form-item label="标签" name="tag">
          <a-input v-model:value="modal.form.tag" placeholder="如：国之瑰宝" />
        </a-form-item>

        <a-form-item label="非遗封面">
          <a-upload-dragger name="file" accept="image/*" :multiple="false" :before-upload="beforeUpload"
            v-model:file-list="fileList">
            <p class="ant-upload-drag-icon">
              <template v-if="modal.form.coverUrl">
                <img :src="modal.form.coverUrl"
                  style="width: 80px; height: 80px; object-fit: cover; border-radius: 8px;" />
              </template>
              <template v-else>
                <file-image-outlined style="font-size: 32px; color: #1890ff;" />
              </template>
            </p>
            <p class="ant-upload-text">点击或拖拽图片到此处上传</p>
          </a-upload-dragger>
        </a-form-item>

        <a-form-item label="简介" name="description">
          <a-textarea :rows="2" v-model:value="modal.form.description" placeholder="请输入列表页显示的简介" />
        </a-form-item>
        <a-form-item label="详情正文" name="content">
          <a-textarea :rows="5" v-model:value="modal.form.content" placeholder="支持输入HTML富文本内容" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { FileImageOutlined } from '@ant-design/icons-vue';
import { createApi, listApi, updateApi, deleteApi } from '/@/api/heritage';
import { BASE_URL } from '/@/store/constants';

const columns = [
  { title: '名称', dataIndex: 'title', key: 'title' },
  { title: '级别', dataIndex: 'level', key: 'level' },
  { title: '标签', dataIndex: 'tag', key: 'tag' },
  { title: '简介', dataIndex: 'description', key: 'description', customRender: ({ text }) => text ? text.substring(0, 15) + '...' : '--' },
  { title: '操作', dataIndex: 'action', key: 'operation', width: 150 }
];

const data = reactive({ list: [], loading: false, selectedRowKeys: [] });
const fileList = ref<any[]>([]);
const modal = reactive({
  visile: false,
  editFlag: false,
  title: '',
  form: {} as any,
  rules: { title: [{ required: true, message: '必填' }] }
});
const myform = ref();

onMounted(() => getDataList());

const getDataList = () => {
  data.loading = true;
  listApi().then(res => { data.list = res.data; data.loading = false; });
};

// 上传前的图片处理与预览拦截
const beforeUpload = (file: File) => {
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  modal.form.imageFile = new File([file], fileName);

  const reader = new FileReader();
  reader.onload = (e) => { modal.form.coverUrl = e.target?.result as string; };
  reader.readAsDataURL(file);
  return false; // 拦截默认上传行为
};

const rowSelection = ref({ onChange: (keys) => data.selectedRowKeys = keys });

const handleAdd = () => {
  myform.value?.resetFields();
  fileList.value = [];
  modal.form = {};
  modal.editFlag = false;
  modal.title = '新增非遗';
  modal.visile = true;
};

const handleEdit = (record) => {
  myform.value?.resetFields();
  fileList.value = [];
  modal.form = { ...record };
  // 拼接完整的图片预览地址
  if (record.image) {
    modal.form.coverUrl = BASE_URL + '/api/staticfiles/image/' + record.image;
  }
  modal.editFlag = true;
  modal.title = '编辑非遗';
  modal.visile = true;
};

const handleCancel = () => modal.visile = false;

const handleOk = () => {
  myform.value?.validate().then(() => {
    // 使用 FormData 打包数据和文件发给后端
    const formData = new FormData();
    if (modal.editFlag) formData.append('id', modal.form.id);
    formData.append('title', modal.form.title || '');
    formData.append('level', modal.form.level || '');
    formData.append('tag', modal.form.tag || '');
    formData.append('description', modal.form.description || '');
    formData.append('content', modal.form.content || '');

    // 如果选择了新图片，加入表单
    if (modal.form.imageFile) {
      formData.append('imageFile', modal.form.imageFile);
    }

    const api = modal.editFlag ? updateApi : createApi;
    api(formData).then(() => {
      modal.visile = false;
      getDataList();
      message.success('操作成功');
    }).catch(err => {
      message.error(err.msg || '操作失败');
    });
  });
};

const confirmDelete = (record) => { deleteApi({ ids: record.id }).then(() => getDataList()); };
const handleBatchDelete = () => {
  if (data.selectedRowKeys.length === 0) return message.warn('请勾选');
  deleteApi({ ids: data.selectedRowKeys.join(',') }).then(() => { data.selectedRowKeys = []; getDataList(); });
};
</script>

<style scoped>
.page-view {
  background: #fff;
  padding: 24px;
  min-height: 100%;
}

.table-operations {
  margin-bottom: 16px;
  text-align: right;
}
</style>