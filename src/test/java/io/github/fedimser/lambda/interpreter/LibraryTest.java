package io.github.fedimser.lambda.interpreter;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.beans.Expression;

public class LibraryTest extends TestCase {
    private LambdaInterpreter li = new LambdaInterpreter();

    // Checks that both expression are reduced to the same expression.
    private void check(String exp1, String exp2) throws Exception {
        assertTrue(li.evaluate(exp1).equals(li.evaluate(exp2)));
    }

    public void testIncrement() throws Exception {
        check("INC 0" ,"1");
        check("INC 1" ,"2");
        check("INC 10" ,"11");
        check("INC 42" ,"43");
    }

    public void testIdentity() throws Exception {
        check("ID" ,"1");
        check("ID 0", "0");
        check("ID 17", "17");
        check("ID ID", "ID");
    }

    public void testSum() throws Exception {
        check("SUM 0 0" ,"0");
        check("SUM 0 1" ,"1");
        check("SUM 1 0" ,"1");
        check("SUM 1 1" ,"2");
        check("SUM 2 1" ,"3");
        check("SUM 1 2" ,"3");
        check("SUM 2 2" ,"4");
        check("SUM 2 3" ,"5");
        check("SUM 2 10" ,"12");
        check("SUM 30 40" ,"70");
    }

    public void testMultiplication() throws Exception {
        check("MUL 0 0" ,"0");
        check("MUL 0 1" ,"0");
        check("MUL 1 0" ,"0");
        check("MUL 50 0" ,"0");
        check("MUL 1 1" ,"1");
        check("MUL 1 2" ,"2");
        check("MUL 1 13" ,"13");
        check("MUL 2 1" ,"2");
        check("MUL 2 2" ,"4");
        check("MUL 2 3" ,"6");
        check("MUL 3 4" ,"12");
        check("MUL 4 3" ,"12");
        check("MUL 4 5" ,"20");
        check("MUL 5 4" ,"20");
        check("MUL 5 7" ,"35");
    }

    public void testPower() throws Exception {
        check("POW 0 0", "1");
        check("POW 1 0", "1");
        check("POW 5 0", "1");
        check("POW 0 1", "0");
        check("POW 0 5", "0");


        check("POW 1 1", "1");
        check("POW 2 1", "2");
        check("POW 17 1", "17");

        check("POW 2 2", "4");
        check("POW 2 3", "8");
        check("POW 2 4", "16");
        check("POW 2 5", "32");
        check("POW 2 6", "64");
        check("POW 3 2", "9");
        check("POW 3 3", "27");
        check("POW 3 4", "81");
        check("POW 10 2", "100");

    }


}
