import { get, post } from '/@/utils/http/axios';

enum URL {
  list = '/api/celebrity/list',
  create = '/api/celebrity/create',
  update = '/api/celebrity/update',
  delete = '/api/celebrity/delete',
}

export const listApi = async (params: any) => get<any>({ url: URL.list, params });

export const createApi = async (data: any) =>
  post<any>({ url: URL.create, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

export const updateApi = async (data: any) =>
  post<any>({ url: URL.update, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

export const deleteApi = async (params: any) => post<any>({ url: URL.delete, params });