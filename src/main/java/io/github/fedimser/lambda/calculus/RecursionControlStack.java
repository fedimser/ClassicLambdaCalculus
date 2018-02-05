package io.github.fedimser.lambda.calculus;

import java.util.HashSet;
import java.util.Set;

public class RecursionControlStack {
    private Set<LbdExpression> stackSet = new HashSet<LbdExpression>();
    private boolean foundCycle = false;
    public static RecursionControlStack INSTANCE = new RecursionControlStack();
    private RecursionControlStack() {}

    public void reset() {
        foundCycle = false;
        stackSet.clear();
    }

    public boolean push(LbdExpression exp) {
        if(foundCycle) return false;
        if(stackSet.contains(exp)) {
            System.out.println("Recursion out!");
            //foundCycle = true;
            return false;
        }
        stackSet.add(exp);
        return true;
    }

    public void pop(LbdExpression exp) {
        stackSet.remove(exp);
    }
}
