package com.vlad.buildrent.exception;

public class EmailAlreadyTakenException extends RuntimeException {
    public EmailAlreadyTakenException(String email) {
        super("Email вже використовується: " + email);
    }
}
