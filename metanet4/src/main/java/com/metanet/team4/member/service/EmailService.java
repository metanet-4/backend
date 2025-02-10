package com.metanet.team4.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 인증번호 생성 메서드
    public String generateAuthCode() {
        Random random = new Random();
        int authCode = 100000 + random.nextInt(900000); // 6자리 랜덤 숫자
        return String.valueOf(authCode);
    }

    // 이메일 전송 메서드
    public void sendAuthCode(String toEmail, String authCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[META THEATRE] 이메일 인증번호 안내");
        String emailBody = "안녕하세요, META THEATRE입니다.\n\n"
                + "META THEATRE 회원가입을 진행하려면 아래 인증번호를 입력해주세요.\n\n"
                + "📌 이메일 인증번호: [ " + authCode + " ]\n\n";

        message.setText(emailBody);

        mailSender.send(message);
    }
}
