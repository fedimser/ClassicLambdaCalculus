package io.github.fedimser.lambda.calculus;

import io.github.fedimser.lambda.interpreter.LambdaException;

public final class Variable extends Expression {
    public final static String VARIABLE_REGEX = "^[a-zA-Z_$][a-zA-Z_$0-9]*$";

    private final String name;

    public Variable(String name) {
        assert (name.matches(VARIABLE_REGEX));
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Expression replace(Variable var, Expression value) throws LambdaException {
        return (this.name.equals(var.name)) ? value : this;
    }
}
