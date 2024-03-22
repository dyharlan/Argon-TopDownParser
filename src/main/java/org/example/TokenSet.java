package org.example;

import org.example.Token;
import org.example.TokenFactory;

import java.util.HashSet;

public class TokenSet {
    private HashSet<Token> tokens;

    public TokenSet() {
        this.tokens = new HashSet<>();
    }

    public void addToken(TokenTypes type, String value, int line) {
        this.tokens.add(TokenFactory.createToken(type, value, line));
    }

    public boolean containsToken(Token token) {
        return this.tokens.contains(token);
    }

    public void printTokens() {
        int i = 1;
        for (Token token : this.tokens) {
            System.out.printf("token %-4d | type: %-10s | value: %-30s | line: %-4d (first occurence)%n",
                    i, token.getType().toString(), token.sanitizedValue(), token.getLine());
            i++;
        }
    }
}