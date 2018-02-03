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

    public void testPredeccesor() throws Exception {
        check("PRED 0" ,"0");
        check("PRED 1" ,"0");
        check("PRED 2" ,"1");
        check("PRED 42" ,"41");
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

    public void testSubtraction() throws Exception {
        // Subtraction over na
        check("SUB 0 0" ,"0");
        check("SUB 1 0" ,"1");
        check("SUB 1 1" ,"0");
        check("SUB 2 0" ,"2");
        check("SUB 2 1" ,"1");
        check("SUB 2 2" ,"0");

        check("SUB 10 4" ,"6");
        check("SUB 36 35" ,"1");
        check("SUB 49 47" ,"2");
        check("SUB 98 8" ,"90");
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
        check("MUL 11 9" ,"99");
    }

    public void testSquare() throws Exception {
        check("SQUARE", "2");

        for(int i=0;i<=5;i++) {
            check("SQUARE " + String.valueOf(i), String.valueOf(i*i));
        }
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
        check("POW 5 2", "25");
        check("POW 10 2", "100");
    }

    public void testLogic() throws Exception {
        check("FALSE", "0");

        check("NOT FALSE", "TRUE");
        check("NOT TRUE", "FALSE");

        check("AND FALSE FALSE", "FALSE");
        check("AND FALSE TRUE", "FALSE");
        check("AND TRUE FALSE", "FALSE");
        check("AND TRUE TRUE", "TRUE");

        check("OR FALSE FALSE", "FALSE");
        check("OR FALSE TRUE", "TRUE");
        check("OR TRUE FALSE", "TRUE");
        check("OR TRUE TRUE", "TRUE");
    }

    public void testComparison() throws Exception {
        check("IS_ZERO 0", "TRUE");
        check("IS_ZERO 1", "FALSE");
        check("IS_ZERO 10", "FALSE");

        check("EQUALS 0 0", "TRUE");
        check("EQUALS 1 1", "TRUE");
        check("EQUALS 5 5", "TRUE");
        check("EQUALS 48 48", "TRUE");

        check("EQUALS 1 0", "FALSE");
        check("EQUALS 2 8", "FALSE");
        check("EQUALS 7 3", "FALSE");

        check("GTE 1 0", "TRUE");
        check("GTE 3 2", "TRUE");
        check("GTE 3 3", "TRUE");
        check("GTE 16 10", "TRUE");
        check("GTE 2 3", "FALSE");
        check("GTE 7 19", "FALSE");

        check("LTE 1 0", "FALSE");
        check("LTE 3 2", "FALSE");
        check("LTE 3 3", "TRUE");
        check("LTE 16 10", "FALSE");
        check("LTE 2 3", "TRUE");
        check("LTE 7 19", "TRUE");

        check("GT 0 0", "FALSE");
        check("GT 1 0", "TRUE");
        check("GT 4 3", "TRUE");
        check("GT 4 4", "FALSE");
        check("GT 4 6", "FALSE");

        check("LT 0 0", "FALSE");
        check("LT 0 1", "TRUE");
        check("LT 3 4", "TRUE");
        check("LT 4 4", "FALSE");
        check("LT 6 4", "FALSE");
    }

    public void testPair() throws Exception {
        check("FIRST(PAIR 10 15)", "10");
        check("SECOND(PAIR 10 15)", "15");
    }

    public void testForCycle() throws Exception {
        check("FOR 5 SQUARE SUM", "55");
        check("FOR 11 ID SUM", "66");
    }


    public void testFactorial() throws Exception {
        check("FACTORIAL 0", "1");
        check("FACTORIAL 1", "1");
        check("FACTORIAL 2", "2");
        check("FACTORIAL 3", "6");
        check("FACTORIAL 4", "24");
        check("FACTORIAL 5", "SUM 100 20");
    }
}
