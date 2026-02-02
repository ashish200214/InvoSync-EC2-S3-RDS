package com.asent.invoSync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmailService {
    @Autowired private JavaMailSender mailSender;

    public void sendQuotationLinks(String toEmail, String subject, String body, List<String> fileUrls){
        if(toEmail==null || toEmail.isBlank()) {
            System.err.println("Email missing, skipping.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(body).append("\n\n");
        if(fileUrls!=null) fileUrls.forEach(u->sb.append(u).append("\n"));
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(sb.toString());
        try{ mailSender.send(msg); } catch(Exception ex){ ex.printStackTrace(); }
    }
}
