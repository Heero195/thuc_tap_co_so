package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.Workspace;
import com.example.demo.entity.WorkspaceUser;
import com.example.demo.exception.DuplicateRecordException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.WorkspaceDto;
import com.example.demo.model.dto.WorkspaceMemberDto;
import com.example.demo.model.request.AddMemberReq;
import com.example.demo.model.request.CreateWorkspaceReq;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkspaceRepository;
import com.example.demo.repository.WorkspaceUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceUserRepository workspaceUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    private void checkManagerRole(Integer workspaceId, Integer userId) {
        com.example.demo.entity.Member member = memberRepository.findByUserId(userId).orElse(null);
        boolean isSystemAdmin = member != null && "Admin".equalsIgnoreCase(member.getRole());

        WorkspaceUser wu = workspaceUserRepository.findByWorkspaceIdAndUserId(workspaceId, userId).orElse(null);
        boolean isManager = wu != null && "Manager".equalsIgnoreCase(wu.getRole());

        if (!isManager && !isSystemAdmin) {
            throw new ForbiddenException("Chỉ Admin hoặc người tạo dự án (Manager) mới có quyền thực hiện hành động này");
        }
    }

    private WorkspaceDto toDto(Workspace ws, Integer currentUserId) {
        WorkspaceDto dto = new WorkspaceDto();
        dto.setId(ws.getId());
        dto.setName(ws.getName());
        dto.setDescription(ws.getDescription());
        dto.setCreatedAt(ws.getCreatedAt());
        if (currentUserId != null) {
            workspaceUserRepository.findByWorkspaceIdAndUserId(ws.getId(), currentUserId)
                    .ifPresent(wu -> dto.setMyRole(wu.getRole()));
        }
        return dto;
    }

    private WorkspaceMemberDto toMemberDto(WorkspaceUser wu) {
        WorkspaceMemberDto dto = new WorkspaceMemberDto();
        dto.setUserId(wu.getUserId());
        dto.setRole(wu.getRole());

        userRepository.findById(wu.getUserId()).ifPresent(u -> dto.setEmail(u.getEmail()));
        memberRepository.findByUserId(wu.getUserId()).ifPresent(m -> {
            dto.setName(m.getFullName());
            dto.setAvatar(m.getAvatar());
            dto.setPhone(m.getPhone());
        });
        return dto;
    }

    @Override
    public List<WorkspaceDto> getMyWorkspaces(Integer userId) {
        List<WorkspaceUser> wuList = workspaceUserRepository.findByUserId(userId);
        return wuList.stream()
                .map(wu -> workspaceRepository.findById(wu.getWorkspaceId())
                        .map(ws -> toDto(ws, userId))
                        .orElse(null))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Override
    public WorkspaceDto getWorkspaceById(Integer id, Integer userId) {
        Workspace ws = workspaceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));
        return toDto(ws, userId);
    }

    @Override
    @Transactional
    public WorkspaceDto createWorkspace(CreateWorkspaceReq req, Integer creatorUserId) {
        Workspace ws = new Workspace();
        ws.setName(req.getName());
        ws.setDescription(req.getDescription());
        ws = workspaceRepository.save(ws);

        WorkspaceUser wu = new WorkspaceUser();
        wu.setWorkspaceId(ws.getId());
        wu.setUserId(creatorUserId);
        wu.setRole("Manager");
        workspaceUserRepository.save(wu);

        return toDto(ws, creatorUserId);
    }

    @Override
    @Transactional
    public void deleteWorkspace(Integer id, Integer userId) {
        checkManagerRole(id, userId);
        Workspace ws = workspaceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));
        workspaceRepository.delete(ws);
    }

    @Override
    public List<WorkspaceMemberDto> getMembers(Integer workspaceId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }
        return workspaceUserRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(this::toMemberDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WorkspaceMemberDto addMember(Integer workspaceId, AddMemberReq req, Integer requesterId) {
        checkManagerRole(workspaceId, requesterId);
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + req.getEmail()));

        if (workspaceUserRepository.findByWorkspaceIdAndUserId(workspaceId, user.getId()).isPresent()) {
            throw new DuplicateRecordException("User is already a member of this workspace");
        }

        WorkspaceUser wu = new WorkspaceUser();
        wu.setWorkspaceId(workspaceId);
        wu.setUserId(user.getId());
        wu.setRole(req.getRole());
        workspaceUserRepository.save(wu);

        return toMemberDto(wu);
    }

    @Override
    @Transactional
    public void removeMember(Integer workspaceId, Integer userId, Integer requesterId) {
        checkManagerRole(workspaceId, requesterId);
        workspaceUserRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found in this workspace"));
        workspaceUserRepository.deleteByWorkspaceIdAndUserId(workspaceId, userId);
    }

    @Override
    @Transactional
    public void updateMemberRole(Integer workspaceId, Integer userId, String newRole, Integer requesterId) {
        checkManagerRole(workspaceId, requesterId);
        WorkspaceUser wu = workspaceUserRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found in this workspace"));
        wu.setRole(newRole);
        workspaceUserRepository.save(wu);
    }
}
