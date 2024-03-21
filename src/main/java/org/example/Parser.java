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
                    break;
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
                    vardeclare();
                    break;
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                    stdio();
                    break;
                default:
                    syntaxError();
                    break;
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
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void compound_statement() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "FILTER":
                case "WHEN":
                    cond_stmt();
                    break;
                case "FERMENT":
                    ferment_stmt();
                    break;
                case "DISTILL":
                    distill_stmt();
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void vardeclare() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                    mut_type();
                    numtype();
                    consume("IDENT");
                    assignment();
                    consume("SEMICOLON");
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
//        mut_type();
//        numtype();
//        ident();
//        assignment();
//        semicolon();
    }

    void mut_type() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                    consume("REACTIVE");
                    break;
                case "INERT":
                    consume("INERT");
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void numtype() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "MOLE32":
                    consume("MOLE32");
                    break;
                case "MOLE64":
                    consume("MOLE64");
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void assignment() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "ASSIGN":
                case "ADDASSIGN":
                case "SUBASSIGN":
                case "MULASSIGN":
                case "DIVASSIGN":
                case "EXPASSIGN":
                    assign_oper();
                    assign_after();
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void assign_oper() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "ASSIGN":
                    consume("ASSIGN");
                    break;
                case "ADDASSIGN":
                    consume("ADDASSIGN");
                    break;
                case "SUBASSIGN":
                    consume("SUBASSIGN");
                    break;
                case "MULASSIGN":
                    consume("MULASSIGN");
                    break;
                case "DIVASSIGN":
                    consume("DIVASSIGN");
                    break;
                case "EXPASSIGN":
                    consume("EXPASSIGN");
                    break;
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

    void assign_after() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "SUB":
                case "NUMLIT":
                case "IDENT": //conflict with IDENT
                case "OPENPAR":
                    numoper();
                    break;
                //case "IDENT": //conflict with IDENT
                    //consume("IDENT");
                    //break;
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

    //NOTE num_final is equivalent to FINAL in the grammar
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

    //Logic and Relations
    void boolderiv() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void boolderiv_x() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void logicop() { //logic operation
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void condition() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void nonbool_rel() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void nonbool_operand() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void bool_rel() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void bool_expr() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void bool_operand() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void relation() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void eq_rel() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    void size_rel() {
        if (pos < inputList.size()) {

        } else {
            syntaxError("End of File Reached");
        }
    }

    //Control and Iterative Statements
    void cond_stmt() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "FILTER":
                    filter_stmt();
                    break;
                case "WHEN":
                    when_stmt();
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void filter_stmt() {
        if (pos < inputList.size()) {
            if (lookahead.equals("FILTER")) {
                filter_expr();
                funnel_expr();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void filter_expr() {
        if (pos < inputList.size()) {
            if (lookahead.equals("FILTER")) {
                consume("FILTER");
                consume("OPENPAR");
                boolderiv();
                consume("CLOSEPAR");
                cond_body();
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
    }

    void cond_body() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE": //from here
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR": //to here is from simple_stmt
                    simple_statement();
                    break;
                case "OPENBR":
                    consume("OPENBR");
                    body_x();
                    consume("CLOSEBR");
                    break;
                default:
                    syntaxError();
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void funnel_expr() {
        if (pos < inputList.size()) {
            if (lookahead.equals("FUNNEL")) {
                consume("FUNNEL");
                funnel_right();
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
        //funnel();
        //funnel_right();
    }

    void funnel_right() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                case "OPENBR": //cond_body
                    cond_body();
                    break;
                case "FILTER":
                    filter_stmt();
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        //cond_body();
        //filter_stmt();
    }

    void body_x() {
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                case "FILTER":
                case "WHEN":
                case "FERMENT":
                case "DISTILL":
                    body();
                    body_x();
                    break;
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
//        body();
//        body_x();
//        emptystr();
    }

    void body() { //note: exactly the same as STATEMENT()
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
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void when_stmt() {
        if (pos < inputList.size()) {
            if (lookahead.equals("WHEN")) {
                when_before();
                when_after();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void when_before() {
        if (pos < inputList.size()) {
            if (lookahead.equals("WHEN")) {
                consume("WHEN");
                consume("OPENPAR");
                consume("IDENT");
                consume("CLOSEPAR");
                consume("OPENBR");
                case_x();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void when_after() {
        if (pos < inputList.size()) {
            if (lookahead.equals("CLOSEBR")) {
                consume("CLOSEBR");
            } else if (lookahead.equals("FUNNEL")) {
                when_default();
                consume("CLOSEBR");
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void when_default() {
        if (pos < inputList.size()) {
            if (lookahead.equals("FUNNEL")) {
                consume("FUNNEL");
                case_stmt();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void case_x() {
        if (pos < inputList.size()) {
            if (lookahead.equals("NUMLIT")) {
                when_case();
                case_x();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    //NOTE: when_case() is CASE in the grammar
    void when_case() {
        if (pos < inputList.size()) {
            if (lookahead.equals("NUMLIT")) {
                numexpr();
                case_stmt();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void case_stmt() {
        if (pos < inputList.size()) {
            if (lookahead.equals("RIGHTARROW")) {
                consume("RIGHTARROW");
                cond_body();
            }
        } else {
            syntaxError("End of File Reached");
        }
    }

    void ferment_stmt() {
        if (pos < inputList.size()) {
            if (lookahead.equals("FERMENT")) {
                consume("FERMENT");
                consume("OPENPAR");
                boolderiv();
                consume("CLOSEPAR");
                cond_body();
            }
        } else {
            syntaxError("End of File Reached");
        }
        //FERMENT OPENPAR BOOLDERIV CLOSEPAR COND_BODY
    }

    void distill_stmt() {
        if (pos < inputList.size()) {
            if (lookahead.equals("DISTILL")) {
                consume("FUNNEL");
                consume("OPENBR");
                body_x();
                consume("CLOSEBR");
                consume("UNTIL");
                consume("OPENPAR");
                boolderiv();
                consume("CLOSEPAR");
                consume("SEMICOLON");
            }
        } else {
            syntaxError("End of File Reached");
        }
        //DISTILL OPENBR BODY_X CLOSEBR UNTIL OPENPAR BOOLDERIV CLOSEPAR SEMICOLON
    }

    // Implement the rest of the methods according to your grammar
}
