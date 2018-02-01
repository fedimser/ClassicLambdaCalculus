package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.VariableStack;


public abstract class LbdExpression {

    /**
     * Applies beta-reduction and eta-conversion where possible.
     * MUST return reference to itself, if no reducing is possible.
     * @return
     * @throws LambdaException
     */
    public abstract LbdExpression reduce() throws LambdaException;


    /**
     * Affects all variables with de Bruijn index more than level.
     * If de Bruijn index is equal to given level, replaces variable with replacement.
     * Otherwise (if it's greater) decrements it.
     * To maintain correct indices in new replacement, when it recursively goes in
     *   another abstraction, it should not only increase level, but also increase all (free) indices in replacement.
     * MUST return reference to itself, if reduction has no effect.
     * @param level
     * @param replacement
     * @return
     * @throws LambdaException
     */
    protected abstract LbdExpression betaReduction(int level, LbdExpression replacement) throws LambdaException;

    /**
     * @param level
     * @return Whether expression has free variable with de Bruijn index, equal to level.
     * @throws LambdaException
     */
    protected abstract boolean hasVariable(int level) throws LambdaException;

    /**
     * Increases all indices which are more or equal to give level.
     * @return
     * @throws LambdaException
     */
    protected abstract LbdExpression increaseFreeIndices(int level) throws LambdaException;

    public Byte[] deBruijnCode(){
        return null;
    }

    public String getClassicFormula() {
        return getClassicFormula(new VariableStack());
    }

    protected abstract String getClassicFormula(VariableStack vStack);

    public String shortFormula() {
        return "";
    }

    public abstract String getDeBruijnFormula();

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (!(obj instanceof LbdExpression)) return false;
        LbdExpression second = (LbdExpression)obj;
        return this.getDeBruijnFormula().equals(second.getDeBruijnFormula());
    }

    @Override
    public int hashCode() {
        return this.getDeBruijnFormula().hashCode();
    }
}
