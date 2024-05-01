package org.example;

public class TokenFactory {
    public static Token createToken(TokenType type, String value, int line) {
        return new Token(type, value, line);
    }
}
