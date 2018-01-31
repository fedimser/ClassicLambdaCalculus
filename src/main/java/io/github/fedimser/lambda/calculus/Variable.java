package io.github.fedimser.lambda.calculus;

public class Variable extends Expression {
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
