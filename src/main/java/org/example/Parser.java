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
        Node root = program();
        if (Objects.equals(lookahead, "EOF")) {
            System.out.println("Accepted");
        } else {
            System.out.println("Error");
        }
        System.out.println("lookahead: "+lookahead);
        return new ParseTree(root);
    }

    void consume(String str, Node parent) {
        if (pos < inputList.size() && Objects.equals(inputList.get(pos), str)) {
            pos++;
            lookahead = inputList.get(pos);
            parent.addChild(new Node(str));
        } else {
            throw new RuntimeException("Expected " + str + " but found " + inputList.get(pos));
        }
    }

    void addToTree(String str) {

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

    Node program() {
        Node node = new Node("program");
        node.addChild(statements());
        return node;
    }

    Node statements() {
        Node node = new Node("statements");
        node.addChild(statement());
        node.addChild(statements_x());
        return node;
    }

    Node statements_x() {
        Node node = new Node("statements_x");
        if (pos < inputList.size() && !Objects.equals(inputList.get(pos), "EOF")) {
            node.addChild(statement());
            node.addChild(statements_x());
        }
        // else ε (do nothing)
        return node;
    }

    Node statement() {
        Node node = new Node("statement");
        // Implement according to your grammar
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                    node.addChild(simple_statement());
                    break;
                case "FILTER":
                case "WHEN":
                case "FERMENT":
                case "DISTILL":
                    node.addChild(compound_statement());
                    break;
//                case "IDENT":
//                    node.addChild(varassign());
//                    break;
                default:
                    if(lookahead.startsWith("IDENT")){
                        node.addChild(varassign());
                    } else {
                        syntaxError("Not a valid statement");
                    }

                    break;
            }
        } else {
            System.out.println("Syntax Error: End of File Reached");
            System.exit(0);
        }
        return node;
    }

    Node simple_statement() {
        Node node = new Node("simple_statement");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                    node.addChild(vardeclare());
                    break;
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                    node.addChild(stdio());
                    break;
                default:
                    syntaxError();
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    // input/output statements
    Node stdio() {
        Node node = new Node("stdio");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "INPUT":
                    node.addChild(stdin());
                    break;
                case "PRINT":
                case "PRINTLN":
                    node.addChild(stdout());
                    break;
                case "PRINTERR":
                    node.addChild(stderr());
                    break;
                default:
                    syntaxError();
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node compound_statement() {
        Node node = new Node("compound_statement");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "FILTER":
                case "WHEN":
                    node.addChild(cond_stmt());
                    break;
                case "FERMENT":
                    node.addChild(ferment_stmt());
                    break;
                case "DISTILL":
                    node.addChild(distill_stmt());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node vardeclare() {
        Node node = new Node("vardeclare");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                    node.addChild(mut_type());
                    node.addChild(numtype());
                    if(lookahead.startsWith("IDENT")){
                        consume(lookahead, node);
                    }
                    //consume("IDENT", node);
                    node.addChild(assignment());
                    consume("SEMICOLON", node);
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
        return node;
    }
    Node varassign(){
        Node node = new Node("varassign");
        if (pos < inputList.size()) {
//            switch (lookahead) {
//                case "IDENT":
//                    consume("IDENT", node);
//                    node.addChild(assignment());
//                    consume("SEMICOLON", node);
//                    break;
//            }
            if(lookahead.startsWith("IDENT")){
                consume(lookahead, node);
                node.addChild(assignment());
                consume("SEMICOLON", node);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node mut_type() {
        Node node = new Node("mut_type");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                    consume("REACTIVE", node);
                    break;
                case "INERT":
                    consume("INERT", node);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node numtype() {
        Node node = new Node("numtype");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "MOLE32":
                    consume("MOLE32", node);
                    break;
                case "MOLE64":
                    consume("MOLE64", node);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node assignment() {
        Node node = new Node("assignment");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "ASSIGN":
                case "ADDASSIGN":
                case "SUBASSIGN":
                case "MULASSIGN":
                case "DIVASSIGN":
                case "EXPASSIGN":
                    node.addChild(assign_oper());
                    node.addChild(assign_after());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node assign_oper() {
        Node node = new Node("assign_oper");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "ASSIGN":
                    consume("ASSIGN", node);
                    break;
                case "ADDASSIGN":
                    consume("ADDASSIGN", node);
                    break;
                case "SUBASSIGN":
                    consume("SUBASSIGN", node);
                    break;
                case "MULASSIGN":
                    consume("MULASSIGN", node);
                    break;
                case "DIVASSIGN":
                    consume("DIVASSIGN", node);
                    break;
                case "EXPASSIGN":
                    consume("EXPASSIGN", node);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node stdin() {
        Node node = new Node("stdin");
        if (pos < inputList.size()) {
            consume("INPUT", node);
            consume("OPENPAR", node);
            node.addChild(content());
            consume("CLOSEPAR", node);
            consume("SEMICOLON", node);
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node stdout() {
        Node node = new Node("stdout");
        if (pos < inputList.size()) {
            node.addChild(print_type());
            consume("OPENPAR", node);
            node.addChild(content());
            consume("CLOSEPAR", node);
            consume("SEMICOLON", node);
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node stderr() {
        Node node = new Node("stderr");
        if (pos < inputList.size()) {
            consume("PRINTERR", node);
            consume("OPENPAR", node);
            node.addChild(content());
            consume("CLOSEPAR", node);
            consume("SEMICOLON", node);
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node print_type() {
        Node node = new Node("print_type");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "PRINT":
                    consume("PRINT", node);
                    break;
                case "PRINTLN":
                    consume("PRINTLN", node);
                    break;
                default:
                    syntaxError();
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
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
    Node content() {
        Node node = new Node("content");
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
//                    node.addChild(strexpr());
//            }
            if(lookahead.startsWith("STRLIT")){
                node.addChild(strexpr());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node assign_after() {
        Node node = new Node("assign_after");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "SUB":
                //case "NUMLIT":
                //case "IDENT": //conflict with IDENT
                case "OPENPAR":
//                    if(lookahead.startsWith("IDENT")){
//                        //consume("IDENT", node);
//                        consume(lookahead, node);
//                    }
                    node.addChild(numoper());
                    break;
                //case "IDENT": //conflict with IDENT
                    //consume("IDENT");
                    //break;
            }
            if(lookahead.startsWith("IDENT")){
                //consume("IDENT", node);
                consume(lookahead, node);
                node.addChild(numoper());
            }
            if(lookahead.startsWith("NUMLIT")){
                node.addChild(numoper());
            }

        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    // string expressions
    Node strexpr() {
        Node node = new Node("strexpr");
        if (pos < inputList.size()) {
            node.addChild(strterm());
            node.addChild(strterm_x());
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node strterm() {
        Node node = new Node("strterm");
        if (pos < inputList.size()) {
//            switch (lookahead){
//                case "IDENT":
//                    consume("IDENT", node);
//                    break;
//                case "STRLIT":
//                    consume("STRLIT", node);
//                    break;
//            }
            if(lookahead.startsWith("IDENT")){
                consume(lookahead, node);
            }else if(lookahead.startsWith("STRLIT")){
                consume(lookahead, node);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node strterm_x() {
        Node node = new Node("strterm_x");
        if (pos< inputList.size()) {
            if (Objects.equals(lookahead, "ADD")) {
                consume("ADD", node);
                node.addChild(strterm());
                node.addChild(strterm_x());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    // numerical expressions
    Node numoper() {
        Node node = new Node("numoper");
        if (pos < inputList.size()) {
            node.addChild(term());
            node.addChild(term_x());
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node term_x() {
        Node node = new Node("term_x");
        if (pos < inputList.size()) {
            if (Objects.equals(lookahead, "ADD")) {
                consume("ADD", node);
                node.addChild(term());
                node.addChild(term_x());
            } else if (Objects.equals(lookahead, "SUB")) {
                consume("SUB", node);
                node.addChild(term());
                node.addChild(term_x());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node term() {
        Node node = new Node("term");
        if (pos < inputList.size()) {
            node.addChild(factor());
            node.addChild(factor_x());
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node factor_x() {
        Node node = new Node("factor_x");
        if (pos < inputList.size()) {
            if (Objects.equals(lookahead, "MUL")) {
                consume("MUL", node);
                node.addChild(factor());
                node.addChild(factor_x());
            } else if (Objects.equals(lookahead, "DIV")) {
                consume("DIV", node);
                node.addChild(factor());
                node.addChild(factor_x());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node factor() {
        Node node = new Node("factor");
        if (pos < inputList.size()) {
            node.addChild(exponent());
            node.addChild(exponent_x());
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node exponent_x() {
        Node node = new Node("exponent_x");
        if (pos < inputList.size()) {
            if (Objects.equals(lookahead, "EXP")) {
                consume("EXP", node);
                node.addChild(exponent());
                node.addChild(exponent_x());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node exponent() {
        Node node = new Node("exponent");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "SUB":
                    consume("SUB", node);
                    node.addChild(num_final());
                    break;
                //case "IDENT":
                //case "NUMLIT":
                case "OPENPAR":
                    node.addChild(num_final());
                    break;
                default:
                    if(lookahead.startsWith("IDENT") || lookahead.startsWith("NUMLIT")){
                        node.addChild(num_final());
                    } else {
                        syntaxError();
                    }
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    //NOTE num_final is equivalent to FINAL in the grammar
    Node num_final() {
        Node node = new Node("num_final");
        if (pos < inputList.size()) {
            if(lookahead.startsWith("NUMLIT")){
                node.addChild(numexpr());
            }else if(lookahead.startsWith("IDENT")){
                consume(lookahead, node);
            }else if(lookahead.equals("OPENPAR")){
                consume("OPENPAR", node);
                numoper();
                consume("CLOSEPAR", node);
            }else {
                syntaxError();
            }
//            switch (lookahead) {
//                case "NUMLIT":
//                    node.addChild(numexpr());
//                    break;
//                case "IDENT":
//                    consume("IDENT", node);
//                    break;
//                case "OPENPAR":
//                    consume("OPENPAR", node);
//                    numoper();
//                    consume("CLOSEPAR", node);
//                    break;
//                default:
//                    syntaxError();
//            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node numexpr() {
        Node node = new Node("numexpr");
        if (pos < inputList.size() && lookahead.startsWith("NUMLIT")) {
            //consume("NUMLIT", node);
            consume(lookahead, node);
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    //Logic and Relations
    Node boolderiv() {
        Node node = new Node("boolderiv");
        if (pos < inputList.size()) {
            switch (lookahead) {
                //case "NUMLIT":
                case "SUB":
                case "OPENPAR": //conflict with openpar
                //case "IDENT":
                case "INVERT":
                case "TRUE":
                case "FALSE":
                    node.addChild(condition());
                    node.addChild(boolderiv_x());
            }
            if(lookahead.startsWith("IDENT") || lookahead.startsWith("NUMLIT")){
                node.addChild(condition());
                node.addChild(boolderiv_x());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node boolderiv_x() {
        Node node = new Node("boolderiv_x");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "AND":
                case "OR":
                    node.addChild(logicop());
                    node.addChild(condition());
                    node.addChild(boolderiv_x());
                    break;
                //else, do nothing
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node logicop() { //logic operation
        Node node = new Node("logicop");
        if (pos < inputList.size()) {
            if (lookahead.equals("AND")) {
                consume("AND", node);
            } else if (lookahead.equals("OR")) {
                consume("OR", node);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
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
    Node condition() {
        Node node = new Node("condition");
        if (pos < inputList.size()) {
            switch (lookahead) {
                //case "NUMLIT":
                case "SUB":
                //case "OPENPAR": //conflict with openpar
                //case "IDENT":
                    node.addChild(nonbool_rel());
                    break;
                case "INVERT":
                //case "OPENPAR": //conflict with openpar
                case "TRUE":
                case "FALSE":
                    node.addChild(bool_rel());
                    break;
                case "OPENPAR": //conflict with openpar
                    consume("OPENPAR", node);
                    //bool_rel();
                    if(pos+1 < inputList.size()){
                        switch(inputList.get(pos+1)){
                            //case "NUMLIT":
                            case "SUB":
                            //case "IDENT":
                                node.addChild(nonbool_rel());
                                break;
                            case "INVERT":
                                //case "OPENPAR": //conflict with openpar
                            case "TRUE":
                            case "FALSE":
                                node.addChild(bool_rel());
                                break;
                            case "CLOSEPAR":
                                consume("OPENPAR", node);
                                break;

                        }
                    }else{
                        syntaxError("End of File Reached");
                    }
                    break;
            }
            if(lookahead.startsWith("IDENT") || ((pos+1 < inputList.size()) && (inputList.get(pos+1).startsWith("IDENT")))){
                node.addChild(nonbool_rel());
            }else if(lookahead.startsWith("NUMLIT") || ((pos+1 < inputList.size()) && (inputList.get(pos+1).startsWith("NUMLIT")))){
                node.addChild(nonbool_rel());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node nonbool_rel() {
        Node node = new Node("nonbool_rel");
        if (pos < inputList.size()) {
            switch (lookahead) {
                //case "IDENT":
                //case "NUMLIT":
                case "SUB":
                case "OPENPAR":
                    node.addChild(nonbool_operand());
                    node.addChild(relation());
                    node.addChild(nonbool_operand());
                    break;
            }
            if(lookahead.startsWith("IDENT") || lookahead.startsWith("NUMLIT")){
                node.addChild(nonbool_operand());
                node.addChild(relation());
                node.addChild(nonbool_operand());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    /*
    OPENPAR:
    numoper
    OPENPAR nonbool_operand CLOSEPAR
    */
    Node nonbool_operand() {
        Node node = new Node("nonbool_operand");
        if (pos < inputList.size()) {
            if(lookahead.startsWith("IDENT")){
                consume(lookahead, node);
            }else if(lookahead.startsWith("NUMLIT")){
                node.addChild(numoper());
            }else if(lookahead.equals("OPENPAR")){
                consume("OPENPAR", node);
                if((pos+1) < inputList.size()){
                    if(inputList.get(pos+1).startsWith("NUMLIT")){
                        node.addChild(numoper());
                    }else{
                        node.addChild(nonbool_operand());
                    }
                }
                consume("CLOSEPAR", node);
            }
//            switch (lookahead) {
//                case "IDENT":
//                    consume("IDENT", node);
//                    break;
//                case "NUMLIT":
//                //case "OPENPAR": //conflict with openpar
//                    node.addChild(numoper());
//                    break;
//                case "OPENPAR": //conflict with openpar
//                    consume("OPENPAR", node);
//                    if((pos+1) < inputList.size()){
//                        if(inputList.get(pos+1).equals("NUMLIT")){
//                            node.addChild(numoper());
//                        }else{
//                            node.addChild(nonbool_operand());
//                        }
//                    }
//                    consume("CLOSEPAR", node);
//                    break;
//            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node bool_rel() {
        Node node = new Node("bool_rel");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "INVERT":
                    consume("INVERT", node);
                    node.addChild(bool_rel());
                    break;
                case "OPENPAR":
                    consume("OPENPAR", node);
                    node.addChild(condition());
                    consume("CLOSEPAR", node);
                    break;
                case "TRUE":
                    consume("TRUE", node);
                    break;
                case "FALSE":
                    consume("FALSE", node);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node bool_expr() {
        Node node = new Node("bool_expr");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "INVERT":
                case "OPENPAR":
                case "TRUE":
                case "FALSE":
                    node.addChild(bool_rel());
                    node.addChild(expr_right());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node expr_right() {
        Node node = new Node("expr_right");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "IS":
                    node.addChild(eq_rel());
                    node.addChild(bool_rel());
                    break;
                case "AND":
                case "OR":
                    node.addChild(logicop());
                    node.addChild(bool_rel());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node bool_operand() {
        Node node = new Node("bool_operand");
        if (pos < inputList.size()) {
            if(lookahead.startsWith("IDENT")){
                consume(lookahead, node);
            }else if(lookahead.equals("OPENPAR")){
                consume("OPENPAR", node);
                node.addChild(bool_operand());
                consume("CLOSEPAR", node);
            }
//            switch (lookahead) {
//                case "IDENT":
//                    consume("IDENT", node);
//                    break;
//                case "OPENPAR":
//                    consume("OPENPAR", node);
//                    node.addChild(bool_operand());
//                    consume("CLOSEPAR", node);
//                    break;
//            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node relation() {
        Node node = new Node("relation");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "IS":
                    node.addChild(eq_rel());
                    break;
                case "GT":
                case "LT":
                case "GTE":
                case "LTE":
                    node.addChild(size_rel());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node eq_rel() {
        Node node = new Node("eq_rel");
        if (pos < inputList.size()) {
            if (lookahead.equals("IS")) {
                consume("IS", node);
                node.addChild(eq_right());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node eq_right() {
        Node node = new Node("eq_right");
        if (pos < inputList.size()) {
            if (lookahead.equals("NOT")) {
                consume("NOT", node);
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node size_rel() {
        Node node = new Node("size_rel");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "GT":
                    consume("GT", node);
                    break;
                case "LT":
                    consume("LT", node);
                    break;
                case "GTE":
                    consume("GTE", node);
                    break;
                case "LTE":
                    consume("LTE", node);
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    //Control and Iterative Statements
    Node cond_stmt() {
        Node node = new Node("cond_stmt");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "FILTER":
                    node.addChild(filter_stmt());
                    break;
                case "WHEN":
                    node.addChild(when_stmt());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node filter_stmt() {
        Node node = new Node("filter_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("FILTER")) {
                node.addChild(filter_expr());
                node.addChild(funnel_expr());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node filter_expr() {
        Node node = new Node("filter_expr");
        if (pos < inputList.size()) {
            if (lookahead.equals("FILTER")) {
                consume("FILTER", node);
                if(pos < inputList.size()){
                    switch (lookahead){
                        case "OPENPAR":
                            consume("OPENPAR", node);
                            node.addChild(boolderiv());
                        case "CLOSEPAR":
                            consume("CLOSEPAR", node);
                            node.addChild(cond_body());
                    }
                }else{
                    syntaxError("End of File Reached");
                }
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node cond_body() {
        Node node = new Node("cond_body");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE": //from here
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR": //to here is from simple_stmt
                    node.addChild(simple_statement());
                    break;
//                case "IDENT":
//                    node.addChild(varassign());
//                    break;
                case "OPENBR":
                    consume("OPENBR", node);
                    node.addChild(body_x());
                    consume("CLOSEBR", node);
                    break;
                default:
                    if(lookahead.startsWith("IDENT")){
                        node.addChild(varassign());
                    }else {
                        syntaxError();
                    }
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node funnel_expr() {
        Node node = new Node("funnel_expr");
        if (pos < inputList.size()) {
            if (lookahead.equals("FUNNEL")) {
                consume("FUNNEL", node);
                node.addChild(funnel_right());
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
        //funnel();
        //funnel_right();
        return node;
    }

    Node funnel_right() {
        Node node = new Node("funnel_right");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                case "OPENBR": //cond_body
                    node.addChild(cond_body());
                    break;
                case "FILTER":
                    node.addChild(filter_stmt());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        //cond_body();
        //filter_stmt();
        return node;
    }

    Node body_x() {
        Node node = new Node("body_x");
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
                    node.addChild(body());
                    node.addChild(body_x());
                    break;
            }
            //else do nothing because epsilon (empty string)
        } else {
            syntaxError("End of File Reached");
        }
//        body();
//        body_x();
//        emptystr();
        return node;
    }

    Node body() { //note: exactly the same as STATEMENT()
        Node node = new Node("body");
        if (pos < inputList.size()) {
            switch (lookahead) {
                case "REACTIVE":
                case "INERT":
                case "INPUT":
                case "PRINT":
                case "PRINTLN":
                case "PRINTERR":
                    node.addChild(simple_statement());
                    break;
                case "FILTER":
                case "WHEN":
                case "FERMENT":
                case "DISTILL":
                    node.addChild(compound_statement());
                    break;
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node when_stmt() {
        Node node = new Node("when_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("WHEN")) {
                node.addChild(when_before());
                node.addChild(when_after());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node when_before() {
        Node node = new Node("when_before");
        if (pos < inputList.size()) {
            if (lookahead.equals("WHEN")) {
                consume("WHEN", node);
                consume("OPENPAR", node);
                consume("IDENT", node);
                consume("CLOSEPAR", node);
                consume("OPENBR", node);
                node.addChild(case_x());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node when_after() {
        Node node = new Node("when_after");
        if (pos < inputList.size()) {
            if (lookahead.equals("CLOSEBR")) {
                consume("CLOSEBR", node);
            } else if (lookahead.equals("FUNNEL")) {
                node.addChild(when_default());
                consume("CLOSEBR", node);
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node when_default() {
        Node node = new Node("when_default");
        if (pos < inputList.size()) {
            if (lookahead.equals("FUNNEL")) {
                consume("FUNNEL", node);
                node.addChild(case_stmt());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node case_x() {
        Node node = new Node("case_x");
        if (pos < inputList.size()) {
            if (lookahead.equals("NUMLIT")) {
                node.addChild(when_case());
                node.addChild(case_x());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    //NOTE: when_case() is CASE in the grammar
    Node when_case() {
        Node node = new Node("when_case");
        if (pos < inputList.size()) {
            if (lookahead.equals("NUMLIT")) {
                node.addChild(numexpr());
                node.addChild(case_stmt());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node case_stmt() {
        Node node = new Node("case_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("RIGHTARROW")) {
                consume("RIGHTARROW", node);
                node.addChild(cond_body());
            }
        } else {
            syntaxError("End of File Reached");
        }
        return node;
    }

    Node ferment_stmt() {
        Node node = new Node("ferment_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("FERMENT")) {
                consume("FERMENT", node);
                consume("OPENPAR", node);
                node.addChild(boolderiv());
                consume("CLOSEPAR", node);
                node.addChild(cond_body());
            }
        } else {
            syntaxError("End of File Reached");
        }
        //FERMENT OPENPAR BOOLDERIV CLOSEPAR COND_BODY
        return node;
    }

    Node distill_stmt() {
        Node node = new Node("distill_stmt");
        if (pos < inputList.size()) {
            if (lookahead.equals("DISTILL")) {
                consume("FUNNEL", node);
                consume("OPENBR", node);
                node.addChild(body_x());
                consume("CLOSEBR", node);
                consume("UNTIL", node);
                consume("OPENPAR", node);
                node.addChild(boolderiv());
                consume("CLOSEPAR", node);
                consume("SEMICOLON", node);
            }
        } else {
            syntaxError("End of File Reached");
        }
        //DISTILL OPENBR BODY_X CLOSEBR UNTIL OPENPAR BOOLDERIV CLOSEPAR SEMICOLON
        return node;
    }
}
