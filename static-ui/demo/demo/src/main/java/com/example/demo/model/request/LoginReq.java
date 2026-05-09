package com.example.demo.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginReq {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Schema(example = "manager@test.com", required = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(example = "123456", required = true)
    private String password;
}
