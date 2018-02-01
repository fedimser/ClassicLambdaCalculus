package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;

import java.util.List;

public abstract class Expression {
    public abstract Expression replace(Variable var, Expression value) throws LambdaException;

    public abstract Expression reduce() throws LambdaException;

}
