package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.LambdaInterpreter;
import io.github.fedimser.lambda.interpreter.VariableStack;

import java.util.EnumMap;
import java.util.Map;


public abstract class LbdExpression {
    public enum FormulaStyle {
        CLASSIC,
        DE_BRUIJN,
        SHORT,
        SHORT_DE_BRUIJN
    }

    private Map<FormulaStyle, String> cachedFormulas =
            new EnumMap<FormulaStyle, String>(FormulaStyle.class);

    /**
     * Applies beta-reduction and eta-conversion where possible.
     * MUST return reference to itself, if no reducing is possible.
     * @return
     * @throws LambdaException
     */
    protected abstract LbdExpression reduce() throws LambdaException;


    public LbdExpression safeReduce() throws LambdaException {
        try {
            return this.reduce();
        } catch (StackOverflowError ex) {
            throw new LambdaException("Stack overflow");
        }
    }

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





    protected abstract String getClassicFormula(VariableStack vStack);

    protected abstract String getDeBruijnFormula();

    protected abstract String getShortFormula(VariableStack vStack) throws LambdaException;

    protected abstract String getShortDeBruijnFormula() throws LambdaException;


    /**
     * Returns string representation of this expression.
     * For example, for Church's number 2:
     *  - Classic formula is "(λa.(λb.(a (a b))))";
     *  - De Bruijn formula is "(λ (λ (2 (2 1))))".
     *  - Short formula is "λab.a(ab)"
     *  - Short De Bruijn formula is "λλ2(21)".
     * @param style Variant of lambda notation.
     * @return Formula.
     */
    public String getFormula(FormulaStyle style) {
        if (cachedFormulas.containsKey(style)) {
            return cachedFormulas.get(style);
        }
        String result = "";

        if(style == FormulaStyle.CLASSIC) {
            result = getClassicFormula(new VariableStack());
        } else if (style == FormulaStyle.DE_BRUIJN) {
            result = getDeBruijnFormula();
        } else if (style == FormulaStyle.SHORT) {
            try {
                return getShortFormula(new VariableStack());
            } catch (LambdaException e) {
                return "Error: " + e.getMessage();
            }
        } else if (style == FormulaStyle.SHORT_DE_BRUIJN) {
            try {
                return getShortDeBruijnFormula();
            } catch (LambdaException e) {
                return "Error: " + e.getMessage();
            }
        }

        cachedFormulas.put(style, result);
        return result;
    }

    @Override
    public String toString() {
        return this.getFormula(FormulaStyle.DE_BRUIJN);
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (!(obj instanceof LbdExpression)) return false;
        LbdExpression second = (LbdExpression)obj;
        return this.toString().equals(second.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
