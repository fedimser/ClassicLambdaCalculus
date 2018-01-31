package io.github.fedimser.lambda.interpreter;

/**
 * Interpreter for classic lambda calculus.
 */
public class LambdaInterpreter {
    private static final String[] reservedWords = {"exit", "lambda"};

    private boolean exited;

    public LambdaInterpreter(){

    }

    public String processCommand(String command) {
        System.out.println("["+command+"]");
        if (command.equals("exit")) {
            exited = true;
            return "";
        } else {
            return "Hello, world.";
        }
    }

    public boolean isExited() {
        return exited;
    }
}
