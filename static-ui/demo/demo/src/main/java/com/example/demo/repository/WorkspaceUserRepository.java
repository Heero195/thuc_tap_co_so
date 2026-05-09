package com.example.demo.repository;

import com.example.demo.entity.WorkspaceUser;
import com.example.demo.entity.WorkspaceUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, WorkspaceUserId> {
    List<WorkspaceUser> findByWorkspaceId(Integer workspaceId);
    List<WorkspaceUser> findByUserId(Integer userId);
    Optional<WorkspaceUser> findByWorkspaceIdAndUserId(Integer workspaceId, Integer userId);
    void deleteByWorkspaceIdAndUserId(Integer workspaceId, Integer userId);
}
