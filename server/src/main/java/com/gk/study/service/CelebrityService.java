package com.gk.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gk.study.entity.Celebrity;

import java.util.List;

// 让接口继承 MyBatis-Plus 的 IService<Celebrity>
public interface CelebrityService extends IService<Celebrity> {

    // 自定义的方法依然保留
    List<Celebrity> getCelebrityList();
}