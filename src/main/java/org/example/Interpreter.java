package org.example;

import org.example.AST.*;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

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
                    case StdioNode p: {
                        StringBuilder sb = new StringBuilder();
                        for(ASTNode child: p.getChildren()){
                            if(child instanceof ContentNode content){
                                if(content.getContentType() == TokenType.STRLIT){
                                    sb.append(content.getContent());
                                    continue;
                                }
                                if(content.getContentType() == TokenType.IDENT){
                                    sb.append(variables.get(content.getContent()).getValue());
                                    continue;
                                }
                            }
                            if(child instanceof StdioNode){
                                StdioNode stdio = (StdioNode) child;
                                if(stdio.getIoType() == IoType.INPUT){
                                    StringBuilder sb2 = new StringBuilder();
                                    if(stdio.getChildren() != null){
                                        for(ASTNode innerChild: stdio.getChildren()) {
                                            if (innerChild instanceof ContentNode content) {
                                                if (content.getContentType() == TokenType.STRLIT) {
                                                    sb2.append(content.getContent());
                                                    continue;
                                                }
                                                if(content.getContentType() == TokenType.IDENT){
                                                    sb.append(variables.get(content.getContent()).getValue());
                                                }
                                            }
                                        }
                                        Scanner input = new Scanner(System.in);
                                        System.out.print(sb2);
                                        sb.append(input.nextLine());
                                        input.close();
                                    }
                                }
                            }
                        }
                        if(p.getIoType() == IoType.PRINT){
                            System.out.print(sb);
                        }else if(p.getIoType() == IoType.PRINTLN){
                            System.out.println(sb);
                        }else if(p.getIoType() == IoType.PRINTERR){
                            System.err.println(sb);
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
            System.exit(1);
        }
        if(node.getType().equals("String")){
            String varName = (String) node.getValue();
            if(variables.get(varName).getValue() instanceof Integer varVal){
               if(node.isNegative()){
                   return -varVal;
               }
               return varVal;
            }else if(variables.get(varName).getValue() instanceof Long){
                System.out.println("The value of " + varName + " cannot be assigned to a Mole32 variable as it contains contains a Mole64 value.");
                System.exit(1);
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
                Integer left = evaluateInt((ArithmeticNode<?>) children.get(0));
                Integer right = evaluateInt((ArithmeticNode<?>) children.get(1));
                System.out.println(left + " - " + right);
                //return evaluateInt((ArithmeticNode<?>) children.get(1)) - evaluateInt((ArithmeticNode<?>) children.get(0));
                return left - right;
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
