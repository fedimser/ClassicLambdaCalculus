package io.github.fedimser.lambda.calculus;

public class Abstraction extends Expression {
    private Variable variable;
    private Expression body;

    @Override
    public String toString() {
        return String.format("(λ%s.%s", variable.toString(), body.toString());
    }
}
