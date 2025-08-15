package com.pickfolio.contest.exception;

public class ContestNotOpenException extends RuntimeException {
    public ContestNotOpenException(String message) {
        super(message);
    }
}
