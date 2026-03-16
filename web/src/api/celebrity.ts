// web/src/api/celebrity.ts
import { get } from '/@/utils/http/axios';

enum URL {
  list = '/api/celebrity/list',
  detail = '/api/celebrity/detail',
}
// 获取名人故事列表接口
export const listApi = async (params: any) =>
  get<any>({ url: URL.list, params });

// 获取名人详情接口
export const detailApi = async (params: any) =>
  get<any>({ url: URL.detail, params });
