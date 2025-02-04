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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // 관리자만 접근 가능
    public ResponseEntity<List<Member>> getAllUsers() {
        List<Member> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * ✅ 사용자 권한 변경 (관리자 전용)
     */
    @PutMapping("/users/{userid}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateUserRole(@PathVariable String userid, @RequestParam String role) {
        adminService.updateUserRole(userid, role);
        return ResponseEntity.ok(userid + "님의 권한이 " + role + "(으)로 변경되었습니다.");
    }

    /**
     * ✅ 사용자 삭제 (관리자 전용)
     */
    @DeleteMapping("/users/{userid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String userid) {
        adminService.deleteUser(userid);
        return ResponseEntity.ok(userid + "님이 삭제되었습니다.");
    }
}
