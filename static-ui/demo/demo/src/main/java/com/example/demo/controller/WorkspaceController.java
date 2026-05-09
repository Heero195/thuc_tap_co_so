package com.example.demo.controller;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.model.dto.WorkspaceDto;
import com.example.demo.model.dto.WorkspaceMemberDto;
import com.example.demo.model.request.AddMemberReq;
import com.example.demo.model.request.CreateWorkspaceReq;
import com.example.demo.service.WorkspaceService;
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
@RequestMapping("/api/workspaces")
@SecurityRequirement(name = "bearerAuth")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @Operation(summary = "Lấy danh sách workspace của tôi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("")
    public ResponseEntity<List<WorkspaceDto>> getMyWorkspaces(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUser().getId();
        return ResponseEntity.ok(workspaceService.getMyWorkspaces(userId));
    }

    @Operation(summary = "Lấy thông tin workspace theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy workspace")
    })
    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceDto> getWorkspaceById(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUser().getId();
        return ResponseEntity.ok(workspaceService.getWorkspaceById(id, userId));
    }

    @Operation(summary = "Tạo workspace mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping("")
    public ResponseEntity<WorkspaceDto> createWorkspace(
            @Valid @RequestBody CreateWorkspaceReq req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUser().getId();
        WorkspaceDto created = workspaceService.createWorkspace(req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Xoá workspace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xoá thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy workspace")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorkspace(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUser().getId();
        workspaceService.deleteWorkspace(id, userId);
        return ResponseEntity.ok("Workspace deleted successfully");
    }

    @Operation(summary = "Lấy danh sách thành viên của workspace")
    @GetMapping("/{id}/members")
    public ResponseEntity<List<WorkspaceMemberDto>> getMembers(@PathVariable Integer id) {
        return ResponseEntity.ok(workspaceService.getMembers(id));
    }

    @Operation(summary = "Thêm thành viên vào workspace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thêm thành công"),
            @ApiResponse(responseCode = "400", description = "Thành viên đã tồn tại"),
            @ApiResponse(responseCode = "404", description = "User không tồn tại")
    })
    @PostMapping("/{id}/members")
    public ResponseEntity<WorkspaceMemberDto> addMember(
            @PathVariable Integer id,
            @Valid @RequestBody AddMemberReq req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer requesterId = userDetails.getUser().getId();
        WorkspaceMemberDto member = workspaceService.addMember(id, req, requesterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @Operation(summary = "Xoá thành viên khỏi workspace")
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<String> removeMember(
            @PathVariable Integer id,
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer requesterId = userDetails.getUser().getId();
        workspaceService.removeMember(id, userId, requesterId);
        return ResponseEntity.ok("Member removed successfully");
    }

    @Operation(summary = "Cập nhật vai trò thành viên")
    @PatchMapping("/{id}/members/{userId}/role")
    public ResponseEntity<String> updateMemberRole(
            @PathVariable Integer id,
            @PathVariable Integer userId,
            @RequestParam String role,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer requesterId = userDetails.getUser().getId();
        workspaceService.updateMemberRole(id, userId, role, requesterId);
        return ResponseEntity.ok("Role updated successfully");
    }
}
