package com.three_csa.argon.SemAnalyzer.Node;

import java.util.List;

public class LoopNode extends ASTNode {
    LoopType loopType;
    List<ASTNode> content;
    public LoopNode(LoopType loopType, List<ASTNode> content) {
        super("Loop: " + loopType.toString());
        this.loopType = loopType;
        this.content = content;
    }

//    public String toString() {
//        return content.toString();
//    }

    public LoopType getLoopType() {
        return loopType;
    }
}
