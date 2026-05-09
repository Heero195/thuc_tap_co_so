package com.example.demo.service;

import com.example.demo.entity.Bug;
import com.example.demo.entity.Member;
import com.example.demo.model.dto.DashboardDeveloperDto;
import com.example.demo.model.dto.DashboardPriorityDto;
import com.example.demo.model.dto.DashboardSummaryDto;
import com.example.demo.repository.BugRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.WorkspaceRepository;
import com.example.demo.repository.WorkspaceUserRepository;
import com.example.demo.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WorkspaceUserRepository workspaceUserRepository;

    @Override
    public DashboardSummaryDto getSummary(Integer workspaceId) {
        validateWorkspace(workspaceId);
        List<Bug> bugs = bugRepository.findByWorkspaceId(workspaceId);

        DashboardSummaryDto dto = new DashboardSummaryDto();
        dto.setOpen(bugs.stream().filter(b -> "Open".equalsIgnoreCase(b.getStatus())).count());
        dto.setInprogress(bugs.stream().filter(b -> "In Progress".equalsIgnoreCase(b.getStatus())).count());
        dto.setResolved(bugs.stream().filter(b -> "Resolved".equalsIgnoreCase(b.getStatus())).count());
        dto.setClosed(bugs.stream().filter(b -> "Closed".equalsIgnoreCase(b.getStatus())).count());

        return dto;
    }

    @Override
    public DashboardPriorityDto getByPriority(Integer workspaceId) {
        validateWorkspace(workspaceId);
        List<Bug> bugs = bugRepository.findByWorkspaceId(workspaceId);

        DashboardPriorityDto dto = new DashboardPriorityDto();
        dto.setHigh(bugs.stream().filter(b -> "High".equalsIgnoreCase(b.getPriority())).count());
        dto.setMedium(bugs.stream().filter(b -> "Medium".equalsIgnoreCase(b.getPriority())).count());
        dto.setLow(bugs.stream().filter(b -> "Low".equalsIgnoreCase(b.getPriority())).count());

        return dto;
    }

    @Override
    public List<DashboardDeveloperDto> getByDeveloper(Integer workspaceId) {
        validateWorkspace(workspaceId);
        List<Bug> bugs = bugRepository.findByWorkspaceId(workspaceId);

        Map<Integer, Long> countsMap = bugs.stream()
                .collect(Collectors.groupingBy(b -> b.getAssigneeId() == null ? -1 : b.getAssigneeId(), Collectors.counting()));

        List<DashboardDeveloperDto> result = new ArrayList<>();

        if (countsMap.containsKey(-1)) {
            result.add(new DashboardDeveloperDto(null, "Unassigned", countsMap.get(-1)));
        }

        countsMap.forEach((assigneeId, count) -> {
            if (assigneeId != -1) {
                String name = memberRepository.findByUserId(assigneeId)
                        .map(Member::getFullName)
                        .orElse("Unknown User");
                result.add(new DashboardDeveloperDto(assigneeId, name, count));
            }
        });

        return result;
    }

    private void validateWorkspace(Integer workspaceId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }
    }
}
