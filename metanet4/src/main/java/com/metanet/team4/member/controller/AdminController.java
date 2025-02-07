package com.metanet.team4.member.controller;

import com.metanet.team4.member.model.Member;
import com.metanet.team4.member.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Map;

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
        byte[] certificateBytes = adminService.getDisabilityCertificate(userId);

        if (certificateBytes == null || certificateBytes.length == 0) {
            return ResponseEntity.ok("등록된 장애인 인증서가 없습니다.");
        }

        // ✅ Base64 인코딩하여 반환 (이미지 또는 파일 형태로 활용 가능)
        String encodedCertificate = Base64.getEncoder().encodeToString(certificateBytes);
        return ResponseEntity.ok(encodedCertificate);
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
    @PutMapping("/users/{userId}/approve-discount")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> approveDiscount(@PathVariable String userId, @RequestBody Map<String, String> request) {
        System.out.println("🟢 [AdminController] 승인 API 호출됨 - User ID: " + userId);
        
        boolean success = adminService.approveDiscount(userId);
        if (success) {
            System.out.println("✅ [AdminController] 승인 완료");
            return ResponseEntity.ok("우대 여부가 승인되었습니다.");
        } else {
            System.out.println("❌ [AdminController] 승인 실패");
            return ResponseEntity.badRequest().body("승인 실패");
        }
    }


}
