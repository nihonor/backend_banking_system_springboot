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

    public void sendTransactionNotification(String to, String transactionType, double amount, double balance,
            String fromAccount, String toAccount) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();
        context.setVariables(Map.of(
                "transactionType", transactionType,
                "amount", amount,
                "balance", balance,
                "fromAccount", fromAccount != null ? fromAccount : "N/A",
                "toAccount", toAccount != null ? toAccount : "N/A",
                "timestamp", LocalDateTime.now().toString()));

        String template = getTemplateForTransactionType(transactionType);
        String htmlContent = templateEngine.process(template, context);

        helper.setTo(to);
        helper.setSubject(getSubjectForTransactionType(transactionType));
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String getTemplateForTransactionType(String transactionType) {
        return switch (transactionType.toUpperCase()) {
            case "DEPOSIT" -> "deposit-notification.html";
            case "WITHDRAW" -> "withdrawal-notification.html";
            case "TRANSFER" -> "transfer-notification.html";
            default -> "transaction-notification.html";
        };
    }

    private String getSubjectForTransactionType(String transactionType) {
        return switch (transactionType.toUpperCase()) {
            case "DEPOSIT" -> "ATM Deposit Confirmation";
            case "WITHDRAW" -> "ATM Withdrawal Confirmation";
            case "TRANSFER" -> "ATM Transfer Confirmation";
            default -> "ATM Transaction Confirmation";
        };
    }
}
