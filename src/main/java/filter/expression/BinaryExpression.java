package filter.expression;

import static filter.expression.BinaryExpression.Operator.AND;
import static filter.expression.BinaryExpression.Operator.OR;
import static filter.expression.Expression.Status.Undetermined;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BinaryExpression extends Expression {

  public static enum Operator {
    OR {
      @Override
      public boolean eval(boolean l, boolean r) {
        return l || r;
      }

      @Override
      public Expression neutral() {
        return F;
      }
    }, AND {
      @Override
      public boolean eval(boolean l, boolean r) {
        return l && r;
      }

      @Override
      public Expression neutral() {
        return T;
      }
    };

    abstract public boolean eval(boolean l, boolean r);

    abstract public Expression neutral();
  }

  public final Operator operator;
  public final Expression left;
  public final Expression right;

  public BinaryExpression(Operator operator, Expression left, Expression right) {
    super(Undetermined);
    this.operator = operator;
    this.left = left;
    this.right = right;
  }

  public BinaryExpression(Status status, Operator operator, Expression left, Expression right) {
    super(status);
    this.operator = operator;
    this.left = left;
    this.right = right;
  }

  @Override
  public <T> T convert(Converter<T> converter) {
    return converter.convert(this);
  }

  public List<Expression> toAndList() {
    if (operator == AND) {
      ArrayList<Expression> result = new ArrayList<>();
      result.addAll(left.toAndList());
      result.addAll(right.toAndList());
      return result;
    }
    return super.toAndList();
  }

  public List<Expression> toOrList() {
    if (operator == OR) {
      ArrayList<Expression> result = new ArrayList<>();
      result.addAll(left.toOrList());
      result.addAll(right.toOrList());
      return result;
    }
    return super.toAndList();
  }

  @Override
  public String toString() {
    return "(" + operator.toString().toLowerCase() + " " + left + " " + right + ")";
  }

  @Override
  public boolean evaluate(Map<String, Boolean> variables) {
    return operator.eval(left.evaluate(variables), right.evaluate(variables));
  }

  @Override
  public int hashCode() {
    return operator.hashCode() ^ left.hashCode() ^ right.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof BinaryExpression) {
      BinaryExpression e = (BinaryExpression)obj;
      return e.operator == operator && e.left.equals(left) && e.right.equals(right);
    }
    return false;
  }

}
