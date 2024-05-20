package com.three_csa.argon.SemAnalyzer;

import com.three_csa.argon.NumericalVariable;
import com.three_csa.argon.SyntaxAnalyzer.ParseTreeNode;
import com.three_csa.argon.SemAnalyzer.Node.*;
import com.three_csa.argon.Tokenizer.TokenType;
import com.three_csa.argon.SyntaxAnalyzer.ParseTree;

import java.util.*;

public class SyntaxTree {
    private final StatementsNode root;
    HashMap<String, NumericalVariable> variables;
    public SyntaxTree(){
        this.root = new StatementsNode();
        this.variables = new HashMap<>();
    }
    public StatementsNode getRoot() {
        return root;
    }

    public HashMap<String, NumericalVariable> getVariables() {
        return variables;
    }
    public void buildAST(ParseTree tree){
        if(tree.getRoot().getValue().equals("program")){
            if (tree.getRoot().getChildren() != null) {
                ParseTreeNode statementsNode = tree.getRoot().getChildren().get(0);
                if(statementsNode != null){
                    //System.out.println("statementsNode: " + statementsNode.getValue());
                    statements(statementsNode,root);
                }
            } else {
                System.out.println("\nNo AST has been built because the AST is empty");
            }
        }
        System.out.println();
        root.print(0);
    }

    public void statements(ParseTreeNode statementsNode, StatementsNode root){
       for(ParseTreeNode child: statementsNode.getChildren()){
           if(child.getValue().equals("statement")){
               //System.out.println("statementNodeOuter: " + child.getValue());
               statement(child, root);
           } else if (child.getValue().equals("statements_x")) {
               //System.out.println("statements_xNodeOuter: " + child.getValue());
               statements_x(child, root);
           }
       }
    }

    public void statement(ParseTreeNode statementNode, StatementsNode root){
        //System.out.println("stmt: " + statementNode.getValue());
        for(ParseTreeNode child: statementNode.getChildren()){
            if(child.getValue().equals("simple_statement")){
                simpleStatement(child, root);
            } else if (child.getValue().equals("compound_statement")){
                compoundStatement(child, root);
            }
        }

    }

    public void statements_x(ParseTreeNode statements_xNode, StatementsNode root){
        if(statements_xNode.getChildren() != null){
            for(ParseTreeNode child : statements_xNode.getChildren()){
                if(child.getValue().equals("statement")){
                    //System.out.println("child:" + child.getValue());
                    statement(child, root);
                }else if(child.getValue().equals("statements_x")){
                    //System.out.println("child:" + child.getValue());
                    statements_x(child, root);
                }
            }
        }
    }

    public void cond_body(ParseTreeNode cond_bodyNode, ASTNode root) { //StatementsNode root) {
        //ASTNode cond_body = new ASTNode("body");
        //root.addChild(cond_body);
        System.out.println("Entering cond_body");
        StatementsNode body = new StatementsNode("Body");
        if(cond_bodyNode.getChildren() != null){
            for(ParseTreeNode child : cond_bodyNode.getChildren()){
                if(child.getValue().equals("simple_statement")) {
//                    simpleStatement(child, root);
                    //StatementsNode body = new StatementsNode("Body");
                    simpleStatement(child, body);
                    //root.addChild(body);
//                } else if (child.getValue().equals("cond_body")){
//                    cond_body(child, root);
//                }
                } else if (child.getValue().equals("statements_x")) {
                    System.out.println("Entering statement_x from cond_body");
                   // StatementsNode body = new StatementsNode("Body");
                    //statements_x(child, root);
                    statements_x(child, body);
                    //root.addChild(body);
                }
            }
        }
        root.addChild(body);
    }

    public void compoundStatement(ParseTreeNode compoundStatementNode, StatementsNode root) {
        ParseTreeNode childNode = compoundStatementNode.getChildren().getFirst();
        if (childNode.getValue().equals("cond_stmt")) {
            //System.out.println("Going to cond_stmt");
            cond_stmt(childNode, root);
            //put the conditional statement function here
//        } else if (childNode.getValue().equals("distill_stmt") || childNode.getValue().equals("ferment_stmt")) {
//            loop(childNode, root);
//        }
        } else if (childNode.getValue().equals("distill_stmt")) {
            distill(childNode, root);
        } else if (childNode.getValue().equals("ferment_stmt")) {
            ferment(childNode, root);
        }
    }

    public void boolderiv(ParseTreeNode boolderivNode, BooleanNode parentNode) {//StatementsNode parentNode) {
        BooleanNode rightNode = null;
        BooleanNode leftNode = null;
        for (ParseTreeNode child : boolderivNode.getChildren()) {
            System.out.println("Boolderiv child node: " + child.getValue());
            if (child.getValue().equals("condition")) {
                leftNode = condition(child);
            } else if (child.getValue().equals("condition_x")) {
                System.out.println("Going to condition_x");
                rightNode = condition_x(child, leftNode);
                System.out.println("\nIs rightNode null after condition_x? " + (rightNode == null));
                if (rightNode == null) {
                    parentNode.addChild(leftNode);
                    return;
                }
            }
        }
        parentNode.addChild(rightNode);
    }

//    public void numoper(ParseTreeNode numOperNode, AssignmentExpressionNode parentNode, TokenType width){
//        ArithmeticNode rightNode = null;
//        ArithmeticNode leftNode = null;
//        for(ParseTreeNode op: numOperNode.getChildren()){
//            if(op.getValue().equals("term")){
//                leftNode = term(op, width);
//            }else if(op.getValue().equals("term_x")){
//                rightNode = term_x(op,width,leftNode);
//                if (rightNode == null) {
//                    // left precedence??
//                    parentNode.addChild(leftNode);
//                    return;
//                }
//            }
//        }
//        parentNode.addChild(rightNode);
//    }

//    public ArithmeticNode term(ParseTreeNode termNode, TokenType width){
//        ArithmeticNode rightNode = null;
//        ArithmeticNode leftNode = null;
//        for(ParseTreeNode op: termNode.getChildren()){
//            if(op.getValue().equals("factor")){
//                leftNode = factor(op, width);
//            }else if(op.getValue().equals("factor_x")){
//                rightNode = factor_x(op,width, leftNode);
//                // left precedence??
//                if(rightNode == null){
//                    return leftNode;
//                }
//            }
//        }
//        return rightNode;
//    }

    public BooleanNode boolderiv_inner(ParseTreeNode boolderivInnerNode) {
        BooleanNode conditionNode = null;
        BooleanNode rightNode = null;
        for (ParseTreeNode child : boolderivInnerNode.getChildren()) {
            if (child.getValue().equals("condition")) {
                rightNode = condition(child);
            } else if (child.getValue().equals("condition_x")) {
                conditionNode = condition_x(child, rightNode);
                if (conditionNode == null) {
                    return rightNode;
                }
            }
        }
        return conditionNode;
    }

