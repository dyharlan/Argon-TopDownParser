package com.three_csa.argon.SemAnalyzer.Node;

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