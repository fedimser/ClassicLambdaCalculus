package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.SyntaxErrorException;


public final class LbdAbstraction extends LbdExpression {
    private final LbdVariable variable;
    private final LbdExpression body;

    public LbdAbstraction(LbdVariable variable, LbdExpression body) {
        this.variable = variable;
        this.body = body;
    }

    public LbdVariable getVariable() {
        return variable;
    }

    public LbdExpression getBody() {

        return body;
    }

    @Override
    public String toString() {
        return String.format("(λ%s.%s)", variable.toString(), body.toString());
    }

    public LbdExpression replace(LbdVariable var, LbdExpression value) throws LambdaException {
        if(this.variable.getName().equals(var.getName())){
            throw new SyntaxErrorException("Clashing variable names.");
        } else {
            return new LbdAbstraction(this.variable, this.body.replace(var, value));
        }
    }

    public LbdExpression reduce() throws LambdaException {
        // Try eta-conversion, namely:
        // λx.(f x2) => f, if x=x2 and f doesn't have free x.
        if (body instanceof LbdApplication){
            LbdApplication fx = (LbdApplication)body;
            LbdExpression x2 =  ((LbdApplication)body).getArgument();
            String x = variable.getName();
            if(x2 instanceof LbdVariable) {
                if(((LbdVariable)x2).getName().equals(x)) {
                    LbdExpression f = fx.getFunction();
                    if(!f.hasFree(x)) {
                        return f;
                    }
                }
            }
        }

        // Try reduce body.
        LbdExpression reducedBody = body.reduce();
        if (reducedBody != body) {
           return new LbdAbstraction(this.variable, reducedBody);
        }

        return this;
    }

    @Override
    public LbdExpression replaceFree(String var, LbdExpression replaceTo) throws LambdaException {
        if(hasFree(var)) {
            return new LbdAbstraction(this.variable, this.body.replaceFree(var, replaceTo));
        } else {
            return this;
        }
    }

    @Override
    public boolean hasFree(String var) {
        return !(variable.getName().equals(var)) && body.hasFree(var);
    }
}
