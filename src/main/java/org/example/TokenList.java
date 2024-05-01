package org.example;

import java.util.ArrayList;
import java.util.List;

public class TokenList {
    private ArrayList<Token> tokens;

    public TokenList() {
        this.tokens = new ArrayList<>();
    }

    public void addToken(Token token) {
        this.tokens.add(token);
    }

    public void addToken(TokenType type, String value, int line) {
        this.tokens.add(TokenFactory.createToken(type, value, line));
    }

    public int findTokenExist(Token token) {
        int i = 0;
        for (Token t : this.tokens) {
            // if value and type is the same
            if (t.getValue().equals(token.getValue()) && t.getType().equals(token.getType())) {
                // System.out.println("MATCH FOUND");
                return i;
            }
            i++;
        }
        return -1;
    }

    public ArrayList<Token> getTokens() {
        return this.tokens;
    }

    public int getSize() {
        return this.tokens.size();
    }

    public Token getLatestToken() {
        return this.tokens.get(getSize() - 1);
    }

    public Token getToken(int x) {
        return tokens.get(x);
    }
    public List<Token> getTokenList(){
        return this.tokens;
    }
    public void printTokens() {
        int i = 1;
        for (Token token : this.tokens) {
            System.out.printf("token %-4d | type: %-10s | value: %-30s | line: %-4d %n",
                    i, token.getType().toString(), token.sanitizedValue(), token.getLine());
            i++;
        }
    }
}
