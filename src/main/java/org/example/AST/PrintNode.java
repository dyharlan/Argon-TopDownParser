package org.example.AST;

public class PrintNode extends ASTNode {
    PrintType printType;
    String content;
    public PrintNode(PrintType printType, String content) {
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

