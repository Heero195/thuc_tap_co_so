package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberDto {
    private Integer userId;
    private String name;
    private String email;
    private String avatar;
    private String phone;
    private String role;
}
