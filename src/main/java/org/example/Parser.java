package org.example;

import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    ArrayList<String> inputList;
    ParseTree parseTree;
    int pos = 0;
    String lookahead;

    public Parser(ArrayList<String> inputList) {
        this.inputList = inputList;
        lookahead = inputList.get(0);
    }

    public ParseTree parse() {
        ParseTreeNode root = program();
        if (Objects.equals(lookahead, "EOF")) {
            System.out.println("Accepted");
        } else {
            System.out.println("Error");
        }
        System.out.println("lookahead: "+lookahead);
        return new ParseTree(root);
    }

    void consume(String str, ParseTreeNode parent) {
        if (pos < inputList.size() && Objects.equals(inputList.get(pos), str)) {
            pos++;
            lookahead = inputList.get(pos);
            parent.addChild(new ParseTreeNode(str));
        } else {
            throw new RuntimeException("Expected " + str + " but found " + inputList.get(pos));
        }
    }

    void emptyString(ParseTreeNode parent) {
        parent.addChild(new ParseTreeNode("Empty"));
    }

    void syntaxError(String message) {
        //System.out.println("Syntax Error: " + message);
        //System.exit(0);
        throw new RuntimeException("Syntax Error: " + message);
    }

    void syntaxError() {
        //System.out.println("Syntax Error");
        //System.exit(0);
        throw new RuntimeException("Syntax Error: ");
    }

    ParseTreeNode program() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("program");
        if(!lookahead.equals("EOF")){
            parseTreeNode.addChild(statements());
        }
        return parseTreeNode;
    }

    ParseTreeNode statements() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("statements");
        parseTreeNode.addChild(statement());
        parseTreeNode.addChild(statements_x());
        return parseTreeNode;
    }

    ParseTreeNode statements_x() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("statements_x");
        if (pos < inputList.size() && !Objects.equals(inputList.get(pos), "EOF")) {
            parseTreeNode.addChild(statement());
            parseTreeNode.addChild(statements_x());
        }else {
            emptyString(parseTreeNode);
        }
        // else ε (do nothing)
        return parseTreeNode;
    }

    ParseTreeNode statement() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("statement");
        // Implement according to your grammar
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                    parseTreeNode.addChild(simple_statement());
                    break;
                case "FILTER":
                case "WHEN":
                case "FERMENT":
                case "DISTILL":
                    parseTreeNode.addChild(compound_statement());
                    break;
                default:
                    if(lookahead.startsWith("IDENT")){
                        parseTreeNode.addChild(varassign());
                    } else {
                        syntaxError("Not a valid statement");
                    }

                    break;
            }
        } else {
            System.out.println("Syntax Error: End of File Reached");
            System.exit(0);
        }
        return parseTreeNode;
    }

    ParseTreeNode simple_statement() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("simple_statement");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                    parseTreeNode.addChild(vardeclare());
                    break;
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                    parseTreeNode.addChild(stdio());
                    break;
                default:
                    syntaxError();
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    // input/output statements
    ParseTreeNode stdio() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("stdio");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "INPUT":
                    parseTreeNode.addChild(stdin());
                    break;
                case "PRINT":
                case "PRINTLN":
                    parseTreeNode.addChild(stdout());
                    break;
                case "PRINTERR":
                    parseTreeNode.addChild(stderr());
                    break;
                default:
                    syntaxError();
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode compound_statement() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("compound_statement");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "FILTER":
                case "WHEN":
                    parseTreeNode.addChild(cond_stmt());
                    break;
                case "FERMENT":
                    parseTreeNode.addChild(ferment_stmt());
                    break;
                case "DISTILL":
                    parseTreeNode.addChild(distill_stmt());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode vardeclare() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("vardeclare");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                    parseTreeNode.addChild(mut_type());
                    parseTreeNode.addChild(numtype());
                    if(lookahead.startsWith("IDENT")){
                        consume(lookahead, parseTreeNode);
                        parseTreeNode.addChild(assignment());
                        consume("SEMICOLON", parseTreeNode);
                    }else{
                        syntaxError("Identifier not found.");
                    }

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
        return parseTreeNode;
    }
    ParseTreeNode varassign(){
        ParseTreeNode parseTreeNode = new ParseTreeNode("varassign");
        if (pos < inputList.size()) {
            if(lookahead.startsWith("IDENT")){
                consume(lookahead, parseTreeNode);
                parseTreeNode.addChild(assignment());
                consume("SEMICOLON", parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode mut_type() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("mut_type");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                    consume("REACTIVE", parseTreeNode);
                    break;
                case "INERT":
                    consume("INERT", parseTreeNode);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode numtype() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("numtype");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "MOLE32":
                    consume("MOLE32", parseTreeNode);
                    break;
                case "MOLE64":
                    consume("MOLE64", parseTreeNode);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode assignment() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("assignment");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "ASSIGN":
                case "ADDASSIGN":
                case "SUBASSIGN":
                case "MULASSIGN":
                case "DIVASSIGN":
                case "EXPASSIGN":
                    parseTreeNode.addChild(assign_oper());
                    parseTreeNode.addChild(assign_after());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode assign_oper() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("assign_oper");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "ASSIGN":
                    consume("ASSIGN", parseTreeNode);
                    break;
                case "ADDASSIGN":
                    consume("ADDASSIGN", parseTreeNode);
                    break;
                case "SUBASSIGN":
                    consume("SUBASSIGN", parseTreeNode);
                    break;
                case "MULASSIGN":
                    consume("MULASSIGN", parseTreeNode);
                    break;
                case "DIVASSIGN":
                    consume("DIVASSIGN", parseTreeNode);
                    break;
                case "EXPASSIGN":
                    consume("EXPASSIGN", parseTreeNode);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode stdin() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("stdin");
        if (pos < inputList.size()) {
            consume("INPUT", parseTreeNode);
            consume("OPENPAR", parseTreeNode);
            parseTreeNode.addChild(content());
            consume("CLOSEPAR", parseTreeNode);
            consume("SEMICOLON", parseTreeNode);
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode stdout() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("stdout");
        if (pos < inputList.size()) {
            parseTreeNode.addChild(print_type());
            consume("OPENPAR", parseTreeNode);
            parseTreeNode.addChild(content());
            consume("CLOSEPAR", parseTreeNode);
            consume("SEMICOLON", parseTreeNode);
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode stderr() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("stderr");
        if (pos < inputList.size()) {
            consume("PRINTERR", parseTreeNode);
            consume("OPENPAR", parseTreeNode);
            parseTreeNode.addChild(content());
            consume("CLOSEPAR", parseTreeNode);
            consume("SEMICOLON", parseTreeNode);
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode print_type() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("print_type");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "PRINT":
                    consume("PRINT", parseTreeNode);
                    break;
                case "PRINTLN":
                    consume("PRINTLN", parseTreeNode);
                    break;
                default:
                    syntaxError();
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    /*
    IDENT:
    strexpr
    numoper
    boolderiv

    OPENPAR
    SUB
    NUMLIT:
    boolderiv
    numoper
     */
    ParseTreeNode content() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("content");
        if (pos < inputList.size()) {
//            switch (lookahead) { // TODO: THIS IS GONNA HAVE PROBLEMS!!
////                case "OPENPAR":
////                case "IDENT":
////                case "SUB":
////                case "NUMLIT":
////                case "INVERT":
////                case "TRUE":
////                case "FALSE":
////                    boolderiv();
////                    break;
////                case "OPENPAR":
////                case "IDENT":
////                case "SUB":
////                case "NUMLIT":
////                    numoper();
////                    break;
//                //case "IDENT":
//                case "STRLIT":
//                    parseTreeNode.addChild(strexpr());
//            }
            if(lookahead.startsWith("STRLIT")){
                parseTreeNode.addChild(strexpr());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode assign_after() {
        ParseTreeNode node = new ParseTreeNode("assign_after");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "SUB":
                case "OPENPAR":
                    System.out.println("LOOKAHEAD: " + lookahead);
                    node.addChild(numoper());
                    break;
                default:
                    if(lookahead.startsWith("NUMLIT") || lookahead.startsWith("IDENT")){
                        node.addChild(numoper());
                    }
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    // string expressions
    ParseTreeNode strexpr() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("strexpr");
        if (pos < inputList.size()) {
            parseTreeNode.addChild(strterm());
            parseTreeNode.addChild(strterm_x());
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode strterm() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("strterm");
        if (pos < inputList.size()) {
            if(lookahead.startsWith("IDENT") || lookahead.startsWith("STRLIT")){
                consume(lookahead, parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode strterm_x() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("strterm_x");
        if (pos< inputList.size()) {
            if (Objects.equals(lookahead, "ADD")) {
                consume("ADD", parseTreeNode);
                parseTreeNode.addChild(strterm());
                parseTreeNode.addChild(strterm_x());
            }else {
                emptyString(parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    // numerical expressions
    ParseTreeNode numoper() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("numoper");
        if (pos < inputList.size()) {
            parseTreeNode.addChild(term());
            parseTreeNode.addChild(term_x());
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode term_x() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("term_x");
        if (pos < inputList.size()) {
            if (Objects.equals(lookahead, "ADD")) {
                consume("ADD", parseTreeNode);
                parseTreeNode.addChild(term());
                parseTreeNode.addChild(term_x());
            } else if (Objects.equals(lookahead, "SUB")) {
                consume("SUB", parseTreeNode);
                parseTreeNode.addChild(term());
                parseTreeNode.addChild(term_x());
            } else {
                emptyString(parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode term() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("term");
        if (pos < inputList.size()) {
            parseTreeNode.addChild(factor());
            parseTreeNode.addChild(factor_x());
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode factor_x() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("factor_x");
        if (pos < inputList.size()) {
            if (Objects.equals(lookahead, "MUL")) {
                consume("MUL", parseTreeNode);
                parseTreeNode.addChild(factor());
                parseTreeNode.addChild(factor_x());
            } else if (Objects.equals(lookahead, "DIV")) {
                consume("DIV", parseTreeNode);
                parseTreeNode.addChild(factor());
                parseTreeNode.addChild(factor_x());
            } else {
                emptyString(parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode factor() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("factor");
        if (pos < inputList.size()) {
            parseTreeNode.addChild(exponent());
            parseTreeNode.addChild(exponent_x());
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode exponent_x() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("exponent_x");
        if (pos < inputList.size()) {
            if (Objects.equals(lookahead, "EXP")) {
                consume("EXP", parseTreeNode);
                parseTreeNode.addChild(exponent());
                parseTreeNode.addChild(exponent_x());
            } else {
                emptyString(parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode exponent() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("exponent");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "SUB":
                    consume("SUB", parseTreeNode);
                    parseTreeNode.addChild(num_final());
                    break;
                case "OPENPAR":
                    parseTreeNode.addChild(num_final());
                    break;
                default:
                    System.out.println("lookahead:"+lookahead);
                    if(lookahead.startsWith("IDENT") || lookahead.startsWith("NUMLIT")){
                        parseTreeNode.addChild(num_final());
                    } else {
                        syntaxError();
                    }
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    //NOTE num_final is equivalent to FINAL in the grammar
    ParseTreeNode num_final() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("num_final");
        if (pos < inputList.size()) {
            if(lookahead.startsWith("NUMLIT")){
                parseTreeNode.addChild(numexpr());
            }else if(lookahead.startsWith("IDENT")){
                consume(lookahead, parseTreeNode);
            }else if(lookahead.equals("OPENPAR")){
                consume("OPENPAR", parseTreeNode);
                parseTreeNode.addChild(numoper());
                consume("CLOSEPAR", parseTreeNode);
            }else {
                syntaxError();
            }
//            switch (lookahead) {
//                case "NUMLIT":
//                    parseTreeNode.addChild(numexpr());
//                    break;
//                case "IDENT":
//                    consume("IDENT", parseTreeNode);
//                    break;
//                case "OPENPAR":
//                    consume("OPENPAR", parseTreeNode);
//                    numoper();
//                    consume("CLOSEPAR", parseTreeNode);
//                    break;
//                default:
//                    syntaxError();
//            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode numexpr() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("numexpr");
        if (pos < inputList.size() && lookahead.startsWith("NUMLIT")) {
            //consume("NUMLIT", parseTreeNode);
            consume(lookahead, parseTreeNode);
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    //Logic and Relations
    ParseTreeNode boolderiv() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("boolderiv");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "SUB":
                case "OPENPAR": //conflict with openpar
                case "INVERT":
                case "TRUE":
                case "FALSE":
                    parseTreeNode.addChild(condition());
                    parseTreeNode.addChild(boolderiv_x());
                    break;
                default:
                    if(lookahead.startsWith("IDENT") || lookahead.startsWith("NUMLIT")){
                        parseTreeNode.addChild(condition());
                        parseTreeNode.addChild(boolderiv_x());
                    }
            }

        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode boolderiv_x() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("boolderiv_x");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "AND":
                case "OR":
                    parseTreeNode.addChild(logicop());
                    parseTreeNode.addChild(condition());
                    parseTreeNode.addChild(boolderiv_x());
                    break;
                default:
                    emptyString(parseTreeNode);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode logicop() { //logic operation
        ParseTreeNode parseTreeNode = new ParseTreeNode("logicop");
        if (pos < inputList.size()) {
            if (lookahead.equals("AND")) {
                consume("AND", parseTreeNode);
            } else if (lookahead.equals("OR")) {
                consume("OR", parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    /*
    Terminals looked ahead for each derivation of condition:
    nonbool_rel:

    bool_rel:



     OPENPAR:
     nonbool_rel
     bool_rel
     OPENPAR bool_rel CLOSEPAR
     */
    ParseTreeNode condition() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("condition");
        if (pos < inputList.size()) {
            switch (lookahead) {
                //case "NUMLIT":
                case "SUB":
                //case "OPENPAR": //conflict with openpar
                //case "IDENT":
                    parseTreeNode.addChild(nonbool_rel());
                    break;
                case "INVERT":
                //case "OPENPAR": //conflict with openpar
                case "TRUE":
                case "FALSE":
                    parseTreeNode.addChild(bool_rel());
                    break;
                case "OPENPAR": //conflict with openpar
                    consume("OPENPAR", parseTreeNode);
                    //bool_rel();
                    if(pos+1 < inputList.size()){
                        switch(inputList.get(pos+1)){
                            //case "NUMLIT":
                            case "SUB":
                            //case "IDENT":
                                parseTreeNode.addChild(nonbool_rel());
                                break;
                            case "INVERT":
                                //case "OPENPAR": //conflict with openpar
                            case "TRUE":
                            case "FALSE":
                                parseTreeNode.addChild(bool_rel());
                                break;
                            case "CLOSEPAR":
                                consume("OPENPAR", parseTreeNode);
                                break;

                        }
                    }else{
                        syntaxError("End of File Reached");
                    }
                    break;
            }
            if(lookahead.startsWith("IDENT") || ((pos+1 < inputList.size()) && (inputList.get(pos+1).startsWith("IDENT")))){
                parseTreeNode.addChild(nonbool_rel());
            }else if(lookahead.startsWith("NUMLIT") || ((pos+1 < inputList.size()) && (inputList.get(pos+1).startsWith("NUMLIT")))){
                parseTreeNode.addChild(nonbool_rel());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode nonbool_rel() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("nonbool_rel");
        if (pos < inputList.size()) {
            switch (lookahead) {
                //case "IDENT":
                //case "NUMLIT":
                case "SUB":
                case "OPENPAR":
                    parseTreeNode.addChild(nonbool_operand());
                    parseTreeNode.addChild(relation());
                    parseTreeNode.addChild(nonbool_operand());
                    break;
            }
            if(lookahead.startsWith("IDENT") || lookahead.startsWith("NUMLIT")){
                parseTreeNode.addChild(nonbool_operand());
                parseTreeNode.addChild(relation());
                parseTreeNode.addChild(nonbool_operand());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    /*
    OPENPAR:
    numoper
    OPENPAR nonbool_operand CLOSEPAR
    */
    ParseTreeNode nonbool_operand() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("nonbool_operand");
        if (pos < inputList.size()) {
            if(lookahead.startsWith("IDENT")){
                consume(lookahead, parseTreeNode);
            }else if(lookahead.startsWith("NUMLIT")){
                parseTreeNode.addChild(numoper());
            }else if(lookahead.equals("OPENPAR")){
                consume("OPENPAR", parseTreeNode);
                if((pos+1) < inputList.size()){
                    if(inputList.get(pos+1).startsWith("NUMLIT")){
                        parseTreeNode.addChild(numoper());
                    }else{
                        parseTreeNode.addChild(nonbool_operand());
                    }
                }
                consume("CLOSEPAR", parseTreeNode);
            }
//            switch (lookahead) {
//                case "IDENT":
//                    consume("IDENT", parseTreeNode);
//                    break;
//                case "NUMLIT":
//                //case "OPENPAR": //conflict with openpar
//                    parseTreeNode.addChild(numoper());
//                    break;
//                case "OPENPAR": //conflict with openpar
//                    consume("OPENPAR", parseTreeNode);
//                    if((pos+1) < inputList.size()){
//                        if(inputList.get(pos+1).equals("NUMLIT")){
//                            parseTreeNode.addChild(numoper());
//                        }else{
//                            parseTreeNode.addChild(nonbool_operand());
//                        }
//                    }
//                    consume("CLOSEPAR", parseTreeNode);
//                    break;
//            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode bool_rel() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("bool_rel");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "INVERT":
                    consume("INVERT", parseTreeNode);
                    parseTreeNode.addChild(bool_rel());
                    break;
                case "OPENPAR":
                    consume("OPENPAR", parseTreeNode);
                    parseTreeNode.addChild(condition());
                    consume("CLOSEPAR", parseTreeNode);
                    break;
                case "TRUE":
                    consume("TRUE", parseTreeNode);
                    break;
                case "FALSE":
                    consume("FALSE", parseTreeNode);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode bool_expr() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("bool_expr");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "INVERT":
                case "OPENPAR":
                case "TRUE":
                case "FALSE":
                    parseTreeNode.addChild(bool_rel());
                    parseTreeNode.addChild(expr_right());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode expr_right() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("expr_right");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "IS":
                    parseTreeNode.addChild(eq_rel());
                    parseTreeNode.addChild(bool_rel());
                    break;
                case "AND":
                case "OR":
                    parseTreeNode.addChild(logicop());
                    parseTreeNode.addChild(bool_rel());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode bool_operand() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("bool_operand");
        if (pos < inputList.size()) {
            if(lookahead.startsWith("IDENT")){
                consume(lookahead, parseTreeNode);
            }else if(lookahead.equals("OPENPAR")){
                consume("OPENPAR", parseTreeNode);
                parseTreeNode.addChild(bool_operand());
                consume("CLOSEPAR", parseTreeNode);
            }
//            switch (lookahead) {
//                case "IDENT":
//                    consume("IDENT", parseTreeNode);
//                    break;
//                case "OPENPAR":
//                    consume("OPENPAR", parseTreeNode);
//                    parseTreeNode.addChild(bool_operand());
//                    consume("CLOSEPAR", parseTreeNode);
//                    break;
//            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode relation() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("relation");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "IS":
                    parseTreeNode.addChild(eq_rel());
                    break;
                case "GT":
                case "LT":
                case "GTE":
                case "LTE":
                    parseTreeNode.addChild(size_rel());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode eq_rel() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("eq_rel");
        if (pos < inputList.size()) {
            if (lookahead.equals("IS")) {
                consume("IS", parseTreeNode);
                parseTreeNode.addChild(eq_right());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode eq_right() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("eq_right");
        if (pos < inputList.size()) {
            if (lookahead.equals("NOT")) {
                consume("NOT", parseTreeNode);
            } else {
                emptyString(parseTreeNode);
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode size_rel() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("size_rel");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "GT":
                    consume("GT", parseTreeNode);
                    break;
                case "LT":
                    consume("LT", parseTreeNode);
                    break;
                case "GTE":
                    consume("GTE", parseTreeNode);
                    break;
                case "LTE":
                    consume("LTE", parseTreeNode);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    //Control and Iterative Statements
    ParseTreeNode cond_stmt() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("cond_stmt");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "FILTER":
                    parseTreeNode.addChild(filter_stmt());
                    break;
                case "WHEN":
                    parseTreeNode.addChild(when_stmt());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode filter_stmt() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("filter_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("FILTER")) {
                parseTreeNode.addChild(filter_expr());
                parseTreeNode.addChild(funnel_expr());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode filter_expr() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("filter_expr");
        if (pos < inputList.size()) {
            if (lookahead.equals("FILTER")) {
                consume("FILTER", parseTreeNode);
                if(pos < inputList.size()){
                    switch (lookahead){
                        case "OPENPAR":
                            consume("OPENPAR", parseTreeNode);
                            parseTreeNode.addChild(boolderiv());
                        case "CLOSEPAR":
                            consume("CLOSEPAR", parseTreeNode);
                            parseTreeNode.addChild(cond_body());
                    }
                }else{
                    syntaxError("End of File Reached");
                }
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode cond_body() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("cond_body");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE": //from here
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR": //to here is from simple_stmt
                    parseTreeNode.addChild(simple_statement());
                    break;
                case "OPENBR":
                    consume("OPENBR", parseTreeNode);
                    parseTreeNode.addChild(body_x());
                    consume("CLOSEBR", parseTreeNode);
                    break;
                default:
                    if(lookahead.startsWith("IDENT")){
                        parseTreeNode.addChild(varassign());
                    }else {
                        syntaxError();
                    }
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode funnel_expr() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("funnel_expr");
        if (pos < inputList.size()) {
            if (lookahead.equals("FUNNEL")) {
                consume("FUNNEL", parseTreeNode);
                parseTreeNode.addChild(funnel_right());
            } else {
                emptyString(parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        //funnel();
        //funnel_right();
        return parseTreeNode;
    }

    ParseTreeNode funnel_right() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("funnel_right");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                case "OPENBR": //cond_body
                    parseTreeNode.addChild(cond_body());
                    break;
                case "FILTER":
                    parseTreeNode.addChild(filter_stmt());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        //cond_body();
        //filter_stmt();
        return parseTreeNode;
    }

    ParseTreeNode body_x() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("body_x");
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
                    parseTreeNode.addChild(body());
                    parseTreeNode.addChild(body_x());
                    break;
                default:
                    emptyString(parseTreeNode);
                    break;
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
//        body();
//        body_x();
//        emptystr();
        return parseTreeNode;
    }

    ParseTreeNode body() { //note: exactly the same as STATEMENT()
        ParseTreeNode parseTreeNode = new ParseTreeNode("body");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                    parseTreeNode.addChild(simple_statement());
                    break;
                case "FILTER":
                case "WHEN":
                case "FERMENT":
                case "DISTILL":
                    parseTreeNode.addChild(compound_statement());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode when_stmt() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("when_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("WHEN")) {
                parseTreeNode.addChild(when_before());
                parseTreeNode.addChild(when_after());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode when_before() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("when_before");
        if (pos < inputList.size()) {
            if (lookahead.equals("WHEN")) {
                consume("WHEN", parseTreeNode);
                consume("OPENPAR", parseTreeNode);
                consume("IDENT", parseTreeNode);
                consume("CLOSEPAR", parseTreeNode);
                consume("OPENBR", parseTreeNode);
                parseTreeNode.addChild(case_x());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode when_after() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("when_after");
        if (pos < inputList.size()) {
            if (lookahead.equals("CLOSEBR")) {
                consume("CLOSEBR", parseTreeNode);
            } else if (lookahead.equals("FUNNEL")) {
                parseTreeNode.addChild(when_default());
                consume("CLOSEBR", parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode when_default() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("when_default");
        if (pos < inputList.size()) {
            if (lookahead.equals("FUNNEL")) {
                consume("FUNNEL", parseTreeNode);
                parseTreeNode.addChild(case_stmt());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode case_x() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("case_x");
        if (pos < inputList.size()) {
            if (lookahead.startsWith("NUMLIT")) {
                parseTreeNode.addChild(when_case());
                parseTreeNode.addChild(case_x());
            }else {
                emptyString(parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    //NOTE: when_case() is CASE in the grammar
    ParseTreeNode when_case() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("when_case");
        if (pos < inputList.size()) {
            if (lookahead.startsWith("NUMLIT")) {
                parseTreeNode.addChild(numexpr());
                parseTreeNode.addChild(case_stmt());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode case_stmt() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("case_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("RIGHTARROW")) {
                consume("RIGHTARROW", parseTreeNode);
                parseTreeNode.addChild(cond_body());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return parseTreeNode;
    }

    ParseTreeNode ferment_stmt() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("ferment_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("FERMENT")) {
                consume("FERMENT", parseTreeNode);
                consume("OPENPAR", parseTreeNode);
                parseTreeNode.addChild(boolderiv());
                consume("CLOSEPAR", parseTreeNode);
                parseTreeNode.addChild(cond_body());
            }
        } else {
            syntaxError("End of File Reached");
        }
        //FERMENT OPENPAR BOOLDERIV CLOSEPAR COND_BODY
        return parseTreeNode;
    }

    ParseTreeNode distill_stmt() {
        ParseTreeNode parseTreeNode = new ParseTreeNode("distill_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("DISTILL")) {
                consume("FUNNEL", parseTreeNode);
                consume("OPENBR", parseTreeNode);
                parseTreeNode.addChild(body_x());
                consume("CLOSEBR", parseTreeNode);
                consume("UNTIL", parseTreeNode);
                consume("OPENPAR", parseTreeNode);
                parseTreeNode.addChild(boolderiv());
                consume("CLOSEPAR", parseTreeNode);
                consume("SEMICOLON", parseTreeNode);
            }
        } else {
            syntaxError("End of File Reached");
        }
        //DISTILL OPENBR BODY_X CLOSEBR UNTIL OPENPAR BOOLDERIV CLOSEPAR SEMICOLON
        return parseTreeNode;
    }
}
