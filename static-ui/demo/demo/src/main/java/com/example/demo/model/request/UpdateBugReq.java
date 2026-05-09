package com.example.demo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBugReq {

    @Schema(example = "Fix login validation", description = "Tiêu đề bug")
    private String title;

    @Schema(example = "Validate email format + show error message", description = "Mô tả chi tiết")
    private String description;

    @Schema(example = "High", description = "Độ ưu tiên: Low, Medium, High, Critical")
    private String priority;

    @Schema(example = "In Progress", description = "Trạng thái: Open, In Progress, Resolved, Closed")
    private String status;

    @Schema(example = "2", description = "ID người được giao xử lý")
    private Integer assigneeId;
}
