package io.github.fedimser.lambda.calculus;

public class Application extends Expression {
    private Expression function;
    private Expression argument;

    @Override
    public String toString() {
        return String.format("(%s %s)", function.toString(), argument.toString());
    }
}
