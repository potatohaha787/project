<template>
  <div>
    <div style="margin-bottom: 16px; display: flex; gap: 16px;">
      <a-input v-model:value="keyword" placeholder="搜索标题或内容" style="width: 200px" @pressEnter="loadData" />
      <a-button type="primary" @click="loadData">搜索</a-button>
    </div>

    <a-table :dataSource="dataList" :columns="columns" rowKey="id" :pagination="pagination" @change="handleTableChange"
      :loading="loading">

      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'cover'">
          <img v-if="record.cover" :src="BASE_URL + '/api/staticfiles/image/' + record.cover"
            style="width: 60px; height: 40px; object-fit: cover; border-radius: 4px;" />
          <span v-else>无图</span>
        </template>

        <template v-if="column.dataIndex === 'type'">
          <a-tag color="green" v-if="record.type === 'guide'">游记</a-tag>
          <a-tag color="blue" v-else-if="record.type === 'ask'">求助</a-tag>
          <a-tag color="orange" v-else-if="record.type === 'mate'">结伴</a-tag>
          <a-tag color="purple" v-else>分享</a-tag>
        </template>

        <template v-if="column.key === 'action'">
          <a-popconfirm title="确定要删除这条记录吗？" @confirm="handleDelete(record.id)">
            <a-button type="link" danger>删除</a-button>
          </a-popconfirm>
        </template>
      </template>

    </a-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { getPostListApi, deletePostApi } from '/@/api/post';
import { BASE_URL } from '/@/store/constants';

const keyword = ref('');
const dataList = ref([]);
const loading = ref(false);

// 分页配置
const pagination = ref({ current: 1, pageSize: 10, total: 0 });

// 表格列定义
const columns = [
  { title: 'ID', dataIndex: 'id', width: 80 },
  { title: '封面', dataIndex: 'cover', width: 100 },
  { title: '类型', dataIndex: 'type', width: 100 },
  { title: '标题', dataIndex: 'title', ellipsis: true },
  { title: '作者', dataIndex: 'authorName', width: 120 },
  { title: '地点', dataIndex: 'location', width: 120 },
  { title: '阅读量', dataIndex: 'pv', width: 100 },
  { title: '操作', key: 'action', width: 120, align: 'center' }
];

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const res = await getPostListApi({ keyword: keyword.value });
    if (res.code === 200) {
      dataList.value = res.data;
      pagination.value.total = res.data.length;
    } else {
      message.error(res.msg);
    }
  } catch (error) {
    message.error('加载失败');
  } finally {
    loading.value = false;
  }
};

// 分页变化
const handleTableChange = (pag) => {
  pagination.value.current = pag.current;
};

// 删除操作
const handleDelete = async (id) => {
  try {
    const res = await deletePostApi({ id: id });
    if (res.code === 200) {
      message.success('删除成功');
      loadData(); // 重新加载表格
    } else {
      message.error(res.msg);
    }
  } catch (error) {
    message.error('删除失败');
  }
};

onMounted(() => {
  loadData();
});
</script>