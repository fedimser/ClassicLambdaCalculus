package io.github.fedimser.lambda.interpreter;

import io.github.fedimser.lambda.calculus.*;
import io.github.fedimser.lambda.calculus.LbdExpression.FormulaStyle;

import java.beans.Expression;
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

    /**
     * Reads user command, executes it aand return answer.
     * Supported commands:
     *  - exit
     *    Exits interpreter.
     *  - [Expression]
     *    Evaluates expression, prints result of evaluation in CLASSIC style.
     *  - [Var] = [Expression]
     *    Evaluates expression, stores it to variable [Var], prints "OK.".
     *  - alias [Expression]
     *    Prints all known aliases for given expression.
     *    If there are more than one, prints them sorted alphabetically
     *      and separated with comma and space.
     *    If there are none, prints "None."
     *  - short [Expression]
     *    Evaluates expression, prints result of evaluation in SHORT style.
     *    If it's impossible (namely, if depth is more than 26), prints
     *      "Error: Too deep for short style."
     *  - db [Expression]
     *    Evaluates expression, prints result of evaluation in DE_BRUIJN style.
     *  - db_short [Expression]
     *    Evaluates expression, prints result of evaluation in DE_BRUIJN_SHORT style.
     *    If it's impossible (namely, if depth is more than 26), prints
     *      "Error: Too deep for short style."
     *   - int [Expression]
     *     Evaluates expression and prints corresponding Church natural.
     *     If it is not Church natural, prints ("Error: Expression is not Church natural.").
     *   - bool [Expression]
     *     Evaluates expression and prints corresponding Church boolean.
     *     If it is not Church boolean, prints ("Error: Expression is not Church boolean.").
     * This is command-line interface of interpreter.
     * @param command User command.
     * @return Answer to be printed to console.
     */
    public String processCommand(String command) {
        assert (!exited);
        RecursionControlStack.INSTANCE.reset();

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
                LbdExpression expression = parseTokenGroup(tokens.subList(2, tokens.size())).safeReduce();
                storeExpression(name, expression);
                return "OK.";
            } else if (tokens.size() >=2 && tokens.get(0).hasText("alias")) {
                LbdExpression expression = parseTokenGroup(tokens.subList(1, tokens.size())).safeReduce();
                List<String> names = inverseIndex.get(expression);
                if(names == null) {
                    return "None.";
                } else {
                    names.sort(String::compareTo);
                    return String.join(", ", names);
                }
            } else if (tokens.size() >=2 && tokens.get(0).hasText("short")) {
                LbdExpression expression = parseTokenGroup(tokens.subList(1, tokens.size())).safeReduce();
                return expression.getFormula(FormulaStyle.SHORT);
            } else if (tokens.size() >=2 && tokens.get(0).hasText("db")) {
                LbdExpression expression = parseTokenGroup(tokens.subList(1, tokens.size())).safeReduce();
                return expression.getFormula(FormulaStyle.DE_BRUIJN);
            } else if (tokens.size() >=2 && tokens.get(0).hasText("db_short")) {
                LbdExpression expression = parseTokenGroup(tokens.subList(1, tokens.size())).safeReduce();
                return expression.getFormula(FormulaStyle.SHORT_DE_BRUIJN);
            } else if (tokens.size() >=2 && tokens.get(0).hasText("int")) {
                LbdExpression expression = parseTokenGroup(tokens.subList(1, tokens.size())).safeReduce();
                return String.valueOf(expression.asNatural());
            } else if (tokens.size() >=2 && tokens.get(0).hasText("bool")) {
                LbdExpression expression = parseTokenGroup(tokens.subList(1, tokens.size())).safeReduce();
                return String.valueOf(expression.asBoolean());
            } else if (tokens.size() >=2 && tokens.get(0).hasText("nored")) {
                LbdExpression expression = parseTokenGroup(tokens.subList(1, tokens.size()));
                return expression.getFormula(FormulaStyle.SHORT);
            } else {
                LbdExpression expression = parseTokenGroup(tokens).safeReduce();
                return expression.getFormula(FormulaStyle.CLASSIC);
            }
        } catch (LambdaException ex) {
          return "Error: " + ex.getMessage();
        }
    }

    /**
     * Parses and safeReduces given string to lambda expression in normal form.
     * @param formula Expression to parseReduce as string.
     * @return parsed and reduced expression.
     * @throws LambdaException When given expression is not syntactically correct or can't be evaluated.
     */
    public LbdExpression parseReduce(String formula) throws LambdaException {
        return parse(formula).safeReduce();
    }


    /**
     * Parses given formula.
     * @param formula Formula
     * @return Parsed expression.
     * @throws SyntaxErrorException If formula is not syntactically correct.
     */
    public LbdExpression parse(String formula) throws SyntaxErrorException {
        return parseTokenGroup(Tokenizer.tokenizeAndGroup(formula));
    }

    private LbdExpression parseToken(Token token, VariableStack vStack) throws SyntaxErrorException {
        if(token.isGroup()) {
            return parseTokenGroup(token.getNestedTokens(), vStack);
        } else {
            // This is variable or alias.
            LbdExpression aliasedExpr = getExpressionByAlias(token.getText());
            if(aliasedExpr!=null){
                return aliasedExpr;
            } else {
                try {
                    return LbdVariable.forIndex(vStack.getDeBruijnIndex(token));
                } catch (DepthException e) {
                    throw new SyntaxErrorException("Too deep", token);
                }
            }
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
                throw new SyntaxErrorException("Variable name expected after λ", tokens.get(0));
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



    private void storeLibraryExpression(String name, String expression, boolean fast) throws LambdaException {
        try {
            LbdExpression exp = parse(expression);
            if(!fast) {
                exp = exp.safeReduce();
            }
            storeExpression(name, exp);
        } catch (LambdaException e) {
            System.out.println("Couldn't parseReduce: " + expression);
            throw e;
        }
        libraryNames.add(name);
    }

    private void storeLibraryExpression(String name, String expression) throws LambdaException {
        storeLibraryExpression(name, expression, false);
    }

    private void storeExpression(String name, LbdExpression expression) throws LambdaException {
        if(libraryNames.contains(name)) {
            throw new LambdaException("Name " + name + " is reserved for library expression.");
        }

        if (expressions.containsKey(name)) {
            inverseIndex.get(expressions.get(name)).remove(name);
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
        // Identity function.
        storeLibraryExpression("ID", "λx.x");

        // Basic arithmetic.
        storeLibraryExpression("INC", "λ a b c.b(a b c)");
        storeLibraryExpression("SUCC", "INC");
        storeLibraryExpression("SUM", "λ a b. a INC b");
        storeLibraryExpression("PLUS", "SUM");
        storeLibraryExpression("ADD", "SUM");
        storeLibraryExpression("MUL", "λ a b c. a(b c)");
        storeLibraryExpression("POW", "λ a b. b a");
        storeLibraryExpression("SQUARE", "λ x . MUL x x");

        // Church's natural numbers.
        storeLibraryExpression("0", "λ a b. b");
        for(int i=1;i<=100;i++){
            storeLibraryExpression(String.valueOf(i), "INC " + String.valueOf(i-1));
        }

        // Boolean logic.
        storeLibraryExpression("FALSE", "λ a b. b");
        storeLibraryExpression("TRUE", "λ a b. a");
        storeLibraryExpression("AND", "λ x y. x y FALSE");
        storeLibraryExpression("OR", "λ x y. x TRUE y");
        storeLibraryExpression("NOT", "λx.x FALSE TRUE");


        // Predecessor and subtraction.
        storeLibraryExpression("Φ", "λ p z.z(INC(p TRUE))(p TRUE)");
        storeLibraryExpression("PRED", "λn.n Φ(λ z.z 0 0)FALSE");
        storeLibraryExpression("SUB", "λ a b. b PRED a");
        storeLibraryExpression("MINUS", "SUB");


        // Equality and inequalities.
        storeLibraryExpression("ISZERO", "λ x. x FALSE NOT FALSE");
        storeLibraryExpression("GTE", "λ x y. ISZERO(x PRED y)");  // Greater than or equal.
        storeLibraryExpression("LTE", "λ x y. GTE y x");  // Less than or equal.
        storeLibraryExpression("GT", "λ x y. GTE x (INC y)");  // Greater than.
        storeLibraryExpression("LT", "λ x y. GTE y (INC x)");  // Less than.
        storeLibraryExpression("EQUALS", "λ x y. AND (GTE x y) (GTE y x)");

        // Minimum and maximum.
        storeLibraryExpression("MIN", "λa b.(GTE a b) b a");
        storeLibraryExpression("MAX", "λa b.(GTE a b) a b");

        // Combinators.
        storeLibraryExpression("Y", "λg.(λx.g(x x))(λx.g(x x))");
        storeLibraryExpression("R", "(λr n.ISZERO n 0(n SUCC(r(PRED n))))");

        // Conditional helpers.
        storeLibraryExpression("INC_IF", "λx c. c (INC x) x");

        // Pairs.
        storeLibraryExpression("PAIR", "λa b f.f a b");
        storeLibraryExpression("FIRST", "λp.p(λa b.a)");
        storeLibraryExpression("SECOND", "λp.p(λa b.b)");
        storeLibraryExpression("CAR", "FIRST");
        storeLibraryExpression("CDR", "SECOND");
        storeLibraryExpression("NIL", "λx.TRUE");

        // Maps range of integers [0 .. n] with f and reduces with c.
        // FOR n f c == c(c(...c(c(f(0), f(1)),f(2))..),f(n)).
        // c is "collector" function. It's recommended to be symmetric and associative.
        // '$NP' is helper function to construct cycle.
        storeLibraryExpression("$NP", "λf c p.PAIR (c(FIRST p)(f(SECOND p))) (INC (SECOND p))");
        storeLibraryExpression("FOR", "λn f c.FIRST( (n (λ p.$NP f c p)) (PAIR(f 0) 1))");

        // Functions that use cycles.
        storeLibraryExpression("FACTORIAL", "λn. FOR (PRED n) INC MUL");

        // Integer division can be expressed using for loop:
        // def div(a, b):
        //   x, y = b, 0
        //   for _ in range(a+1):
        //     x, y = x + b, (y+1 if x<=a else y)
        //   return y
        // This is extremely inefficient, but works.
        String divLoopBody =
                "λ xy.(PAIR (ADD (FIRST xy) b) (INC_IF (SECOND xy) (GTE a (FIRST xy))))";
        storeLibraryExpression("DIV", "λ a b. SECOND((a (" + divLoopBody + ")) (PAIR b 0) )");

        // Remainder.
        // a % b = a - (a / b) * b.
        storeLibraryExpression("MOD", "λ a b. SUB a (MUL (DIV a b) b)");
    }


    public boolean isExited() {
        return exited;
    }

}
