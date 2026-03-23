package com.gk.study.service;

import com.gk.study.entity.Heritage;
import java.util.List;

public interface HeritageService {
    List<Heritage> getHeritageList();
    // 增加这一个方法
    Heritage getHeritageDetail(String id);
}