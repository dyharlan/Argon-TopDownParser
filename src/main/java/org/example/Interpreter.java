package org.example;

import org.example.AST.ASTNode;
import org.example.AST.PrintNode;
import org.example.AST.PrintType;
import org.example.AST.StatementsNode;

public class Interpreter {
    //add tables for declared stuff

    public Interpreter() {

    }
    public void interpret(StatementsNode root){
        if(root.getStatements() != null){
            for(ASTNode statement: root.getStatements()){
                //laging nahuhuli stderr sa java??
                if(statement instanceof PrintNode p){
                    if(p.getPrintType() == PrintType.PRINT){
                        //System.out.println("PRINT");
                        System.out.print(p);
                    }else if(p.getPrintType() == PrintType.PRINTLN){
                        //System.out.println("PRINTLN");
                        System.out.println(p);
                    }else if(p.getPrintType() == PrintType.PRINTERR){
                        //System.out.println("PRINTERR");
                        System.err.println(p);
                    }
                }
            }
        }

    }
}
