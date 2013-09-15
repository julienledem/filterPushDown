package filter;

import static filter.expression.Expression.Status.Pushable;

import java.util.StringTokenizer;

import filter.expression.BinaryExpression;
import filter.expression.Expression;
import filter.expression.BinaryExpression.Operator;
import filter.expression.Expression.Status;
import filter.expression.NotExpression;
import filter.expression.VariableExpression;

public class Parser {
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

  public Expression parse(String s) {
    StringTokenizer st = new StringTokenizer(s, "\"() \t\n", true);
    return parseFormula(st);
  }

  private Expression parseExpressionContent(StringTokenizer st) {
    String t = read(st);
    if (t.equals("or") || t.equals("and")) {
      Expression left = parseFormula(st);
      Expression right = parseFormula(st);
      expect(st, ")");
      return new BinaryExpression(
          t.equals("or") ? Operator.OR : Operator.AND,
          left, right
          );
    } else if (t.equals("not")) {
      Expression operand = parseFormula(st);
      expect(st, ")");
      return new NotExpression(operand);
    }
    throw new RuntimeException("expected or, and or not. Got " + t);
  }

  private Expression parseFormula(StringTokenizer st) {
    String t = read(st);
    if (t.equals("T")) {
      return Expression.T;
    } else if (t.equals("F")) {
      return Expression.F;
    } else if (t.equals("(")) {
      return parseExpressionContent(st);
    } else {
      Status s;
      if (t.startsWith("v")) {
        s = Pushable;
      } else if (t.startsWith("w")) {
        s = Status.Unpushable;
      } else {
        throw new RuntimeException("expected v or w got " + t);
      }
      return new VariableExpression(s, t);
    }
  }

  private void expect(StringTokenizer st, String expected) {
    String t = read(st);
    if (!t.equals(expected)) {
      throw new RuntimeException("expected " + expected + " got " + t);
    }
  }

  private String read(StringTokenizer st) {
    if (st.hasMoreElements()) {
      String t = st.nextToken();
      if (isWS(t)) {
        t = read(st);
      }
      return t;
    } else {
      throw new RuntimeException("EOF");
    }
  }

  private boolean isWS(String t) {
    return t.equals(" ") || t.equals("\t") || t.equals("\n");
  }
}
