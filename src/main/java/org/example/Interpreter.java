package org.example;

import org.example.AST.*;

import java.util.HashMap;
import java.util.List;

public class Interpreter {
    //add tables for declared stuff
    HashMap<String, NumericalVariable> variables;
    public Interpreter(HashMap<String, NumericalVariable> variables) {
        this.variables = variables;
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
                        if(variables.get(vdNode.getVarName()).getType().equals("Integer")){
                            NumericalVariable<Integer> var = ( NumericalVariable<Integer> ) variables.get(vdNode.getVarName());

                            if(vdNode.getChildren().getFirst() instanceof AssignmentExpressionNode ae){
                                ArithmeticNode<?> an = (ArithmeticNode<?>) ae.getChildren().getFirst();
                                var.setValue(evaluateInt(an));
                                System.out.println("value: "+var.getValue());
                            }else{
                                throw new RuntimeException("Error: unexpected arithmetic expression");
                            }
                        }else if(variables.get(vdNode.getVarName()).getType().equals("Long")){
                            NumericalVariable<Long> var = ( NumericalVariable<Long> ) variables.get(vdNode.getVarName());

                            if(vdNode.getChildren().getFirst() instanceof AssignmentExpressionNode ae){
                                ArithmeticNode<?> an = (ArithmeticNode<?>) ae.getChildren().getFirst();
                                var.setValue(evaluateLong(an));
                                System.out.println("value: "+var.getValue());
                            }else{
                                throw new RuntimeException("Error: unexpected arithmetic expression");
                            }
                        }


                    }
                    break;
                    case VarAssignmentNode vaNode: {
//                        if(variables.get(vdNode.getVarName()).getValue() instanceof Integer && vdNode.getVarMutability() == TokenType.MOLE32){
//                            NumericalVariable<Integer> var = variables.get(vdNode.getVarName());
//                            var.setValue();
//                            int x = 1;
//                            x+=1;
//                        }


                    }
                    break;
                    default:
                        throw new IllegalStateException("Unexpected type of statement: " + statement.getExpressionType());
                }

            }
        }
    }

    public Integer evaluateInt(ArithmeticNode<?> node){
        if(node.getType().equals("Integer")){
            Integer lit = (Integer) node.getValue();
            if(node.isNegative()){
                return -lit;
            }
            return lit;
        }
        if(node.getType().equals("Long")){
            System.out.println("Mole64 Value: " + node.getValue() + " cannot be assigned to a Mole32 variable.");
            System.exit(0);
        }
        if(node.getType().equals("String")){
            String varName = (String) node.getValue();
            if(variables.get(varName).getValue() instanceof Integer varVal){
               if(node.isNegative()){
                   return -varVal;
               }
               return varVal;
            }else if(variables.get(varName).getValue() instanceof Long varVal){
                System.out.println("The value of " + varName + " cannot be assigned to a Mole32 variable as it contains contains a Mole64 value.");
                System.exit(0);
            }

        }
        if(node.getType().equals("TokenType")){
            TokenType op = (TokenType) node.getValue();
            if(op == TokenType.ADD){
                List<ASTNode> children = node.getChildren();
                return evaluateInt((ArithmeticNode<?>) children.get(1)) + evaluateInt((ArithmeticNode<?>) children.get(0));
            }
            if(op == TokenType.SUB){
                List<ASTNode> children = node.getChildren();
                return evaluateInt((ArithmeticNode<?>) children.get(1)) - evaluateInt((ArithmeticNode<?>) children.get(0));
            }
            if(op == TokenType.MUL){
                List<ASTNode> children = node.getChildren();
                return evaluateInt((ArithmeticNode<?>) children.get(1)) * evaluateInt((ArithmeticNode<?>) children.get(0));
            }
            if(op == TokenType.DIV){
                List<ASTNode> children = node.getChildren();
                return evaluateInt((ArithmeticNode<?>) children.get(1)) / evaluateInt((ArithmeticNode<?>) children.get(0));
            }
            if(op == TokenType.EXP){
                List<ASTNode> children = node.getChildren();
                return (int) Math.pow(evaluateInt((ArithmeticNode<?>) children.get(1)), evaluateInt((ArithmeticNode<?>) children.get(0)));
            }
        }
        throw new ArithmeticException("Invalid Arithmetic Expression");
    }

    public Long evaluateLong(ArithmeticNode<?> node){
        if(node.getType().equals("Integer")){
            Integer lit = (Integer) node.getValue();
            if(node.isNegative()){
                return (long) -lit;
            }
            return (long) lit;
        }
        if(node.getType().equals("Long")){
            Long lit = (Long) node.getValue();
            if(node.isNegative()){
                return -lit;
            }
            return lit;
        }
        if(node.getType().equals("String")){
            String varName = (String) node.getValue();
            if(variables.get(varName).getValue() instanceof Integer varVal){
                if(node.isNegative()){
                    return (long) -varVal;
                }
                return (long) varVal;
            }else if(variables.get(varName).getValue() instanceof Long varVal){
                if(node.isNegative()){
                    return -varVal;
                }
                return varVal;
            }

        }
        if(node.getType().equals("TokenType")){
            TokenType op = (TokenType) node.getValue();
            if(op == TokenType.ADD){
                List<ASTNode> children = node.getChildren();
                return evaluateLong((ArithmeticNode<?>) children.get(1)) + evaluateLong((ArithmeticNode<?>) children.get(0));
            }
            if(op == TokenType.SUB){
                List<ASTNode> children = node.getChildren();
                return evaluateLong((ArithmeticNode<?>) children.get(1)) - evaluateLong((ArithmeticNode<?>) children.get(0));
            }
            if(op == TokenType.MUL){
                List<ASTNode> children = node.getChildren();
                return evaluateLong((ArithmeticNode<?>) children.get(1)) * evaluateLong((ArithmeticNode<?>) children.get(0));
            }
            if(op == TokenType.DIV){
                List<ASTNode> children = node.getChildren();
                return evaluateLong((ArithmeticNode<?>) children.get(1)) / evaluateLong((ArithmeticNode<?>) children.get(0));
            }
            if(op == TokenType.EXP){
                List<ASTNode> children = node.getChildren();
                return (long) Math.pow(evaluateLong((ArithmeticNode<?>) children.get(1)), evaluateLong((ArithmeticNode<?>) children.get(0)));
            }
        }
        throw new ArithmeticException("Invalid Arithmetic Expression");
    }
}
