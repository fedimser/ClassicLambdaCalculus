package io.github.fedimser.lambda.interpreter;

import java.util.List;

public class Token {
    private String text=null;
    private int start;
    private List<Token> nestedTokens=null;

    public Token(String text) {
        this.text = text;
    }

    public Token(String text, int start) {
        this.text = text;
        this.start = start;
    }

    public Token(List<Token> nestedTokens) {
        this.nestedTokens = nestedTokens;
        this.start = nestedTokens.get(0).getStart();
    }

    public String getText() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public List<Token> getNestedTokens() {
        return nestedTokens;
    }

    public boolean hasText(String text) {
        return this.text!=null && this.text.equals(text);
    }

    public boolean isGroup(){
        return nestedTokens != null;
    }
}
