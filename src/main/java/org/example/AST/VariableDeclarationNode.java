package org.example.AST;

import org.example.TokenType;


public class VariableDeclarationNode extends VarAssignmentNode {
    TokenType varMutability;
    TokenType varType;
    public VariableDeclarationNode(TokenType varMutability, TokenType varType, String varName) {
        super("Variable Declaration");
        if(varMutability == TokenType.REACTIVE || varMutability == TokenType.INERT){
            this.varMutability = varMutability;
        } else {
            //do error handling
        }

        if(varType == TokenType.MOLE32 || varType == TokenType.MOLE64){
            this.varType = varType;
        } else {
            //do error handling
        }
        this.varName = varName;
    }

}

