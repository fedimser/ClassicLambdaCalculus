package io.github.fedimser.lambda.interpreter;

public class DepthException extends LambdaException {
    public DepthException() {
        super("Too deep");
    }
}
