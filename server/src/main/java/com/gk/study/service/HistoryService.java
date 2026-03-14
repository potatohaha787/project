package com.gk.study.service;

import com.gk.study.entity.History;
import java.util.List;

public interface HistoryService {
    List<History> getHistoryList();
    void createHistory(History history);
    void deleteHistory(String id);
    void updateHistory(History history);
}