package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Celebrity;
import com.gk.study.service.CelebrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/celebrity")
public class CelebrityController {
    @Autowired
    private CelebrityService celebrityService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Celebrity> list = celebrityService.getCelebrityList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(@RequestParam("id") Long id) {
        Celebrity detail = celebrityService.getCelebrityDetail(id);
        if (detail == null) {
            return new APIResponse(ResponeCode.FAIL, "人物不存在", null);
        }
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", detail);
    }
}