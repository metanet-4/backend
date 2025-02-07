package com.metanet.team4.member.service;

import com.metanet.team4.member.mapper.MemberMapper;
import com.metanet.team4.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberMapper memberMapper;

    /**
     * ✅ 모든 사용자 조회 (관리자 전용)
     */
    public List<Member> getAllUsers() {
        return memberMapper.selectAllMembers();
    }

    /**
     * ✅ 장애인 인증서 조회 (관리자 전용)
     */
    public byte[] getDisabilityCertificate(String userId) {
        InputStream inputStream = memberMapper.getDisabilityCertificate(userId);
        if (inputStream == null) {
            return new byte[0];  // ✅ NULL 방지
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();  // ✅ InputStream을 byte[]로 변환
        } catch (IOException e) {
            throw new RuntimeException("장애인 인증서 변환 오류", e);
        }
    }

    /**
     * ✅ 사용자 권한 변경 (관리자 전용)
     */
    public void updateUserRole(String userId, String role) {
        Member user = memberMapper.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + userId);
        }

        memberMapper.updateUserRole(userId, role);
        System.out.println("🟢 사용자 권한 변경: " + userId + " -> " + role);
    }

    /**
     * ✅ 사용자 삭제 (관리자 전용)
     */
    public void deleteUser(String userid) {
        Member user = memberMapper.findByUserId(userid);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + userid);
        }

        memberMapper.deleteMemberById(userid);
        System.out.println("🟢 사용자 삭제: " + userid);
    }

    /**
     * ✅ 관리자가 우대 여부 승인 (is_discounted = 1로 변경)
     */
    public boolean approveDiscount(String userId) {
        int updatedRows = memberMapper.approveDiscount(userId);
        return updatedRows > 0; // 성공 여부 반환
    }

}