    public BooleanNode condition(ParseTreeNode conditionNode) { //, StatementsNode root) {
        BooleanNode rightNode = null;
        BooleanNode leftNode = null;
//        for (ParseTreeNode child : conditionNode.getChildren()) {
//            System.out.println("Child node of condition node: " + child.getValue());
//        }
        for (ParseTreeNode child : conditionNode.getChildren()) {
            System.out.println("Child node of condition node: " + child.getValue());
            if (child.getValue().equals("pair")) {
                System.out.println("Going to pair");
                leftNode = pair(child);
            } else if (child.getValue().equals("pair_x")) {
                System.out.println("Going to pair_x");
                rightNode = pair_x(child, leftNode);
                if (rightNode == null) {
                    //System.out.println("Returning leftNode from condition: " + leftNode.getValue());
                    return leftNode;
                }
            }
        }
        return rightNode;
    }

//    public ArithmeticNode term(ParseTreeNode termNode, TokenType width){
//        //return new ArithmeticNode<String>("term" + x++,width);
//        ArithmeticNode rightNode = null;
//        ArithmeticNode leftNode = null;
//        for(ParseTreeNode op: termNode.getChildren()){
//            if(op.getValue().equals("factor")){
//                leftNode = factor(op, width);
//            }else if(op.getValue().equals("factor_x")){
//                rightNode = factor_x(op,width, leftNode);
//                // left precedence??
//                if(rightNode == null){
//                    return leftNode;
//                }
//            }
//        }
//        return rightNode;
//    }

//    public ArithmeticNode exponent(ParseTreeNode exponentNode, TokenType width){
//        List<ParseTreeNode> children = exponentNode.getChildren();
//        if(children.get(0).getValue().equals("SUB")){
//            if(children.get(1).getValue().equals("num_final")){
//                ArithmeticNode finalNode = num_final(children.get(1),width);
//                finalNode.setNegative(true);
//                return finalNode;
//            }
//        }else if(children.get(0).getValue().equals("num_final")){
//            return num_final(children.getFirst(), width);
//        }
//        return null;
//    }

    public BooleanNode condition_x(ParseTreeNode condition_xNode, BooleanNode<?> leftNode) {
        BooleanNode<TokenType> subroot = null;
        BooleanNode rightNode = null;
        BooleanNode<TokenType> r = null;
        for (ParseTreeNode child : condition_xNode.getChildren()) { //child nodes
            System.out.println("\ncondition_x node child: " + child.getValue());
            switch (child.getValue()) {
                case "OR" -> {
                    if (!child.getValue().startsWith("OR")) {
                        System.out.println("Invalid operator for condition_x!");
                        System.out.println(child.getValue());
                        System.exit(1);
                    }
                    System.out.println("Creating a new subroot for condition_x");
                    subroot = new BooleanNode<>(child.getValue(),TokenType.OR);
                    subroot.addChild(leftNode);
                    System.out.println("Subroot node: " + subroot.getValue());
                    System.out.println("Subroot node's left child: " + leftNode.getValue());
                }
                case "condition" -> {
                    System.out.println("Entered condition case for condition_x");
                    rightNode = condition(child);
                    System.out.println("rightNode from condition_x is null: " + (rightNode == null));
                    if (subroot == null) {
                        return rightNode;
                    } else {
                        subroot.addChild(rightNode);
//                        System.out.println("Subroot node value at condition_x: " + subroot.getValue());
//                        System.out.println("Subroot's left node value at condition_x: " + leftNode.getValue());
//                        System.out.println("Subroot's right node value at condition_x: " + rightNode.getValue());
                    }
                }
                case "condition_x" -> {
                    r = condition_x(child, subroot);
                    System.out.println("Is r null? " + (r == null));
                    if (r == null) {
                        return subroot;
                    } else {
                        return r;
                    }
                }
            }
        }
        return subroot;
    }

//    public ArithmeticNode term_x(ParseTreeNode termNode, TokenType width, ArithmeticNode<?> leftNode){
//        ArithmeticNode<TokenType> subroot = null;
//        ArithmeticNode rightNode = null;
//        ArithmeticNode<TokenType> r = null;
//        for(ParseTreeNode op: termNode.getChildren()){
//            switch (op.getValue()) {
//                case "ADD", "SUB" -> {
//                    TokenType operator = null;
//                    if (op.getValue().equals("ADD")) {
//                        operator = TokenType.ADD;
//                    } else if (op.getValue().equals("SUB")) {
//                        operator = TokenType.SUB;
//                    } else {
//                        System.out.println("LanguageError: Invalid operator for term!!!");
//                        System.exit(1);
//                    }
//                    subroot = new ArithmeticNode<>(op.getValue() , width);
//                    subroot.setValue(operator);
//                    subroot.addChild(leftNode);
//                }
//                case "term" ->{
//                    rightNode = term(op, width);
//                    if(subroot == null){
//                        return rightNode;
//                    }else {
//                        subroot.addChild(rightNode);
//                    }
//                }
//                case "term_x" ->{
//                    r = term_x(op, width,subroot);
//                    if (r == null) {
//                        // left precedence??
//                        return subroot;
//                    } else{
//                        // left precedence??
//                        return r;
//                    }
//                }
//
//            }
//        }
//        return subroot;
//    }

//    public ArithmeticNode exponent_x(ParseTreeNode exponentNode, TokenType width, ArithmeticNode leftNode){
//        ArithmeticNode<TokenType> subroot = null;
//        ArithmeticNode rightNode = null;
//        ArithmeticNode r = null;
//        for(ParseTreeNode op: exponentNode.getChildren()){
//            switch (op.getValue()) {
//                case "EXP" -> {
//                    TokenType operator = TokenType.EXP;
//                    subroot = new ArithmeticNode<>(op.getValue() , width);
//                    subroot.setValue(operator);
//                    subroot.addChild(leftNode);
//                }
//                case "exponent" ->{
//                    rightNode = exponent(op, width);
//                    if(subroot == null){
//                        return rightNode;
//                    }else{
//                        subroot.addChild(rightNode);
//                    }
//                }
//                case "exponent_x" ->{
//                    r = exponent_x(op, width, subroot);
//                    if (r == null) {
//                        // left precedence??
//                        return subroot;
//                    }else {
//                        // left precedence??
//                        return r;
//                    }
//                }
//
//            }
//        }
//        return subroot;
//    }
//
//    public ArithmeticNode factor_x(ParseTreeNode factorNode, TokenType width, ArithmeticNode<?> leftNode){
//        ArithmeticNode<TokenType> subroot = null;
//        ArithmeticNode  rightNode = null;
//        ArithmeticNode r = null;
//        for(ParseTreeNode op: factorNode.getChildren()){
//            switch (op.getValue()) {
//                case "MUL", "DIV" -> {
//                    TokenType operator = null;
//                    if (op.getValue().equals("MUL")) {
//                        operator = TokenType.MUL;
//                    } else if (op.getValue().equals("DIV")) {
//                        operator = TokenType.DIV;
//                    } else {
//                        System.out.println("LanguageError: Invalid operator for factor_x!!!");
//                        System.exit(1);
//                    }
//                    subroot = new ArithmeticNode<>(op.getValue() , width);
//                    subroot.setValue(operator);
//                    subroot.addChild(leftNode);
//
//                }
//                case "factor" ->{
//                    rightNode = factor(op, width);
//                    if(subroot == null){
//                        return rightNode;
//                    }else{
//                        subroot.addChild(rightNode);
//                    }
//                }
//
//                case "factor_x" ->{
//                    r = factor_x(op, width, subroot);
//                    if (r == null) {
//                        //left precedence??
//                        return subroot;
//                    }else {
//                        //left precedence??
//                        return r;
//                    }
//                }
//            }
//        }
//        return subroot;
//    }

