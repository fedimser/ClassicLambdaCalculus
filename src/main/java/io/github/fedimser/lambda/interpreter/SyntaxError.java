package io.github.fedimser.lambda.interpreter;

public class SyntaxError extends Exception {
    public SyntaxError(String message) {
        super(message);
    }
}
