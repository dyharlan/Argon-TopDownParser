package org.example.AST;

import org.example.TokenType;


public class VariableDeclarationNode extends VarAssignmentNode implements Comparable<VariableDeclarationNode> {
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

    @Override
    public int compareTo(VariableDeclarationNode o) {
        return this.varName.compareTo(o.varName);
    }
}

