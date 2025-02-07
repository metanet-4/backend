package com.metanet.team4.file.controller;

import com.metanet.team4.file.service.MemberFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final MemberFileService memberFileService;

    // 프로필 등록
    @PostMapping("/profile")
    public ResponseEntity<String> uploadProfile(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            memberFileService.updateProfile(id, fileData);
            return ResponseEntity.ok("Profile uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile image.");
        }
    }

    // 프로필 조회
    @GetMapping("/profile/{id}")
    public ResponseEntity<byte[]> getProfile(@PathVariable Long id) {
        byte[] image = memberFileService.getProfile(id);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.IMAGE_JPEG_VALUE); // 이미지 타입 지정
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    // 프로필 삭제
    @DeleteMapping("/profile/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        memberFileService.deleteProfile(id);
        return ResponseEntity.ok("Profile deleted successfully.");
    }

    // 인증서 업로드
    @PostMapping("/certificate")
    public ResponseEntity<String> uploadCertificate(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            memberFileService.uploadCertificate(id, fileData);
            return ResponseEntity.ok("Certificate uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload certificate.");
        }
    }

    // 인증서 조회
    @GetMapping("/certificate/{id}")
    public ResponseEntity<byte[]> getCertificate(@PathVariable Long id) {
        byte[] certificate = memberFileService.getCertificate(id);
        if (certificate == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.IMAGE_JPEG_VALUE);
        return new ResponseEntity<>(certificate, headers, HttpStatus.OK);
    }

    // 인증서 삭제
    @DeleteMapping("/certificate/{id}")
    public ResponseEntity<String> deleteCertificate(@PathVariable Long id) {
        memberFileService.deleteCertificate(id);
        return ResponseEntity.ok("Certificate deleted successfully.");
    }
}
