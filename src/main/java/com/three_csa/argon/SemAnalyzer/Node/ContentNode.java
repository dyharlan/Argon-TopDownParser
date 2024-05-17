package com.three_csa.argon.SemAnalyzer.Node;

import com.three_csa.argon.Tokenizer.TokenType;

public class ContentNode extends  ASTNode {
    private TokenType contentType;
    private String content;
    public ContentNode(String expressionType, TokenType contentType, String content) {
        super(expressionType);
        this.contentType = contentType;
        this.content = content;
    }
    public TokenType getContentType() {
        return contentType;
    }
    public void setContentType(TokenType contentType) {
        this.contentType = contentType;
    }
    public String getContent() {
        return content;
    }
}
