package com.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '%':
                addToken(PERCENTAGE);
                break;
            case '_':
                addToken(UNDERSCORE);
                break;
            case '!':
                addToken(matchAndConsume('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(matchAndConsume('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(matchAndConsume('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(matchAndConsume('=') ? GREATER_EQUAL : GREATER);
                break;

            case '/':
                if (matchAndConsume('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (matchAndConsume('*')) {
                    multiLineComment();
                } else {
                    addToken(SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                line++;
                break;
            case '"':
                string();
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character");
                }
        }
    }

    private void multiLineComment() {
        while (!isAtEnd() && (peek() != '*' || (!isLast() && peekNext() != '/'))) {
            char c = advance();
            if (c == '\n'){
                line++;
            }
            if (c == '/' && !isLast() && peek() == '*') {
                advance();
                multiLineComment();
            }
        }
        if (!isAtEnd()){
            advance();
        }
        if (!isAtEnd()){
            advance();
        }
    }

    private boolean isLast() {
        return current == source.length() - 1;
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        TokenType type = keywords.get(source.substring(start, current));
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
    }

    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            do advance();
            while (isDigit(peek()));
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 > source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated String");
        }
        advance();
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean matchAndConsume(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private void addToken(TokenType token) {
        addToken(token, null);
    }

    private void addToken(TokenType token, Object literal) {
        String text = this.source.substring(start, current);
        tokens.add(new Token(token, text, literal, line));
    }

    boolean isNextLine() {
        return source.charAt(current) == '\n';
    }


    boolean isNumber(char c) {
        return switch (c) {
            case '0' -> true;
            case '1' -> true;
            case '2' -> true;
            case '3' -> true;
            case '4' -> true;
            case '5' -> true;
            case '6' -> true;
            case '7' -> true;
            case '9' -> true;
            case '8' -> true;
            case '.' -> true;
            default -> false;
        };
    }

    boolean isAtEnd() {
        return current >= source.length();
    }

    int getLine() {
        return line;
    }

    private char advance() {
        return this.source.charAt(this.current++);
    }

}