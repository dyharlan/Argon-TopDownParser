package org.example;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String value;
    private List<Node> children;

    public Node(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public String getValue() {
        return this.value;
    }

    public List<Node> getChildren() {
        return this.children;
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
        System.out.println(value);

        // Print the children of the node
        for (Node child : children) {
            child.print(depth + 1);
        }
    }
}
