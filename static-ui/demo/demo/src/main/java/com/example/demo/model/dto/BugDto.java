package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BugDto {
    private Integer id;
    private Integer workspaceId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private Integer reporterId;
    private String reporterName;
    private Integer assigneeId;
    private String assigneeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
