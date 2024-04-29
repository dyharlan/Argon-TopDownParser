package org.example;

import java.util.ArrayList;
import java.util.List;

class ASTNode {

}
enum PrintType {
    PRINT,
    PRINTLN,
    PRINTERR;
    public String toString() {
        return switch (this) {
            case PRINT -> "PRINT";
            case PRINTLN -> "PRINTLN";
            case PRINTERR -> "PRINTERR";
        };
    }

}
class StatementsNode extends ASTNode {
    private final List<ASTNode> statements;

    StatementsNode() {
        this.statements = new ArrayList<>();
    }

    public void addStatement(ASTNode statement) {
        this.statements.add(statement);
    }
    public List<ASTNode> getStatements() {
        return statements;
    }
}
class PrintNode extends ASTNode {
    PrintType printType;
    StringBuilder content;
    public PrintNode(PrintType printType, StringBuilder content) {
        this.printType = printType;
        this.content = content;
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
        if(simpleStatementNode.getChildren().get(0).getValue().equals("stdio")){
            ParseTreeNode stdioNode = simpleStatementNode.getChildren().get(0);
            stdio(stdioNode);
        }
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
        root.addStatement(new PrintNode(type, sb));
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
            sb.append(strlit.getValue(), 7, strlit.getValue().length()-1);
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
