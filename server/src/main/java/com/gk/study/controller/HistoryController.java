package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.History;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoryController {

    private final static Logger logger = LoggerFactory.getLogger(HistoryController.class);

    @Autowired
    HistoryService service;

    // 任何人都可访问的查询列表接口（用于前端大屏展示）
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<History> list =  service.getHistoryList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 仅限管理员访问：创建历史事件
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(History history) throws IOException {
        service.createHistory(history);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    // 仅限管理员访问：删除历史事件（支持批量删除）
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteHistory(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    // 仅限管理员访问：更新历史事件
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(History history) throws IOException {
        service.updateHistory(history);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

}