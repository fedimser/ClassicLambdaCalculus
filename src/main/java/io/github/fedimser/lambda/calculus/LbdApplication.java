package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.VariableStack;

import java.io.ByteArrayOutputStream;

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
        if(this.reduced!=null) return this.reduced;
        this.reduced = this;

        if(RecursionControlStack.INSTANCE.push(this)) {
            // Try beta-reduction, namely:
            // (λx.y)z => y[x->z]
            if (this.function instanceof LbdAbstraction) {
                LbdAbstraction lxy = (LbdAbstraction) this.function;
                this.reduced = lxy.getBody().betaReduction(1, this.argument).reduce();
            } else {
                LbdExpression newFunction = function.reduce();
                LbdExpression newArgument = argument.reduce();

                if (newFunction != function || newArgument != argument) {
                    this.reduced = (new LbdApplication(newFunction, newArgument)).reduce();
                }
            }
        }

        RecursionControlStack.INSTANCE.pop(this);
        return this.reduced;
    }

    @Override
    protected LbdExpression betaReduction(int level, LbdExpression replacement) throws LambdaException {
        LbdExpression newFunction = function.betaReduction(level, replacement);
        LbdExpression newArgument = argument.betaReduction(level, replacement);
        if(newFunction!=function || newArgument!=argument) {
            return (new LbdApplication(newFunction, newArgument));//.reduce();
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

    @Override
    protected String getShortFormula(VariableStack vStack) throws LambdaException {
        String f1 = function.getShortFormula(vStack);
        String f2 = argument.getShortFormula(vStack);
        return String.format(getMinimalParentheses(), f1, f2);
    }

    @Override
    protected String getShortDeBruijnFormula() throws LambdaException {
        String f1 = function.getShortDeBruijnFormula();
        String f2 = argument.getShortDeBruijnFormula();
        return String.format(getMinimalParentheses(), f1, f2);
    }

    private String getMinimalParentheses() {
        if(argument instanceof LbdApplication) {
            return "%s(%s)";
        } else if (function instanceof LbdAbstraction) {
            // This can happen only for recursive infinitely reducing function, like Y combinator.
            return  "(%s)%s";
        } else {
            return "%s%s";
        }
    }

    @Override
    protected void writeSignature(ByteArrayOutputStream buf) {
        buf.write(255);
        this.function.writeSignature(buf);
        this.argument.writeSignature(buf);
    }
}
