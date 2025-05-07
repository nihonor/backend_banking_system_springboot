package com.atmSim.atm.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendWithdrawalEmail(String to, double amount, double balance) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();
        context.setVariables(Map.of(
                "amount", amount,
                "balance", balance,
                "timestamp", LocalDateTime.now().toString()
        ));

        String htmlContent = templateEngine.process("withdrawal-notification.html", context);

        helper.setTo(to);
        helper.setSubject("ATM Withdrawal Confirmation");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
