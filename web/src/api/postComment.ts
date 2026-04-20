import { get, post } from '/@/utils/http/axios';

export const getAdminPostCommentListApi = async (params: any) => get({ url: '/api/postComment/adminList', params: params });
export const deletePostCommentApi = async (params: any) => post({ url: '/api/postComment/delete', params: params });