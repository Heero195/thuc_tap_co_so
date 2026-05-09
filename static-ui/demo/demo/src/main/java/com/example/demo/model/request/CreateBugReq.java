package com.example.demo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBugReq {

    @NotBlank(message = "Title is required")
    @Schema(example = "Gãy layout màn hình Login", description = "Tiêu đề bug")
    private String title;

    @Schema(example = "Khi xem trên điện thoại, nút đăng nhập bị méo", description = "Mô tả chi tiết")
    private String description;

    @Schema(example = "High", description = "Độ ưu tiên: Low, Medium, High, Critical")
    private String priority;

    @Schema(example = "Open", description = "Trạng thái: Open, In Progress, Resolved, Closed")
    private String status;

    @Schema(example = "2", description = "ID người được giao xử lý (tuỳ chọn)")
    private Integer assigneeId;
}
