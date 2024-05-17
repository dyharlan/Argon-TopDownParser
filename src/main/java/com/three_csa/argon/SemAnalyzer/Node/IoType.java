package com.three_csa.argon.SemAnalyzer.Node;


public enum IoType {
    PRINT,
    PRINTLN,
    PRINTERR,
    INPUT;
    public String toString() {
        return switch (this) {
            case PRINT -> "PRINT";
            case PRINTLN -> "PRINTLN";
            case PRINTERR -> "PRINTERR";
            case INPUT -> "INPUT";
        };
    }
}

