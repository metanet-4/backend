package com.metanet.team4.file.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.metanet.team4.file.service.MemberFileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/file/member/{id}")
@RequiredArgsConstructor
public class FileController {
    private final MemberFileService memberFileService;

    // 프로필 등록
    @PutMapping("/profile")
    public ResponseEntity<String> uploadProfile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            memberFileService.updateProfile(id, fileData);
            return ResponseEntity.ok("Profile uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile image.");
        }
    }

    // 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<byte[]> getProfile(@PathVariable Long id) {
    	// BLOB 데이터를 Map 형태로 가져오기
        Map<String, Object> imageMap = memberFileService.getProfile(id);
        
        // 이미지가 없는 경우 200 OK + null 반환
        if (imageMap == null) {
            return ResponseEntity.ok().body(null);
        }

        // BLOB 데이터 가져오기
        Object blobObj = imageMap.get("IMAGE");
        byte[] imageBytes = null;

        // BLOB 객체일 경우
        if (blobObj instanceof Blob) {
            try (InputStream inputStream = ((Blob) blobObj).getBinaryStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                imageBytes = outputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
          // 이미 byte[] 타입일 경우
        } else if (blobObj instanceof byte[]) {
            imageBytes = (byte[]) blobObj;
        }

        // imageBytes가 없을 때
        if (imageBytes == null || imageBytes.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "image/*");
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    // 프로필 삭제
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        memberFileService.deleteProfile(id);
        return ResponseEntity.ok("Profile deleted successfully.");
    }

    // 인증서 등록
    @PutMapping("/certificate")
    public ResponseEntity<String> uploadCertificate(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            memberFileService.uploadCertificate(id, fileData);
            return ResponseEntity.ok("Certificate uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload certificate.");
        }
    }

    // 인증서 조회
    @GetMapping("/certificate")
    public ResponseEntity<byte[]> getCertificate(@PathVariable Long id) {
    	// BLOB 데이터를 Map 형태로 가져오기
        Map<String, Object> imageMap = memberFileService.getCertificate(id);
        
        // 이미지가 없는 경우 200 OK + null 반환
        if (imageMap == null) {
            return ResponseEntity.ok().body(null);
        }

        // BLOB 데이터 가져오기
        Object blobObj = imageMap.get("DISABILITY_CERTIFICATE");
        byte[] imageBytes = null;

        // BLOB 객체일 경우
        if (blobObj instanceof Blob) {
            try (InputStream inputStream = ((Blob) blobObj).getBinaryStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                imageBytes = outputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
          // 이미 byte[] 타입일 경우
        } else if (blobObj instanceof byte[]) {
            imageBytes = (byte[]) blobObj;
        }

        // imageBytes가 없을 때
        if (imageBytes == null || imageBytes.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "image/*");
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    // 인증서 삭제
    @DeleteMapping("/certificate")
    public ResponseEntity<String> deleteCertificate(@PathVariable Long id) {
        memberFileService.deleteCertificate(id);
        return ResponseEntity.ok("Certificate deleted successfully.");
    }
}