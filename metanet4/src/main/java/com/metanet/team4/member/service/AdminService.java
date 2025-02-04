package com.metanet.team4.member.service;

import com.metanet.team4.member.mapper.member.MemberMapper;
import com.metanet.team4.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberMapper memberMapper;

    /**
     * 전체 사용자 조회
     */
    public List<Member> getAllUsers() {
        return memberMapper.selectAllMembers();
    }

    /**
     * 사용자 권한 변경
     */
    public void updateUserRole(String userid, String role) {
        Member user = memberMapper.findByUserid(userid);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + userid);
        }

        memberMapper.updateUserRole(userid, role);
        System.out.println("🟢 사용자 권한 변경: " + userid + " -> " + role);
    }

    /**
     * 사용자 삭제
     */
    public void deleteUser(String userid) {
        Member user = memberMapper.findByUserid(userid);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + userid);
        }

        memberMapper.deleteMemberById(userid);
        System.out.println("🟢 사용자 삭제: " + userid);
    }
}
