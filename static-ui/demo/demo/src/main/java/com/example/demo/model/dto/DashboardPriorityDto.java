package com.example.demo.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPriorityDto {
    private long high;
    private long medium;
    private long low;
}
