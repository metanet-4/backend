package com.metanet.team4.member.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.metanet.team4.member.dto.MemberUpdateRequest;
import com.metanet.team4.member.mapper.MemberMapper;
import com.metanet.team4.member.model.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * ✅ 사용자 정보 조회
     */
    public Member getUserInfo(String userId) {
        Member member = memberMapper.findByUserId(userId);
        if (member == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return member;
    }

    /**
     * ✅ 회원 정보 수정 (이름, 이메일, 비밀번호 변경)
     */
    public void updateUserInfo(MemberUpdateRequest request) {
        // 기존 회원 정보 조회
        Member existingMember = memberMapper.findByUserId(request.getUserId());
        if (existingMember == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 비밀번호 변경 여부 확인
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!request.getPassword().equals(request.getPassword2())) {
                throw new RuntimeException("입력한 비밀번호와 비밀번호 확인이 일치하지 않습니다. 다시 입력해주세요.");
            }
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            memberMapper.updatePassword(request.getUserId(), encodedPassword);
        }

        // 사용자 정보 업데이트
        existingMember.setName(request.getName());
        existingMember.setEmail(request.getEmail());
        memberMapper.updateMember(existingMember);
    }


    /**
     * ✅ 프로필 사진 변경
     */
    public void updateProfilePic(String userId, MultipartFile file) throws IOException {
        String uploadDir = "uploads/profile-pics/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = userId + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        memberMapper.updateProfilePic(userId, filename);
    }

    /**
     * ✅ 사용자 스스로 회원 탈퇴
     */
    public void deleteUser(String userId) {
        memberMapper.deleteMemberById(userId);
    }

    /**
     * ✅ 장애인 인증서 조회
     */
    public String getDisabilityCertificate(String userId) {
        return memberMapper.getDisabilityCertificate(userId);
    }
}
