package com.example.demo.repository;

import com.example.demo.entity.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Integer> {
    List<Bug> findByWorkspaceId(Integer workspaceId);
    List<Bug> findByWorkspaceIdAndStatus(Integer workspaceId, String status);
    List<Bug> findByAssigneeId(Integer assigneeId);
    List<Bug> findByReporterId(Integer reporterId);
}
