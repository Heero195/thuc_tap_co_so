package com.example.demo.service;

import com.example.demo.model.dto.WorkspaceDto;
import com.example.demo.model.dto.WorkspaceMemberDto;
import com.example.demo.model.request.AddMemberReq;
import com.example.demo.model.request.CreateWorkspaceReq;

import java.util.List;

public interface WorkspaceService {
    List<WorkspaceDto> getMyWorkspaces(Integer userId);
    WorkspaceDto getWorkspaceById(Integer id, Integer userId);
    WorkspaceDto createWorkspace(CreateWorkspaceReq req, Integer creatorUserId);
    void deleteWorkspace(Integer id, Integer userId);

    List<WorkspaceMemberDto> getMembers(Integer workspaceId);
    WorkspaceMemberDto addMember(Integer workspaceId, AddMemberReq req, Integer requesterId);
    void removeMember(Integer workspaceId, Integer userId, Integer requesterId);
    void updateMemberRole(Integer workspaceId, Integer userId, String newRole, Integer requesterId);
}
