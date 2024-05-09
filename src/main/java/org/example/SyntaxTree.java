package org.example;

import org.example.AST.*;

import javax.sound.midi.Soundbank;
import java.util.*;

public class SyntaxTree {
    private final StatementsNode root;
    private int processedStatement;
    HashMap<String, NumericalVariable> variables;
    public SyntaxTree(){
        this.root = new StatementsNode();
        this.variables = new HashMap<>();
        processedStatement = 0;
    }
    public StatementsNode getRoot() {
        return root;
    }

    public HashMap<String, NumericalVariable> getVariables() {
        return variables;
    }
    public void buildAST(ParseTree tree){
        if(tree.getRoot().getValue().equals("program")){
            ParseTreeNode statementsNode = tree.getRoot().getChildren().get(0);
            if(statementsNode != null){
                //System.out.println("statementsNode: " + statementsNode.getValue());
                statements(statementsNode,root);
            }
        }
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


    public void simpleStatement(ParseTreeNode simpleStatementNode, StatementsNode root){
        ParseTreeNode childNode = simpleStatementNode.getChildren().getFirst();
        if(childNode.getValue().equals("stdio")){
            stdio(childNode, root);
        } else if(childNode.getValue().equals("vardeclare")){
            vardeclare(childNode, root);
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
                System.out.println("\n\nUninitialized Variables are not allowed in argon.");
                System.exit(0);
            }
            if(variables.containsKey(varName)){
                System.out.println("Variable " + varName + " has already been declared.");
                System.exit(0);
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
        assert assignmentParseTreeNode.getChildren().getFirst().getValue().equals("assign_oper"):"Invalid index to check for assign oper!!!";
        String varAssignType = assignmentParseTreeNode.getChildren().getFirst().getChildren().getFirst().getValue();
        System.out.println("var assign type: "+varAssignType);
        AssignmentExpressionNode assignmentExpressionNode = null;
        if(varAssignType.equals("ASSIGN")){
            assignmentExpressionNode = new AssignmentExpressionNode(TokenType.ASSIGN);
        }else if(variableDeclarationNode instanceof VariableDeclarationNode){
            System.out.println("Compount assignemnt operations are not allowed on variable declarations.");
            System.exit(0);
        }else {
            switch (varAssignType) {
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
            System.out.println("type: " + type.getValue());
            if(type.getValue().equals("numexpr")){
                if(type.getChildren().getFirst().getValue().startsWith("NUMLIT")){
                    String literal = type.getChildren().getFirst().getValue().substring(7, type.getChildren().getFirst().getValue().length()-1);
                    //handle different bases later
                    if(width == TokenType.MOLE32){
                        ArithmeticNode<Integer> intLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE32);
                        try{
                            intLit.setValue(Integer.parseInt(literal));
                        }catch (NumberFormatException nfe){
                            System.out.println("The value " + literal + " is not valid for " + width);
                            System.exit(0);
                        }
                        return intLit;
                    }
                    if(width == TokenType.MOLE64){
                        ArithmeticNode<Long> longLit = new ArithmeticNode<>("Numerical Literal: "+ literal, TokenType.MOLE64);
                        try{
                            longLit.setValue(Long.parseLong(literal));
                        }catch (NumberFormatException nfe){
                            System.out.println("The value " + literal + " is not valid for " + width);
                            System.exit(0);
                        }
                        return longLit;
                    }
                }
            }else if(type.getValue().startsWith("IDENT")){
                String varName = type.getValue().substring(6, type.getValue().length()-1);
                ArithmeticNode<String> varNode = new ArithmeticNode<>("Numerical Variable "+ x++ +": " + varName, TokenType.IDENT);
                if(!variables.containsKey(varName)){
                    System.out.println("Variable " + varName + " has not been declared, or is uninitialized.");
                    System.exit(0);
                }
                varNode.setValue(varName);

                return varNode;
            }else if(type.getValue().startsWith("numoper")){
                return numoper_inner(type,width);
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
        PrintType type = null;
        //System.out.println("val" +stderrNode.getChildren().get(0).getValue());
        assert stderrNode.getChildren().getFirst().getValue().equals("PRINTERR"): "type not equal to stderr!!!";
        if(stderrNode.getChildren().getFirst().getValue().equals("PRINTERR")){
            type = PrintType.PRINTERR;
        }
        String contents = extractContents(stderrNode);
        root.addChild(new PrintNode(type, contents));
    }

    public void stdout(ParseTreeNode stdoutNode, StatementsNode root){
        PrintType type = switch (stdoutNode.getChildren().getFirst().getChildren().getFirst().getValue()) {
            case "PRINT" -> PrintType.PRINT;
            case "PRINTLN" -> PrintType.PRINTLN;
            default -> null;
        };
        String contents = extractContents(stdoutNode);
        root.addChild(new PrintNode(type, contents));
    }

    private String extractContents(ParseTreeNode stdNode) {
        ParseTreeNode contents = stdNode.getChildren().get(2);
        StringBuilder sb = new StringBuilder();
        if(contents.getChildren() != null){
            for(ParseTreeNode content: contents.getChildren()){
                if(content.getValue().equals("strexpr")){
                    strExpr(content, sb);
                }
//                else if (content.getValue().equals("stdin")) {
//                    sb.append(stdin(content));
//                }
            }
        }
        return sb.toString();
        //this.processedStatement++;
    }

    public String stdin(ParseTreeNode stdinNode){
        String msg = "";
        for(ParseTreeNode child: stdinNode.getChildren()){
            if(child.getValue().equals("content")){
                msg = msg.concat(extractContents(stdinNode));
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.print(msg);
        String inputString = "";
        try{
            inputString=inputString.concat(in.next());
        }catch (NoSuchElementException e){
            in.close();
            Scanner in2 = new Scanner(System.in);
            inputString=inputString.concat(in2.next());
            in2.close();
        }
        return inputString;
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
            for(ParseTreeNode term: strTermNode.getChildren()){
                if(term.getValue().startsWith("STRLIT")){
                    String escapedString = getEscapedString(term);
                    sb.append(escapedString, 7, escapedString.length()-1);
                }else if(term.getValue().equals("stdin")){
                    sb.append(stdin(term));
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