    public BooleanNode pair(ParseTreeNode pairNode) {
        List<ParseTreeNode> children = pairNode.getChildren();
//        for (ParseTreeNode node : children) {
//            System.out.println("Child of pair node: " + node.getValue());
//        }
        if (children.get(0).getValue().equals("INVERT")) { // for the ! or unary invert operation
            if (children.get(1).getValue().equals("boolval")) {
                BooleanNode finalNode = boolval(children.get(1));
                finalNode.setInverted(true); //do a function that inverts the boolean value
                return finalNode;
            }
        } else if (children.get(0).getValue().equals("boolval")) {
            System.out.println("pair child: " + children.get(0).getValue());
            return boolval(children.getFirst());
        }
        System.out.println("Pair node child: " + children.getFirst().getValue());
        System.out.println("null printing for pair node");
        return null;
    }

    public BooleanNode pair_x(ParseTreeNode pair_xNode, BooleanNode<?> leftNode) {
        BooleanNode<TokenType> subroot = null;
        BooleanNode rightNode = null;
        BooleanNode<TokenType> r = null;
        for (ParseTreeNode child : pair_xNode.getChildren()) {
            System.out.println("Child node of pair_x node: " + child.getValue());
            switch (child.getValue()) {
                case "AND" -> {
                    subroot = new BooleanNode<>(child.getValue(),TokenType.AND);
                    subroot.addChild(leftNode);
                }
                case "pair" -> {
                    rightNode = pair(child);
                    if (subroot == null){
                        return rightNode;
                    } else {
                        subroot.addChild(rightNode);
                    }
                }
                case "pair_x" -> {
                    r = pair_x(child, subroot);
                    if (r == null) {
                        return subroot;
                    } else {
                        return r;
                    }
                }
//                default -> {
//                    System.out.println("Invalid operator for pair_x!");
//                    System.out.println(child.getValue());
//                    System.exit(1);
//                }
            }
        }
        return subroot;
    }

//    public ArithmeticNode exponent_x(ParseTreeNode exponentNode, TokenType width, ArithmeticNode leftNode){
//        ArithmeticNode<TokenType> subroot = null;
//        ArithmeticNode rightNode = null;
//        ArithmeticNode r = null;
//        for(ParseTreeNode op: exponentNode.getChildren()){
//            switch (op.getValue()) {
//                case "EXP" -> {
//                    TokenType operator = TokenType.EXP;
//                    subroot = new ArithmeticNode<>(op.getValue() , width);
//                    subroot.setValue(operator);
//                    subroot.addChild(leftNode);
//                }
//                case "exponent" ->{
//                    rightNode = exponent(op, width);
//                    if(subroot == null){
//                        return rightNode;
//                    }else{
//                        subroot.addChild(rightNode);
//                    }
//                }
//                case "exponent_x" ->{
//                    r = exponent_x(op, width, subroot);
//                    if (r == null) {
//                        // left precedence??
//                        return subroot;
//                    }else {
//                        // left precedence??
//                        return r;
//                    }
//                }
//
//            }
//        }
//        return subroot;
//    }

    public BooleanNode boolval(ParseTreeNode boolvalNode) { //IN PROGRESS
        BooleanNode subtree = null;
        ArithmeticNode operandNodeL = null;
        BooleanNode operatorNode = null;
        ArithmeticNode operandNodeR = null;
        for (ParseTreeNode child : boolvalNode.getChildren()) {
            System.out.println("Child node of boolvalNode: " + child.getValue());
            if (child.getValue().equals("bool_operand")) {
                System.out.println("bool_operand child value: " + child.getChildren().getFirst().getValue());
                if (child.getChildren().getFirst().getValue().equals("TRUE") || child.getChildren().getFirst().getValue().equals("FALSE")) {
                    System.out.println("Returning Boolean Node from boolval");
                    BooleanNode finalNode;
                    if (child.getChildren().getFirst().getValue().equals("TRUE")) {
                        finalNode = new BooleanNode("true", true); //boolval(child);
                    } else {
                        finalNode = new BooleanNode("false", false); //boolval(child);
                    }
                    return finalNode;
                }
            } else if (child.getValue().equals("boolderiv")) {
                return boolderiv_inner(child);
            } else if (child.getValue().equals("numoper")) {
                if (operandNodeL == null) {
                    operandNodeL = numoper(child);
                } else {
                    operandNodeR = numoper(child);
                }
            } else if (child.getValue().equals("relation")) {
                //operatorNode = relation(child, operandNodeL, operandNodeR);
                operatorNode = relation(child);
            }
            if(operatorNode != null && operandNodeL != null && operandNodeR != null){
                subtree = new BooleanNode<>("Relation: "+operatorNode.getValue());
                subtree.setValue(operatorNode.getValue());
                subtree.addChild(operandNodeL);
                subtree.addChild(operandNodeR);
            }
        }

        return subtree;
    }

    public BooleanNode relation(ParseTreeNode relationNode) {//, ASTNode leftNode, ASTNode rightNode) {
        for (ParseTreeNode child : relationNode.getChildren()) {
            System.out.println("Child node of relationNode: " + child.getValue());
            if (child.getValue().equals("eq_rel")) {
                return eq_rel(child);//, leftNode, rightNode);
            } else if (child.getValue().equals("size_rel")) {
                return size_rel(child);//, leftNode, rightNode);
            }
        }
        return null;
    }

