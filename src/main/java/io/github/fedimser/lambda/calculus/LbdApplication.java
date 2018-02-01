package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.VariableStack;

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

    public LbdExpression reduce() throws LambdaException {
        // Try beta-reduction, namely:
        // (λx.y)z => y[x->z]
        if(this.function instanceof LbdAbstraction) {
            LbdAbstraction lxy = (LbdAbstraction) this.function;
            return lxy.getBody().betaReduction(1, this.argument).reduce();
        }

        LbdExpression newFunction = function.reduce();
        LbdExpression newArgument = argument.reduce();
        if(newFunction!=function || newArgument!=argument) {
            return (new LbdApplication(newFunction, newArgument)).reduce();
        }

        return this;
    }

    @Override
    protected LbdExpression betaReduction(int level, LbdExpression replacement) throws LambdaException {
        LbdExpression newFunction = function.betaReduction(level, replacement);
        LbdExpression newArgument = argument.betaReduction(level, replacement);
        if(newFunction!=function || newArgument!=argument) {
            return (new LbdApplication(newFunction, newArgument)).reduce();
        }
        return this;
    }

    @Override
    protected boolean hasVariable(int level) throws LambdaException {
        return function.hasVariable(level) || argument.hasVariable(level);
    }

    @Override
    protected LbdExpression increaseFreeIndices(int level) throws LambdaException {
        LbdExpression newFunction = function.increaseFreeIndices(level);
        LbdExpression newArgument = argument.increaseFreeIndices(level);
        if(newFunction!=function || newArgument!=argument) {
            return (new LbdApplication(newFunction, newArgument)).reduce();
        }
        return this;
    }

    @Override
    protected String getClassicFormula(VariableStack vStack) {
        return String.format("(%s %s)", function.getClassicFormula(vStack), argument.getClassicFormula(vStack));
    }

    @Override
    public String getDeBruijnFormula() {
        return String.format("(%s %s)", function.getDeBruijnFormula(), argument.getDeBruijnFormula());
    }
}
