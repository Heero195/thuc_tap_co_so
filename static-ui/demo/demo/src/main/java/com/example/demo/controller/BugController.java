package com.example.demo.controller;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.model.dto.BugDto;
import com.example.demo.model.dto.BugHistoryDto;
import com.example.demo.model.dto.CommentDto;
import com.example.demo.model.request.CreateBugReq;
import com.example.demo.model.request.CreateCommentReq;
import com.example.demo.model.request.UpdateBugReq;
import com.example.demo.service.BugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class BugController {

    @Autowired
    private BugService bugService;

    @Operation(summary = "Lấy danh sách bug của workspace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Workspace không tìm thấy")
    })
    @GetMapping("/api/workspaces/{workspaceId}/bugs")
    public ResponseEntity<List<BugDto>> getBugsByWorkspace(@PathVariable Integer workspaceId) {
        return ResponseEntity.ok(bugService.getBugsByWorkspace(workspaceId));
    }

    @Operation(summary = "Tạo bug mới trong workspace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Workspace không tìm thấy")
    })
    @PostMapping("/api/workspaces/{workspaceId}/bugs")
    public ResponseEntity<BugDto> createBug(
            @PathVariable Integer workspaceId,
            @Valid @RequestBody CreateBugReq req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer reporterId = userDetails.getUser().getId();
        BugDto created = bugService.createBug(workspaceId, req, reporterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Lấy thông tin chi tiết bug")
    @GetMapping("/api/bugs/{bugId}")
    public ResponseEntity<BugDto> getBugById(@PathVariable Integer bugId) {
        return ResponseEntity.ok(bugService.getBugById(bugId));
    }

    @Operation(summary = "Cập nhật bug (title, status, priority, assignee)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Bug không tìm thấy")
    })
    @PutMapping("/api/bugs/{bugId}")
    public ResponseEntity<BugDto> updateBug(
            @PathVariable Integer bugId,
            @RequestBody UpdateBugReq req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer updatedBy = userDetails.getUser().getId();
        return ResponseEntity.ok(bugService.updateBug(bugId, req, updatedBy));
    }

    @Operation(summary = "Xoá bug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xoá thành công"),
            @ApiResponse(responseCode = "404", description = "Bug không tìm thấy")
    })
    @DeleteMapping("/api/bugs/{bugId}")
    public ResponseEntity<String> deleteBug(
            @PathVariable Integer bugId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUser().getId();
        bugService.deleteBug(bugId, userId);
        return ResponseEntity.ok("Bug deleted successfully");
    }

    @Operation(summary = "Lấy lịch sử thay đổi của bug")
    @GetMapping("/api/bugs/{bugId}/history")
    public ResponseEntity<List<BugHistoryDto>> getBugHistory(@PathVariable Integer bugId) {
        return ResponseEntity.ok(bugService.getBugHistory(bugId));
    }

    @Operation(summary = "Lấy danh sách comment của bug")
    @GetMapping("/api/bugs/{bugId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Integer bugId) {
        return ResponseEntity.ok(bugService.getComments(bugId));
    }

    @Operation(summary = "Thêm comment vào bug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thêm comment thành công"),
            @ApiResponse(responseCode = "404", description = "Bug không tìm thấy")
    })
    @PostMapping("/api/bugs/{bugId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Integer bugId,
            @Valid @RequestBody CreateCommentReq req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUser().getId();
        CommentDto comment = bugService.addComment(bugId, req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
}
