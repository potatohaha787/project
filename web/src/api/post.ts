import { get, post } from '/@/utils/http/axios';

export const getPostListApi = (params?: any) =>
  get({ url: '/api/post/list', params });

export const getPostDetailApi = (params: { id: string | number }) =>
  get({ url: '/api/post/detail', params });

export const createPostApi = (data: any) =>
  post({ url: '/api/post/create', data });

export const updatePostApi = (data: any) =>
  post({ url: '/api/post/update', data });

export const deletePostApi = (params: { id: string | number }) =>
  get({ url: '/api/post/delete', params });