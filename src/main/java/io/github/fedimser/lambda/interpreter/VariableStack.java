package io.github.fedimser.lambda.interpreter;

import io.github.fedimser.lambda.calculus.LbdVariable;

import java.util.Stack;

public class VariableStack {
    private static final String VARIABLE_NAME_REGEX = "^[a-zA-Z_$][a-zA-Z_$0-9]*$";
    private static final int MAXIMAL_DEPTH = 256;
    private static final String[] DEFAULT_VAR_NAMES = new String[]{
            "a","b","c","d","e","f","g","h","i","j","k","l","m",
            "n","o","p","q","r","s","t","u","v","w","x","y","z"};


    private String[] stack = new String[MAXIMAL_DEPTH];
    private int top = 0;

    public VariableStack() {

    }

    public int getDeBruijnIndex(Token token) throws SyntaxErrorException {
        // TODO: Optimize this(?)
        String varName = token.getText();
        for(int i=top-1, idx=1;i>=0;i--,idx++) {
            if (stack[i].equals(varName)) return idx;
        }
        throw new SyntaxErrorException("Stray free variable " + token.getText(), token);
    }

    public void push(Token token) throws SyntaxErrorException {
        if(!token.getText().matches(VARIABLE_NAME_REGEX)) {
            throw new SyntaxErrorException("Bad variable name: " + token.getText(), token);
        }
        stack[top++] = token.getText();
    }

    public String pushDefault() {
        String name = defaultName(top);
        stack[top++] = name;
        return name;
    }

    public String getNameForDeBruijnIndex(int deBruijnIndex) {
        return stack[top-deBruijnIndex];
    }

    private String defaultName(int index) {
        if(index < DEFAULT_VAR_NAMES.length) {
            return  DEFAULT_VAR_NAMES[index];
        } else {
            return "x" + String.valueOf(index-DEFAULT_VAR_NAMES.length);
        }
    }

    public void pop() {
        assert (top>0);
        top--;
        stack[top]=null;
    }
}
