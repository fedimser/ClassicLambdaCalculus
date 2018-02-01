package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.SyntaxErrorException;

public final class Abstraction extends Expression {
    private final Variable variable;
    private final Expression body;

    public Abstraction(Variable variable, Expression body) {
        this.variable = variable;
        this.body = body;
    }

    public Variable getVariable() {
        return variable;
    }

    public Expression getBody() {

        return body;
    }

    @Override
    public String toString() {
        return String.format("(λ%s.%s)", variable.toString(), body.toString());
    }

    public Expression replace(Variable var, Expression value) throws LambdaException {
        if(this.variable.getName().equals(var.getName())){
            throw new SyntaxErrorException("Clashing variable names.");
        } else {
            return new Abstraction(this.variable, this.body.replace(var, value));
        }
    }

    public Expression reduce() throws LambdaException {
        return this;
    }
}
