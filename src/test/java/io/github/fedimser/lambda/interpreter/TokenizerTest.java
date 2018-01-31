package io.github.fedimser.lambda.interpreter;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class TokenizerTest extends TestCase {
    private void testTokenizer(String expression, String... expectedTokens) {
        List<String> tokens = Tokenizer.tokenize(expression);
        assertEquals(Arrays.asList(expectedTokens), tokens);
    }

    public void testTokenize() throws Exception {
        testTokenizer("");
        testTokenizer("  ");
        testTokenizer("_a01", "_a01");
        testTokenizer("abc.d,e", "abc", ".", "d,e");
        testTokenizer("  123   ab12 (ghe13  )  ", "123", "ab12", "(", "ghe13", ")");
        testTokenizer("lambdax.x", "lambdax", ".", "x");
        testTokenizer("λx.x", "λ", "x", ".", "x");
        testTokenizer("(λx.x)", "(", "λ", "x", ".", "x", ")");
        testTokenizer("(λxy.xy)", "(", "λ", "xy", ".", "xy", ")");
        testTokenizer("(λx y.x y)", "(", "λ", "x", "y", ".", "x", "y", ")");
        testTokenizer("λabc.a(bc)", "λ", "abc", ".", "a", "(", "bc", ")");
        testTokenizer("λ a b c . a(b c) ", "λ", "a", "b", "c", ".", "a", "(", "b", "c", ")");
        testTokenizer("λλ", "λ","λ");
        testTokenizer("αα", "αα");

    }
}