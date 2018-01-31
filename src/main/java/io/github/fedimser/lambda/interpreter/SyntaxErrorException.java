package io.github.fedimser.lambda.interpreter;

public class SyntaxErrorException extends LambdaException {
    public SyntaxErrorException(String message) {
        super(message);
    }
}
