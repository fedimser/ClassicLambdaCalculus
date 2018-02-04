package io.github.fedimser.lambda.interpreter;

import io.github.fedimser.lambda.calculus.LbdExpression;
import io.github.fedimser.lambda.calculus.LbdExpression.FormulaStyle;
import junit.framework.TestCase;

public class StabilityTest extends TestCase {
    private LambdaInterpreter li = new LambdaInterpreter();

    // First, evaluates given expession.
    // Than gets its classic formula.
    // Than parses it and gets classic formula for new expression.
    // Checks that given string is classic formula of expression it describes.both formulas are the same.
    private void check(String... formulas) throws Exception {
        for(String f : formulas) {
            LbdExpression ex1 = li.parseReduce(f);
            String s1 = ex1.getFormula(FormulaStyle.CLASSIC);
            LbdExpression ex2 = li.parseReduce(s1);
            String s2 = ex2.getFormula(FormulaStyle.CLASSIC);
            assertEquals(s1, s2);
        }
    }

    public void testStability() throws Exception {
        check("ID", "SUM", "MUL", "SUB", "INC", "PRED", "POW");
        check("TRUE", "FALSE", "NOT", "AND", "OR");
        check("ISZERO", "GTE", "LTE", "GT", "LT", "EQUALS");
        check("PAIR", "FIRST", "SECOND");
        check("FOR", "FACTORIAL");
    }

}
