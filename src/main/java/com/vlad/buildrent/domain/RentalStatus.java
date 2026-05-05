package com.vlad.buildrent.domain;

public enum RentalStatus {
    PENDING,
    CONFIRMED,
    PAID,
    ACTIVE,
    RETURNED,
    CANCELLED;

    public boolean canTransitionTo(RentalStatus next) {
        return switch (this) {
            case PENDING   -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == PAID || next == CANCELLED;
            case PAID      -> next == ACTIVE || next == CANCELLED;
            case ACTIVE    -> next == RETURNED;
            case RETURNED, CANCELLED -> false;
        };
    }

    public boolean blocksAvailability() {
        return this == CONFIRMED || this == PAID || this == ACTIVE;
    }
}
