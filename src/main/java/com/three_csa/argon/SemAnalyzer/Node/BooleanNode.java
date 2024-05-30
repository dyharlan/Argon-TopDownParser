package com.three_csa.argon.SemAnalyzer.Node;

public class BooleanNode<T> extends ASTNode {

    private T value;
    private boolean isInverted;

    public BooleanNode(String expressionType) {
        super(expressionType);
    }

    public BooleanNode(String expressionType, T value) {
        super(expressionType);
        setExpressionType(expressionType);
        this.value = value;
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