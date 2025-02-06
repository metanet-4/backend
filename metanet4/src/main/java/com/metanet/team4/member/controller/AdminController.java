package com.metanet.team4.member.controller;

import com.metanet.team4.member.model.Member;
import com.metanet.team4.member.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * ✅ 모든 사용자 조회 (관리자 전용)
     */
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Member>> getAllUsers() {
        List<Member> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * ✅ 장애인 인증서 조회 (관리자 전용)
     */
    @GetMapping("/users/{userId}/certificate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> getDisabilityCertificate(@PathVariable String userId) {
        String certificate = adminService.getDisabilityCertificate(userId);
        return ResponseEntity.ok(certificate);
    }

    /**
     * ✅ 사용자 권한 변경 (관리자 전용)
     */
    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateUserRole(@PathVariable String userId, @RequestParam String role) {
        adminService.updateUserRole(userId, role);
        return ResponseEntity.ok(userId + "님의 권한이 " + role + "(으)로 변경되었습니다.");
    }

    /**
     * ✅ 사용자 삭제 (관리자 전용)
     */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(userId + "님이 삭제되었습니다.");
    }

    /**
     * ✅ 관리자가 우대 여부 승인 (is_discounted = 1로 변경)
     */
    @PostMapping("/users/{userId}/approve-discount")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> approveDiscount(@PathVariable String userId) {
        adminService.approveDiscount(userId);
        return ResponseEntity.ok(userId + "님의 우대 여부가 승인되었습니다.");
    }
}
