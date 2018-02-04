package io.github.fedimser.lambda.temp;

import java.io.ByteArrayOutputStream;

public class ByteTest {
    public static void main( String[] args ) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(48);
        buf.write(49);
        String result = buf.toString();
        System.out.println(result);
    }
}
