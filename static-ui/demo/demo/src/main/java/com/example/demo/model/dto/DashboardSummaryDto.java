package com.example.demo.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    private long open;
    private long inprogress;
    private long resolved;
    private long closed;
}
