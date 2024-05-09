package org.example.AST;

public class PrintNode extends ASTNode {
    PrintType printType;
    public PrintNode(PrintType printType) {
        super("Print");
        this.printType = printType;
    }

    public PrintType getPrintType() {
        return printType;
    }
}