    public BooleanNode eq_rel(ParseTreeNode eq_relNode) {
        BooleanNode<TokenType> eq_operator = null;
        for (ParseTreeNode child : eq_relNode.getChildren()) {
            System.out.println("Child node of eq_relNode: " + child.getValue());
            if (child.getValue().equals("IS")) {
                eq_operator = new BooleanNode("IS",TokenType.IS);
            } else if (child.getValue().equals("eq_right")) {
                if (child.getChildren().getFirst().getValue().equals("NOT")) {
                    eq_operator = new BooleanNode("NOT",TokenType.NOT);
                }
            }
        }
        return eq_operator;
    }

    public BooleanNode size_rel(ParseTreeNode size_relNode) {
        BooleanNode<TokenType> size_operator = null;
        for (ParseTreeNode child : size_relNode.getChildren()) {
            System.out.println("Child node of size_relNode: " + child.getValue());
            switch(child.getValue()) {
                case "GT" -> size_operator = new BooleanNode<>("GT",TokenType.GT);
                case "LT" -> size_operator = new BooleanNode<>("LT", TokenType.LT);
                case "GTE" -> size_operator = new BooleanNode<>("GTE", TokenType.GTE);
                case "LTE" -> size_operator = new BooleanNode<>("LTE", TokenType.LTE);
            }
        }
        return size_operator;
    }

    public ArithmeticNode numoper(ParseTreeNode numoperNode) {
        ArithmeticNode rightNode = null;
        ArithmeticNode leftNode = null;
        for (ParseTreeNode child : numoperNode.getChildren()) {
            System.out.println("Child node of numoper node: " + child.getValue());
            if (child.getValue().equals("term")) {
                leftNode = term(child, TokenType.MOLE64);
            } else if (child.getValue().equals("term_x")) {
                rightNode = term_x(child, TokenType.MOLE64, leftNode);
                if (rightNode == null) {
                    return leftNode;
                }
            }
        }
        return rightNode;
    }

//    public void numoper(ParseTreeNode numOperNode, AssignmentExpressionNode parentNode, TokenType width){
//        ArithmeticNode rightNode = null;
//        ArithmeticNode leftNode = null;
//        for(ParseTreeNode op: numOperNode.getChildren()){
//            if(op.getValue().equals("term")){
//                leftNode = term(op, width);
//            }else if(op.getValue().equals("term_x")){
//                rightNode = term_x(op,width,leftNode);
//                if (rightNode == null) {
//                    // left precedence??
//                    parentNode.addChild(leftNode);
//                    return;
//                }
//            }
//        }
//        parentNode.addChild(rightNode);
//    }

    //    public ArithmeticNode num_final(ParseTreeNode finalNode, TokenType width){ //the function itself is meant for operands
//        for(ParseTreeNode type: finalNode.getChildren()){
//            if(type.getValue().equals("numexpr")){
//                if(type.getChildren().getFirst().getValue().startsWith("NUMLIT")){
//                    String literal = type.getChildren().getFirst().getValue().substring(7, type.getChildren().getFirst().getValue().length()-1);
//                    if(width == TokenType.MOLE32){
//                        ArithmeticNode<Integer> intLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE32);
//                        try{
//                            if(literal.toLowerCase().startsWith("0x")){
//                                intLit.setValue(Integer.parseInt(literal.toLowerCase().substring(2), 16));
//                            }else if(literal.toLowerCase().startsWith("0c")){
//                                intLit.setValue(Integer.parseInt(literal.toLowerCase().substring(2), 8));
//                            } else {
//                                intLit.setValue(Integer.parseInt(literal));
//                            }
//
//                        }catch (NumberFormatException nfe){
//                            System.out.println("The value " + literal.toLowerCase() + " is not valid for " + width);
//
//                            System.exit(1);
//                        }
//                        return intLit;
//                    }
//                    if(width == TokenType.MOLE64){
//                        ArithmeticNode<Long> longLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE64);
//                        try{
//                            if(literal.toLowerCase().startsWith("0x")){
//                                longLit.setValue(Long.parseLong(literal.toLowerCase().substring(2), 16));
//                            }else if(literal.toLowerCase().startsWith("0c")){
//                                longLit.setValue(Long.parseLong(literal.toLowerCase().substring(2), 8));
//                            } else {
//                                longLit.setValue(Long.parseLong(literal));
//                            }
//
//                        }catch (NumberFormatException nfe){
//                            System.out.println("The value " + literal.toLowerCase() + " is not valid for " + width);
//                            System.exit(1);
//                        }
//                        return longLit;
//                    }
//                }
//            }else if(type.getValue().startsWith("IDENT")){
//                String varName = type.getValue().substring(6, type.getValue().length()-1);
//                ArithmeticNode<String> varNode = new ArithmeticNode<>("Numerical Variable "+ x++ +": " + varName, TokenType.IDENT);
//                if(!variables.containsKey(varName)){
//                    System.out.println("Variable " + varName + " has not been declared, or is uninitialized.");
//                    System.exit(1);
//                }
//                varNode.setValue(varName);
//
//                return varNode;
//            }else if(type.getValue().startsWith("numoper")){
//                return numoper_inner(type,width);
//            }
//        }
//        return null;
//    }

    public void loop(ParseTreeNode loopNode, StatementsNode root) {
        //System.out.println(loopNode.getChildren().get(0).getValue());
        //System.out.println(loopNode.getChildren().get(1).getValue());
        //System.out.println(loopNode.getChildren().get(0).getChildren().getFirst().getValue());
        //System.out.println(loopNode.getChildren().get(0).getChildren().get(1).getValue());
        //LoopType type = switch (loopNode.getChildren().getFirst().getChildren().getFirst().getValue()) {
        LoopType type = switch (loopNode.getChildren().getFirst().getValue()) {
            case "DISTILL" -> LoopType.DISTILL;
            case "FERMENT" -> LoopType.FERMENT;
            default -> null;
        };
        //LoopNode ln = new LoopNode("LOOP",type);
        LoopNode ln = null;
        if (type == LoopType.DISTILL) {
            ln = new LoopNode("DISTILL", type);
            //distill(loopNode, root);
        } else if (type == LoopType.FERMENT) {
            ln = new LoopNode("FERMENT", type);
            //ferment(loopNode, root);
        }
        root.addChild(ln);
//        if (loopNode.getChildren().getFirst().getValue().equals("distill")) {
//            ParseTreeNode distillNode = loopNode.getChildren().getFirst();
//            distill(distillNode, root);
//        } else if (loopNode.getChildren().getFirst().getValue().equals("ferment")) {
//            ParseTreeNode fermentNode = loopNode.getChildren().getFirst();
//            ferment(fermentNode, root);
//        }
    }

//    public void stdout(ParseTreeNode stdoutNode, StatementsNode root){
//        IoType type = switch (stdoutNode.getChildren().getFirst().getChildren().getFirst().getValue()) {
//            case "PRINT" -> IoType.PRINT;
//            case "PRINTLN" -> IoType.PRINTLN;
//            default -> null;
//        };
//        StdioNode p = new StdioNode("PRINT",type);
//        extractContents(stdoutNode, p);
//        root.addChild(p);
//    }

//    public void stdio(ParseTreeNode stdioNode, StatementsNode root){
//        if(stdioNode.getChildren().getFirst().getValue().equals("stdout")){
//            ParseTreeNode stdoutNode = stdioNode.getChildren().getFirst();
//            //System.out.println("stdoutNode: " + stdoutNode.getValue());
//            stdout(stdoutNode,root);
//        }else if(stdioNode.getChildren().getFirst().getValue().equals("stderr")){
//            ParseTreeNode stderrNode = stdioNode.getChildren().getFirst();
//            //System.out.println("stdoutNode: " + stdoutNode.getValue());
//            stderr(stderrNode,root);
//        }
//    }

