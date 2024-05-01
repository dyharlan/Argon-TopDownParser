package org.example.AST;

public class PrintNode extends ASTNode {
    PrintType printType;
    StringBuilder content;
    public PrintNode(PrintType printType, StringBuilder content) {
        super("Print");
        this.printType = printType;
        this.content = content;
    }

    public String toString() {
        return content.toString();
    }

    public PrintType getPrintType() {
        return printType;
    }
}

