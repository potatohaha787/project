package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.PostComment;
import com.gk.study.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/postComment")
public class PostCommentController {

    @Autowired
    private PostCommentService postCommentService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(PostComment postComment) {
        postCommentService.createPostComment(postComment);
        return new APIResponse(ResponeCode.SUCCESS, "游记评论发表成功");
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String postId) {
        List<PostComment> list = postCommentService.getPostCommentList(postId);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    public APIResponse like(Long id) {
        postCommentService.likePostComment(id);
        return new APIResponse(ResponeCode.SUCCESS, "点赞成功");
    }
}