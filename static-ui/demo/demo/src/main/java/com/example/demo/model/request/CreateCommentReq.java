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
public class CreateCommentReq {

    @NotBlank(message = "Comment text is required")
    @Schema(example = "Lỗi này có vẻ gấp, Dev ưu tiên sửa nhé!", description = "Nội dung comment")
    private String text;
}
