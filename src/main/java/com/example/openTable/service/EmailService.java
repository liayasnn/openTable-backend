package com.example.openTable.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

//Send confirmation email
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String to, String orderNumber) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Order Confirmation - " + orderNumber);

            String emailContent = "<html><body>"
                    + "<h2>Thank you for your recent purchase with OpenTable!</h2>"
                    + "<p>Dear Customer,</p>"
                    + "<p>Your order number is <strong>" + orderNumber + "</strong>.</p>"
                    + "<p>We are currently processing your order, and you will receive a notification once your order has been shipped.</p>"
                    + "<p>If you have any questions or need further assistance, please do not hesitate to contact our support team.</p>"
                    + "<p>Thank you for contributing to OpenTable. </p>"
                    + "<br>"
                    + "<p>Best regards,</p>"
                    + "<p>OpenTable Support Team</p>"
                    + "</body></html>";

            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send order confirmation email", e);
        }
    }
    
}