package com.pickfolio.contest.exception;

public class UserAlreadyInContestException extends RuntimeException {
    public UserAlreadyInContestException(String message) {
        super(message);
    }
}
