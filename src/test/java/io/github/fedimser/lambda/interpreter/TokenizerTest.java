package io.github.fedimser.lambda.interpreter;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TokenizerTest extends TestCase {


    private void flattenTokensRecursive(List<Token> tokens, List<String> result) {
        for (Token token : tokens) {
            if (token.isGroup()) {
                result.add("(");
                flattenTokensRecursive(token.getNestedTokens(), result);
                result.add(")");
            } else {
                result.add(token.getText());
            }
        }
    }

    private List<String> flattenTokens(List<Token> tokens) {
        List<String> result = new ArrayList<String>();
        flattenTokensRecursive(tokens, result);
        return result;
    }

    // Checks that tokens are evaluated correctly.
    // Then checks that they are grouped correctly.
    // Parentheses should be balanced and there should be at least one token between corresponding parentheses.
    private void testTokensText(String expression, String... expectedTokens) throws SyntaxErrorException {
        List<String> list1 = flattenTokens(Tokenizer.tokenizeFlat(expression));
        assertEquals(Arrays.asList(expectedTokens), list1);
        List<String> list2 = flattenTokens(Tokenizer.tokenizeAndGroup(expression));
        assertEquals(list1, list2);
    }

    private void expectSyntaxError(String expression) {
        boolean errorThrown = false;
        try {
            Tokenizer.tokenizeAndGroup(expression);
        } catch (SyntaxErrorException e) {
            errorThrown = true;
        }
        assertTrue(errorThrown);
    }

    private void testTokensStart(String expression, Integer... expectedStarts) {
        List<Integer> starts = Tokenizer.tokenizeFlat(expression).
                stream().
                map(x -> x.getStart()).
                collect(Collectors.toList());
        assertEquals(Arrays.asList(expectedStarts), starts);
    }


    public void testCanTokenize() throws Exception {
        testTokensText("");
        testTokensText("  ");
        testTokensText("_a01", "_a01");
        testTokensText("abc.d,e", "abc", ".", "d,e");
        testTokensText("  123   ab12 (ghe13  )  ", "123", "ab12", "(", "ghe13", ")");
        testTokensText("lambdax.x", "lambdax", ".", "x");
        testTokensText("λx.x", "λ", "x", ".", "x");
        testTokensText("(λx.x)", "(", "λ", "x", ".", "x", ")");
        testTokensText("(λxy.xy)", "(", "λ", "xy", ".", "xy", ")");
        testTokensText("(λx y.x y)", "(", "λ", "x", "y", ".", "x", "y", ")");
        testTokensText("λabc.a(bc)", "λ", "abc", ".", "a", "(", "bc", ")");
        testTokensText("λ a b c . a(b c) ", "λ", "a", "b", "c", ".", "a", "(", "b", "c", ")");
        testTokensText("λλ", "λ", "λ");
        testTokensText("αα", "αα");
        testTokensText("a=λx.x", "a", "=", "λ", "x", ".", "x");
    }

    public void testCanHandleParentheses() throws Exception {
        testTokensText("a(b(c(d)))", "a", "(", "b", "(", "c", "(", "d", ")", ")", ")");
        testTokensText("A(b)", "A", "(", "b", ")");
        testTokensText("A((b))", "A", "(", "(", "b", ")", ")");
        expectSyntaxError("A()");
        expectSyntaxError("A(b()");
        expectSyntaxError("A((b)");
        expectSyntaxError("A(");
        expectSyntaxError("A)");
    }

    public void testCanDetermineTokenStarts() throws Exception {
        testTokensStart("abc def", 0, 4);
        testTokensStart("λname1. name1 name2", 0, 1, 6, 8, 14);
    }


}