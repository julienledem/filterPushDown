package filter;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import filter.expression.Expression;

public class TestParser {
//      formula     := variable | literal | expression
//      variable    := cheap | expensive
//      cheap       := v[0-9]+
//      expensive   := w[0-9]+
//      literal     := "T" | "F"
//      expression  := conjunction | disjunction | negation
//      conjunction := "(and" ws formula ws formula ")"
//      disjunction := "(or" ws formula ws formula ")"
//      negation    := "(not" ws formula ")"
//      ws          := " "+

  Parser parser = new Parser();

  @Test
  public void test() {
    validate("(and (or (or v1 v2) v3) (or (or v1 v2) v4))");
    validate("(and v1 T)");
  }

  @Test
  public void testRandom() {
    for (int i = 0; i < 100; i++) {
      Expression e = TestPusher.randomExpression(6, 20);
      validate(e.toString());
    }
  }

  private void validate(String string) {
    assertEquals(string, parser.parse(string).toString());
  }

}
