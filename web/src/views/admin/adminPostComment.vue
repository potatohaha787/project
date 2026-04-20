<template>
  <div class="admin-post-comment">
    <div class="search-wrap">
      <a-input v-model:value="keyword" placeholder="搜索评论内容" style="width: 250px; margin-right: 16px;"
        @keyup.enter="fetchData" />
      <a-button type="primary" @click="fetchData">查询</a-button>
    </div>

    <a-table :dataSource="dataList" :columns="columns" :loading="loading" rowKey="id" :pagination="{ pageSize: 10 }">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-popconfirm title="确认删除该条评论吗？" ok-text="确认" cancel-text="取消" @confirm="handleDelete(record)">
            <a style="color: #ff4d4f;">删除</a>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
// 替换为你实际存放 API 的路径
import { getAdminPostCommentListApi, deletePostCommentApi } from '/@/api/postComment'

const keyword = ref('')
const loading = ref(false)
const dataList = ref([])

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '评论内容', dataIndex: 'content', key: 'content', ellipsis: true },
  { title: '所属游记', dataIndex: 'title', key: 'title', ellipsis: true },
  { title: '发布用户', dataIndex: 'username', key: 'username', width: 120 },
  { title: '获赞数', dataIndex: 'likeCount', key: 'likeCount', width: 100 },
  { title: '发布时间', dataIndex: 'commentTime', key: 'commentTime', width: 180 },
  { title: '操作', key: 'action', width: 100, align: 'center' }
]

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAdminPostCommentListApi({ keyword: keyword.value })
    if (res.code === 200) {
      dataList.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleDelete = async (record) => {
  try {
    const formData = new URLSearchParams()
    formData.append('id', record.id)
    const res = await deletePostCommentApi(formData)
    if (res.code === 200) {
      message.success('删除成功')
      fetchData()
    }
  } catch (error) {
    message.error('删除失败')
  }
}
</script>

<style scoped lang="less">
.admin-post-comment {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 100px);

  .search-wrap {
    margin-bottom: 24px;
    display: flex;
    align-items: center;
  }
}
</style>