package com.geremere.two_factor_auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final Random rand = new Random();
    private final String TEXT = "Dear %s,\n\n" +
            "Your one-time code is: **%s**. Please use this code within the next 5 minutes for verification.\n\n" +
            "If you didn't request this code, you can safely ignore this email.\n\n" +
            "Best Regards,\n" +
            "Awesome company";


    public String sendSimpleMail(String username, String mail) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("rad.imamow@yandex.ru");
        msg.setTo(mail);
        msg.setSubject("One Time Code");
        String code = String.valueOf(rand.nextInt(100000, 999999));
        msg.setText(String.format(TEXT, username, code));
        mailSender.send(msg);
        return code;
    }
}
