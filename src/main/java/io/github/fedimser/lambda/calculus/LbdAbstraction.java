package io.github.fedimser.lambda.calculus;


import io.github.fedimser.lambda.interpreter.LambdaException;
import io.github.fedimser.lambda.interpreter.SyntaxErrorException;
import io.github.fedimser.lambda.interpreter.VariableStack;


public final class LbdAbstraction extends LbdExpression {
    private final LbdExpression body;

    public LbdAbstraction(LbdExpression body) {
        this.body = body;
    }


    public LbdExpression getBody() {
        return body;
    }

    public LbdExpression reduce() throws LambdaException {
        // Try eta-conversion, namely:
        // λx.(f x) => f, if f doesn't have free x.
        if (body instanceof LbdApplication){
            LbdApplication fx = (LbdApplication)body;
            LbdExpression x2 =  ((LbdApplication)body).getArgument();
            if(x2 instanceof LbdVariable) {
                if(((LbdVariable)x2).getDeBruijnIndex() == 1) {
                    LbdExpression f = fx.getFunction();
                    if(!f.hasVariable(1)) {
                        return f.betaReduction(1, null).reduce();
                    }
                }
            }
        }

        // Try reduce body.
        LbdExpression reducedBody = body.reduce();
        if (reducedBody != body) {
           return (new LbdAbstraction(reducedBody)).reduce();
        }

        return this;
    }

    @Override
    protected LbdExpression betaReduction(int level, LbdExpression replacement) throws LambdaException {
        LbdExpression newReplacement = (replacement == null)?null : replacement.increaseFreeIndices(level);
        LbdExpression reducedBody = body.betaReduction(level+1, newReplacement) ;
        if (reducedBody != body) {
            return (new LbdAbstraction(reducedBody)).reduce();
        }

        return this;
    }

    @Override
    protected boolean hasVariable(int level) throws LambdaException {
        return body.hasVariable(level+1);
    }

    @Override
    protected LbdExpression increaseFreeIndices(int level) throws LambdaException {
        return new LbdAbstraction(this.body.increaseFreeIndices(level+1));
    }

    @Override
    protected String getClassicFormula(VariableStack vStack) {
        String varName = vStack.pushDefault();
        String result = String.format("(λ%s.%s)", varName, body.getClassicFormula(vStack));
        vStack.pop();
        return result;
    }

    @Override
    public String getDeBruijnFormula() {
        return  String.format("(λ %s)", body.getDeBruijnFormula());
    }

    @Override
    protected String getShortFormula(VariableStack vStack) throws LambdaException {
        LbdExpression abs = this;
        StringBuilder variables = new StringBuilder();
        int depthCtr=0;
        while (abs instanceof LbdAbstraction) {
            variables.append(vStack.pushDefault());
            abs = ((LbdAbstraction)abs).body;
            depthCtr++;
        }

        String result = String.format("λ%s.%s", variables.toString(), abs.getShortFormula(vStack));

        for(int i=0;i<depthCtr;i++) {
            vStack.pop();
        }
        return result;
    }

    @Override
    protected String getShortDeBruijnFormula() throws LambdaException {
        return  String.format("λ%s", body.getShortDeBruijnFormula());
    }

}
