package com.gk.study.service;

import com.gk.study.entity.PostComment;
import java.util.List;

public interface PostCommentService {
    void createPostComment(PostComment postComment);
    void likePostComment(Long id);
    List<PostComment> getPostCommentList(String postId);
}