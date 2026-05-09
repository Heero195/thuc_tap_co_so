package com.example.demo.repository;

import com.example.demo.entity.BugHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugHistoryRepository extends JpaRepository<BugHistory, Integer> {
    List<BugHistory> findByBugIdOrderByCreatedAtAsc(Integer bugId);
}
