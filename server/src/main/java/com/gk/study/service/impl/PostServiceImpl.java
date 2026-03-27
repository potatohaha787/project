package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Post;
import com.gk.study.mapper.PostMapper;
import com.gk.study.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Override
    public List<Post> getPostList(String type, String keyword) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        // 状态为1代表正常发布的
        queryWrapper.eq("status", "1");

        // 如果传了类型 (如 guide, ask)，则按类型筛选
        if (!StringUtils.isEmpty(type) && !"all".equals(type)) {
            queryWrapper.eq("type", type);
        }

        // 如果有关键字，进行标题或内容的模糊搜索
        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.and(wrapper ->
                    wrapper.like("title", keyword).or().like("content", keyword)
            );
        }

        // 按照时间倒序排列
        queryWrapper.orderByDesc("create_time");

        return this.list(queryWrapper);
    }

    @Override
    public void createPost(Post post) {
        post.setCreateTime(String.valueOf(System.currentTimeMillis()));
        post.setPv(0);
        post.setLikeCount(0);
        post.setCollectCount(0);
        if (StringUtils.isEmpty(post.getStatus())) {
            post.setStatus("1"); // 默认发布
        }
        this.save(post);
    }

    @Override
    public void updatePost(Post post) {
        this.updateById(post);
    }

    @Override
    public void deletePost(Long id) {
        this.removeById(id);
    }

    @Override
    public void addPv(Long id) {
        Post post = this.getById(id);
        if (post != null) {
            post.setPv((post.getPv() == null ? 0 : post.getPv()) + 1);
            this.updateById(post);
        }
    }
}