package com.metanet.team4.member.service;

import com.metanet.team4.member.dto.PasswordChangeRequest;
import com.metanet.team4.member.mapper.MemberMapper;
import com.metanet.team4.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * ✅ 사용자 정보 조회
     */
    public Member getUserInfo(String userid) {
        Member member = memberMapper.findByUserid(userid);
        if (member == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return member;
    }

    /**
     * ✅ 비밀번호 변경 로직
     */
    public void changePassword(String userid, PasswordChangeRequest request) {
        Member member = memberMapper.findByUserid(userid);

        if (member == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호 암호화 후 저장
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        memberMapper.updatePassword(userid, encodedNewPassword);
    }

    /**
     * ✅ 프로필 사진 변경 로직
     */
    public void updateProfilePic(String userid, MultipartFile file) throws IOException {
        String uploadDir = "uploads/profile-pics/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = userid + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        memberMapper.updateProfilePic(userid, filename);
    }

    /**
     * ✅ 사용자 스스로 회원 탈퇴 로직 (DB에서 계정 삭제)
     */
    public void deleteUser(String userid) {
        memberMapper.deleteMemberById(userid);
    }
}
