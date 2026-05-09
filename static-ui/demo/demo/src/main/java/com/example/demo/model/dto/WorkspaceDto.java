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
public class WorkspaceDto {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    private String myRole;
}
