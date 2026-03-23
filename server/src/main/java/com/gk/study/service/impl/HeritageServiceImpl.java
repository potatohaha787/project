package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Heritage;
import com.gk.study.mapper.HeritageMapper;
import com.gk.study.service.HeritageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HeritageServiceImpl extends ServiceImpl<HeritageMapper, Heritage> implements HeritageService {

    @Autowired
    HeritageMapper mapper;

    @Override
    public List<Heritage> getHeritageList() {
        // 直接查出所有非遗数据
        return mapper.selectList(new QueryWrapper<>());
    }
    // 加上实现逻辑
    @Override
    public Heritage getHeritageDetail(String id) {
        return mapper.selectById(id);
    }
}