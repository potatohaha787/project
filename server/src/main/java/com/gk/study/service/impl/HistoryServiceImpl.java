package com.gk.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.History;
import com.gk.study.mapper.HistoryMapper;
import com.gk.study.service.HistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History> implements HistoryService {

    @Override
    public List<History> getHistoryList() {
        return this.list(); // MyBatis-Plus 自带的查询全部方法
    }

    @Override
    public void createHistory(History history) {
        this.save(history);
    }

    @Override
    public void deleteHistory(String id) {
        this.removeById(id);
    }

    @Override
    public void updateHistory(History history) {
        this.updateById(history);
    }
}