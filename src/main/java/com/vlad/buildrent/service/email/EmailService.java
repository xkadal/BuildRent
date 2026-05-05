package com.vlad.buildrent.service.email;

import com.vlad.buildrent.domain.Rental;

public interface EmailService {

    void sendOrderCreated(Rental rental);

    void sendOrderConfirmed(Rental rental);

    void sendOrderPaid(Rental rental);

    void sendOrderReturned(Rental rental);

    void sendOrderCancelled(Rental rental);
}
