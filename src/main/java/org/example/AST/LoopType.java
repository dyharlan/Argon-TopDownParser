package org.example.AST;

public enum LoopType {
    DISTILL,
    FERMENT;

    public String toString() {
        return switch(this) {
            case DISTILL -> "DISTILL";
            case FERMENT -> "FERMENT";
        };
    }
}