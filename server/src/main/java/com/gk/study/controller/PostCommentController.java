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

    // 👇 新增接口
    @RequestMapping(value = "/userList", method = RequestMethod.GET)
    public APIResponse userList(String userId) {
        List<PostComment> list = postCommentService.getUserPostCommentList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 👇 新增：获取“我的消息”列表中的文章评论
    @RequestMapping(value = "/myMessages", method = RequestMethod.GET)
    public APIResponse myMessages(String userId) {
        List<PostComment> list = postCommentService.getCommentsOnMyPosts(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 👇 新增：后台获取游记评论列表
    @RequestMapping(value = "/adminList", method = RequestMethod.GET)
    public APIResponse adminList(String keyword) {
        List<PostComment> list = postCommentService.getAdminPostCommentList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 👇 新增：后台删除游记评论
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(Long id) {
        postCommentService.deletePostComment(id);
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}