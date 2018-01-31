package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;

public abstract class Expression {
    public abstract Expression replace(Variable var, Expression value) throws LambdaException;

    public static Expression parse(String string) throws LambdaException {
        throw new LambdaException("Not implemented");
    }
}
