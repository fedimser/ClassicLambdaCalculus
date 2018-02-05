package io.github.fedimser.lambda.interpreter;

import junit.framework.TestCase;

public class LibraryTest extends TestCase {
    private LambdaInterpreter li = new LambdaInterpreter();

    // Checks that both expression are reduced to the same expression.
    private void check(String exp1, String exp2) throws Exception {
        assertTrue(li.parseReduce(exp1).equals(li.parseReduce(exp2)));
    }

    private void checkInt(String exp, int value) throws Exception {
        assertEquals(value, li.parseReduce(exp).asNatural());
    }

    private void checkBool(String exp, boolean value) throws Exception {
        assertEquals(value, li.parseReduce(exp).asBoolean());
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
        check("SUB 6 5" ,"1");
        check("SUB 9 7" ,"2");
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

    public void testDivision() throws Exception {
        //check("IDIV 4 2" ,"2");
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
        checkInt("POW 5 2", 25);
        checkInt("POW 10 2", 100);
    }

    public void testLogic() throws Exception {
        checkBool("FALSE", false);
        checkBool("TRUE", true);

        checkBool("NOT FALSE", true);
        checkBool("NOT TRUE", false);

        checkBool("AND FALSE FALSE", false);
        checkBool("AND FALSE TRUE", false);
        checkBool("AND TRUE FALSE", false);
        checkBool("AND TRUE TRUE", true);

        checkBool("OR FALSE FALSE", false);
        checkBool("OR FALSE TRUE", true);
        checkBool("OR TRUE FALSE", true);
        checkBool("OR TRUE TRUE", true);
    }

    public void testComparison() throws Exception {
        checkBool("ISZERO 0", true);
        checkBool("ISZERO 1", false);
        checkBool("ISZERO 10", false);

        checkBool("EQUALS 0 0", true);
        checkBool("EQUALS 1 1", true);
        checkBool("EQUALS 5 5", true);

        checkBool("EQUALS 1 0", false);
        checkBool("EQUALS 2 8", false);
        checkBool("EQUALS 7 3", false);

        checkBool("GTE 1 0", true);
        checkBool("GTE 3 2", true);
        checkBool("GTE 3 3", true);
        checkBool("GTE 16 10", true);
        checkBool("GTE 2 3", false);
        checkBool("GTE 7 19", false);

        checkBool("LTE 1 0", false);
        checkBool("LTE 3 2", false);
        checkBool("LTE 3 3", true);
        checkBool("LTE 16 10", false);
        checkBool("LTE 2 3", true);
        checkBool("LTE 7 19", true);

        checkBool("GT 0 0", false);
        checkBool("GT 1 0", true);
        checkBool("GT 4 3", true);
        checkBool("GT 4 4", false);
        checkBool("GT 4 6", false);

        checkBool("LT 0 0", false);
        checkBool("LT 0 1", true);
        checkBool("LT 3 4", true);
        checkBool("LT 4 4", false);
        checkBool("LT 6 4", false);
    }

    public void testMinMax() throws Exception {
        checkInt("MIN 0 3", 0);
        checkInt("MIN 7 2", 2);
        checkInt("MIN 6 8", 6);
        checkInt("MIN 4 4", 4);

        checkInt("MAX 0 3", 3);
        checkInt("MAX 7 2", 7);
        checkInt("MAX 6 8", 8);
        checkInt("MAX 4 4", 4);
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
        checkInt("FACTORIAL 0", 1);
        checkInt("FACTORIAL 1", 1);
        checkInt("FACTORIAL 2", 2);
        checkInt("FACTORIAL 3", 6);
        checkInt("FACTORIAL 4", 24);
        checkInt("FACTORIAL 5", 120);
    }
}
