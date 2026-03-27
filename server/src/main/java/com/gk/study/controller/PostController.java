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

@CrossOrigin // 允许跨域
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    private final String UPLOAD_PATH = System.getProperty("user.dir") + "/upload/image/";

    /**
     * 获取帖子/游记列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(@RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            List<Post> list = postService.getPostList(type, keyword);
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
        } catch (Exception e) {
            // 🌟 如果报错了，打印在控制台，并返回 200 JSON 错误，防止被拦截为 CORS
            e.printStackTrace();
            return new APIResponse(ResponeCode.FAIL, "后端代码报错了: " + e.getMessage());
        }
    }

    /**
     * 获取帖子详情
     */
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

    /**
     * 创建帖子/游记
     */
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

    // ... saveFile、update、delete 暂略 ...

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