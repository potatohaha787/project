package com.gk.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Post;
import com.gk.study.entity.PostCollect;
import com.gk.study.mapper.PostCollectMapper;
import com.gk.study.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/postCollect")
public class PostCollectController {

    @Autowired
    private PostCollectMapper postCollectMapper;

    @Autowired
    private PostService postService;

    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public APIResponse collect(Long id, Long userId) { // 👈 注意：这里前端传的参数名可能是 id 或 postId
        // 1. 获取参数
        Long targetPostId = id; // 根据你前端的 formData.append('id', postId) 来的

        // 2. 检查是否重复
        QueryWrapper<PostCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", targetPostId).eq("user_id", userId);
        if (postCollectMapper.selectCount(queryWrapper) > 0) {
            return new APIResponse(ResponeCode.FAIL, "您已经收藏过啦");
        }

        // 3. 🚨核心修复：向 b_post_collect 插入数据🚨
        PostCollect pc = new PostCollect();
        pc.setPostId(targetPostId);
        pc.setUserId(userId);
        pc.setCollectTime(String.valueOf(System.currentTimeMillis()));
        postCollectMapper.insert(pc); // 👈 这一行如果不执行，表里就没数据！

        // 4. 更新 b_post 数量
        Post post = postService.getById(targetPostId);
        if (post != null) {
            int currentCount = post.getCollectCount() == null ? 0 : post.getCollectCount();
            post.setCollectCount(currentCount + 1);
            postService.updateById(post);
        }

        return new APIResponse(ResponeCode.SUCCESS, "收藏成功");
    }

    @RequestMapping(value = "/cancelCollect", method = RequestMethod.POST)
    public APIResponse cancelCollect(Long postId, Long userId) {
        // 1. 删除 b_post_collect 中的记录
        QueryWrapper<PostCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).eq("user_id", userId);
        postCollectMapper.delete(queryWrapper);

        // 2. 将 b_post 表的收藏数 - 1
        Post post = postService.getById(postId);
        if (post != null && post.getCollectCount() != null && post.getCollectCount() > 0) {
            post.setCollectCount(post.getCollectCount() - 1);
            postService.updateById(post);
        }

        return new APIResponse(ResponeCode.SUCCESS, "已取消收藏");
    }
}