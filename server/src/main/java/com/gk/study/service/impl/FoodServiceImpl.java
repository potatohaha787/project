package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Food;
import com.gk.study.mapper.FoodMapper;
import com.gk.study.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {
    @Autowired
    FoodMapper mapper;

    @Override
    public List<Food> getFoodList() {
        return mapper.selectList(new QueryWrapper<>());
    }
    @Override
    public Food getFoodDetail(String id) {
        return mapper.selectById(id);
    }
}