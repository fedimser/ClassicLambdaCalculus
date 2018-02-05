package io.github.fedimser.lambda.calculus;

import io.github.fedimser.lambda.interpreter.LambdaInterpreter;
import io.github.fedimser.lambda.calculus.LbdExpression.FormulaStyle;
import junit.framework.TestCase;

public class LbdExpressionTest extends TestCase {
    private LambdaInterpreter li = new LambdaInterpreter();

    public void testGetFormula() throws Exception {
        LbdExpression churchTwo = li.parseReduce("2");
        assertEquals("(λa.(λb.(a (a b))))", churchTwo.getFormula(FormulaStyle.CLASSIC));
        assertEquals("(λ (λ (2 (2 1))))", churchTwo.getFormula(FormulaStyle.DE_BRUIJN));
        assertEquals("λab.a(ab)", churchTwo.getFormula(FormulaStyle.SHORT));
        assertEquals("λλ2(21)", churchTwo.getFormula(FormulaStyle.SHORT_DE_BRUIJN));
    }

    public void testAsNatural() throws Exception {
        assertEquals(0, li.parseReduce("0").asNatural());
        assertEquals(1, li.parseReduce("1").asNatural());
        assertEquals(2, li.parseReduce("2").asNatural());
        assertEquals(10, li.parseReduce("10").asNatural());
        assertEquals(42, li.parseReduce("42").asNatural());
    }

    public void testAsBoolean() throws Exception {
        assertEquals(false, li.parseReduce("FALSE").asBoolean());
        assertEquals(true, li.parseReduce("TRUE").asBoolean());
    }
}