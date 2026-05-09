package com.example.demo.controller;

import com.example.demo.model.dto.DashboardDeveloperDto;
import com.example.demo.model.dto.DashboardPriorityDto;
import com.example.demo.model.dto.DashboardSummaryDto;
import com.example.demo.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "Lấy thống kê số lượng bug theo trạng thái")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary(@RequestParam Integer workspaceId) {
        return ResponseEntity.ok(dashboardService.getSummary(workspaceId));
    }

    @Operation(summary = "Lấy thống kê số lượng bug theo mức độ ưu tiên")
    @GetMapping("/by-priority")
    public ResponseEntity<DashboardPriorityDto> getByPriority(@RequestParam Integer workspaceId) {
        return ResponseEntity.ok(dashboardService.getByPriority(workspaceId));
    }

    @Operation(summary = "Lấy thống kê số lượng bug theo người gán")
    @GetMapping("/by-developer")
    public ResponseEntity<List<DashboardDeveloperDto>> getByDeveloper(@RequestParam Integer workspaceId) {
        return ResponseEntity.ok(dashboardService.getByDeveloper(workspaceId));
    }
}
