package com.metanet.team4.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.dto.LoginRequest;
import com.metanet.team4.member.dto.SignupRequest;
import com.metanet.team4.member.mapper.MemberMapper;
import com.metanet.team4.member.model.Member;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    public String registerUser(SignupRequest request) {
        if (!request.getPassword().equals(request.getPassword2())) {
            throw new RuntimeException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // ✅ 아이디 중복 검사
        Member findUser = memberMapper.findByUserId(request.getUserId());
        if (findUser != null) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        // ✅ 이메일 중복 검사 (추가 가능)
        // Member findByEmail = memberMapper.findByEmail(request.getEmail());
        // if (findByEmail != null) {
        //    throw new RuntimeException("이미 사용 중인 이메일입니다.");
        // }

        Member member = new Member();
        member.setUserId(request.getUserId());
        member.setName(request.getName());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setPhone(request.getPhone());
        member.setEmail(request.getEmail());
        member.setRole("ROLE_USER"); // 기본 권한
        member.setBirthday(request.getBirthday()); // ✅ 생일 반영
        member.setGender(request.getGender());     // ✅ 성별 반영

        // ✅ 장애인 인증서 파일 처리
        if (request.getDisabilityCertificate() != null) {
            try {
                MultipartFile file = request.getDisabilityCertificate();
                byte[] fileBytes = file.getBytes();
                String encodedFile = Base64.getEncoder().encodeToString(fileBytes);
                member.setDisabilityCertificate(encodedFile);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류 발생");
            }
        }

        memberMapper.insertMember(member);
        return "회원가입 성공";
    }

    /**
     * 로그인 (JWT 발급)
     */
    public String loginUser(LoginRequest request) {
        // ✅ 최신 사용자 정보 가져오기
        Member member = memberMapper.findByUserId(request.getUserId());

        if (member == null) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // ✅ 최신 역할 가져오기 (DB에서 강제 조회)
        String latestRole = memberMapper.findByUserId(request.getUserId()).getRole();

        if (latestRole == null) {
            System.out.println("🔴 [오류] 최신 역할(role)이 null입니다. userId: " + request.getUserId());
            throw new RuntimeException("서버 오류: 사용자의 역할이 없습니다.");
        }

        System.out.println("🟢 [로그인 성공] 사용자: " + member.getUserId() + ", 최신 역할: " + latestRole);

        // ✅ 최신 ROLE을 포함한 JWT 발급
        return jwtUtil.generateToken(member.getUserId(), latestRole);
    }

    /**
     * 사용자 조회 (userId로 찾기)
     */
    public Member findByUserId(String userId) {
        System.out.println("🟢 [DEBUG] findByUserId 호출됨 - userId: " + userId);
        Member member = memberMapper.findByUserId(userId);

        if (member != null) {
            System.out.println("🟢 [DEBUG] 조회된 Member 객체: " + member);
            System.out.println("🟢 [DEBUG] 조회된 Member userId: " + member.getUserId());
        } else {
            System.out.println("🔴 [ERROR] Member 조회 실패! userId: " + userId);
        }

        return member;
    }


}
