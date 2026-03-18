package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Celebrity;
import com.gk.study.service.CelebrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/celebrity")
public class CelebrityController {

    @Autowired
    private CelebrityService celebrityService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Celebrity> list = celebrityService.getCelebrityList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Celebrity celebrity) throws IOException {
        saveImage(celebrity); // 保存图片
        celebrityService.save(celebrity);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Celebrity celebrity) throws IOException {
        saveImage(celebrity); // 保存图片
        celebrityService.updateById(celebrity);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        celebrityService.removeByIds(Arrays.asList(ids.split(",")));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    private void saveImage(Celebrity celebrity) throws IOException {
        MultipartFile file = celebrity.getImageFile();
        if (file != null && !file.isEmpty()) {
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
            // 指定存放目录
            String filePath = System.getProperty("user.dir") + "/upload/image/" + fileName;
            File dest = new File(filePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
            celebrity.setImage(fileName); // 将文件名存入数据库字段
        }
    }
}