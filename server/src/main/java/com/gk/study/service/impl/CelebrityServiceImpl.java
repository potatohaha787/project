package com.gk.study.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Celebrity;
import com.gk.study.mapper.CelebrityMapper;
import com.gk.study.service.CelebrityService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CelebrityServiceImpl extends ServiceImpl<CelebrityMapper, Celebrity> implements CelebrityService {
    @Override
    public List<Celebrity> getCelebrityList() {
        QueryWrapper<Celebrity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("create_time"); // 按照创建时间排序
        return this.baseMapper.selectList(queryWrapper);
    }
}