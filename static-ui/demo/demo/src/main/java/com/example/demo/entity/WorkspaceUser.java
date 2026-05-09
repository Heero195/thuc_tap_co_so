package com.example.demo.entity;

import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workspace_users")
@IdClass(WorkspaceUserId.class)
public class WorkspaceUser {

    @Id
    @Column(name = "workspace_id")
    private Integer workspaceId;

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "role", nullable = false)
    private String role;
}
