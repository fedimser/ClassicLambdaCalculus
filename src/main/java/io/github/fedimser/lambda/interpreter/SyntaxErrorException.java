package io.github.fedimser.lambda.interpreter;

public class SyntaxErrorException extends LambdaException {
    public SyntaxErrorException(String message) {
        super(message);
    }

    public SyntaxErrorException(String message, Token token) {
        super(String.format("Syntax error at %d: %s", token.getStart(), message));
    }
}
