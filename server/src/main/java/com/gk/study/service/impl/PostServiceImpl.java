package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Post;
import com.gk.study.entity.User; // 引入 User 实体
import com.gk.study.mapper.PostMapper;
import com.gk.study.service.PostService;
import com.gk.study.service.UserService; // 引入 UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    // 注入 UserService 来获取用户信息
    @Autowired
    private UserService userService;

    @Override
    public List<Post> getPostList(String type, String keyword, Long userId) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "1"); // 只查正常发布的帖子

        if (!StringUtils.isEmpty(type) && !"all".equals(type)) {
            queryWrapper.eq("type", type);
        }

        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.and(wrapper ->
                    wrapper.like("title", keyword).or().like("content", keyword)
            );
        }

        // 🌟 新增：如果传入了 userId，则只查询该用户的游记/帖子
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }

        queryWrapper.orderByDesc("create_time");
        List<Post> postList = this.list(queryWrapper);

        // 核心：遍历帖子，附加上真实用户的昵称和头像
        for (Post post : postList) {
            if (post.getUserId() != null) {
                User user = userService.getUserDetail(String.valueOf(post.getUserId()));

                if (user != null) {
                    // 如果有昵称显示昵称，没有则显示用户名
                    post.setAuthorName(StringUtils.isEmpty(user.getNickname()) ? user.getUsername() : user.getNickname());
                    post.setAuthorAvatar(user.getAvatar());
                }
            }
        }

        return postList;
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

    @Override
    public void likePost(Long id) {
        Post post = this.getById(id);
        if (post != null) {
            post.setLikeCount((post.getLikeCount() == null ? 0 : post.getLikeCount()) + 1);
            this.updateById(post);
        }
    }

    @Override
    public void cancelLikePost(Long id) {
        Post post = this.getById(id);
        if (post != null && post.getLikeCount() != null && post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            this.updateById(post);
        }
    }

    @Override
    public void collectPost(Long id) {
        Post post = this.getById(id);
        if (post != null) {
            post.setCollectCount((post.getCollectCount() == null ? 0 : post.getCollectCount()) + 1);
            this.updateById(post);
        }
    }

    @Override
    public void cancelCollectPost(Long id) {
        Post post = this.getById(id);
        if (post != null && post.getCollectCount() != null && post.getCollectCount() > 0) {
            post.setCollectCount(post.getCollectCount() - 1);
            this.updateById(post);
        }
    }
}