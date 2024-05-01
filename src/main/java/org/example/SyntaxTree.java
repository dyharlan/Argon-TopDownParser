package org.example;

import org.example.AST.*;

import java.util.List;


class AssignmentExpressionNode extends ASTNode {
    TokenType varAssignType;
    public AssignmentExpressionNode(TokenType varAssignType) {
        super("Assignment Expression");
        this.varAssignType = varAssignType;
        assert varAssignType == TokenType.ASSIGN || varAssignType == TokenType.ADDASSIGN || varAssignType == TokenType.SUBASSIGN || varAssignType == TokenType.EXPASSIGN || varAssignType == TokenType.MULASSIGN || varAssignType == TokenType.DIVASSIGN : "Invalid assign type!!!";
    }

}

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
        ParseTreeNode childNode = simpleStatementNode.getChildren().get(0);
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
            String varName = null;
            //mut_type
            if(varAttribs.get(0).getValue().equals("REACTIVE")){
                mutability = TokenType.REACTIVE;
            }else if(varAttribs.get(0).getValue().equals("INERT")){
                mutability = TokenType.INERT;
            }
            //numtype
            if(varAttribs.get(1).getValue().equals("MOLE32")){
                numType = TokenType.MOLE32;
            }else if(varAttribs.get(1).getValue().equals("MOLE64")){
                numType = TokenType.MOLE64;
            }
            //IDENT
            varName = varAttribs.get(2).getValue().substring(6,varAttribs.get(2).getValue().length()-1);
            VariableDeclarationNode variableDeclarationNode = new VariableDeclarationNode(mutability, numType, varName);
            if(varAttribs.get(3).getChildren() == null){
                System.out.println("\n\nUninitialized Variables are not allowed in argon.");
                System.exit(0);
            }
            assignment(varAttribs.get(3),variableDeclarationNode);
            root.addChild(variableDeclarationNode);


        }
    }
    public void assignment(ParseTreeNode assignmentParseTreeNode, VarAssignmentNode variableDeclarationNode){
        assert assignmentParseTreeNode.getChildren().get(0).getValue().equals("assign_oper"):"Invalid index to check for assign oper!!!";
        String varAssignType = assignmentParseTreeNode.getChildren().get(0).getChildren().get(0).getValue();
        System.out.println("var assign type: "+varAssignType);
        AssignmentExpressionNode assignmentExpressionNode = null;
        if(varAssignType.equals("ASSIGN")){
            assignmentExpressionNode = new AssignmentExpressionNode(TokenType.ASSIGN);
        }else if(varAssignType.equals("ADDASSIGN")){
            assignmentExpressionNode = new AssignmentExpressionNode(TokenType.ADDASSIGN);
        }else if(varAssignType.equals("SUBASSIGN")){
            assignmentExpressionNode = new AssignmentExpressionNode(TokenType.SUBASSIGN);
        }else if(varAssignType.equals("EXPASSIGN")){
            assignmentExpressionNode = new AssignmentExpressionNode(TokenType.EXPASSIGN);
        }else if(varAssignType.equals("MULASSIGN")){
            assignmentExpressionNode = new AssignmentExpressionNode(TokenType.MULASSIGN);
        }else if(varAssignType.equals("DIVASSIGN")){
            assignmentExpressionNode = new AssignmentExpressionNode(TokenType.DIVASSIGN);
        } else {
            System.out.println("\n\nInvalid Assignment Operation.");
            System.exit(0);
        }
        assert assignmentParseTreeNode.getChildren().get(1).getValue().equals("assign_after"):"Invalid index to check for assign_after!!!";
        if(assignmentParseTreeNode.getChildren().get(1).getValue().equals("assign_after")){
            assign_after(assignmentParseTreeNode.getChildren().get(1),assignmentExpressionNode);
        }

        variableDeclarationNode.addChild(assignmentExpressionNode);
    }

    public void assign_after(ParseTreeNode assignAfterNode, AssignmentExpressionNode parentNode){
        assert assignAfterNode.getChildren().get(0).getValue().equals("numoper"):"Invalid index to check for numoper!!!";
        numoper(assignAfterNode.getChildren().get(0), parentNode);
    }
    public void numoper(ParseTreeNode numOperNode, AssignmentExpressionNode parentNode){
        for(ParseTreeNode term: numOperNode.getChildren()){
            if(term.getValue().equals("term")){
                term(term,parentNode);
            }else if(term.getValue().equals("term_x")){
                term_x(term,parentNode);
            }
        }
    }

    public void term(ParseTreeNode termNode,AssignmentExpressionNode parentNode){

    }

    public void term_x(ParseTreeNode termNode,AssignmentExpressionNode parentNode){

    }

    public void stdio(ParseTreeNode stdioNode){
        if(stdioNode.getChildren().get(0).getValue().equals("stdout")){
            ParseTreeNode stdoutNode = stdioNode.getChildren().get(0);
            //System.out.println("stdoutNode: " + stdoutNode.getValue());
            stdout(stdoutNode);
        }else if(stdioNode.getChildren().get(0).getValue().equals("stderr")){
            ParseTreeNode stderrNode = stdioNode.getChildren().get(0);
            //System.out.println("stdoutNode: " + stdoutNode.getValue());
            stderr(stderrNode);
        }
    }

    public void stderr(ParseTreeNode stderrNode){
        PrintType type = null;
        //System.out.println("val" +stderrNode.getChildren().get(0).getValue());
        assert stderrNode.getChildren().get(0).getValue().equals("PRINTERR"): "type not equal to stderr!!!";
        if(stderrNode.getChildren().get(0).getValue().equals("PRINTERR")){
            type = PrintType.PRINTERR;
        }
        extractContents(stderrNode, type);
    }

    public void stdout(ParseTreeNode stdoutNode){
        PrintType type = switch (stdoutNode.getChildren().get(0).getChildren().get(0).getValue()) {
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
            ParseTreeNode strlit = strTermNode.getChildren().get(0);
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
