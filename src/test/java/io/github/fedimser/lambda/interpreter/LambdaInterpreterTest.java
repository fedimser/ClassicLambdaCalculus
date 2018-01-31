package io.github.fedimser.lambda.interpreter;

import junit.framework.TestCase;

public class LambdaInterpreterTest extends TestCase {

    public void testCanExit()throws Exception {
        LambdaInterpreter interpreter = new LambdaInterpreter();
        assertFalse(interpreter.isExited());
        interpreter.processCommand("abc");
        assertFalse(interpreter.isExited());
        interpreter.processCommand("exit");
        assertTrue(interpreter.isExited());
    }
}