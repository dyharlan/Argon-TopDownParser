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
                    syntaxError();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    // input/output statements
    void stdio() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "INPUT":
                    stdin();
                    break;
                case "PRINT":
                case "PRINTLN":
                    stdout();
                    break;
                case "PRINTERR":
                    stderr();
                    break;
                default:
                    syntaxError();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void stdin() {
        if (pos < inputList.size()) {
            consume("INPUT");
            consume("OPENPAR");
            content();
            consume("CLOSEPAR");
            consume("SEMICOLON");
        } else {
            syntaxError("End of File Reached");
        }
    }

    void stdout() {
        if (pos < inputList.size()) {
            print_type();
            consume("OPENPAR");
            content();
            consume("CLOSEPAR");
            consume("SEMICOLON");
        } else {
            syntaxError("End of File Reached");
        }
    }

    void stderr() {
        if (pos < inputList.size()) {
            consume("PRINTERR");
            consume("OPENPAR");
            content();
            consume("CLOSEPAR");
            consume("SEMICOLON");
        } else {
            syntaxError("End of File Reached");
        }
    }

    void print_type() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "PRINT":
                    consume("PRINT");
                    break;
                case "PRINTLN":
                    consume("PRINTLN");
                    break;
                default:
                    syntaxError();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void content() {
        if (pos < inputList.size()) {
            switch (lookahead) { // TODO: THIS IS GONNA HAVE PROBLEMS!!
                case "OPENPAR":
                case "IDENT":
                case "SUB":
                case "NUMLIT":
                case "INVERT":
                case "TRUE":
                case "FALSE":
                    boolderiv();
                    break;
                case "IDENT":
                case "SUB":
                case "NUMLIT":
                    numoper();
                    break;
                case "IDENT":
                case "STRLIT":
                    strexpr();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    // string expressions
    void strexpr() {
        if (pos < inputList.size()) {
            strterm();
            strterm_x();
        } else {
            syntaxError("End of File Reached");
        }
    }

    void strterm() {
        if (pos < inputList.size()) {
            consume("IDENT");
            consume("STRLIT");
        } else {
            syntaxError("End of File Reached");
        }
    }

    void strterm_x() {
        if (pos< inputList.size()) {
            if (Objects.equals(lookahead, "ADD")) {
                consume("ADD");
                strterm();
                strterm_x();
            } else {
                // e-production
                return;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    // numerical expressions
    void numoper() {
        if (pos < inputList.size()) {
            term();
            term_x();
        } else {
            syntaxError("End of File Reached");
        }
    }

    void term_x() {
        if (pos < inputList.size()) {
            if (Objects.equals(lookahead, "ADD")) {
                consume("ADD");
                term();
                term_x();
            } else if (Objects.equals(lookahead, "SUB")) {
                consume("SUB");
                term();
                term_x();
            } else {
                return;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void term() {
            if (pos < inputList.size()) {
                factor();
                factor_x();
            } else {
                syntaxError("End of File Reached");
            }
    }

    void factor_x() {
        if (pos < inputList.size()) {
            if (Objects.equals(lookahead, "MUL")) {
                consume("MUL");
                factor();
                factor_x();
            } else if (Objects.equals(lookahead, "DIV")) {
                consume("DIV");
                factor();
                factor_x();
            } else {
                return;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void factor() {
        if (pos < inputList.size()) {
            exponent();
            exponent_x();
        } else {
            syntaxError("End of File Reached");
        }
    }

    void exponent_x() {
        if (pos < inputList.size()) {
            if (Objects.equals(lookahead, "EXP")) {
                consume("EXP");
                exponent();
                exponent_x();
            } else {
                return;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void exponent() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "SUB":
                    consume("SUB");
                    num_final();
                    break;
                case "IDENT":
                case "NUMLIT":
                case "OPENPAR":
                    num_final();
                    break;
                default:
                    syntaxError();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void num_final() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "NUMLIT":
                    numexpr();
                    break;
                case "IDENT":
                    consume("IDENT");
                    break;
                case "OPENPAR":
                    consume("OPENPAR");
                    numoper();
                    consume("CLOSEPAR");
                    break;
                default:
                    syntaxError();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void numexpr() {
        if (pos < inputList.size()) {
            consume("NUMLIT");
        } else {
            syntaxError("End of File Reached");
        }
    }


    // Implement the rest of the methods according to your grammar
}
