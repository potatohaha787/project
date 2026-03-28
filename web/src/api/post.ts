import { get, post } from '/@/utils/http/axios';


export const getPostListApi = (params?: any) =>
  get({ url: '/api/post/list', params });

export const getPostDetailApi = (params: { id: string | number }) =>
  get({ url: '/api/post/detail', params });

export const createPostApi = (data: any) =>
  post({
    url: '/api/post/create',
    data,
    headers: { 'Content-Type': 'multipart/form-data;charset=UTF-8' }
  });
export const updatePostApi = (data: any) =>
  post({
    url: '/post/update',
    data,
    headers: { 'Content-Type': 'multipart/form-data;charset=UTF-8' }
  });

// 5. 删除帖子/游记 (改为 get 请求，与后端对应)
export const deletePostApi = (params: { id: string | number }) =>
  get({ url: '/post/delete', params });