package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;

public abstract class LbdExpression {
    public abstract LbdExpression replace(LbdVariable var, LbdExpression value) throws LambdaException;

    /**
     * MUST return reference to itself, if no reducing is possible.
     * @return
     * @throws LambdaException
     */
    public abstract LbdExpression reduce() throws LambdaException;

    public abstract LbdExpression replaceFree(String var, LbdExpression replaceTo) throws LambdaException;

    public abstract boolean hasFree(String var);


}
