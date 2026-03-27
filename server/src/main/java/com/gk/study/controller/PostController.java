package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Post;
import com.gk.study.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    // 模拟文件上传的本地目录 (根据您项目实际配置调整)
    private final String UPLOAD_PATH = System.getProperty("user.dir") + "/upload/image/";

    /**
     * 获取帖子/游记列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(@RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "keyword", required = false) String keyword) {
        List<Post> list = postService.getPostList(type, keyword);
        // TODO: 这里您可以额外注入 UserService，遍历 list 将作者的头像和昵称查出来赋值给 authorName, authorAvatar
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 获取帖子详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(@RequestParam("id") Long id) {
        Post post = postService.getById(id);
        if (post != null) {
            // 每次访问详情，浏览量+1
            postService.addPv(id);
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", post);
        }
        return new APIResponse(ResponeCode.FAIL, "记录不存在");
    }

    /**
     * 创建帖子/游记 (支持表单提交带封面图片)
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Post post) throws IOException {
        // 处理图片上传
        if (post.getImageFile() != null && !post.getImageFile().isEmpty()) {
            String fileName = saveFile(post.getImageFile());
            post.setCover(fileName);
        }

        postService.createPost(post);
        return new APIResponse(ResponeCode.SUCCESS, "发布成功");
    }

    /**
     * 更新帖子/游记
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Post post) throws IOException {
        if (post.getImageFile() != null && !post.getImageFile().isEmpty()) {
            String fileName = saveFile(post.getImageFile());
            post.setCover(fileName);
        }
        postService.updatePost(post);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 删除帖子
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(@RequestParam("id") Long id) {
        postService.deletePost(id);
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 私有方法：保存上传的文件
     */
    private String saveFile(MultipartFile file) throws IOException {
        File dir = new File(UPLOAD_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成唯一文件名，防止覆盖
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + ext;

        File dest = new File(UPLOAD_PATH + newFileName);
        file.transferTo(dest);
        return newFileName;
    }
}