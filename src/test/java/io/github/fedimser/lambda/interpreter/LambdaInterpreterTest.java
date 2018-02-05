package io.github.fedimser.lambda.interpreter;

import io.github.fedimser.lambda.calculus.LbdExpression;
import io.github.fedimser.lambda.calculus.LbdExpression.FormulaStyle;
import junit.framework.TestCase;

public class LambdaInterpreterTest extends TestCase {
    public void testCanExit()throws Exception {
        LambdaInterpreter li = new LambdaInterpreter();
        assertFalse(li.isExited());
        li.processCommand("abc");
        assertFalse(li.isExited());
        li.processCommand("exit");
        assertTrue(li.isExited());
    }

    public void testInstantEvaluation() throws Exception {
        LambdaInterpreter li = new LambdaInterpreter();
        assertEquals("(λa.a)", li.processCommand("lambda x . ID x "));
        assertEquals("(λa.(λb.(a (a (a (a b))))))", li.processCommand("SUM 2 2"));
    }

    public void testAssign() throws Exception {
        LambdaInterpreter li = new LambdaInterpreter();
        assertEquals("OK.", li.processCommand("myID = λa.a"));
        assertEquals("(λa.(λb.(a (a b))))", li.processCommand("myID 2"));
    }

    public void testAlias() throws Exception {
        LambdaInterpreter li = new LambdaInterpreter();
        assertEquals("1, ID", li.processCommand("alias λa.a"));
        assertEquals("OK.", li.processCommand("ONE = 1"));
        assertEquals("1, ID, ONE", li.processCommand("alias λa.a"));

        String expr = "λa b c d. b (d a) c";
        assertEquals("None.", li.processCommand("alias "+ expr));
        assertEquals("OK.", li.processCommand("myVar1 = " + expr));
        assertEquals("myVar1", li.processCommand("alias" + expr));
        assertEquals("OK.", li.processCommand("myVar2 = " + expr));
        assertEquals("myVar1, myVar2", li.processCommand("alias " + expr));

        // This checks that when we re-assign variable, old reference is removed from inverse index.
        assertEquals("OK.", li.processCommand("myVar1 = SUM"));
        assertEquals("myVar2", li.processCommand("alias " + expr));
        assertEquals("PLUS, SUM, myVar1", li.processCommand("alias SUM"));
    }

    public void testPrintStyles() {
        LambdaInterpreter li = new LambdaInterpreter();
        assertEquals("(λ (λ (2 (2 1))))", li.processCommand("db 2"));
        assertEquals("λab.a(ab)", li.processCommand("short 2"));
        assertEquals("λλ2(21)", li.processCommand("db_short 2"));
    }

    public void testNaturalEvaluation() {
        LambdaInterpreter li = new LambdaInterpreter();
        assertEquals("5", li.processCommand("int SUM 2 3"));
        assertEquals("64", li.processCommand("int 2 8"));
    }

    public void testBooleanEvaluation() {
        LambdaInterpreter li = new LambdaInterpreter();
        assertEquals("true", li.processCommand("bool ISZERO 0"));
        assertEquals("false", li.processCommand("bool ISZERO 1"));
    }


    public void testErrors() {
        LambdaInterpreter li = new LambdaInterpreter();
        assertEquals("Error: Empty command.", li.processCommand(""));
        assertEquals("Error: Empty command.", li.processCommand("  "));

        assertEquals("Error: Name SUM is reserved for library expression.",
                li.processCommand("SUM = lambda b c. c b b"));
        assertEquals("Error: Name 1 is reserved for library expression.",
                li.processCommand("1 = ID"));


        assertEquals("Error: Syntax error at 7: Dot expected.",
                li.processCommand("lambda x"));
        assertEquals("Error: Syntax error at 12: Stray free variable y.",
                li.processCommand("lambda x. x y"));
        assertEquals("Error: Syntax error at 7: Bad variable name: !.",
                li.processCommand("lambda !. !"));
        assertEquals("Error: Syntax error at 11: ) expected.",
                li.processCommand("lambda a. (a"));
        assertEquals("Error: Syntax error at 11: Extra ).",
                li.processCommand("lambda a. a)"));

        assertEquals("Error: Expression is not Church natural.",
                li.processCommand("int TRUE"));
        assertEquals("Error: Expression is not Church boolean.",
                li.processCommand("bool 2"));


        // assertEquals("Error: Stack overflow.", li.processCommand("(lambda x. x x)(lambda x.x x)"));
    }



}