package io.github.fedimser.lambda.interpreter;

import java.util.*;

public class Tokenizer {
    private static final List<Character> SPECIAL_CHARACTERS = Arrays.asList('λ', '.', '(', ')');

    /**
     * Splits input string to tokens, by following rules:
     *  - Characters from SPECIAL_CHARACTERS are always tokens (of length 1), regardless previous and next character.
     *  - Any sequence of other non-space character is a token.
     *  - Two tokens must be separated with one or several spaces, unless one of them is special character.
     * This is simple pre-processing for further parsing.
     * @param expression
     * @return List of tokens in given string.
     */
    public static List<String> tokenize(String expression) {
        List<String> result = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        int start=0;
        for(int i=0; i < expression.length();i++) {
            char currentChar = expression.charAt(i);
            if (SPECIAL_CHARACTERS.contains(currentChar)) {
                if(sb.length()!=0) {
                    result.add(sb.toString());
                    sb.setLength(0);
                }
                result.add(Character.toString(currentChar));
            } else if (Character.isSpaceChar(currentChar) ) {
                if(sb.length()!=0) {
                    result.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(currentChar);
            }
        }


        if(sb.length()!=0) {
            result.add(sb.toString());
            sb.setLength(0);
        }

        return result;
    }
}
