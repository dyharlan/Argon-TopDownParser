package com.three_csa.argon.SemAnalyzer.Node;


public class VarAssignmentNode extends ASTNode {
    protected String varName;
    public VarAssignmentNode(String varName) {
        super(varName);
        this.varName = varName;
    }
    public VarAssignmentNode(String expressionType, String varName) {
        super(expressionType);
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }
}

