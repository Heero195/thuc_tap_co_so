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
public class CreateWorkspaceReq {

    @NotBlank(message = "Workspace name is required")
    @Schema(example = "Project CRM NextGen", description = "Tên workspace")
    private String name;

    @Schema(example = "Dự án quản lý khách hàng", description = "Mô tả workspace (tuỳ chọn)")
    private String description;
}
