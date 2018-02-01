package io.github.fedimser.lambda.interpreter;

import io.github.fedimser.lambda.calculus.LbdAbstraction;
import io.github.fedimser.lambda.calculus.LbdApplication;
import io.github.fedimser.lambda.calculus.LbdExpression;
import io.github.fedimser.lambda.calculus.LbdVariable;

import java.util.*;

/**
 * Interpreter for classic lambda calculus.
 */
public class LambdaInterpreter {
    private static final String[] reservedWords = {"exit", "alias"};
    private Map<String, LbdExpression> expressions = new HashMap<String, LbdExpression>();
    private Map<LbdExpression, List<String> > inverseIndex = new HashMap<LbdExpression, List<String> >();
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
                LbdExpression expression = parseTokenGroup(tokens.subList(2, tokens.size())).reduce();
                storeExpression(name, expression);
                return "";
            } else if (tokens.size() >=2 && tokens.get(0).hasText("alias")) {
                LbdExpression expression = parseTokenGroup(tokens.subList(1, tokens.size())).reduce();
                List<String> names = inverseIndex.get(expression);
                if(names == null) {
                    return "None.";
                } else {
                    return String.join(", ", names);
                }
            } else {
                LbdExpression expression = parseTokenGroup(tokens).reduce();
                return expression.getClassicFormula();
            }
        } catch (LambdaException ex) {
          return "Error: " + ex.getMessage();
        }
    }

    public LbdExpression evaluate(String expression) throws LambdaException {
        return parseTokenGroup(Tokenizer.tokenizeAndGroup(expression)).reduce();
    }


    private LbdExpression parseToken(Token token, VariableStack vStack) throws SyntaxErrorException {
        if(token.isGroup()) {
            return parseTokenGroup(token.getNestedTokens(), vStack);
        } else {
            // This is variable or alias.
            LbdExpression aliasedExpr = getExpressionByAlias(token.getText());
            return (aliasedExpr!=null) ? aliasedExpr : LbdVariable.forIndex(vStack.getDeBruijnIndex(token));
        }
    }


    private LbdExpression parseTokenGroup(List<Token> tokens, VariableStack vStack) throws SyntaxErrorException {
        int n = tokens.size();
        if(n==1) {
            return parseToken(tokens.get(0), vStack);
        }

        if (tokens.get(0).hasText("λ") || tokens.get(0).hasText("lambda")) {
            // This is lambda abstraction.
            int varCount=0;
            int dotIndex=-1;
            for(int i=1;i<n;i++) {
                Token token = tokens.get(i);
                if(token.hasText(".")) {
                    dotIndex=i;
                    break;
                } else {
                    vStack.push(token);
                    varCount++;
                }
            }
            if(varCount==0) {
                throw new SyntaxErrorException("Variable name expected after λ.", tokens.get(0));
            } else if (dotIndex == -1) {
                throw new SyntaxErrorException("Dot expected", tokens.get(n-1));
            } else if (dotIndex == n-1) {
                throw new SyntaxErrorException("Expression expected", tokens.get(n-1));
            }


            LbdExpression body = parseTokenGroup(tokens.subList(dotIndex+1, n), vStack);
            for (int i=0;i<varCount;i++) {
                body = new LbdAbstraction(body);
                vStack.pop();
            }
            return body;
        } else {
            LbdExpression function = parseToken(tokens.get(0), vStack);
            LbdExpression argument = parseToken(tokens.get(1), vStack);
            LbdExpression ret = new LbdApplication(function, argument);
            for(int i=2;i<n;i++) {
                ret = new LbdApplication(ret,  parseToken(tokens.get(i), vStack));
            }
            return ret;
        }
    }

    private LbdExpression parseTokenGroup(List<Token> tokens) throws SyntaxErrorException {
        return parseTokenGroup(tokens, new VariableStack());
    }



    private void storeLibraryExpression(String name, String expression) throws LambdaException {
        storeExpression(name, evaluate(expression));
        libraryNames.add(name);
    }

    private void storeExpression(String name, LbdExpression expression) throws LambdaException {
        if(libraryNames.contains(name)) {
            throw new LambdaException("Name " + name + "is reserved for library expression.");
        }
        expressions.put(name, expression);
        if(inverseIndex.containsKey(expression)) {
            inverseIndex.get(expression).add(name);
        } else {
            List<String> newList = new ArrayList<String>();
            newList.add(name);
            inverseIndex.put(expression, newList);
        }
    }

    private LbdExpression getExpressionByAlias(String name) {
        return expressions.get(name);
    }

    private void loadLibrary() throws LambdaException {
        storeLibraryExpression("ID", "λx.x");
        storeLibraryExpression("0", "λ a b. b");
        storeLibraryExpression("1", "ID");
        storeLibraryExpression("INC", "λ a b c.b(a b c)");
        storeLibraryExpression("SUM", "λ a b. a INC b");
        storeLibraryExpression("MUL", "λ a b c. a(b c)");
        storeLibraryExpression("POW", "λ a b. (b (λ x . MUL a x) ) 1");
        storeLibraryExpression("FALSE", "0");
        storeLibraryExpression("TRUE", "λ a b. a");

        for(int i=2;i<=100;i++){
            storeLibraryExpression(String.valueOf(i), "INC " + String.valueOf(i-1));
        }

    }


    public boolean isExited() {
        return exited;
    }

}
