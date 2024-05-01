package org.example.AST;


public enum PrintType {
    PRINT,
    PRINTLN,
    PRINTERR;
    public String toString() {
        return switch (this) {
            case PRINT -> "PRINT";
            case PRINTLN -> "PRINTLN";
            case PRINTERR -> "PRINTERR";
        };
    }
}

