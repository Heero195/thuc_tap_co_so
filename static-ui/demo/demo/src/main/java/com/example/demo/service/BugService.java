package com.example.demo.service;

import com.example.demo.model.dto.BugDto;
import com.example.demo.model.dto.BugHistoryDto;
import com.example.demo.model.dto.CommentDto;
import com.example.demo.model.request.CreateBugReq;
import com.example.demo.model.request.CreateCommentReq;
import com.example.demo.model.request.UpdateBugReq;

import java.util.List;

public interface BugService {
    List<BugDto> getBugsByWorkspace(Integer workspaceId);
    BugDto getBugById(Integer bugId);
    BugDto createBug(Integer workspaceId, CreateBugReq req, Integer reporterId);
    BugDto updateBug(Integer bugId, UpdateBugReq req, Integer updatedByUserId);
    void deleteBug(Integer bugId, Integer userId);

    List<BugHistoryDto> getBugHistory(Integer bugId);
    List<CommentDto> getComments(Integer bugId);
    CommentDto addComment(Integer bugId, CreateCommentReq req, Integer userId);
}
