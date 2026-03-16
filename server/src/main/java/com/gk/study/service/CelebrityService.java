package com.gk.study.service;
import com.gk.study.entity.Celebrity;
import java.util.List;

public interface CelebrityService {
    List<Celebrity> getCelebrityList();
    Celebrity getCelebrityDetail(Long id);
}