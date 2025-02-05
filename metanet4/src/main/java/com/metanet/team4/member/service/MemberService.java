package com.metanet.team4.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.dto.LoginRequest;
import com.metanet.team4.member.dto.SignupRequest;
import com.metanet.team4.member.mapper.MemberMapper;
import com.metanet.team4.member.model.Member;

import lombok.RequiredArgsConstructor;

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

        Member findUser = memberMapper.findByUserid(request.getUserId());
        if (findUser != null) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        Member member = new Member();
        member.setUserId(request.getUserId());
        member.setName(request.getName());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setPhone(request.getPhone());
        member.setEmail(request.getEmail());
        member.setRole("ROLE_USER"); // 기본 권한

        memberMapper.insertMember(member);
        return "회원가입 성공";
    }

    /**
     * 로그인 (JWT 발급)
     */
    public String loginUser(LoginRequest request) {
        // ✅ 최신 사용자 정보 가져오기
        Member member = memberMapper.findByUserid(request.getUserid());

        if (member == null) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // ✅ 최신 역할 가져오기 (DB에서 강제 조회)
        String latestRole = memberMapper.findByUserid(request.getUserid()).getRole();

        if (latestRole == null) {
            System.out.println("🔴 [오류] 최신 역할(role)이 null입니다. userid: " + request.getUserid());
            throw new RuntimeException("서버 오류: 사용자의 역할이 없습니다.");
        }

        System.out.println("🟢 [로그인 성공] 사용자: " + member.getUserId() + ", 최신 역할: " + latestRole);

        // ✅ 최신 ROLE을 포함한 JWT 발급
        return jwtUtil.generateToken(member.getUserId(), latestRole);
    }

    /**
     * 사용자 조회 (userid로 찾기)
     */
    public Member findByUserid(String userid) {
        Member member = memberMapper.findByUserid(userid);

        if (member == null) {
            System.out.println("🔴 [오류] 존재하지 않는 사용자: " + userid);
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        return member;
    }
}
