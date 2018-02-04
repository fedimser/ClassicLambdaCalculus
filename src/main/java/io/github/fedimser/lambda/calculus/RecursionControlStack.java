package io.github.fedimser.lambda.calculus;

import java.util.HashSet;
import java.util.Set;

public class RecursionControlStack {
    private Set<LbdExpression> stackSet = new HashSet<LbdExpression>();
    public static RecursionControlStack INSTANCE = new RecursionControlStack();
    private RecursionControlStack() {}

    public void reset() {
        stackSet.clear();
    }

    public boolean push(LbdExpression exp) {
        if(stackSet.contains(exp)) return false;
        stackSet.add(exp);
        return true;
    }

    public void pop(LbdExpression exp) {
        stackSet.remove(exp);
    }
}
