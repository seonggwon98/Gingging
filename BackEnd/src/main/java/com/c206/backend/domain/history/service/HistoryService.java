package com.c206.backend.domain.history.service;

import com.c206.backend.domain.history.dto.response.HistoryListResponseDto;

import java.util.List;

public interface HistoryService {

    List<HistoryListResponseDto> historyList();
}
