package com.gk.study.service.impl;

import com.gk.study.entity.PostComment;
import com.gk.study.mapper.PostCommentMapper;
import com.gk.study.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PostCommentServiceImpl implements PostCommentService {

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Override
    public void createPostComment(PostComment postComment) {
        // 后台自动注入当前时间和初始点赞数为0
        postComment.setCommentTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        postComment.setLikeCount(0);
        postCommentMapper.insert(postComment);
    }

    @Override
    public List<PostComment> getPostCommentList(String postId) {
        return postCommentMapper.listPostComments(postId);
    }
    @Override
    public void likePostComment(Long id) {
        postCommentMapper.incrementLikeCount(id);
    }
}