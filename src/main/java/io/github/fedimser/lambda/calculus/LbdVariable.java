package io.github.fedimser.lambda.calculus;

import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.SyntaxErrorException;
import io.github.fedimser.lambda.interpreter.Token;

public final class LbdVariable extends LbdExpression {
    public final static String VARIABLE_REGEX = "^[a-zA-Z_$][a-zA-Z_$0-9]*$";

    private final String name;

    public LbdVariable(Token token) throws SyntaxErrorException {
        if(!token.getText().matches(VARIABLE_REGEX)){
            throw  new SyntaxErrorException("Bad variable name: " + token.getText(), token);
        }
        this.name = token.getText();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public LbdExpression replace(LbdVariable var, LbdExpression value) throws LambdaException {
        return (this.name.equals(var.name)) ? value : this;
    }

    public LbdExpression reduce() throws LambdaException {
        return this;
    }

    @Override
    public LbdExpression replaceFree(String var, LbdExpression replaceTo) throws LambdaException {
        if(name.equals(var)) {
            return replaceTo;
        } else {
            return this;
        }
    }

    @Override
    public boolean hasFree(String var) {
        return (name.equals(var));
    }

}