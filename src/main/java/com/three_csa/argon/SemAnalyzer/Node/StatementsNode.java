package com.three_csa.argon.SemAnalyzer.Node;


public class StatementsNode extends ASTNode {
    public StatementsNode() {
        super("Statements");
    }

    public StatementsNode(String str) {
        super(str); //only used when declaring bodies in filter, funnel, distill, and ferment
    }

}

