package com.metanet.team4.file.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberFileMapper {
    // 프로필 등록, 조회, 삭제
    void updateProfile(@Param("id") Long id, @Param("image") byte[] image);
    Map<String,Object> getProfile(@Param("id") Long id);
    void deleteProfile(@Param("id") Long id);
    
    // 인증서 등록, 조회, 삭제
    void uploadCertificate(@Param("id") Long id, @Param("disabilityCertificate") byte[] disabilityCertificate);
    Map<String,Object> getCertificate(@Param("id") Long id);
    void deleteCertificate(@Param("id") Long id);
}
