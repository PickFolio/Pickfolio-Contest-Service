package com.pickfolio.contest.exception;

public class InvalidStockSymbolException extends RuntimeException {
    public InvalidStockSymbolException(String message) {
        super(message);
    }
}
