package com.example.demo.service;

import com.example.demo.entity.Bug;
import com.example.demo.entity.BugHistory;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.entity.WorkspaceUser;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.BugDto;
import com.example.demo.model.dto.BugHistoryDto;
import com.example.demo.model.dto.CommentDto;
import com.example.demo.model.request.CreateBugReq;
import com.example.demo.model.request.CreateCommentReq;
import com.example.demo.model.request.UpdateBugReq;
import com.example.demo.repository.BugHistoryRepository;
import com.example.demo.repository.BugRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.WorkspaceRepository;
import com.example.demo.repository.WorkspaceUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BugServiceImpl implements BugService {

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private BugHistoryRepository bugHistoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WorkspaceUserRepository workspaceUserRepository;

    private String getMemberName(Integer userId) {
        if (userId == null) return null;
        return memberRepository.findByUserId(userId)
                .map(Member::getFullName)
                .orElse("Unknown");
    }

    private void checkWorkspaceMember(Integer workspaceId, Integer userId) {
        if (!workspaceUserRepository.findByWorkspaceIdAndUserId(workspaceId, userId).isPresent()) {
            throw new ForbiddenException("Bạn không phải là thành viên của workspace này");
        }
    }

    private void checkCanAssign(Integer workspaceId, Integer userId) {
        Member member = memberRepository.findByUserId(userId).orElse(null);
        boolean isSystemAdmin = member != null && "Admin".equalsIgnoreCase(member.getRole());

        WorkspaceUser wu = workspaceUserRepository.findByWorkspaceIdAndUserId(workspaceId, userId).orElse(null);
        boolean isManager = wu != null && "Manager".equalsIgnoreCase(wu.getRole());

        if (!isManager && !isSystemAdmin) {
            throw new ForbiddenException("Chỉ Manager hoặc người tạo dự án mới có quyền gán hoặc thay đổi người xử lý.");
        }
    }

    private BugDto toDto(Bug bug) {
        BugDto dto = new BugDto();
        dto.setId(bug.getId());
        dto.setWorkspaceId(bug.getWorkspaceId());
        dto.setTitle(bug.getTitle());
        dto.setDescription(bug.getDescription());
        dto.setPriority(bug.getPriority());
        dto.setStatus(bug.getStatus());
        dto.setReporterId(bug.getReporterId());
        dto.setReporterName(getMemberName(bug.getReporterId()));
        dto.setAssigneeId(bug.getAssigneeId());
        dto.setAssigneeName(getMemberName(bug.getAssigneeId()));
        dto.setCreatedAt(bug.getCreatedAt());
        dto.setUpdatedAt(bug.getUpdatedAt());
        return dto;
    }

    private BugHistoryDto toHistoryDto(BugHistory bh) {
        BugHistoryDto dto = new BugHistoryDto();
        dto.setId(bh.getId());
        dto.setBugId(bh.getBugId());
        dto.setUpdatedBy(bh.getUpdatedBy());
        dto.setUpdatedByName(getMemberName(bh.getUpdatedBy()));
        dto.setOldStatus(bh.getOldStatus());
        dto.setNewStatus(bh.getNewStatus());
        dto.setChangeLog(bh.getChangeLog());
        dto.setCreatedAt(bh.getCreatedAt());
        return dto;
    }

    private CommentDto toCommentDto(Comment c) {
        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setBugId(c.getBugId());
        dto.setUserId(c.getUserId());
        dto.setUserName(getMemberName(c.getUserId()));
        dto.setText(c.getText());
        dto.setCreatedAt(c.getCreatedAt());
        memberRepository.findByUserId(c.getUserId())
                .ifPresent(m -> dto.setUserAvatar(m.getAvatar()));
        return dto;
    }

    @Override
    public List<BugDto> getBugsByWorkspace(Integer workspaceId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }

        return bugRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BugDto getBugById(Integer bugId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new NotFoundException("Bug not found"));
        return toDto(bug);
    }

    @Override
    @Transactional
    public BugDto createBug(Integer workspaceId, CreateBugReq req, Integer reporterId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }
        checkWorkspaceMember(workspaceId, reporterId);

        if (req.getAssigneeId() != null) {
            checkCanAssign(workspaceId, reporterId);
        }

        Bug bug = new Bug();
        bug.setWorkspaceId(workspaceId);
        bug.setTitle(req.getTitle());
        bug.setDescription(req.getDescription());
        bug.setPriority(req.getPriority() != null ? req.getPriority() : "Medium");
        bug.setStatus(req.getStatus() != null ? req.getStatus() : "Open");
        bug.setReporterId(reporterId);
        bug.setAssigneeId(req.getAssigneeId());
        bug = bugRepository.save(bug);
        return toDto(bug);
    }

    @Override
    @Transactional
    public BugDto updateBug(Integer bugId, UpdateBugReq req, Integer updatedByUserId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new NotFoundException("Bug not found"));

        checkWorkspaceMember(bug.getWorkspaceId(), updatedByUserId);

        String oldStatus = bug.getStatus();

        if (req.getTitle() != null) bug.setTitle(req.getTitle());
        if (req.getDescription() != null) bug.setDescription(req.getDescription());
        if (req.getPriority() != null) bug.setPriority(req.getPriority());
        if (req.getStatus() != null) bug.setStatus(req.getStatus());

        if (req.getAssigneeId() != null && !req.getAssigneeId().equals(bug.getAssigneeId())) {
            checkCanAssign(bug.getWorkspaceId(), updatedByUserId);
            bug.setAssigneeId(req.getAssigneeId());
        }

        bug = bugRepository.save(bug);

        String newStatus = bug.getStatus();
        if (req.getStatus() != null && !req.getStatus().equals(oldStatus)) {
            BugHistory history = new BugHistory();
            history.setBugId(bug.getId());
            history.setUpdatedBy(updatedByUserId);
            history.setOldStatus(oldStatus);
            history.setNewStatus(newStatus);
            history.setChangeLog("Status changed from " + oldStatus + " to " + newStatus);
            bugHistoryRepository.save(history);
        }

        return toDto(bug);
    }

    @Override
    @Transactional
    public void deleteBug(Integer bugId, Integer userId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new NotFoundException("Bug not found"));

        Member member = memberRepository.findByUserId(userId).orElse(null);
        boolean isSystemAdmin = member != null && "Admin".equalsIgnoreCase(member.getRole());

        WorkspaceUser wu = workspaceUserRepository.findByWorkspaceIdAndUserId(bug.getWorkspaceId(), userId).orElse(null);
        boolean isManager = wu != null && "Manager".equalsIgnoreCase(wu.getRole());

        if (!isManager && !isSystemAdmin) {
            throw new ForbiddenException("Chỉ Admin hoặc người tạo dự án (Manager) mới có quyền xoá bug. Tester và Dev chỉ có thể đổi trạng thái.");
        }

        bugRepository.delete(bug);
    }

    @Override
    public List<BugHistoryDto> getBugHistory(Integer bugId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new NotFoundException("Bug not found"));

        return bugHistoryRepository.findByBugIdOrderByCreatedAtAsc(bugId)
                .stream()
                .map(this::toHistoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getComments(Integer bugId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new NotFoundException("Bug not found"));
        return commentRepository.findByBugIdOrderByCreatedAtAsc(bugId)
                .stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer bugId, CreateCommentReq req, Integer userId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new NotFoundException("Bug not found"));

        checkWorkspaceMember(bug.getWorkspaceId(), userId);

        Comment comment = new Comment();
        comment.setBugId(bugId);
        comment.setUserId(userId);
        comment.setText(req.getText());
        comment.setCreatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return toCommentDto(comment);
    }
}
