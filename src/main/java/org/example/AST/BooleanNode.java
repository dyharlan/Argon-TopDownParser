package org.example.AST;

import org.example.Token;
import org.example.TokenType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanNode<T> extends ASTNode {

    private T value;
    private boolean isInverted;

    public BooleanNode(String expressionType) {
        super(expressionType);
    }

    public boolean isInverted() {
        return isInverted;
    }

    public void setInverted(Boolean inverted) {
        isInverted = inverted;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getType(){
        return value.getClass().getSimpleName();
    }

}