package com.pickfolio.contest.exception;

public class InvalidTransactionRequestException extends RuntimeException {
    public InvalidTransactionRequestException(String message) {
        super(message);
    }
}
