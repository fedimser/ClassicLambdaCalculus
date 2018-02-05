package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.VariableStack;

import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;


public abstract class LbdExpression {
    public enum FormulaStyle {
        CLASSIC,
        DE_BRUIJN,
        SHORT,
        SHORT_DE_BRUIJN,
        SIGNATURE
    }

    private int cachedHashCode=0;
    private Map<FormulaStyle, String> cachedFormulas =
            new EnumMap<FormulaStyle, String>(FormulaStyle.class);

    // If it's not null, it must equal to reduced form of this expression.
    protected LbdExpression reduced = null;

    /**
     * Applies beta-reduction and eta-conversion where possible.
     * MUST return reference to itself, if no reducing is possible.
     * @return
     * @throws LambdaException
     */
    protected abstract LbdExpression reduce() throws LambdaException;


    public LbdExpression safeReduce() throws LambdaException {
        RecursionControlStack.INSTANCE.reset();
        if(this.reduced == null) {
            reduced = this.reduce();
            reduced.reduced = reduced;
        }
        return reduced;
    }

    public boolean isReduced() {
        return (reduced == this);
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
        } else if (style == FormulaStyle.SIGNATURE) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            writeSignature(buf);
            result = buf.toString();
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
        return this.getFormula(FormulaStyle.SIGNATURE).equals(second.getFormula(FormulaStyle.SIGNATURE));
    }

    @Override
    public int hashCode() {
        if(cachedHashCode==0){
            cachedHashCode = this.getFormula(FormulaStyle.SIGNATURE).hashCode();
            assert (cachedHashCode!=0);
        }
        return cachedHashCode;
    }

    protected abstract void writeSignature(ByteArrayOutputStream buf);

    /**
     * If expression is Church natural, returns number it represents.
     * Otherwise returns -1.
     * Assumes that expression is reduced. Otherwise throws assertion error.
     * @return Corresponding number or -1 if it is not Church natural.
     */
    private int extractNatural() {
        assert (isReduced());

        LbdExpression t = this;
        if(!(t instanceof LbdAbstraction)) return -1;
        t = ((LbdAbstraction)t).getBody();

        if (t instanceof LbdVariable) {
            assert(((LbdVariable) t).getDeBruijnIndex() == 1);
            return 1;
        } else if (t instanceof  LbdAbstraction) {
            t= ((LbdAbstraction) t).getBody();
        } else {
            return -1;
        }

        for(int i=0;;i++) {
            if(t instanceof LbdVariable) {
                if (((LbdVariable)t).getDeBruijnIndex() == 1) {
                    return i;
                } else {
                    return -1;
                }
            } else if (t instanceof LbdApplication) {
                LbdExpression left = ((LbdApplication)t).getFunction();
                if (!(left instanceof LbdVariable)) return -1;
                if (((LbdVariable)left).getDeBruijnIndex()!=2) return -1;
                t = ((LbdApplication)t).getArgument();
            } else {
                return -1;
            }
        }
    }

    public int asNatural() throws LambdaException {
        if (!isReduced()) throw new LambdaException("Expression is not reduced");
        int result = extractNatural();
        if(result==-1) {
            throw new LambdaException("Expression is not Church natural");
        } else {
            return result;
        }
    }

    public boolean asBoolean() throws LambdaException {
        if (!isReduced()) throw new LambdaException("Expression is not reduced");
        String formula = getFormula(FormulaStyle.SHORT_DE_BRUIJN);
        if(formula.equals("λλ2")){
            return true;
        } else if (formula.equals("λλ1")) {
            return false;
        } else {
            throw new LambdaException("Expression is not Church boolean");
        }
    }

}
