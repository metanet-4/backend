package com.metanet.team4.member.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    // ✅ 업로드할 디렉토리 경로 (서버 내부 경로)
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * ✅ 파일 저장 메서드
     * @param file 업로드할 파일
     * @return 저장된 파일 경로
     * @throws IOException
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("업로드된 파일이 없습니다.");
        }

        // 📌 파일명 중복 방지를 위해 UUID 사용
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자 추출
        String uniqueFilename = UUID.randomUUID() + extension;

        // 📌 저장할 경로 설정
        String filePath = UPLOAD_DIR + uniqueFilename;
        File dest = new File(filePath);

        // 📌 디렉토리가 없으면 생성
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        // 📌 파일 저장
        file.transferTo(dest);

        System.out.println("🟢 파일 저장 완료: " + filePath);
        return filePath;
    }

    /**
     * ✅ 파일 삭제 메서드
     * @param filePath 삭제할 파일 경로
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("🔴 파일 삭제 중 오류 발생: " + filePath);
            return false;
        }
    }
}
