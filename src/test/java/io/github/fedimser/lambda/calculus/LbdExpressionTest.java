package io.github.fedimser.lambda.calculus;

import io.github.fedimser.lambda.interpreter.LambdaInterpreter;
import io.github.fedimser.lambda.calculus.LbdExpression.FormulaStyle;
import junit.framework.TestCase;

public class LbdExpressionTest extends TestCase {
    private LambdaInterpreter li = new LambdaInterpreter();

    public void testGetFormula() throws Exception {
        LbdExpression churchTwo = li.evaluate("2");
        assertEquals("(λa.(λb.(a (a b))))", churchTwo.getFormula(FormulaStyle.CLASSIC));
        assertEquals("(λ (λ (2 (2 1))))", churchTwo.getFormula(FormulaStyle.DE_BRUIJN));
        assertEquals("λab.a(ab)", churchTwo.getFormula(FormulaStyle.SHORT));
        assertEquals("λλ2(21)", churchTwo.getFormula(FormulaStyle.SHORT_DE_BRUIJN));
    }
}