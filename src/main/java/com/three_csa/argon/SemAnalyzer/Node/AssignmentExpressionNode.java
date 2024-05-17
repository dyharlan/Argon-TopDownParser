package com.three_csa.argon.SemAnalyzer.Node;

import com.three_csa.argon.Tokenizer.TokenType;


public class AssignmentExpressionNode extends ASTNode {
    private TokenType varAssignType;
    public AssignmentExpressionNode(TokenType varAssignType) {
        super("Assignment Expression");
        this.varAssignType = varAssignType;
        assert varAssignType == TokenType.ASSIGN || varAssignType == TokenType.ADDASSIGN || varAssignType == TokenType.SUBASSIGN || varAssignType == TokenType.EXPASSIGN || varAssignType == TokenType.MULASSIGN || varAssignType == TokenType.DIVASSIGN : "Invalid assign type!!!";
    }

    public TokenType getVarAssignType() {
        return varAssignType;
    }

}





