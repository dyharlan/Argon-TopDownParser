package org.example;

import org.example.AST.*;

public class Interpreter {
    //add tables for declared stuff

    public Interpreter() {

    }
    public void interpret(StatementsNode root){
        if(root.getChildren() != null){
            for(ASTNode statement: root.getChildren()){
                //laging nahuhuli stderr sa java??
                switch (statement){
                    case PrintNode p: {
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
                    break;
                    case VariableDeclarationNode vdNode: {

                    }
                    break;
                    default:
                        throw new IllegalStateException("Unexpected type of statement: " + statement.getExpressionType());
                }

            }
        }
    }
}
