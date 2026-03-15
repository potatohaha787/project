package com.gk.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.History;
import com.gk.study.mapper.HistoryMapper;
import com.gk.study.service.HistoryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History> implements HistoryService {

    @Override
    public List<History> getHistoryList() {
        return this.list();
    }

    @Override
    public void createHistory(History history) {
        saveImageFile(history); // 存入前先处理图片
        this.save(history);
    }

    @Override
    public void deleteHistory(String id) {
        this.removeById(id);
    }

    @Override
    public void updateHistory(History history) {
        saveImageFile(history); // 更新前处理图片
        this.updateById(history);
    }


    // 🌟 核心：处理前端传来的图片文件并保存到本地目录
    private void saveImageFile(History history) {
        MultipartFile file = history.getImageFile();
        if (file != null && !file.isEmpty()) {
            try {
                // 获取文件原本的后缀名 (如 .jpg, .png)
                String originalFilename = file.getOriginalFilename();
                String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

                // 为了防止文件名重复，生成一个随机的 UUID 作为新文件名
                String newFileName = UUID.randomUUID().toString() + suffix;

                // 获取当前项目目录下的 upload/image/ 路径
                String path = System.getProperty("user.dir") + "/upload/image/" + newFileName;
                File dest = new File(path);

                // 如果文件夹不存在，自动创建
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }

                // 将文件真正写入硬盘
                file.transferTo(dest);

                // 将新生成的文件名赋值给 image 字段，这样就能存入数据库了
                history.setImage(newFileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}