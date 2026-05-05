package com.vlad.buildrent.exception;

public class NotEnoughAvailabilityException extends RuntimeException {
    public NotEnoughAvailabilityException(String equipmentName, int requested, int available) {
        super("Недостатньо одиниць «" + equipmentName + "»: запит " + requested + ", доступно " + available);
    }
}
