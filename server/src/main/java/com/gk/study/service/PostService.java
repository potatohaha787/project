package com.gk.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gk.study.entity.Post;

import java.util.List;

public interface PostService extends IService<Post> {

    // 获取帖子列表 (支持根据类型、关键字搜索)
    List<Post> getPostList(String type, String keyword);

    // 创建帖子
    void createPost(Post post);

    // 更新帖子
    void updatePost(Post post);

    // 删除帖子
    void deletePost(Long id);

    // 增加浏览量
    void addPv(Long id);
}