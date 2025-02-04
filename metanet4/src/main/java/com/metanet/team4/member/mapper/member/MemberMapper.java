package com.metanet.team4.member.mapper.member;

import com.metanet.team4.member.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {

    // ✅ userid로 회원 정보 조회
    Member findByUserid(@Param("userid") String userid);

    // ✅ 회원 가입
    int insertMember(Member member);

    // ✅ 전체 사용자 조회 (관리자 전용)
    List<Member> selectAllMembers();

    // ✅ 사용자 권한 변경 (관리자 전용)
    void updateUserRole(@Param("userid") String userid, @Param("role") String role);

    // ✅ 사용자 삭제 (관리자 & 사용자 탈퇴)
    void deleteMemberById(@Param("userid") String userid);

    // ✅ 사용자 정보 업데이트
    void updateMember(Member member);

    // ✅ 사용자 비밀번호 변경
    void updatePassword(@Param("userid") String userid, @Param("password") String password);

    // ✅ 사용자 프로필 사진 변경
    void updateProfilePic(@Param("userid") String userid, @Param("filename") String filename);
}