    public void distill(ParseTreeNode distillNode, StatementsNode root){
        LoopNode ln = new LoopNode("DISTILL", LoopType.DISTILL);
        for (ParseTreeNode child : distillNode.getChildren()) {
            System.out.println("Distill child: " + child.getValue());
            if (child.getValue().equals("DISTILL")) {
                //LoopNode ln = new LoopNode("DISTILL", LoopType.DISTILL);
                root.addChild(ln);
            } else if (child.getValue().equals("statements_x")) {
                StatementsNode body = new StatementsNode("Body");
                statements_x(child, body);
                ln.addChild(body);
                //root.addChild(body);
            } else if (child.getValue().equals("boolderiv")) {
                BooleanNode cond = new BooleanNode("Condition");
                //boolderiv(child, root);
                boolderiv(child, cond);
                //root.addChild(cond);
                ln.addChild(cond);
            }
//            } else {
//                System.out.println("Illegal value found at distill: " + child.getValue());
//                System.exit(1);
//            }
        }
//        ParseTreeNode bodyNode = distillNode.getChildren().get(0); //List<ParseTreeNode>
//        System.out.println(bodyNode.getValue());
//        ParseTreeNode conditionNode = distillNode.getChildren().get(1);
//        System.out.println(conditionNode.getValue());
//        if (bodyNode.getChildren() != null) {
//            for (ParseTreeNode child : bodyNode.getChildren()) {
//                if (child.getValue().equals("statement")){
//                    statement(child, root);
//                } else if (child.getValue().equals("statements_x")){
//                    statements_x(child, root);
//                }
//            }
//        }
    }

    public void ferment(ParseTreeNode fermentNode, StatementsNode root){
//        ParseTreeNode bodyNode = statementsNode.getChildren().get(1);
//        System.out.println(bodyNode.getValue());
//        ParseTreeNode conditionNode = statementsNode.getChildren().get(0);
//        System.out.println(conditionNode.getValue());
//        if (bodyNode.getChildren() != null) {
//            for (ParseTreeNode child : bodyNode.getChildren()) {
//                if (child.getValue().equals("statement")){
//                    statement(child, root);
//                } else if (child.getValue().equals("statements_x")){
//                    statements_x(child, root);
//                }
//            }
//        }
        LoopNode ln = new LoopNode("FERMENT", LoopType.FERMENT);
        for (ParseTreeNode child : fermentNode.getChildren()) {
            System.out.println("ferment children: " + child.getValue());
            if (child.getValue().startsWith("FERMENT")) {
                //LoopNode ln = new LoopNode("FERMENT", LoopType.FERMENT);
                root.addChild(ln);
            }else if (child.getValue().startsWith("boolderiv")) {
                //add a new node for the condition
                BooleanNode cond = new BooleanNode("Condition");
                //boolderiv(child, root);
                boolderiv(child, cond);
                ln.addChild(cond);
                //root.addChild(cond);
            } else if (child.getValue().startsWith("cond_body")) {
                System.out.println("Entering cond_body");
                //cond_body(child, root);
                cond_body(child, ln);
            }
//            } else {
//                System.out.println("Illegal value found: " + child.getValue());
//                System.exit(1);
//            }
        }
    }

    public void cond_stmt(ParseTreeNode cond_stmtNode, StatementsNode root) {
        for (ParseTreeNode child : cond_stmtNode.getChildren()) {
//            if (child.getValue().startsWith("filter_stmt")) {
//                //System.out.println("Going to filter");
//                filter(child, root);
//            }
            //System.out.println("Cond_stmt children: " + child.getValue());
            if (child.getValue().equals("filter_stmt")) {
                filter_stmt(child, root);
            }
        }
    }

    public void filter_stmt(ParseTreeNode filter_exprNode, StatementsNode root) {
        //System.out.println("entered filter_stmt");
        for (ParseTreeNode child : filter_exprNode.getChildren()) {
            if (child.getValue().equals("filter_expr")) {
                //System.out.println("going to filter method");
                filter(child, root);
            } else if (child.getValue().equals("funnel_expr")){
                funnel(child, root);
            }
        }
    }

    public void filter(ParseTreeNode filterNode, StatementsNode root){
        //ConditionalNode condFilter = null;
        //ConditionalNode condFunnel = null;
        ConditionalNode cn = new ConditionalNode("FILTER");
        for (ParseTreeNode child : filterNode.getChildren()) {
            //System.out.println("Filter children: " + child.getValue());
            if (child.getValue().equals("FILTER")) {
                System.out.println("adding filter node to root");
                root.addChild(cn);
            } else if (child.getValue().equals("boolderiv")) {
                BooleanNode cond = new BooleanNode("Condition");
                //boolderiv(child, root);
                boolderiv(child, cond);
                cn.addChild(cond);
            } else if (child.getValue().equals("cond_body")) {
                cond_body(child, cn);
            }
        }
    }

    public void funnel(ParseTreeNode funnelNode, ASTNode root) {
        ConditionalNode cn = new ConditionalNode("FUNNEL");
        for (ParseTreeNode child : funnelNode.getChildren()) {
            if (child.getValue().equals("FUNNEL")) {
                root.addChild(cn);
            } else if (child.getValue().equals("funnel_right")) { //contains the body of the funnel
                funnel(child, cn);
            } else if (child.getValue().equals("cond_body")) {
                cond_body(child, root);
            }
        }
    }

