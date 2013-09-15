package filter.expression;

import static filter.expression.Expression.Status.Undetermined;

import java.util.Map;

public class NotExpression extends Expression {

  public final Expression operand;

  public NotExpression(Expression operand) {
    super(Undetermined);
    this.operand = operand;
  }

  public NotExpression(Status status, Expression operand) {
    super(status);
    this.operand = operand;
  }

  public boolean isNegated() {
    return true;
  }

  public Object getId() {
    return operand.getId();
  }

  @Override
  public <T> T convert(Converter<T> converter) {
    return converter.convert(this);
  }

  @Override
  public String toString() {
    return
        "(not " + operand + ")";
  }

  @Override
  public boolean evaluate(Map<String, Boolean> variables) {
    return !operand.evaluate(variables);
  }

  @Override
  public int hashCode() {
    return ~ operand.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NotExpression) {
      NotExpression e = (NotExpression)obj;
      return (e.operand.equals(operand));
    }
    return false;
  }
}
