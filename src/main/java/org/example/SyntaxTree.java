package org.example;

import org.example.AST.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxTree {
    private final StatementsNode root;

    private int processedStatement;
    public SyntaxTree(ParseTree tree){
        this.root = new StatementsNode();
        processedStatement = 0;
    }
    public StatementsNode getRoot() {
        return root;
    }

    public void buildAST(ParseTree tree){
        if(tree.getRoot().getValue().equals("program")){
            ParseTreeNode statementsNode = tree.getRoot().getChildren().get(0);
            if(statementsNode != null){
                //System.out.println("statementsNode: " + statementsNode.getValue());
                statements(statementsNode);
            }
        }
        root.print(0);
    }

    public void statements(ParseTreeNode statementsNode){
       for(ParseTreeNode child: statementsNode.getChildren()){
           if(child.getValue().equals("statement")){
               //System.out.println("statementNodeOuter: " + child.getValue());
               statement(child);
           } else if (child.getValue().equals("statements_x")) {
               //System.out.println("statements_xNodeOuter: " + child.getValue());
               statements_x(child);
           }
       }
    }

    public void statement(ParseTreeNode statementNode){
        //System.out.println("stmt: " + statementNode.getValue());
        for(ParseTreeNode child: statementNode.getChildren()){
            if(child.getValue().equals("simple_statement")){
                simpleStatement(child);
            }
        }

    }

    public void statements_x(ParseTreeNode statements_xNode){
        if(statements_xNode.getChildren() != null){
            for(ParseTreeNode child : statements_xNode.getChildren()){
                if(child.getValue().equals("statement")){
                    //System.out.println("child:" + child.getValue());
                    statement(child);
                }else if(child.getValue().equals("statements_x")){
                    //System.out.println("child:" + child.getValue());
                    statements_x(child);
                }
            }
        }
    }


    public void simpleStatement(ParseTreeNode simpleStatementNode){
        ParseTreeNode childNode = simpleStatementNode.getChildren().getFirst();
        if(childNode.getValue().equals("stdio")){
            stdio(childNode);
        } else if(childNode.getValue().equals("vardeclare")){
            vardeclare(childNode);
        }
    }

    public void vardeclare(ParseTreeNode varNode){
        if(varNode.getChildren() != null){
            List<ParseTreeNode> varAttribs = varNode.getChildren();
            TokenType mutability = null;
            TokenType numType = null;
            String varName;
            //mut_type
            if(varAttribs.get(0).getChildren().getFirst().getValue().equals("REACTIVE")){
                mutability = TokenType.REACTIVE;
            }else if(varAttribs.get(0).getChildren().getFirst().getValue().equals("INERT")){
                mutability = TokenType.INERT;
            }
            //numtype
            if(varAttribs.get(1).getChildren().getFirst().getValue().equals("MOLE32")){
                numType = TokenType.MOLE32;
            }else if(varAttribs.get(1).getChildren().getFirst().getValue().equals("MOLE64")){
                numType = TokenType.MOLE64;
            }
            //IDENT
            varName = varAttribs.get(2).getValue().substring(6,varAttribs.get(2).getValue().length()-1);
            VariableDeclarationNode variableDeclarationNode = new VariableDeclarationNode(mutability, numType, varName);
            if(varAttribs.get(3).getChildren() == null){
                System.out.println("\n\nUninitialized Variables are not allowed in argon.");
                System.exit(0);
            }
            assignment(varAttribs.get(3),variableDeclarationNode,numType);
            root.addChild(variableDeclarationNode);


        }
    }
    public void assignment(ParseTreeNode assignmentParseTreeNode, VarAssignmentNode variableDeclarationNode, TokenType width){
        assert assignmentParseTreeNode.getChildren().getFirst().getValue().equals("assign_oper"):"Invalid index to check for assign oper!!!";
        String varAssignType = assignmentParseTreeNode.getChildren().getFirst().getChildren().getFirst().getValue();
        System.out.println("var assign type: "+varAssignType);
        AssignmentExpressionNode assignmentExpressionNode = null;
        switch (varAssignType) {
            case "ASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.ASSIGN);
            case "ADDASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.ADDASSIGN);
            case "SUBASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.SUBASSIGN);
            case "EXPASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.EXPASSIGN);
            case "MULASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.MULASSIGN);
            case "DIVASSIGN" -> assignmentExpressionNode = new AssignmentExpressionNode(TokenType.DIVASSIGN);
            default -> {
                System.out.println("\n\nInvalid Assignment Operation.");
                System.exit(0);
            }
        }
        assert assignmentParseTreeNode.getChildren().get(1).getValue().equals("assign_after"):"Invalid index to check for assign_after!!!";
        if(assignmentParseTreeNode.getChildren().get(1).getValue().equals("assign_after")){
            assign_after(assignmentParseTreeNode.getChildren().get(1),assignmentExpressionNode,width);
        }

        variableDeclarationNode.addChild(assignmentExpressionNode);
    }

    public void assign_after(ParseTreeNode assignAfterNode, AssignmentExpressionNode parentNode, TokenType width){
        assert assignAfterNode.getChildren().getFirst().getValue().equals("numoper"):"Invalid index to check for numoper!!!";
        numoper(assignAfterNode.getChildren().getFirst(), parentNode, width);
    }

    public void numoper(ParseTreeNode numOperNode, AssignmentExpressionNode parentNode, TokenType width){
        ArithmeticNode<TokenType> termNode = null;
        ArithmeticNode rightNode;
        Collections.reverse(numOperNode.getChildren());
        for(ParseTreeNode op: numOperNode.getChildren()){
            if(op.getValue().equals("term")){
                rightNode = term(op, width);
                if (termNode == null) {
                    // left precedence??
                    parentNode.addChild(rightNode);
                    return;
                }else{
                    // left precedence??
                    termNode.addChild(rightNode);
                }
            }else if(op.getValue().equals("term_x")){
                termNode = term_x(op,width);
            }
        }
        parentNode.addChild(termNode);
    }

    public ArithmeticNode numoper_inner(ParseTreeNode numOperInnerNode, TokenType width){
        ArithmeticNode<TokenType> termNode = null;
        ArithmeticNode rightNode;
        Collections.reverse(numOperInnerNode.getChildren());
        for(ParseTreeNode op: numOperInnerNode.getChildren()){
            if(op.getValue().equals("term")){
                rightNode = term(op, width);
                if (termNode == null) {
                    // left precedence??
                    return rightNode;
                }else{
                    // left precedence??
                    termNode.addChild(rightNode);
                }
            }else if(op.getValue().equals("term_x")){
                termNode = term_x(op,width);
            }
        }
        return termNode;
    }
    int x = 0;
    public ArithmeticNode term(ParseTreeNode termNode, TokenType width){
        //return new ArithmeticNode<String>("term" + x++,width);
        ArithmeticNode<TokenType> factorNode = null;
        ArithmeticNode rightNode;
        Collections.reverse(termNode.getChildren());
        for(ParseTreeNode op: termNode.getChildren()){
            if(op.getValue().equals("factor")){
                rightNode = factor(op, width);
                // left precedence??
                if(factorNode == null){
                    return rightNode;
                }else {
                    factorNode.addChild(rightNode);
                }
            }else if(op.getValue().equals("factor_x")){
                factorNode = factor_x(op,width);

            }
        }
        return factorNode;
    }

    public ArithmeticNode factor(ParseTreeNode factorNode, TokenType width){
        //return new ArithmeticNode<String>("factor" + x++,width);
        ArithmeticNode<TokenType> exponentNode  = null;
        ArithmeticNode rightNode;
        Collections.reverse(factorNode.getChildren());
        for(ParseTreeNode op: factorNode.getChildren()){
            if(op.getValue().equals("exponent")){
                rightNode = exponent(op, width);
                // left precedence??
                if(exponentNode == null){
                    return rightNode;
                }else {
                    exponentNode.addChild(rightNode);
                }
            }else if(op.getValue().equals("exponent_x")){
                exponentNode = exponent_x(op,width);

            }
        }
        return exponentNode;
    }

    public ArithmeticNode exponent(ParseTreeNode exponentNode, TokenType width){
        //return new ArithmeticNode<String>("exponent" + x++,width);
        boolean isNegative = false;
        Collections.reverse(exponentNode.getChildren());
        List<ParseTreeNode> children = exponentNode.getChildren();
        if(children.get(0).getValue().equals("SUB")){
            if(children.get(1).getValue().equals("num_final")){
                List<ParseTreeNode> finalChildren = children.get(1).getChildren();
                for(ParseTreeNode type: finalChildren){
                    System.out.println("value: "+type.getValue());
                    if(type.getValue().equals("numexpr")){
                        if(type.getChildren().get(0).getValue().startsWith("NUMLIT")){
                            String literal = type.getValue().substring(7, type.getChildren().get(0).getValue().length()-1);
                            //handle different bases later
                            if(width == TokenType.MOLE32){
                                ArithmeticNode<Integer> intLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE32);
                                intLit.setValue(Integer.parseInt(literal));
                                intLit.setNegative(true);
                                return intLit;
                            }
                            if(width == TokenType.MOLE64){
                                ArithmeticNode<Long> longLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE64);
                                longLit.setValue(Long.parseLong(literal));
                                longLit.setNegative(true);
                                return longLit;
                            }
                        }
                    }else if(type.getValue().startsWith("IDENT")){
                        String varName = type.getValue().substring(7, finalChildren.getFirst().getValue().length()-1);
                        ArithmeticNode<String> varNode = new ArithmeticNode<>("Numerical Variable: " + varName, TokenType.IDENT);
                        varNode.setValue(varName);
                        varNode.setNegative(true);
                        return varNode;
                    }else if(type.getValue().startsWith("NUMOPER")){
                        ArithmeticNode inner = numoper_inner(type,width);
                        inner.setNegative(true);
                        return inner;
                    }
                }

            }
        }else if(children.get(0).getValue().equals("num_final")){
            List<ParseTreeNode> finalChildren = children.get(0).getChildren();
            for(ParseTreeNode type: finalChildren){
                System.out.println("value: "+type.getValue());

                if(type.getValue().equals("numexpr")){
                    if(type.getChildren().get(0).getValue().startsWith("NUMLIT")){
                        String literal = type.getChildren().get(0).getValue().substring(7, type.getChildren().get(0).getValue().length()-1);
                        //handle different bases later
                        if(width == TokenType.MOLE32){
                            ArithmeticNode<Integer> intLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE32);
                            intLit.setValue(Integer.parseInt(literal));
                            intLit.setNegative(true);
                            return intLit;
                        }
                        if(width == TokenType.MOLE64){
                            ArithmeticNode<Long> longLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE64);
                            longLit.setValue(Long.parseLong(literal));
                            longLit.setNegative(true);
                            return longLit;
                        }
                    }
                }else if(type.getValue().startsWith("IDENT")){
                    String varName = type.getValue().substring(6, type.getValue().length()-1);
                    ArithmeticNode<String> varNode = new ArithmeticNode<>("Numerical Variable: " + varName, TokenType.IDENT);
                    System.out.println("ident1: "+ type.getValue().substring(6, type.getValue().length()-1));
                    varNode.setValue(varName);
                    varNode.setNegative(true);
                    return varNode;
                }else if(type.getValue().startsWith("NUMOPER")){
                    ArithmeticNode inner = numoper_inner(type,width);
                    inner.setNegative(true);
                    return inner;
                }
            }
        }
        return null;
    }



    public ArithmeticNode<TokenType> exponent_x(ParseTreeNode exponentNode, TokenType width){
        ArithmeticNode<TokenType> subroot = null;
        ArithmeticNode rightNode = null;
        ArithmeticNode<TokenType> r = null;
        Collections.reverse(exponentNode.getChildren());
        for(ParseTreeNode op: exponentNode.getChildren()){
            switch (op.getValue()) {
                case "EXP" -> {
                    TokenType operator = TokenType.EXP;
                    subroot = new ArithmeticNode<>(op.getValue() , width);
                    subroot.setValue(operator);
                    if (r == null) {
                        // left precedence??
                        subroot.addChild(rightNode);
                    }else {
                        System.out.println(r);
                        // left precedence??
                        r.addChild(rightNode);
                        subroot.addChild(r);
                        return subroot;
                    }
                }
                case "exponent" ->
                        rightNode = exponent(op, width);
                case "exponent_x" ->
                        r = exponent_x(op, width);
            }
        }
        return subroot;
    }

    public ArithmeticNode<TokenType> factor_x(ParseTreeNode factorNode, TokenType width){
        ArithmeticNode<TokenType> subroot = null;
        ArithmeticNode rightNode = null;
        ArithmeticNode<TokenType> r = null;
        Collections.reverse(factorNode.getChildren());
        for(ParseTreeNode op: factorNode.getChildren()){
            switch (op.getValue()) {
                case "MUL", "DIV" -> {
                    TokenType operator = null;
                    if (op.getValue().equals("MUL")) {
                        operator = TokenType.MUL;
                    } else if (op.getValue().equals("DIV")) {
                        operator = TokenType.DIV;
                    } else {
                        System.out.println("Invalid operator for factor_x!!!");
                        System.exit(0);
                    }
                    subroot = new ArithmeticNode<>(op.getValue() , width);
                    subroot.setValue(operator);
                    if (r == null) {
                        // left precedence??
                        subroot.addChild(rightNode);
                    }else {
                        System.out.println(r);
                        // left precedence??
                        r.addChild(rightNode);
                        subroot.addChild(r);
                        return subroot;
                    }
                }
                case "factor" ->
                    rightNode = factor(op, width);
                case "factor_x" ->
                    r = factor_x(op, width);
            }
        }
        return subroot;
    }



    public ArithmeticNode<TokenType> term_x(ParseTreeNode termNode, TokenType width){
        ArithmeticNode<TokenType> subroot = null;
        ArithmeticNode rightNode = null;
        ArithmeticNode<TokenType> r = null;
        Collections.reverse(termNode.getChildren());
        for(ParseTreeNode op: termNode.getChildren()){
            switch (op.getValue()) {
                case "ADD", "SUB" -> {
                    TokenType operator = null;
                    if (op.getValue().equals("ADD")) {
                        operator = TokenType.ADD;
                    } else if (op.getValue().equals("SUB")) {
                        operator = TokenType.SUB;
                    } else {
                        System.out.println("Invalid operator for term!!!");
                        System.exit(0);
                    }
                    subroot = new ArithmeticNode<>(op.getValue() , width);
                    subroot.setValue(operator);
                    if (r == null) {
                        // left precedence??
                        subroot.addChild(rightNode);
                    }else{
                        System.out.println(r);
                        // left precedence??
                        r.addChild(rightNode);
                        subroot.addChild(r);
                        return subroot;
                    }
                }
                case "term" ->
                    rightNode = term(op, width);
                case "term_x" ->
                    r = term_x(op, width);
            }
        }
        return subroot;
    }




    public void stdio(ParseTreeNode stdioNode){
        if(stdioNode.getChildren().getFirst().getValue().equals("stdout")){
            ParseTreeNode stdoutNode = stdioNode.getChildren().getFirst();
            //System.out.println("stdoutNode: " + stdoutNode.getValue());
            stdout(stdoutNode);
        }else if(stdioNode.getChildren().getFirst().getValue().equals("stderr")){
            ParseTreeNode stderrNode = stdioNode.getChildren().getFirst();
            //System.out.println("stdoutNode: " + stdoutNode.getValue());
            stderr(stderrNode);
        }
    }

    public void stderr(ParseTreeNode stderrNode){
        PrintType type = null;
        //System.out.println("val" +stderrNode.getChildren().get(0).getValue());
        assert stderrNode.getChildren().getFirst().getValue().equals("PRINTERR"): "type not equal to stderr!!!";
        if(stderrNode.getChildren().getFirst().getValue().equals("PRINTERR")){
            type = PrintType.PRINTERR;
        }
        extractContents(stderrNode, type);
    }

    public void stdout(ParseTreeNode stdoutNode){
        PrintType type = switch (stdoutNode.getChildren().getFirst().getChildren().getFirst().getValue()) {
            case "PRINT" -> PrintType.PRINT;
            case "PRINTLN" -> PrintType.PRINTLN;
            default -> null;
        };
        extractContents(stdoutNode, type);
    }

    private void extractContents(ParseTreeNode stderrNode, PrintType type) {
        ParseTreeNode contents = stderrNode.getChildren().get(2);
        StringBuilder sb = new StringBuilder();
        if(contents.getChildren() != null){
            for(ParseTreeNode content: contents.getChildren()){
                if(content.getValue().equals("strexpr")){
                    strExpr(content, sb);
                }
            }
        }
        root.addChild(new PrintNode(type, sb));
        this.processedStatement++;
    }

    public void strExpr(ParseTreeNode strExprNode, StringBuilder sb){
        for(ParseTreeNode expr: strExprNode.getChildren()){
            if(expr.getValue().equals("strterm")){
                strTerm(expr, sb);
            }else if(expr.getValue().equals("strterm_x")){
                strTerm_x(expr, sb);
            }
        }
    }

    public void strTerm(ParseTreeNode strTermNode, StringBuilder sb){
        if(strTermNode.getValue().equals("strterm")){
            ParseTreeNode strlit = strTermNode.getChildren().getFirst();
            String escapedString = strlit.getValue().replace("\\n","\n");
            //re-escape escape sequences
            escapedString = escapedString.replace("\\n","\n");
            escapedString = escapedString.replace("\\t","\t");
            escapedString = escapedString.replace("\\r","\r");
            escapedString = escapedString.replace("\\b","\b");
            escapedString = escapedString.replace("\\\"","\"");
            escapedString = escapedString.replace("\\f","\f");
            escapedString = escapedString.replace("\\\\","\\");
            sb.append(escapedString, 7, escapedString.length()-1);
        }
    }
    public void strTerm_x(ParseTreeNode strTerm_xNode, StringBuilder sb){
       if(strTerm_xNode.getChildren() != null){
           for(ParseTreeNode term: strTerm_xNode.getChildren()){
               if(term.getValue().equals("strterm")){
                   strTerm(term, sb);
               }else if(term.getValue().equals("strterm_x")){
                   strTerm_x(term, sb);
               }
           }
       }
    }




}
