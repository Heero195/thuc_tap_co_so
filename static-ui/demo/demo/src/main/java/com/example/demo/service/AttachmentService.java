package com.example.demo.service;

import com.example.demo.model.dto.AttachmentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    AttachmentDto uploadAttachment(Integer bugId, MultipartFile file, Integer userId);
    List<AttachmentDto> getAttachmentsByBug(Integer bugId);
    void deleteAttachment(Integer id, Integer userId);
    AttachmentDto getAttachmentById(Integer id);
}
