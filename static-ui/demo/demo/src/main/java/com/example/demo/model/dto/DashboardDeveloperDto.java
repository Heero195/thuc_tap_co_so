package com.example.demo.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDeveloperDto {
    private Integer assigneeId;
    private String assigneeName;
    private long count;
}
