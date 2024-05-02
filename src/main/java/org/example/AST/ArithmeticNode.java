package org.example.AST;

import org.example.TokenType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArithmeticNode<T> extends ASTNode {
    //TokenType operator;
    private T value;
    private final TokenType width;
    public ArithmeticNode(String expressionType, TokenType width) {
        super(expressionType);
        this.width = width;
    }
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        switch (value){
            case Integer i:
                if(width == TokenType.MOLE32 && i.compareTo(Integer.MAX_VALUE) >= 1){
                    System.out.println("Value too big to fit inside the destination.");
                    System.exit(0);
                }else if (width == TokenType.MOLE32 && i.compareTo(Integer.MIN_VALUE) <= -1) {
                    System.out.println("Value too small to fit inside the destination.");
                    System.exit(0);
                }else{
                    this.value = value;
                }
            break;
            case Long l:
                if(width == TokenType.MOLE64 && l.compareTo(Long.MAX_VALUE) >= 1){
                    System.out.println("Value too big to fit inside the destination.");
                    System.exit(0);
                } else if (width == TokenType.MOLE32 && l.compareTo(Long.MIN_VALUE) <= -1) {
                    System.out.println("Value too small to fit inside the destination.");
                    System.exit(0);
                }else{
                    this.value = value;
                }
                break;
            case String ident:
                Pattern pattern = Pattern.compile("_(([a-zA-Z0-9]|_)*)|[a-zA-Z](([a-zA-Z0-9]|_)*)$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(ident);
                boolean matchFound = matcher.find();
                if(!matchFound) {
                    System.out.println("Invalid identifier.");
                    System.exit(0);
                }else {
                    this.value = value;
                }

            default:
                if(value instanceof TokenType t){

                    if(t.equals(TokenType.ADD) || t.equals(TokenType.SUB) || t.equals(TokenType.MUL) || t.equals(TokenType.DIV) || t.equals(TokenType.EXP)){
                        this.value = value;
                    }else{
                        System.out.println("Illegal arithmetic expression: "+expressionType);
                        System.exit(0);
                    }
                }else{
                    System.out.println("Unexpected value: " + value);
                    System.exit(0);
                }

        }

    }
}

