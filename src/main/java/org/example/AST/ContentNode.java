package org.example.AST;

import org.example.TokenType;
import org.w3c.dom.Node;

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
