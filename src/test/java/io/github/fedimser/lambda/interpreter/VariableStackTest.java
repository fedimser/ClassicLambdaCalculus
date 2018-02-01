package io.github.fedimser.lambda.interpreter;

import junit.framework.TestCase;

public class VariableStackTest extends TestCase {
    public void testPushAndPop() throws Exception {
        VariableStack vStack = new VariableStack();
        vStack.push(new Token("x"));
        assertEquals(1, vStack.getDeBruijnIndex(new Token("x")));
        vStack.push(new Token("y"));
        assertEquals(2, vStack.getDeBruijnIndex(new Token("x")));
        assertEquals(1, vStack.getDeBruijnIndex(new Token("y")));
        vStack.push(new Token("z"));
        assertEquals(3, vStack.getDeBruijnIndex(new Token("x")));
        assertEquals(2, vStack.getDeBruijnIndex(new Token("y")));
        assertEquals(1, vStack.getDeBruijnIndex(new Token("z")));
        vStack.pop();
        assertEquals(2, vStack.getDeBruijnIndex(new Token("x")));
        assertEquals(1, vStack.getDeBruijnIndex(new Token("y")));
        vStack.pop();
        assertEquals(1, vStack.getDeBruijnIndex(new Token("x")));
    }

    public void testCanHandleClashes() throws Exception {
        VariableStack vStack = new VariableStack();
        vStack.push(new Token("x"));
        vStack.push(new Token("y"));
        vStack.push(new Token("x"));
        vStack.push(new Token("y"));
        assertEquals(2, vStack.getDeBruijnIndex(new Token("x")));
        vStack.pop();
        assertEquals(1, vStack.getDeBruijnIndex(new Token("x")));
        vStack.pop();
        assertEquals(2, vStack.getDeBruijnIndex(new Token("x")));
        vStack.pop();
        assertEquals(1, vStack.getDeBruijnIndex(new Token("x")));
    }

    public void testCanGenerateNames()throws Exception {
        VariableStack vStack = new VariableStack();
        String name1 = vStack.pushDefault();
        String name2 = vStack.pushDefault();
        assertEquals(name2, vStack.getNameForDeBruijnIndex(1));
        assertEquals(name1, vStack.getNameForDeBruijnIndex(2));
        vStack.pop();
        assertEquals(name1, vStack.getNameForDeBruijnIndex(1));
    }

}