    public void simpleStatement(ParseTreeNode simpleStatementNode, StatementsNode root){
        ParseTreeNode childNode = simpleStatementNode.getChildren().getFirst();
        if(childNode.getValue().equals("stdio")){
            stdio(childNode, root);
        } else if(childNode.getValue().equals("vardeclare")){
            vardeclare(childNode, root);
        } else if(childNode.getValue().equals("varassign")){
            varassign(childNode, root);
        }
    }

    public void varassign(ParseTreeNode varAssignNode, StatementsNode root){
        String id = "";
        for(ParseTreeNode child: varAssignNode.getChildren()){
            if(child.getValue().startsWith("IDENT")){
                id = child.getValue().substring(6, child.getValue().length()-1);
                if(!variables.containsKey(id)){
                    System.out.println("TypeError: Variable " + id + " has not been declared.");
                    System.exit(1);
                }
                NumericalVariable v = variables.get(id);
                if(!v.isMutable() && v.hasBeenAssigned()){
                    System.out.println("MutabilityError: Inert Variable " + id + " already contains a value which cannot be changed.");
                    System.exit(1);
                }

            }
            if(child.getValue().equals("assignment")){
                if(variables.get(id).getType().equals("Integer")){
                    VarAssignmentNode vn = new VarAssignmentNode("Variable assignment for: " + id, id);
                    assignment(child.getChildren().getFirst(),vn, TokenType.MOLE32);
                    root.addChild(vn);
                } else if(variables.get(id).getType().equals("Long")){
                    VarAssignmentNode vn = new VarAssignmentNode("Variable assignment for: " + id, id);
                    assignment(child,vn,TokenType.MOLE64);
                    root.addChild(vn);
                }else{
                    System.out.println("LanguageError: There was an error parsing the variable assignment statement.");
                    System.exit(1);
                }
            }
        }
    }

    public void vardeclare(ParseTreeNode varNode, StatementsNode root){
        if(varNode.getChildren() != null){
            List<ParseTreeNode> varAttribs = varNode.getChildren();
            TokenType numType = null;
            String varName;
            boolean isMutable = false;
            VariableDeclarationNode variableDeclarationNode = null;
            //mut_type
            //IDENT
            varName = varAttribs.get(2).getValue().substring(6,varAttribs.get(2).getValue().length()-1);
            //numtype
            if(varAttribs.get(1).getChildren().getFirst().getValue().equals("MOLE32")){
                numType = TokenType.MOLE32;
            }else if(varAttribs.get(1).getChildren().getFirst().getValue().equals("MOLE64")){
                numType = TokenType.MOLE64;
            }
            if(varAttribs.get(0).getChildren().getFirst().getValue().equals("REACTIVE")){
                isMutable = true;
                variableDeclarationNode = new VariableDeclarationNode(TokenType.REACTIVE, numType, varName);
            }else if(varAttribs.get(0).getChildren().getFirst().getValue().equals("INERT")){
                variableDeclarationNode = new VariableDeclarationNode(TokenType.INERT, numType, varName);
            }

            if(varAttribs.get(3).getChildren() == null){
                System.out.println("\n\nVarError: Uninitialized Variables are not allowed in argon.");
                System.exit(1);
            }
            if(variables.containsKey(varName)){
                System.out.println("VarError: Variable " + varName + " has already been declared.");
                System.exit(1);
            }
            assignment(varAttribs.get(3),variableDeclarationNode,numType);
            if(numType == TokenType.MOLE32){
                NumericalVariable<Integer> var32 = new NumericalVariable<>(varName,0, isMutable);
                variables.put(varName, var32);
            }else if(numType == TokenType.MOLE64){
                NumericalVariable<Long> var64 = new NumericalVariable<>(varName,0L, isMutable);
                variables.put(varName, var64);
            }

            root.addChild(variableDeclarationNode);
        }
    }
    public void assignment(ParseTreeNode assignmentParseTreeNode, VarAssignmentNode variableDeclarationNode, TokenType width){
       // System.out.println(assignmentParseTreeNode.getChildren().getFirst().getValue());
        assert assignmentParseTreeNode.getChildren().getFirst().getValue().equals("assign_oper"):"Invalid index to check for assign oper!!!";
        String varAssignType = assignmentParseTreeNode.getChildren().getFirst().getChildren().getFirst().getValue();
       // System.out.println("var assign type: "+varAssignType);
        AssignmentExpressionNode assignmentExpressionNode = null;
        if(varAssignType.equals("ASSIGN")){
            assignmentExpressionNode = new AssignmentExpressionNode(TokenType.ASSIGN);
        }else if(variableDeclarationNode instanceof VariableDeclarationNode){
            System.out.println("VarError: Compound assignemnt operations are not allowed on variable declarations.");
            System.exit(1);
        }else {
            switch (varAssignType) {
                case "ADDASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.ADDASSIGN);
                case "SUBASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.SUBASSIGN);
                case "EXPASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.EXPASSIGN);
                case "MULASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.MULASSIGN);
                case "DIVASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.DIVASSIGN);
                default -> {
                    System.out.println("\n\nLanguageError: Invalid Assignment Operation.");
                    System.exit(1);
                }
            }
        }

        assert assignmentParseTreeNode.getChildren().get(1).getValue().equals("assign_after"):"Invalid index to check for assign_after!!!";
        if(assignmentParseTreeNode.getChildren().get(1).getValue().equals("assign_after")){
            assign_after(assignmentParseTreeNode.getChildren().get(1),assignmentExpressionNode,width);
        }

        variableDeclarationNode.addChild(assignmentExpressionNode);
    }

    public void assign_after(ParseTreeNode assignAfterNode, AssignmentExpressionNode parentNode, TokenType width){
        if(assignAfterNode.getChildren() != null){
            numoper(assignAfterNode.getChildren().getFirst(), parentNode, width);
        }else{
            System.out.println("SyntaxError: Missing expression after = on variable declaration.");
            System.exit(1);
        }
    }

    public void numoper(ParseTreeNode numOperNode, AssignmentExpressionNode parentNode, TokenType width){
        ArithmeticNode rightNode = null;
        ArithmeticNode leftNode = null;
        for(ParseTreeNode op: numOperNode.getChildren()){
            if(op.getValue().equals("term")){
                leftNode = term(op, width);
            }else if(op.getValue().equals("term_x")){
                rightNode = term_x(op,width,leftNode);
                if (rightNode == null) {
                    // left precedence??
                    parentNode.addChild(leftNode);
                    return;
                }
            }
        }
        parentNode.addChild(rightNode);
    }

