package com.gk.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gk.study.entity.Food;
import java.util.List;

// 关键修复：继承 IService<Food>
public interface FoodService extends IService<Food> {
    List<Food> getFoodList();
    Food getFoodDetail(String id);
}