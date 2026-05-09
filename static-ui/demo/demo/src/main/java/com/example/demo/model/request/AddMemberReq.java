package com.example.demo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddMemberReq {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Schema(example = "dev@test.com", description = "Email của thành viên cần thêm vào workspace")
    private String email;

    @NotBlank(message = "Role is required")
    @Schema(example = "Developer", description = "Vai trò: Manager, Developer, Tester")
    private String role;
}
