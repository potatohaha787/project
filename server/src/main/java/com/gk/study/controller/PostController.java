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

    private final String UPLOAD_PATH = System.getProperty("user.dir") + "/upload/image/";


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(@RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "userId", required = false) Long userId) { // 🌟 新增 userId 参数
        try {
            // 🌟 传入 userId 给 Service 层
            List<Post> list = postService.getPostList(type, keyword, userId);
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return new APIResponse(ResponeCode.FAIL, "后端代码报错了: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(@RequestParam("id") Long id) {
        try {
            Post post = postService.getById(id);
            if (post != null) {
                postService.addPv(id);
                return new APIResponse(ResponeCode.SUCCESS, "查询成功", post);
            }
            return new APIResponse(ResponeCode.FAIL, "记录不存在");
        } catch (Exception e) {
            e.printStackTrace();
            return new APIResponse(ResponeCode.FAIL, "详情接口报错: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Post post) {
        try {
            if (post.getImageFile() != null && !post.getImageFile().isEmpty()) {
                String fileName = saveFile(post.getImageFile());
                post.setCover(fileName);
            }
            postService.createPost(post);
            return new APIResponse(ResponeCode.SUCCESS, "发布成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new APIResponse(ResponeCode.FAIL, "发布报错: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Post post) {
        try {
            // 如果上传了新图片，则替换封面
            if (post.getImageFile() != null && !post.getImageFile().isEmpty()) {
                String fileName = saveFile(post.getImageFile());
                post.setCover(fileName);
            }
            postService.updateById(post); // 注意：取决于你 Service 的写法，如果是 MyBatis-Plus 通常是 updateById
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new APIResponse(ResponeCode.FAIL, "更新报错: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public APIResponse delete(@RequestParam("id") Long id) {
        try {
            postService.deletePost(id); // 使用你在 PostServiceImpl 中实现的方法
            return new APIResponse(ResponeCode.SUCCESS, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new APIResponse(ResponeCode.FAIL, "删除报错: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        File dir = new File(UPLOAD_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + ext;
        File dest = new File(UPLOAD_PATH + newFileName);
        file.transferTo(dest);
        return newFileName;
    }
}