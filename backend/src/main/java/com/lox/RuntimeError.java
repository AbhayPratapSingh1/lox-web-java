package com.lox;

public class RuntimeError extends RuntimeException {

    final Token token;

    public RuntimeError(Token token, String message) {
        this.token = token;
        super(message);
    }
}
