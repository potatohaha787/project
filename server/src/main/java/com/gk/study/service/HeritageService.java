package com.gk.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gk.study.entity.Heritage;
import java.util.List;

// 关键修复：继承 IService<Heritage>
public interface HeritageService extends IService<Heritage> {
    List<Heritage> getHeritageList();
    Heritage getHeritageDetail(String id);
}