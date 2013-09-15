package filter;

import static filter.expression.BinaryExpression.Operator.AND;
import static filter.expression.BinaryExpression.Operator.OR;
import static filter.expression.Expression.F;
import static filter.expression.Expression.T;
import static filter.expression.Expression.and;
import static filter.expression.Expression.not;
import static filter.expression.Expression.or;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import filter.expression.BinaryExpression;
import filter.expression.ConstantExpression;
import filter.expression.Converter;
import filter.expression.Expression;
import filter.expression.Expression.Status;
import filter.expression.NotExpression;
import filter.expression.VariableExpression;

public class TestPusher {

  Pusher pusher = new Pusher();

  @Test
  public void testPusher() {
    test(not(T));
    test(and("v1", "w2"));
    test(and(or("v1", "v2"), or("v1", "v3")));
    test(and(or(or("v1", "v2"), "v3"), or(or("v1", "v2"), "v4")));
    test(or("v1", and("v2", "w1")));
    test(or("v1", not(or(not("v2"), "w1"))));
    test(not(or("v1",or("v1", not(or(not("v2"), "w1"))))));
    test(or(and("v1", "v2"), not(or(not("v2"), "w1"))));
    test(or(and("v1", "b"), or(not("v2"), "w1")));
    test(not(or("v1", or("v1", not(or(not("v2"), T))))));
    test(not(or("v1", or("v1", not(or(not("v2"), F))))));
  }

  @Test
  public void testRandom() {
    int variableCount = 20;
    int expressionMaxDepth = 6;
    for (int j = 0; j < 100; j++) {
      Expression e = randomExpression(expressionMaxDepth, variableCount);
      test(false, e);
    }
  }

  static Expression randomExpression(int expressionMaxDepth, int variableCount) {
    if (expressionMaxDepth == 0) {
      if (flip() && flip()) {
        return new ConstantExpression(flip());
      } else {
        final int i = (int)(Math.random() * variableCount);
        return new VariableExpression(
            Status.Undetermined,
            (i > (variableCount * 3 / 4) ? "w" : "v") + i);
      }
    } else if (flip() || flip()) {
      return new BinaryExpression(
          flip() ? OR : AND,
              randomExpression(expressionMaxDepth - 1, variableCount),
              randomExpression(expressionMaxDepth - 1, variableCount));
    } else {
      return new NotExpression(
              randomExpression(expressionMaxDepth - 1, variableCount));
    }
  }

  static boolean flip() {
    return Math.random() < 0.5;
  }

  private void test(Expression e) {
    test(true, e);
  }

  private void test(boolean exact, Expression e) {
    List<String> variables = e.convert(new Converter<List<String>>() {

      @Override
      public List<String> convert(VariableExpression e) {
        return Arrays.asList(e.getName());
      }

      @Override
      public List<String> convert(NotExpression e) {
        return e.operand.convert(this);
      }

      @Override
      public List<String> convert(BinaryExpression binaryExpression) {
        List<String> merged = new ArrayList<>();
        merged.addAll(binaryExpression.left.convert(this));
        merged.addAll(binaryExpression.right.convert(this));
        return merged;
      }

      @Override
      public List<String> convert(ConstantExpression constantExpression) {
        return new ArrayList<>(0);
      }
    });
    System.out.println("original:t " + e);
    Expression converted = pusher.canonicalConjunction(e);
    System.out.println("converted: " + converted);
    Expression pushed = pusher.pushDownCanonical(converted);
    System.out.println("pushed: " + pushed);

    int combinations = (int)Math.pow(2, variables.size());
    Map<String, Boolean> values = new HashMap<>();
    int filteredPushed = 0;
    int filtered = 0;
    int valueCount = exact ? combinations : 10000;
    String message = "\nexp: " + e + "\nconv: " + converted + "\nvals: ";
    for (int i = 0; i < valueCount; i++) {
      int value = exact ? i : (int)(Math.random() * combinations);
      for (int j = 0; j < variables.size(); j++) {
        values.put(variables.get(j), ((value >> j) & 1) == 1);
      }
      boolean orig = e.evaluate(values);
      boolean conv = converted.evaluate(values);
      if ((orig && conv) || (!orig && !conv)) {
        assertEquals( message + values.toString(), orig, conv);
      }
      if (pushed != null) {
        assertEquals(orig, pushed.evaluate(values) && conv);
        if (!pushed.evaluate(values)) {
          ++ filteredPushed;
        }
      }
      if (!orig) {
        ++ filtered;
      }
    }
    System.out.println("pushed filter: "
        + filteredPushed + " / " + filtered
        +" of " + valueCount + " records");
    System.out.println();
  }

}