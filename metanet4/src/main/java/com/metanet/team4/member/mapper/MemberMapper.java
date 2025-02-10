package com.metanet.team4.member.mapper;

import com.metanet.team4.member.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.InputStream;
import java.util.List;

@Mapper
public interface MemberMapper {

    // ✅ userId로 회원 정보 조회
    Member findByUserId(@Param("userId") String userId);

    // ✅ 회원 가입 (새로운 필드 추가)
    int insertMember(Member member);

    // ✅ userId 중복 확인
    int countByUserId(@Param("userId") String userId);

    // ✅ phone 중복 확인
    int countByPhone(@Param("phone") String phone);

    // ✅ 전체 사용자 조회 (관리자 전용)
    List<Member> selectAllMembers();

    // ✅ 사용자 권한 변경 (관리자 전용)
    void updateUserRole(@Param("userId") String userId, @Param("role") String role);

    // ✅ 사용자 삭제 (관리자 & 사용자 탈퇴)
    void deleteMemberById(@Param("userId") String userId);

    // ✅ 사용자 정보 업데이트 (새로운 필드 반영)
    void updateMember(Member member);

    // ✅ 사용자 비밀번호 변경
    void updatePassword(@Param("userId") String userId, @Param("password") String password);

    // ✅ 사용자 프로필 사진 변경 (BLOB 데이터 처리)
    void updateProfilePic(@Param("userId") String userId, @Param("image") byte[] image);

    // ✅ 장애인 인증서 파일 저장 (BLOB 데이터 처리)
    void updateDisabilityCertificate(@Param("userId") String userId, @Param("disabilityCertificate") byte[] disabilityCertificate);

    // ✅ 관리자: 우대 여부 승인 (is_discounted = 1로 변경)
    int updateDiscountStatus(@Param("userId") String userId, @Param("status") int status);

    // ✅ 장애인 인증서 파일 조회 (쿼리는 XML에서 처리)
    InputStream getDisabilityCertificate(@Param("userId") String userId);
}
