import { get, post } from '/@/utils/http/axios';

enum URL {
    list = '/api/comment/list',
    create = '/api/comment/create',
    delete = '/api/comment/delete',
    listThingComments = '/api/comment/listThingComments',
    listUserComments = '/api/comment/listUserComments',
    like = '/api/comment/like'
}

const listApi = async (params: any) => get<any>({ url: URL.list, params: params });

// ✅ 去掉了干扰的 headers，让浏览器自己处理 FormData
const createApi = async (data: any) => post<any>({
    url: URL.create,
    data: data
});

const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params });

const listThingCommentsApi = async (params: any) => get<any>({ url: URL.listThingComments, params: params });

const listUserCommentsApi = async (params: any) => get<any>({ url: URL.listUserComments, params: params });

const likeApi = async (params: any) => post<any>({ url: URL.like, params: params });

export { listApi, createApi, deleteApi, listThingCommentsApi, listUserCommentsApi, likeApi };