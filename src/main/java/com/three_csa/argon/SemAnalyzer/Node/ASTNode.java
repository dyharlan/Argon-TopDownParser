package com.three_csa.argon.SemAnalyzer.Node;

import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    protected List<ASTNode> children;
    protected final String expressionType;
    public ASTNode(String expressionType) {
        this.expressionType = expressionType;
    }
    public void print(int depth) {
        // Print indentation
        for (int i = 0; i < depth; i++) {
            System.out.print(" | ");
            if (i == depth-1) {
                System.out.print(" ↳ ");
            }
        }

        // Print the value of the node
        System.out.println(expressionType);

        // Print the children of the node
        if(children != null){
            for (ASTNode child : children) {
                child.print(depth + 1);
            }
        }
    }
    public void addChild(ASTNode child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        this.children.add(child);
    }
    public void addChild(ASTNode child, Integer pos) {
        if (children == null) {
            children = new ArrayList<>();
        }
        this.children.add(pos,child);
    }
    public List<ASTNode> getChildren() {
        return children;
    }

    public String getExpressionType() {
        return expressionType;
    }
}

