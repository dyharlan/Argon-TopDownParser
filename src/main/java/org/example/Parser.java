package org.example;

import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    ArrayList<String> inputList;
    int pos = 0;
    String lookahead;

    public Parser(ArrayList<String> inputList) {
        this.inputList = inputList;
        lookahead = inputList.getFirst();
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
            lookahead = inputList.get(pos);
        } else {
            throw new RuntimeException("Expected " + str + " but found " + inputList.get(pos));
        }
    }

    void syntaxError(String message) {
        System.out.println("Syntax Error: " + message);
        System.exit(0);
    }

    void syntaxError() {
        System.out.println("Syntax Error");
        System.exit(0);
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

            }
        } else {
            System.out.println("Syntax Error: End of File Reached");
            System.exit(0);
        }
    }

    void simple_statement() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                    mut_type();
                    break;
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                    stdio();
                default:
                    System.out.println("Syntax Error");
                    System.exit(0);
            }
        } else {
            System.out.println("Syntax Error: End of File Reached");
            System.exit(0);
        }
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

    void assign_oper() {

    }



    // Implement the rest of the methods according to your grammar
}
