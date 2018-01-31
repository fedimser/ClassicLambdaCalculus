package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;

public final class Application extends Expression {
    private final Expression function;
    private final Expression argument;

    public Application(Expression function, Expression argument) {
        this.function = function;
        this.argument = argument;
    }

    public Expression betaReduce() throws LambdaException {
        if(!(this.function instanceof Abstraction)) {
            throw new LambdaException("Can't beta-reduce Application, first part of which is not Abstraction.");
        }
        Abstraction abstraction = (Abstraction)function;
        return abstraction.getBody().replace(abstraction.getVariable(), this.argument);
    }

    @Override
    public String toString() {
        return String.format("(%s %s)", function.toString(), argument.toString());
    }

    public Expression replace(Variable var, Expression value) throws LambdaException {
        return new Application(function.replace(var, value), argument.replace(var, value));
    }
}
