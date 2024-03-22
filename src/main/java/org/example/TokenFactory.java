package org.example;

public class TokenFactory {
    public static Token createToken(TokenTypes type, String value, int line) {
        return new Token(type, value, line);
    }
}
