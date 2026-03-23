import { get } from '/@/utils/http/axios';

// 规范路径，加上 /api
enum URL {
  list = '/api/heritage/list',
  // 【新增详情路径】
  detail = '/api/heritage/detail',
}

const listApi = async (params?: any) => get<any>({ url: URL.list, params: params, data: {}, headers: {} });

// 【新增详情 API 函数】
const detailApi = async (params?: any) => get<any>({ url: URL.detail, params: params, data: {}, headers: {} });

export { listApi, detailApi };