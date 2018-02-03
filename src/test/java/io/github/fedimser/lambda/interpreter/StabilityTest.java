package io.github.fedimser.lambda.interpreter;

import io.github.fedimser.lambda.calculus.LbdExpression;
import io.github.fedimser.lambda.calculus.LbdExpression.FormulaStyle;
import io.github.fedimser.lambda.interpreter.LambdaInterpreter;
import junit.framework.TestCase;

public class StabilityTest extends TestCase {
    private LambdaInterpreter li = new LambdaInterpreter();

    // Checks that both expression are reduced to the same expression.
    private void check(String exp1, String exp2) throws Exception {
        assertTrue(li.evaluate(exp1).equals(li.evaluate(exp2)));
    }


    private void checkStableClassic(String s) throws Exception {
        assertEquals(s, li.evaluate(s).getFormula(FormulaStyle.CLASSIC));
    }

}
