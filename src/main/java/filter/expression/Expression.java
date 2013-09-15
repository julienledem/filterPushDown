package filter.expression;

import static filter.expression.BinaryExpression.Operator.AND;
import static filter.expression.BinaryExpression.Operator.OR;
import static filter.expression.Expression.Status.*;
import static filter.expression.Expression.Status.Undetermined;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import filter.expression.BinaryExpression.Operator;


public abstract class Expression {

  public static enum Status { Pushable, Unpushable, Undetermined}

  final public Status status;

  protected Expression(Status status) {
    super();
    this.status = status;
  }

  public boolean isNegated() {
    return false;
  }

  public Object getId() {
    return this;
  }

  public boolean isConstant() {
    return false;
  }

  abstract public <T> T convert(Converter<T> converter);

  public boolean isPushable() {
    return status == Pushable;
  }

  public List<Expression> toAndList() {
    return Arrays.asList(this);
  }

  public List<Expression> toOrList() {
    return Arrays.asList(this);
  }

  public abstract boolean evaluate(Map<String, Boolean> variables);

  public static Expression toBinaryExpression(Operator op, Collection<? extends Expression> espressions) {
    Expression result = null;
    for (Expression e : espressions) {
      if (result == null) {
        result = e;
      } else {
        result = new BinaryExpression(
            e.isPushable() && result.isPushable() ? Pushable : op == OR ? Unpushable : Undetermined,
            op, result, e
            );
      }
    }
    if (result == null) {
      return op.neutral();
    }
    return result;
  }

  public static Expression toAnd(Collection<? extends Expression> and) {
    return toBinaryExpression(AND, and);
  }

  public static Expression toOr(Collection<? extends Expression> or) {
    return toBinaryExpression(OR, or);
  }


  public static Expression or(String l, String r) {
    return or(e(l), e(r));
  }

  public static Expression or(String l, Expression r) {
    return or(e(l), r);
  }

  public static Expression or(Expression l, String r) {
    return or(l, e(r));
  }

  public static Expression or(Expression l, Expression r) {
    return new BinaryExpression(OR, l, r);
  }

  public static Expression and(String l, String r) {
    return and(e(l), e(r));
  }

  public static Expression and(String l, Expression r) {
    return and(e(l), r);
  }

  public static Expression and(Expression l, String r) {
    return and(l, e(r));
  }

  public static Expression and(Expression l, Expression r) {
    return new BinaryExpression(AND, l, r);
  }

  public static Expression not(Expression e) {
    return new NotExpression(e);
  }

  public static Expression not(String name) {
    return not(e(name));
  }

  public static VariableExpression e(String l) {
    return new VariableExpression(Undetermined, l);
  }

  public static final ConstantExpression T = new ConstantExpression(true);
  public static final ConstantExpression F = new ConstantExpression(false);
}
