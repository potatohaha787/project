import { get, post } from '/@/utils/http/axios';

enum URL {
  list = '/api/celebrity/list',
  create = '/api/celebrity/create',
  update = '/api/celebrity/update',
  delete = '/api/celebrity/delete',
}

// 获取名人故事列表接口
export const listApi = async (params: any) => get<any>({ url: URL.list, params });
// 新增名人接口
export const createApi = async (data: any) => post<any>({ url: URL.create, data });
// 更新名人接口
export const updateApi = async (data: any) => post<any>({ url: URL.update, data });
// 删除名人接口
export const deleteApi = async (params: any) => post<any>({ url: URL.delete, params });