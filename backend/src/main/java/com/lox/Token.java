package com.lox;

import java.util.Objects;

public class Token {
    final int line;
    final String lexeme;
    final TokenType type;
    final Object literal;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.line = line;
        this.lexeme = lexeme;
        this.literal = literal;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return line == token.line && Objects.equals(lexeme, token.lexeme) && type == token.type && Objects.equals(literal, token.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, lexeme, type, literal);
    }
}
