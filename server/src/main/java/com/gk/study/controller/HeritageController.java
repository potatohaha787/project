package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Heritage;
import com.gk.study.service.HeritageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/heritage")
public class HeritageController {

    @Autowired
    HeritageService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Heritage> list = service.getHeritageList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }
    // 增加这个详情接口
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String id){
        Heritage heritage = service.getHeritageDetail(id);
        if(heritage == null){
            return new APIResponse(ResponeCode.FAIL, "未找到该非遗项目", null);
        }
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", heritage);
    }
}