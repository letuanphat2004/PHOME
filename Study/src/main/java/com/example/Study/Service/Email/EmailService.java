package com.example.Study.Service.Email;

import com.example.Study.Model.DTO.EmailDTO;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService {

    @Autowired

    private JavaMailSender mailSender;
     @Async
    public void sendEmail(EmailDTO emailDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(emailDTO.getTo());
            helper.setSubject(emailDTO.getSubject());
            helper.setText(emailDTO.getBody(), true);
            mailSender.send(message);
            log.info("Success");
        } catch (MessagingException e) {
            log.info(e+""); // You should handle exceptions properly in a real application
        }
    }
}
