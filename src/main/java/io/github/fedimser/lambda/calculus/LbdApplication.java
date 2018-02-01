package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;

public final class LbdApplication extends LbdExpression {
    private final LbdExpression function;
    private final LbdExpression argument;

    public LbdApplication(LbdExpression function, LbdExpression argument) {
        this.function = function;
        this.argument = argument;
    }

    public LbdExpression getFunction() {
        return function;
    }

    public LbdExpression getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return String.format("(%s %s)", function.toString(), argument.toString());
    }

    public LbdExpression replace(LbdVariable var, LbdExpression value) throws LambdaException {
        return new LbdApplication(function.replace(var, value), argument.replace(var, value));
    }

    public LbdExpression reduce() throws LambdaException {
        // Try beta-reduction, namely:
        // (λx.y)z => y[x->z]
        if(this.function instanceof LbdAbstraction){
            LbdAbstraction lxy = (LbdAbstraction) this.function;
            String x = lxy.getVariable().getName();
            LbdExpression y = lxy.getBody();
            LbdExpression reduced = y.replaceFree(x, this.argument);
            return reduced.reduce();
        }

        LbdExpression reducedFunction = function.reduce();
        LbdExpression reducedArgument = argument.reduce();
        if(reducedFunction!=function || reducedArgument!=argument) {
            return (new LbdApplication(reducedFunction, reducedArgument)).reduce();
        }

        return this;
    }

    @Override
    public LbdExpression replaceFree(String var, LbdExpression replaceTo) throws LambdaException {
        if(hasFree(var)) {
            return new LbdApplication(function.replaceFree(var, replaceTo), argument.replaceFree(var, replaceTo));
        } else {
            return this;
        }
    }

    @Override
    public boolean hasFree(String var) {
        return function.hasFree(var) || argument.hasFree(var);
    }
}
