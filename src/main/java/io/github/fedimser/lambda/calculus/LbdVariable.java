package io.github.fedimser.lambda.calculus;

import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.SyntaxErrorException;
import io.github.fedimser.lambda.interpreter.Token;
import io.github.fedimser.lambda.interpreter.VariableStack;

import java.beans.Expression;

public final class LbdVariable extends LbdExpression {
    private final int deBruijnIndex;

    private LbdVariable(int deBruijnIndex) {
        this.deBruijnIndex = deBruijnIndex;
    }

    public int getDeBruijnIndex() {
        return deBruijnIndex;
    }

    public static LbdVariable forIndex(int deBruijnIndex) {
        return new LbdVariable(deBruijnIndex);
    }

    @Override
    public String toString() {
        return String.valueOf(deBruijnIndex);
    }


    @Override
    public LbdExpression reduce() throws LambdaException {
        return this;
    }

    @Override
    protected LbdExpression betaReduction(int level, LbdExpression replacement) throws LambdaException {
        if(deBruijnIndex >level) {
            return LbdVariable.forIndex(deBruijnIndex-1);
        } else if (deBruijnIndex == level) {
            return replacement;
        } else {
            return this;
        }
    }


    @Override
    protected boolean hasVariable(int level) throws LambdaException {
        return (deBruijnIndex==level);
    }

    @Override
    protected LbdExpression increaseFreeIndices(int level) throws LambdaException {
        if(deBruijnIndex>=level) return LbdVariable.forIndex(deBruijnIndex+1);
        else return this;
    }

    @Override
    protected String getClassicFormula(VariableStack vStack) {
        return vStack.getNameForDeBruijnIndex(deBruijnIndex);
    }

    @Override
    public String getDeBruijnFormula() {
        return String.valueOf(this.deBruijnIndex);
    }

    @Override
    protected String getShortFormula(VariableStack vStack) throws LambdaException {
        String name = vStack.getNameForDeBruijnIndex(deBruijnIndex);
        if (name.length()!=1) throw new LambdaException("Too deep for short style");
        return name;
    }

    @Override
    protected String getShortDeBruijnFormula() throws LambdaException {
        if (deBruijnIndex >= 10) throw new LambdaException("Too deep for short style");
        return String.valueOf(deBruijnIndex);
    }
}
