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
        :scroll="{ x: 'max-content' }" :row-selection="rowSelection" :pagination="{
          size: 'default',
          current: data.page,
          pageSize: data.pageSize,
          onChange: (current) => (data.page = current),
          showSizeChanger: false,
          showTotal: (total) => `共${total}条数据`,
        }">
        <template #bodyCell="{ text, record, index, column }">
          <template v-if="column.key === 'imageUrl'">
            <img :src="text" style="width: 80px;height: 50px;object-fit: cover;" />
          </template>
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a href="#">删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>
    </div>

    <div>
      <a-modal :visible="modal.visile" :forceRender="true" :title="modal.title" ok-text="确认" cancel-text="取消"
        @cancel="handleCancel" @ok="handleOk">
        <div>
          <a-form ref="myform" :label-col="{ style: { width: '80px' } }" :model="modal.form" :rules="modal.rules">
            <a-row :gutter="24">
              <a-col span="24">
                <a-form-item label="事件图片">
                  <a-upload-dragger name="file" accept="image/*" :multiple="false" :before-upload="beforeUpload">
                    <p class="ant-upload-text">
                      请选择要上传的事件图片
                    </p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="年份" name="year">
                  <a-input placeholder="例：1152年" v-model:value="modal.form.year" />
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="事件标题" name="title">
                  <a-input placeholder="请输入事件标题" v-model:value="modal.form.title" />
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="事件简介" name="brief">
                  <a-textarea placeholder="请输入简短介绍" v-model:value="modal.form.brief" :rows="2" />
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="详细内容" name="detail">
                  <a-textarea placeholder='请输入JSON数组格式，如：["第一段", "第二段"]' v-model:value="modal.form.detail" :rows="4" />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
      </a-modal>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { FormInstance, message } from 'ant-design-vue';
import { createApi, listApi, updateApi, deleteApi } from '/@/api/history';
import { BASE_URL } from "/@/store/constants";

const columns = reactive([
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    align: 'center' as const
  },
  {
    title: '年份',
    dataIndex: 'year',
    key: 'year',
    align: 'center' as const
  },
  {
    title: '事件标题',
    dataIndex: 'title',
    key: 'title',
    align: 'center' as const
  },
  {
    title: '图片',
    dataIndex: 'imageUrl',
    key: 'imageUrl',
    align: 'center',
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'operation',
    align: 'center',
    fixed: 'right',
    width: 140,
  },
]);

const beforeUpload = (file: File) => {
  // 改文件名
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  console.log(copyFile);
  modal.form.imageFile = copyFile;
  return false; // 阻止默认上传，由后端统一处理
};

// 页面数据
const data = reactive({
  list: [],
  loading: false,
  keyword: '',
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
});

// 弹窗数据源
const modal = reactive({
  visile: false,
  editFlag: false,
  title: '',
  form: {
    id: undefined,
    year: undefined,
    title: undefined,
    brief: undefined,
    detail: undefined,
    image: undefined,
    imageFile: undefined as File | undefined,
    imageUrl: undefined,
  },
  rules: {
    year: [{ required: true, message: '请输入年份', trigger: 'blur' }],
    title: [{ required: true, message: '请输入事件标题', trigger: 'blur' }],
  },
});

const myform = ref<FormInstance>();

onMounted(() => {
  getList();
});

const getList = () => {
  data.loading = true;
  listApi({
    keyword: data.keyword,
  })
    .then((res) => {
      data.loading = false;
      console.log(res);
      res.data.forEach((item: any, index: any) => {
        item.index = index + 1;
        if (item.image) {
          // 这里如果是网络图片(http开头)就直接用，否则拼接后台地址
          if (item.image.startsWith('http')) {
            item.imageUrl = item.image;
          } else {
            item.imageUrl = BASE_URL + '/api/staticfiles/image/' + item.image;
          }
        }
      });
      data.list = res.data;
    })
    .catch((err) => {
      data.loading = false;
      console.log(err);
    });
};

const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: any[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增';
  // 重置
  for (const key in modal.form) {
    modal.form[key as keyof typeof modal.form] = undefined;
  }
};

const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑';
  // 重置
  for (const key in modal.form) {
    modal.form[key as keyof typeof modal.form] = undefined;
  }
  for (const key in record) {
    if (key in modal.form) {
      modal.form[key as keyof typeof modal.form] = record[key];
    }
  }
};

const confirmDelete = (record: any) => {
  console.log('delete', record);
  deleteApi({ ids: record.id })
    .then((res) => {
      getList();
    })
    .catch((err) => {
      message.error(err.msg || '操作失败');
    });
};

const handleBatchDelete = () => {
  console.log(data.selectedRowKeys);
  if (data.selectedRowKeys.length <= 0) {
    message.warn('请勾选删除项');
    return;
  }
  deleteApi({ ids: data.selectedRowKeys.join(',') })
    .then((res) => {
      message.success('删除成功');
      data.selectedRowKeys = [];
      getList();
    })
    .catch((err) => {
      message.error(err.msg || '操作失败');
    });
};

const handleOk = () => {
  myform.value?.validate()
    .then(() => {
      const formData = new FormData();
      if (modal.form.id) {
        formData.append('id', modal.form.id.toString());
      }
      if (modal.form.year) {
        formData.append('year', modal.form.year);
      }
      if (modal.form.title) {
        formData.append('title', modal.form.title);
      }
      if (modal.form.brief) {
        formData.append('brief', modal.form.brief);
      }
      if (modal.form.detail) {
        formData.append('detail', modal.form.detail);
      }
      if (modal.form.imageFile) {
        formData.append('imageFile', modal.form.imageFile);
      }

      if (modal.editFlag) {
        updateApi(formData)
          .then((res) => {
            hideModal();
            getList();
          })
          .catch((err) => {
            console.log(err);
            message.error(err.msg || '操作失败');
          });
      } else {
        createApi(formData)
          .then((res) => {
            hideModal();
            getList();
          })
          .catch((err) => {
            console.log(err);
            message.error(err.msg || '操作失败');
          });
      }
    })
    .catch((err) => {
      console.log('校验失败');
    });
};

const handleCancel = () => {
  hideModal();
};

// 恢复表单初始状态
const resetModal = () => {
  myform.value?.resetFields();
};

// 关闭弹窗
const hideModal = () => {
  modal.visile = false;
};
</script>

<style scoped lang="less">
.page-view {
  min-height: 100%;
  background: #fff;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.table-operations {
  margin-bottom: 16px;
  text-align: right;
}

.table-operations>button {
  margin-right: 8px;
}
</style>