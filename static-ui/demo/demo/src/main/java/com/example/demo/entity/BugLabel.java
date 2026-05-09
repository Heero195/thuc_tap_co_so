package com.example.demo.entity;

import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bug_labels")
@IdClass(BugLabelId.class)
public class BugLabel {

    @Id
    @Column(name = "bug_id")
    private Integer bugId;

    @Id
    @Column(name = "label_id")
    private Integer labelId;
}
