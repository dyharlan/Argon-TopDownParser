package org.example.AST;

public class StdioNode extends ASTNode {
    IoType ioType;
    public StdioNode(String expressionType, IoType ioType) {
        super(expressionType);
        this.ioType = ioType;
    }

    public IoType getIoType() {
        return ioType;
    }
}