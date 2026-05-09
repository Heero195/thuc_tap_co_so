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
public class BugHistoryDto {
    private Integer id;
    private Integer bugId;
    private Integer updatedBy;
    private String updatedByName;
    private String oldStatus;
    private String newStatus;
    private String changeLog;
    private LocalDateTime createdAt;
}
