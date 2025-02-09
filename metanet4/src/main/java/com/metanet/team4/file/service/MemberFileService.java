package com.metanet.team4.file.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.team4.file.mapper.MemberFileMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberFileService {
    private final MemberFileMapper memberFileMapper;

    // 프로필 등록
    @Transactional
    public void updateProfile(Long id, byte[] image) {
        memberFileMapper.updateProfile(id, image);
    }

    // 프로필 조회
    public Map<String,Object> getProfile(Long id) {
        return memberFileMapper.getProfile(id);
    }

    // 프로필 삭제
    @Transactional
    public void deleteProfile(Long id) {
        memberFileMapper.deleteProfile(id);
    }

    // 인증서 등록
    @Transactional
    public void uploadCertificate(Long id, byte[] disabilityCertificate) {
        memberFileMapper.uploadCertificate(id, disabilityCertificate);
    }

    // 인증서 조회
    public Map<String,Object> getCertificate(Long id) {
        return memberFileMapper.getCertificate(id);
    }

    // 인증서 삭제
    @Transactional
    public void deleteCertificate(Long id) {
        memberFileMapper.deleteCertificate(id);
    }
}
