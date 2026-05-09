package com.example.demo.controller;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.AttachmentDto;
import com.example.demo.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class AttachmentController {

    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/upload";

    @Autowired
    private AttachmentService attachmentService;

    @Operation(summary = "Tải file đính kèm lên Bug")
    @PostMapping("/api/bugs/{bugId}/attachments")
    public ResponseEntity<AttachmentDto> uploadAttachment(
            @PathVariable Integer bugId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUser().getId();
        AttachmentDto result = attachmentService.uploadAttachment(bugId, file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Lấy danh sách file đính kèm của Bug")
    @GetMapping("/api/bugs/{bugId}/attachments")
    public ResponseEntity<List<AttachmentDto>> getAttachments(@PathVariable Integer bugId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByBug(bugId));
    }

    @Operation(summary = "Xoá file đính kèm")
    @DeleteMapping("/api/attachments/{id}")
    public ResponseEntity<String> deleteAttachment(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUser().getId();
        attachmentService.deleteAttachment(id, userId);
        return ResponseEntity.ok("Attachment deleted successfully");
    }

    @Operation(summary = "Tải về file đính kèm")
    @GetMapping("/api/attachments/{id}/download")
    public ResponseEntity<UrlResource> downloadAttachment(@PathVariable Integer id) {
        AttachmentDto attachment = attachmentService.getAttachmentById(id);
        File file = new File(UPLOAD_DIR + File.separator + attachment.getName());

        if (!file.exists()) {
            throw new NotFoundException("File not found on server");
        }

        try {
            UrlResource resource = new UrlResource(file.toURI());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error occurred while downloading file");
        }
    }
}
