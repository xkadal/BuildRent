package com.vlad.buildrent.service.email;

import com.vlad.buildrent.domain.Rental;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateRenderer renderer;

    @Value("${buildrent.mail.from:noreply@buildrent.pp.ua}")
    private String from;

    @Override
    public void sendOrderCreated(Rental rental) {
        send(rental, "Замовлення створено", "order-created");
    }

    @Override
    public void sendOrderConfirmed(Rental rental) {
        send(rental, "Замовлення підтверджено", "order-confirmed");
    }

    @Override
    public void sendOrderPaid(Rental rental) {
        send(rental, "Замовлення оплачено", "order-paid");
    }

    @Override
    public void sendOrderReturned(Rental rental) {
        send(rental, "Дякуємо за повернення обладнання", "order-returned");
    }

    @Override
    public void sendOrderCancelled(Rental rental) {
        send(rental, "Замовлення скасовано", "order-cancelled");
    }

    private void send(Rental rental, String subject, String template) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(rental.getClient().getEmail());
            helper.setSubject("BuildRent — " + subject + " (№ " + rental.getOrderNumber() + ")");
            helper.setText(renderer.render(template, rental, subject), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.warn("Не вдалося надіслати email на {}: {}", rental.getClient().getEmail(), e.getMessage());
        }
    }
}
