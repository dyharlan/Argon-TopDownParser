package com.three_csa.argon.Tokenizer;

public enum TokenType {
    // single character tokens (0-7)
    ASSIGN, COMMA, SEMICOLON, OPENPAR,
    CLOSEPAR, OPENBR, CLOSEBR, INVERT,

    // possibly two character tokens (8-23)
    ADD, ADDASSIGN,
    SUB, SUBASSIGN, RIGHTARROW,
    MUL, MULASSIGN,
    DIV, DIVASSIGN,
    EXP, EXPASSIGN,
    GT, GTE,
    LT, LTE,

    // logic operators (24-27)
    IS, NOT, AND, OR,

    // escape sequences (28-35)
    NEWLINE, HORZTAB,
    CARGRET, BACKSPC,
    BACKSLSH, SINGQUOT,
    DOUBQUOT, FORMFEED,

    // literals (36-39)
    IDENT, NUMLIT, STRLIT, BOOLLIT,

    // reserved words (40-56)
    CATALYZE, DECOMPOSE, DISTILL, FUNNEL, FILTER,
    FERMENT, FALSE, INERT, INPUT, MOLE32, MOLE64,
    PRINT, PRINTLN, PRINTERR, REACTIVE, TRUE, UNTIL, WHEN,

    // end of code (57)
    EOF
}
