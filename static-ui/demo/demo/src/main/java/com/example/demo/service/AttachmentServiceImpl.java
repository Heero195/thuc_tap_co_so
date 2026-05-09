package com.example.demo.service;

import com.example.demo.entity.Attachment;
import com.example.demo.entity.Bug;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.AttachmentDto;
import com.example.demo.model.mapper.AttachmentMapper;
import com.example.demo.repository.AttachmentRepository;
import com.example.demo.repository.BugRepository;
import com.example.demo.repository.WorkspaceUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/upload";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private WorkspaceUserRepository workspaceUserRepository;

    private void checkWorkspaceMember(Integer workspaceId, Integer userId) {
        if (!workspaceUserRepository.findByWorkspaceIdAndUserId(workspaceId, userId).isPresent()) {
            throw new ForbiddenException("Bạn không phải là thành viên của workspace này");
        }
    }

    @Override
    public AttachmentDto uploadAttachment(Integer bugId, MultipartFile file, Integer userId) {

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new NotFoundException("Bug not found with ID: " + bugId));

        checkWorkspaceMember(bug.getWorkspaceId(), userId);

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File serverFile = new File(UPLOAD_DIR + File.separator + fileName);

        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
            stream.write(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }

        Attachment attachment = new Attachment();
        attachment.setBugId(bugId);
        attachment.setName(fileName);
        attachment.setSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());

        attachment = attachmentRepository.save(attachment);

        return AttachmentMapper.toDto(attachment);
    }

    @Override
    public List<AttachmentDto> getAttachmentsByBug(Integer bugId) {
        if (!bugRepository.existsById(bugId)) {
            throw new NotFoundException("Bug not found with ID: " + bugId);
        }
        return attachmentRepository.findByBugId(bugId).stream()
                .map(AttachmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAttachment(Integer id, Integer userId) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attachment not found"));

        Bug bug = bugRepository.findById(attachment.getBugId())
                .orElseThrow(() -> new NotFoundException("Bug not found for this attachment"));

        checkWorkspaceMember(bug.getWorkspaceId(), userId);

        File file = new File(UPLOAD_DIR + File.separator + attachment.getName());
        if (file.exists()) {
            file.delete();
        }

        attachmentRepository.delete(attachment);
    }

    @Override
    public AttachmentDto getAttachmentById(Integer id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attachment not found"));
        return AttachmentMapper.toDto(attachment);
    }
}
