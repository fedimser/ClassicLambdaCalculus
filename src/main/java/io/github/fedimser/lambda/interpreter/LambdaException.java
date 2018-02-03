package io.github.fedimser.lambda.interpreter;

public class LambdaException extends Exception {
  private static String ensureTrailingPeriod(String x) {
      return x.charAt(x.length()-1)=='.' ? x: x+".";
  }

  public LambdaException(String message) {
      super(ensureTrailingPeriod(message));
  }

}
