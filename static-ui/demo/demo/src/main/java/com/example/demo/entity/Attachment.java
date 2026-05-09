package com.example.demo.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "bug_id")
    private Integer bugId;

    @Column(name = "file_name")
    private String name;

    @Column(name = "file_size")
    private Long size;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}
