package com.example.demo.model.mapper;

import com.example.demo.entity.Attachment;
import com.example.demo.model.dto.AttachmentDto;

public class AttachmentMapper {
    public static AttachmentDto toDto(Attachment attachment) {
        if (attachment == null) return null;

        AttachmentDto dto = new AttachmentDto();
        dto.setId(attachment.getId());
        dto.setBugId(attachment.getBugId());
        dto.setName(attachment.getName());
        dto.setSize(attachment.getSize());
        dto.setUploadedAt(attachment.getUploadedAt());

        dto.setDownloadUrl("/api/attachments/" + attachment.getId() + "/download");

        return dto;
    }
}
