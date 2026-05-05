package com.vlad.buildrent.exception;

import com.vlad.buildrent.domain.RentalStatus;

public class IllegalStateTransitionException extends RuntimeException {
    public IllegalStateTransitionException(RentalStatus from, RentalStatus to) {
        super("Неможливий перехід статусу: " + from + " → " + to);
    }

    public IllegalStateTransitionException(String message) {
        super(message);
    }
}
