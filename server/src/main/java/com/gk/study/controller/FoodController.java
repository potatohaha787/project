package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Food;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    FoodService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Food> list = service.getFoodList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String id){
        Food food = service.getFoodDetail(id);
        if(food == null){
            return new APIResponse(ResponeCode.FAIL, "未找到该美食项目", null);
        }
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", food);
    }

    // 🌟 核心修改：支持接收 MultipartFile imageFile 并保存到磁盘
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Food food, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // 确保文件名唯一
            String newFileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            // 保存到当前项目 server 目录下的 upload/image/ 文件夹中
            File dest = new File(System.getProperty("user.dir") + "/upload/image/" + newFileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            imageFile.transferTo(dest);
            // 存入数据库的是图片名称
            food.setImage(newFileName);
        }
        food.setCreateTime(String.valueOf(System.currentTimeMillis()));
        service.save(food);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    // 🌟 核心修改：支持更新时的图片替换
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Food food, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String newFileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            File dest = new File(System.getProperty("user.dir") + "/upload/image/" + newFileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            imageFile.transferTo(dest);
            food.setImage(newFileName);
        }
        service.updateById(food);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.removeById(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}