    public ArithmeticNode numoper_inner(ParseTreeNode numOperInnerNode, TokenType width){
        ArithmeticNode termNode = null;
        ArithmeticNode rightNode = null;
        //Collections.reverse(numOperInnerNode.getChildren());
        for(ParseTreeNode op: numOperInnerNode.getChildren()){
            if(op.getValue().equals("term")){
                rightNode = term(op, width);
            }else if(op.getValue().equals("term_x")){
                termNode = term_x(op,width,rightNode);
                if (termNode == null) {
                    // left precedence??
                    return rightNode;
                }

            }
        }
        return termNode;
    }
    int x = 0;
    public ArithmeticNode term(ParseTreeNode termNode, TokenType width){
        //return new ArithmeticNode<String>("term" + x++,width);
        ArithmeticNode rightNode = null;
        ArithmeticNode leftNode = null;
        for(ParseTreeNode op: termNode.getChildren()){
            if(op.getValue().equals("factor")){
                leftNode = factor(op, width);
            }else if(op.getValue().equals("factor_x")){
                rightNode = factor_x(op,width, leftNode);
                // left precedence??
                if(rightNode == null){
                    return leftNode;
                }
            }
        }
        return rightNode;
    }

    public ArithmeticNode factor(ParseTreeNode factorNode, TokenType width){
        ArithmeticNode<TokenType> rightNode  = null;
        ArithmeticNode leftNode = null;
        for(ParseTreeNode op: factorNode.getChildren()){
            if(op.getValue().equals("exponent")){
                leftNode = exponent(op, width);
            }else if(op.getValue().equals("exponent_x")){
                rightNode = exponent_x(op,width,leftNode);
                // left precedence??
                if(rightNode == null){
                    return leftNode;
                }
            }
        }
        return rightNode;
    }

    public ArithmeticNode exponent(ParseTreeNode exponentNode, TokenType width){
        List<ParseTreeNode> children = exponentNode.getChildren();
        if(children.get(0).getValue().equals("SUB")){
            if(children.get(1).getValue().equals("num_final")){
                ArithmeticNode finalNode = num_final(children.get(1),width);
                finalNode.setNegative(true);
                return finalNode;
            }
        }else if(children.get(0).getValue().equals("num_final")){
           return num_final(children.getFirst(), width);
        }
        return null;
    }

