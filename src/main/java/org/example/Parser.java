package org.example;

import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    ArrayList<String> inputList;
    int pos = 0;
    String lookahead;

    public Parser(ArrayList<String> inputList) {
        this.inputList = inputList;
    }

    void parse() {
        program();
        if (Objects.equals(lookahead, "EOF")) {
            System.out.println("Accepted");
        } else {
            System.out.println("Error");
        }
    }

    void consume(String str) {
        if (pos < inputList.size() && Objects.equals(inputList.get(pos), str)) {
            pos++;
        } else {
            throw new RuntimeException("Expected " + str + " but found " + inputList.get(pos));
        }
    }

    void program() {
        statements();
    }

    void statements() {
        statement();
        statements_x();
    }

    void statements_x() {
        if (pos < inputList.size() && Objects.equals(inputList.get(pos), "SEMICOLON")) {
            statement();
            statements_x();
        }
        // else ε (do nothing)
    }

    void statement() {
        // Implement according to your grammar
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                    simple_statement();
                    break;
                case "FILTER":
                case "WHEN":
                case "FERMENT":
                case "DISTILL":
                    compound_statement();
                default:
                    System.out.println("Syntax Error");
                    System.exit(0);
            }
        }
    }

    void simple_statement() {
        vardeclare();
        stdio();
    }

    void compound_statement() {
        cond_stmt();
        ferment_stmt();
        distill_stmt();
    }

    void vardeclare() {
        mut_type();
        numtype();
        ident();
        assignment();
        semicolon();
    }

    void mut_type() {
        reactive();
        inert();
    }

    void numtype() {
        mole64();
        mole32();
    }

    void assignment() {
        if () {
            assign_oper();
        }


    }

    // Implement the rest of the methods according to your grammar
}
