package io.github.fedimser.lambda.interpreter;

import java.util.*;

public class Tokenizer {
    private static final List<Character> SPECIAL_CHARACTERS = Arrays.asList('λ', '.', '(', ')', '=');

    /**
     * Splits input string to tokens, by following rules:
     *  - Characters from SPECIAL_CHARACTERS are always tokens (of length 1), regardless previous and next character.
     *  - Any sequence of other non-space character is a token.
     *  - Two tokens must be separated with one or several spaces, unless one of them is special character.
     * This is simple pre-processing for further parsing.
     * @param expression
     * @return List of tokens in given string.
     */
    public static List<Token> tokenizeFlat(String expression) {
        List<Token> result = new ArrayList<Token>();
        int start=0;
        for(int i=0; i < expression.length();i++) {
            char currentChar = expression.charAt(i);
            if (SPECIAL_CHARACTERS.contains(currentChar)) {
                if(i>start) result.add(new Token(expression.substring(start,i), start));
                result.add(new Token(Character.toString(currentChar),i));
                start=i+1;
            } else if (Character.isSpaceChar(currentChar) ) {
                if(i>start) result.add(new Token(expression.substring(start,i), start));
                start=i+1;
            }
        }

        if(expression.length()>start) result.add(new Token(expression.substring(start), start));

        return result;
    }

    private static List<Token> collectGroups(ListIterator<Token> iter) throws SyntaxErrorException {
        return collectGroups(iter, false);
    }

    // Collects all to first ")".
    // This ")" should be skipped by iterator by not included in answer.
    private static List<Token> collectGroups(ListIterator<Token> iter, boolean isNested) throws SyntaxErrorException {
        List<Token> collectedTokens = new ArrayList<Token>();
        while(iter.hasNext()) {
            Token token = iter.next();
            if (token.hasText("(")) {
                List<Token> nestedTokens = collectGroups(iter, true);
                if(nestedTokens.isEmpty()) {
                    throw new SyntaxErrorException("No tokens between parentheses.", token);
                }
                collectedTokens.add( new Token( nestedTokens));
            } else if (token.hasText(")")) {
                if(!isNested) {
                    throw new SyntaxErrorException("Extra )", token);
                }
                return collectedTokens;
            } else {
                collectedTokens.add(token);
            }
        }

        if(isNested) {
            throw new SyntaxErrorException(") expected.", iter.previous());
        }
        return collectedTokens;
    }

    public static List<Token> tokenizeAndGroup(String expression) throws SyntaxErrorException {
        List<Token> flatTokens = tokenizeFlat(expression);
        return collectGroups(flatTokens.listIterator());
    }
}
