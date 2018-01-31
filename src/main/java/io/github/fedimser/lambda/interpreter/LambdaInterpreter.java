package io.github.fedimser.lambda.interpreter;

import io.github.fedimser.lambda.calculus.Expression;

import java.util.HashMap;
import java.util.Map;

/**
 * Interpreter for classic lambda calculus.
 */
public class LambdaInterpreter {
    private static final String[] reservedWords = {"exit", "lambda"};
    private Map<String, Expression> vars = new HashMap<String, Expression>(); //

    private boolean exited;

    public LambdaInterpreter() {
        exited = false;
    }

    public String processCommand(String command) {
        System.out.println("["+command+"]");
        if (command.equals("exit")) {
            exited = true;
            return "";
        } else {
            try {
                Expression expression = Expression.parse(command);
                return expression.toString();
            } catch (LambdaException ex) {
              return "Error: " + ex.getMessage();
            }
        }
    }

    public boolean isExited() {
        return exited;
    }

}
