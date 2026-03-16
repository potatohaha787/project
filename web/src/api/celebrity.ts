// web/src/api/celebrity.ts
import { get } from '/@/utils/http/axios';

// 获取名人故事列表接口
export const listApi = async (params: any) =>
  get<any>({ url: '/celebrity/list', params });