package com.vlad.buildrent.service.email;

import com.vlad.buildrent.domain.Rental;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!prod")
@RequiredArgsConstructor
public class LoggingEmailService implements EmailService {

    private final EmailTemplateRenderer renderer;

    @Override
    public void sendOrderCreated(Rental rental) {
        log("Замовлення створено", "order-created", rental);
    }

    @Override
    public void sendOrderConfirmed(Rental rental) {
        log("Замовлення підтверджено", "order-confirmed", rental);
    }

    @Override
    public void sendOrderPaid(Rental rental) {
        log("Замовлення оплачено", "order-paid", rental);
    }

    @Override
    public void sendOrderReturned(Rental rental) {
        log("Дякуємо за повернення обладнання", "order-returned", rental);
    }

    @Override
    public void sendOrderCancelled(Rental rental) {
        log("Замовлення скасовано", "order-cancelled", rental);
    }

    private void log(String subject, String template, Rental rental) {
        String body = renderer.render(template, rental, subject);
        log.info("\n========== EMAIL ==========\nTo: {}\nSubject: BuildRent — {} (№ {})\n{}\n===========================",
                rental.getClient().getEmail(), subject, rental.getOrderNumber(), body);
    }
}
