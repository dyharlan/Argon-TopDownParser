package com.three_csa.argon.Tokenizer;

public class Token {
    // private String name;
    private TokenType type;
    private String value;
    private int line;

    public Token(TokenType type, String value, int line) {
        // this.name = name;
        this.type = type;
        this.value = value;
        this.line = line;
    }

    // getters and setters
    /*
     * deprecate name field
     * public String getName() {
     * return name;
     * }
     * 
     * public void setName(String name) {
     * this.name = name;
     * }
     */

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLine() {
        return line;
    }

    public String sanitizedValue() {
        if (this.value == null)
            return null;
        return this.value.toString()
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
                .replace("\r", "\\r")
                .replace("\b", "\\b")
                //.replace("\'", "\\\'")
                .replace("\"", "\\\"")
                .replace("\f", "\\f");
    }

    // overrides for the tokenset
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Token other = (Token) obj;
        if (type != other.type)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
