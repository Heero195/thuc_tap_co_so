package com.example.demo.model.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateUserReq {

    @NotBlank(message = "Full name is required")
    @Schema(
            example = "Sam Smith",
            description = "Full name cannot be empty",
            required = true
    )
    @JsonAlias("full_name")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Schema(
            example = "sam.smith@gmail.com",
            description = "Email cannot be empty",
            required = true
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 20, message = "Password must be between 4 and 20 characters")
    @Schema(
            example = "verysecretpassword",
            description = "Password can't be empty",
            required = true
    )
    private String password;

    @Schema(
            example = "manager",
            description = "Role of the user (manager, developer, tester)"
    )
    private String role;

    @Pattern(regexp = "(^$|(09|01[2|6|8|9])+([0-9]{8})\\b)", message = "Please provide a valid phone number")
    @Schema(
            example = "0916016972",
            description = "Phone number is optional"
    )
    private String phone;
}