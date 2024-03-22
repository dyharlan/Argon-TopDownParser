package org.example;

import java.util.ArrayList;
import java.util.List;

public class ParseTree {
    private Node root;

    public ParseTree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return this.root;
    }

    public void print() {
        root.print(0);
    }
}

