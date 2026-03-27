// 文件路径: web/src/api/post.ts
import { get, post } from '/@/utils/http/axios';

// 1. 获取帖子/游记列表 (GET 请求用 params 拼接在 URL 后面)
export const getPostListApi = (params?: any) =>
  get({ url: '/post/list', params });

// 2. 获取帖子详情
export const getPostDetailApi = (params: { id: string | number }) =>
  get({ url: '/post/detail', params });

// 3. 发布帖子/游记 (POST 请求，带有图片的表单内容放入 data 中)
export const createPostApi = (data: any) =>
  post({
    url: '/post/create',
    data, // 注意这里用 data 而不是 params
    headers: { 'Content-Type': 'multipart/form-data;charset=UTF-8' }
  });