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
// ------ 把下面这四行追加到 post.ts 文件的最下面 ------

export const likePostApi = (params: any) =>
  post({ url: '/api/post/like', params });

export const cancelLikePostApi = (params: any) =>
  post({ url: '/api/post/cancelLike', params });

export const collectPostApi = (params: any) =>
  post({ url: '/api/post/collect', params });

export const cancelCollectPostApi = (params: any) =>
  post({ url: '/api/post/cancelCollect', params });

