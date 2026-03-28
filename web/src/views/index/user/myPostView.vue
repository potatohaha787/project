<template>
  <div class="myPostView">
    <div class="title">我的游记与发帖</div>
    <div class="post-list">
      <a-table :dataSource="dataList" :columns="columns" rowKey="id" :loading="loading" :pagination="{ pageSize: 10 }">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'type'">
            <a-tag color="green" v-if="record.type === 'guide'">游记</a-tag>
            <a-tag color="blue" v-else-if="record.type === 'ask'">求助</a-tag>
            <a-tag color="orange" v-else-if="record.type === 'mate'">结伴</a-tag>
            <a-tag color="purple" v-else>分享</a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-button type="link" @click="goToDetail(record.id)">查看</a-button>
            <a-popconfirm title="确定删除这条记录吗？" @confirm="handleDelete(record.id)">
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { getPostListApi, deletePostApi } from '/@/api/post';

// 🌟 1. 引入并使用 userStore 来获取当前登录用户的 ID
import { useUserStore } from '/@/store';
const userStore = useUserStore();

const router = useRouter();
const dataList = ref([]);
const loading = ref(false);

const columns = [
  { title: '类型', dataIndex: 'type', width: 80 },
  { title: '标题', dataIndex: 'title', ellipsis: true },
  { title: '发布时间', dataIndex: 'createTime', width: 150 },
  { title: '阅读量', dataIndex: 'pv', width: 80 },
  { title: '操作', key: 'action', width: 120, align: 'center' }
];

const loadData = async () => {
  loading.value = true;
  try {
    // 🌟 2. 在请求参数中带上当前用户的 userId
    const res = await getPostListApi({
      userId: userStore.user_id
    });

    if (res.code === 200) {
      dataList.value = res.data;
    }
  } catch (error) {
    message.error('加载失败');
  } finally {
    loading.value = false;
  }
};

const goToDetail = (id) => {
  router.push({ path: '/index/ForumDetail', query: { id } });
};

const handleDelete = async (id) => {
  try {
    const res = await deletePostApi({ id });
    if (res.code === 200) {
      message.success('删除成功');
      loadData();
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

<style scoped lang="less">
.myPostView {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  min-height: 500px;

  .title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
}
</style>