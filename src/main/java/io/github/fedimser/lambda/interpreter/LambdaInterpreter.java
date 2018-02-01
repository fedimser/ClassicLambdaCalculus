package io.github.fedimser.lambda.interpreter;

import io.github.fedimser.lambda.calculus.Abstraction;
import io.github.fedimser.lambda.calculus.Application;
import io.github.fedimser.lambda.calculus.Expression;
import io.github.fedimser.lambda.calculus.Variable;

import java.util.*;

/**
 * Interpreter for classic lambda calculus.
 */
public class LambdaInterpreter {
    private static final String[] reservedWords = {"exit", "alias"};
    private Map<String, Expression> expressions = new HashMap<String, Expression>(); //
    private Set<String> libraryNames = new HashSet<String>();

    private boolean exited;

    public LambdaInterpreter() {
        exited = false;
        try {
            loadLibrary();
        } catch (LambdaException ex) {
            System.out.println("Couldn't load library.\n" + ex.getMessage());
            exited = true;
        }
    }

    public String processCommand(String command) {
        try {
            List<Token> tokens = Tokenizer.tokenizeAndGroup(command);
            if (tokens.size() == 0) {
                throw new LambdaException("Empty command");
            }

            if (tokens.get(0).hasText("exit")) {
                exited = true;
                return "";
            }

            if (tokens.size() >=3 && tokens.get(1).hasText("=")) {
                String name = tokens.get(0).getText();
                Expression expression = parseTokenGroup(tokens.subList(2, tokens.size()));
                storeUserExpression(name, expression);
                return "";
            } else if (tokens.size() >=2 && tokens.get(0).hasText("alias")) {
                throw new LambdaException("alias not implemented yet");
            } else {
                Expression expression = parseTokenGroup(tokens);
                return expression.reduce().toString();
            }
        } catch (LambdaException ex) {
          return "Error: " + ex.getMessage();
        }
    }


    private Expression parseToken(Token token) throws SyntaxErrorException {
        if(token.isGroup()) {
            return parseTokenGroup(token.getNestedTokens());
        } else {
            // This is variable or alias.
            Expression aliasedExpr = getExpressionByAlias(token.getText());
            return (aliasedExpr!=null) ? aliasedExpr : new Variable(token);
        }
    }

    private Expression parseTokenGroup(List<Token> tokens) throws SyntaxErrorException {
        int n = tokens.size();
        if(n==1) {
            return parseToken(tokens.get(0));
        }

        if (tokens.get(0).hasText("λ") || tokens.get(0).hasText("lambda")) {
            // This is lambda abstraction.
            List<Token> vars = new ArrayList<Token>();

            int dotIndex=n;
            for(int i=1;i<n;i++) {
                Token token = tokens.get(i);
                if(token.hasText(".")) {
                    dotIndex=i;
                    break;
                } else {
                    vars.add(token);
                }
            }
            if(vars.isEmpty()) {
                throw new SyntaxErrorException("Variable name expected after λ.", tokens.get(0));
            } else if (dotIndex == -1) {
                throw new SyntaxErrorException("Dot expected", tokens.get(n-1));
            } else if (dotIndex == n-1) {
                throw new SyntaxErrorException("Expression expected", tokens.get(n-1));
            }

            Collections.reverse(vars);

            Expression body = parseTokenGroup(tokens.subList(dotIndex+1, n));
            for (Token var : vars) {
                body = new Abstraction(new Variable(var), body);
            }
            return body;
        } else {
            Expression ret = new Application(parseToken(tokens.get(0)), parseToken(tokens.get(1)));
            for(int i=2;i<n;i++) {
                ret = new Application(ret,  parseToken(tokens.get(i)));
            }
            return ret;
        }
    }


    private Expression parseExpression(String expression) throws SyntaxErrorException {
        return parseTokenGroup(Tokenizer.tokenizeAndGroup(expression));
    }

    private void storeLibraryExpression(String name, String expression) throws LambdaException {
        libraryNames.add(name);
        expressions.put(name, parseExpression(expression));
    }

    private void storeUserExpression(String name, Expression expression) throws LambdaException {
        if(libraryNames.contains(name)) {
            throw new LambdaException("Name " + name + "is reserved for library expression.");
        }
        expressions.put(name, expression);
    }

    private Expression getExpressionByAlias(String name) {
        return expressions.get(name);
    }

    private void loadLibrary() throws LambdaException {
        storeLibraryExpression("ID", "λx.x");
        storeLibraryExpression("1", "ID");
        storeLibraryExpression("INC", "λ a b c.b(a b c)");
        storeLibraryExpression("SUM", "λ a b. a INC b");
        storeLibraryExpression("MUL", "λ a b c. a(b c)");
    }


    public boolean isExited() {
        return exited;
    }

}
