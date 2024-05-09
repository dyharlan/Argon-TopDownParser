package org.example.AST;


public class VarAssignmentNode extends ASTNode {
    protected String varName;
    public VarAssignmentNode(String varName) {
        super("VarAssignment");
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

