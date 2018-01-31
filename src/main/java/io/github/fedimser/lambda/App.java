package io.github.fedimser.lambda;

import io.github.fedimser.lambda.interpreter.LambdaInterpreter;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        LambdaInterpreter interpreter = new LambdaInterpreter();
        Scanner in = new Scanner(System.in);
        while(!interpreter.isExited()){
            System.out.print("> ");
            String input = in.nextLine();
            System.out.println( interpreter.processCommand(input));
        }
    }
}
