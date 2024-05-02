package org.example.AST;

import org.example.TokenType;


public class AssignmentExpressionNode extends ASTNode {
    TokenType varAssignType;
    public AssignmentExpressionNode(TokenType varAssignType) {
        super("Assignment Expression");
        this.varAssignType = varAssignType;
        assert varAssignType == TokenType.ASSIGN || varAssignType == TokenType.ADDASSIGN || varAssignType == TokenType.SUBASSIGN || varAssignType == TokenType.EXPASSIGN || varAssignType == TokenType.MULASSIGN || varAssignType == TokenType.DIVASSIGN : "Invalid assign type!!!";
    }

}