    public ArithmeticNode num_final(ParseTreeNode finalNode, TokenType width){
        for(ParseTreeNode type: finalNode.getChildren()){
            if(type.getValue().equals("numexpr")){
                if(type.getChildren().getFirst().getValue().startsWith("NUMLIT")){
                    String literal = type.getChildren().getFirst().getValue().substring(7, type.getChildren().getFirst().getValue().length()-1);
                    if(width == TokenType.MOLE32){
                        ArithmeticNode<Integer> intLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE32); //create a new ArithmeticNode
                        try{
                            if(literal.toLowerCase().startsWith("0x")){
                                intLit.setValue(Integer.parseInt(literal.toLowerCase().substring(2), 16));
                            }else if(literal.toLowerCase().startsWith("0c")){
                                intLit.setValue(Integer.parseInt(literal.toLowerCase().substring(2), 8));
                            } else {
                                intLit.setValue(Integer.parseInt(literal));
                            }

                        }catch (NumberFormatException nfe){
                            System.out.println("TypeError: The value " + literal.toLowerCase() + " is not valid for " + width);

                            System.exit(1);
                        }
                        return intLit; //then return it
                    }
                    if(width == TokenType.MOLE64){
                        ArithmeticNode<Long> longLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE64);
                        try{
                            if(literal.toLowerCase().startsWith("0x")){
                                longLit.setValue(Long.parseLong(literal.toLowerCase().substring(2), 16));
                            }else if(literal.toLowerCase().startsWith("0c")){
                                longLit.setValue(Long.parseLong(literal.toLowerCase().substring(2), 8));
                            } else {
                                longLit.setValue(Long.parseLong(literal));
                            }

                        }catch (NumberFormatException nfe){
                            System.out.println("TypeError: The value " + literal.toLowerCase() + " is not valid for " + width);
                            System.exit(1);
                        }
                        return longLit;
                    }
                }
            }else if(type.getValue().startsWith("IDENT")){
                String varName = type.getValue().substring(6, type.getValue().length()-1);
                ArithmeticNode<String> varNode = new ArithmeticNode<>("Numerical Variable "+ x++ +": " + varName, TokenType.IDENT);
                if(!variables.containsKey(varName)){
                    System.out.println("VarError: Variable " + varName + " has not been declared, or is uninitialized.");
                    System.exit(1);
                }
                varNode.setValue(varName);

                return varNode;
            }else if(type.getValue().startsWith("numoper")){
                return numoper_inner(type,width);
            }else if(type.getValue().equals("stdin")){
                ArithmeticNode<TokenType> stdin = new ArithmeticNode<>("Input", width);
                stdin.setValue(TokenType.INPUT);
                stdin.addChild(stdin(type));
                return stdin;
            }
        }
        return null;
    }



    public ArithmeticNode exponent_x(ParseTreeNode exponentNode, TokenType width, ArithmeticNode leftNode){
        ArithmeticNode<TokenType> subroot = null;
        ArithmeticNode rightNode = null;
        ArithmeticNode r = null;
        for(ParseTreeNode op: exponentNode.getChildren()){
            switch (op.getValue()) {
                case "EXP" -> {
                    TokenType operator = TokenType.EXP;
                    subroot = new ArithmeticNode<>(op.getValue() , width);
                    subroot.setValue(operator);
                    subroot.addChild(leftNode);
                }
                case "exponent" ->{
                    rightNode = exponent(op, width);
                    if(subroot == null){
                        return rightNode;
                    }else{
                        subroot.addChild(rightNode);
                    }
                }
                case "exponent_x" ->{
                    r = exponent_x(op, width, subroot);
                    if (r == null) {
                        // left precedence??
                        return subroot;
                    }else {
                        // left precedence??
                        return r;
                    }
                }

            }
        }
        return subroot;
    }

    public ArithmeticNode factor_x(ParseTreeNode factorNode, TokenType width, ArithmeticNode<?> leftNode){
        ArithmeticNode<TokenType> subroot = null;
        ArithmeticNode  rightNode = null;
        ArithmeticNode r = null;
        for(ParseTreeNode op: factorNode.getChildren()){
            switch (op.getValue()) {
                case "MUL", "DIV" -> {
                    TokenType operator = null;
                    if (op.getValue().equals("MUL")) {
                        operator = TokenType.MUL;
                    } else if (op.getValue().equals("DIV")) {
                        operator = TokenType.DIV;
                    } else {
                        System.out.println("LanguageError: Invalid operator for factor_x!!!");
                        System.exit(1);
                    }
                    subroot = new ArithmeticNode<>(op.getValue() , width);
                    subroot.setValue(operator);
                    subroot.addChild(leftNode);

                }
                case "factor" ->{
                    rightNode = factor(op, width);
                    if(subroot == null){
                        return rightNode;
                    }else{
                        subroot.addChild(rightNode);
                    }
                }

                case "factor_x" ->{
                    r = factor_x(op, width, subroot);
                    if (r == null) {
                        //left precedence??
                        return subroot;
                    }else {
                        //left precedence??
                        return r;
                    }
                }
            }
        }
        return subroot;
    }



    public ArithmeticNode term_x(ParseTreeNode termNode, TokenType width, ArithmeticNode<?> leftNode){
        ArithmeticNode<TokenType> subroot = null;
        ArithmeticNode rightNode = null;
        ArithmeticNode<TokenType> r = null;
        for(ParseTreeNode op: termNode.getChildren()){
            switch (op.getValue()) {
                case "ADD", "SUB" -> {
                    TokenType operator = null;
                    if (op.getValue().equals("ADD")) {
                        operator = TokenType.ADD;
                    } else if (op.getValue().equals("SUB")) {
                        operator = TokenType.SUB;
                    } else {
                        System.out.println("LanguageError: Invalid operator for term!!!");
                        System.exit(1);
                    }
                    subroot = new ArithmeticNode<>(op.getValue() , width);
                    subroot.setValue(operator);
                    subroot.addChild(leftNode);
                }
                case "term" ->{
                    rightNode = term(op, width);
                    if(subroot == null){
                        return rightNode;
                    }else {
                        subroot.addChild(rightNode);
                    }
                }
                case "term_x" ->{
                    r = term_x(op, width,subroot);
                    if (r == null) {
                        // left precedence??
                        return subroot;
                    } else{
                        // left precedence??
                        return r;
                    }
                }

            }
        }
        return subroot;
    }




    public void stdio(ParseTreeNode stdioNode, StatementsNode root){
        if(stdioNode.getChildren().getFirst().getValue().equals("stdout")){
            ParseTreeNode stdoutNode = stdioNode.getChildren().getFirst();
            //System.out.println("stdoutNode: " + stdoutNode.getValue());
            stdout(stdoutNode,root);
        }else if(stdioNode.getChildren().getFirst().getValue().equals("stderr")){
            ParseTreeNode stderrNode = stdioNode.getChildren().getFirst();
            //System.out.println("stdoutNode: " + stdoutNode.getValue());
            stderr(stderrNode,root);
        }
    }



    public void stderr(ParseTreeNode stderrNode, StatementsNode root){
        IoType type = null;
        //System.out.println("val" +stderrNode.getChildren().get(0).getValue());
        assert stderrNode.getChildren().getFirst().getValue().equals("PRINTERR"): "type not equal to stderr!!!";
        if(stderrNode.getChildren().getFirst().getValue().equals("PRINTERR")){
            type = IoType.PRINTERR;
        }
        //String contents = extractContents(stderrNode);
        StdioNode p = new StdioNode("PRINTERR",type);
        extractContents(stderrNode, p);
        root.addChild(p);
    }

    public void stdout(ParseTreeNode stdoutNode, StatementsNode root){
        IoType type = switch (stdoutNode.getChildren().getFirst().getChildren().getFirst().getValue()) {
            case "PRINT" -> IoType.PRINT;
            case "PRINTLN" -> IoType.PRINTLN;
            default -> null;
        };
        StdioNode p = new StdioNode("PRINT",type);
        extractContents(stdoutNode, p);
        root.addChild(p);
    }

    private void extractContents(ParseTreeNode stdNode, StdioNode root) {
        ParseTreeNode contents = stdNode.getChildren().get(2);
        if(contents.getChildren() != null){
            content(contents,root);
        }
    }

    public StdioNode stdin(ParseTreeNode stdinNode){
        StdioNode in = new StdioNode("Input",IoType.INPUT);
        extractContents(stdinNode, in);
        return in;
    }

    public void content(ParseTreeNode strExprNode, StdioNode root){
        for(ParseTreeNode expr: strExprNode.getChildren()){
            if(expr.getValue().equals("strterm")){
                strTerm(expr, root);
            }else if(expr.getValue().equals("strterm_x")){
                strTerm_x(expr, root);
            }
        }
    }

    public void strTerm(ParseTreeNode strTermNode, StdioNode root){
        if(strTermNode.getValue().equals("strterm")){
            if(strTermNode.getChildren() != null){
                for(ParseTreeNode term: strTermNode.getChildren()){
                    if(term.getValue().startsWith("STRLIT")){
                        String escapedString = getEscapedString(term);
                        String escapedSubstring = getEscapedString(term).substring(7,escapedString.length()-1);
                        //System.out.println("add");
                        root.addChild(new ContentNode("Strlit: " + escapedSubstring,TokenType.STRLIT, escapedSubstring));
                    }else if(term.getValue().startsWith("IDENT")){
                        String ident = term.getValue().substring(6, term.getValue().length()-1);
                        if(!variables.containsKey(ident)){
                            System.out.println("VarError: Variable " + ident + " has not been declared, or is uninitialized.");
                            System.exit(1);
                        }
                        root.addChild(new ContentNode("IDENT: " + ident, TokenType.IDENT, ident));
                    }else if(term.getValue().equals("stdin")){
                        if(root.getIoType() == IoType.INPUT){
                            System.out.println("LanguageError: Nested input statements are not allowed in Argon.");
                            System.exit(1);
                        }
                        root.addChild(stdin(term));
                    }
                }
            }
        }
    }

    private static String getEscapedString(ParseTreeNode term) {
        String escapedString = term.getValue().replace("\\n","\n");
        //re-escape escape sequences
        escapedString = escapedString.replace("\\n","\n");
        escapedString = escapedString.replace("\\t","\t");
        escapedString = escapedString.replace("\\r","\r");
        escapedString = escapedString.replace("\\b","\b");
        escapedString = escapedString.replace("\\\"","\"");
        escapedString = escapedString.replace("\\f","\f");
        escapedString = escapedString.replace("\\\\","\\");
        return escapedString;
    }

    public void strTerm_x(ParseTreeNode strTerm_xNode, StdioNode root){
       if(strTerm_xNode.getChildren() != null){
           for(ParseTreeNode term: strTerm_xNode.getChildren()){
               if(term.getValue().equals("strterm")){
                   strTerm(term, root);
               }else if(term.getValue().equals("strterm_x")){
                   strTerm_x(term, root);
               }
           }
       }
    }

}
