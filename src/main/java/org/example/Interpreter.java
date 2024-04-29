package org.example;

public class Interpreter {
    //add tables for declared stuff
    StatementsNode root;
    public Interpreter(StatementsNode root) {
        this.root = root;
    }

    public void interpret(){
        for(ASTNode statement: root.getStatements()){
            //laging nahuhuli stderr sa java??
            if(statement instanceof PrintNode p){
                if(p.printType == PrintType.PRINT){
                    System.out.println("PRINT");
                    System.out.print(p.content.toString());
                }else if(p.printType == PrintType.PRINTLN){
                    System.out.println("PRINTLN");
                    System.out.println(p.content.toString());
                }else if(p.printType == PrintType.PRINTERR){
                    System.out.println("PRINTERR");
                    System.err.println(p.content.toString());
                }
            }
        }
    }
}
