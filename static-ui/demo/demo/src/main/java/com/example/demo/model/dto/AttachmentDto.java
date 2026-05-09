package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {
    private Integer id;
    private Integer bugId;
    private String name;
    private Long size;
    private LocalDateTime uploadedAt;
    private String downloadUrl;
}
