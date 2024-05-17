package com.three_csa.argon.SemAnalyzer.Node;

import com.three_csa.argon.Tokenizer.TokenType;


public class VariableDeclarationNode extends VarAssignmentNode implements Comparable<VariableDeclarationNode> {
    TokenType varMutability;
    TokenType varType;

    public VariableDeclarationNode(TokenType varMutability, TokenType varType, String varName) {
        super("Variable Declaration for: " + varName);
        if(varMutability == TokenType.REACTIVE || varMutability == TokenType.INERT){
            this.varMutability = varMutability;
        } else {
            System.out.println("LanguageError: Variable Mutability not recognized");
            System.exit(1);
        }

        if(varType == TokenType.MOLE32 || varType == TokenType.MOLE64){
            this.varType = varType;
        } else {
            System.out.println("LanguageError: Variable Size not recognized");
            System.exit(1);
        }
        this.varName = varName;
    }

    public TokenType getVarMutability() {
        return varMutability;
    }

    @Override
    public int compareTo(VariableDeclarationNode o) {
        return this.varName.compareTo(o.varName);
    }
}

