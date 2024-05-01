package org.example.AST;

import java.util.List;


public class StatementsNode extends ASTNode {
    public StatementsNode() {
        super("Statements");
    }

    public List<ASTNode> getStatements() {
        return children;
    }
}

