package org.example;

public class ParseTree {
    private ParseTreeNode root;

    public ParseTree(ParseTreeNode root) {
        this.root = root;
    }

    public ParseTreeNode getRoot() {
        return this.root;
    }

    public void print() {
        root.print(0);
    }
}

