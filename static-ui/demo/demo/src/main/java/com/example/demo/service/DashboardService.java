package com.example.demo.service;

import com.example.demo.model.dto.DashboardDeveloperDto;
import com.example.demo.model.dto.DashboardPriorityDto;
import com.example.demo.model.dto.DashboardSummaryDto;

import java.util.List;

public interface DashboardService {
    DashboardSummaryDto getSummary(Integer workspaceId);
    DashboardPriorityDto getByPriority(Integer workspaceId);
    List<DashboardDeveloperDto> getByDeveloper(Integer workspaceId);
}
