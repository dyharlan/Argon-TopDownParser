package com.three_csa.argon.SemAnalyzer.Node;

import com.three_csa.argon.Tokenizer.TokenType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArithmeticNode<T> extends ASTNode {
    //TokenType operator;
    private T value;
    private final TokenType width;
    private boolean isNegative;
    public ArithmeticNode(String expressionType, TokenType width) {
        super(expressionType);
        this.width = width;
        this.isNegative = false;
    }
    public T getValue() {
        return value;
    }
    
    public String getType(){
        return value.getClass().getSimpleName();
    }

    public boolean isNegative() {
        return isNegative;
    }

    public void setNegative(boolean negative) {
        isNegative = negative;
    }

    public void setValue(T value) {
        switch (value){
            case Integer i:
                if(width == TokenType.MOLE32 && i.compareTo(Integer.MAX_VALUE) >= 1){
                    System.out.println("TypeError: Value too big to fit inside the destination.");
                    System.exit(1);
                }else if (width == TokenType.MOLE32 && i.compareTo(Integer.MIN_VALUE) <= -1) {
                    System.out.println("TypeError: Value too small to fit inside the destination.");
                    System.exit(1);
                }else{
                    this.value = value;
                }
            break;
            case Long l:
                if(width == TokenType.MOLE64 && l.compareTo(Long.MAX_VALUE) >= 1){
                    System.out.println("TypeError: Value too big to fit inside the destination: " + value);
                    System.exit(1);
                } else if (width == TokenType.MOLE32 && l.compareTo(Long.MIN_VALUE) <= -1) {
                    System.out.println("TypeError: Value too small to fit inside the destination: " + value);
                    System.exit(1);
                }else{
                    this.value = value;
                }
                break;
            case String ident:
                Pattern pattern = Pattern.compile("_(([a-zA-Z0-9]|_)*)|[a-zA-Z](([a-zA-Z0-9]|_)*)$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(ident);
                boolean matchFound = matcher.find();
                if(!matchFound) {
                    System.out.println("VarError: Invalid identifier: " + value);
                    System.exit(1);
                }else {
                    this.value = value;
                }
                break;
            default:
                if(value instanceof TokenType t){
                    if(t.equals(TokenType.ADD) || t.equals(TokenType.SUB) || t.equals(TokenType.MUL) || t.equals(TokenType.DIV) || t.equals(TokenType.EXP) || t.equals(TokenType.INPUT)){
                        this.value = value;
                    }else{
                        System.out.println("LanguageError: Illegal arithmetic expression: "+expressionType);
                        System.exit(1);
                    }
                }else{
                    System.out.println("LanguageError: Unexpected value: " + value);
                    System.exit(1);
                }

        }

    }
}

