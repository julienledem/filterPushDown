package filter.expression;

import static filter.expression.Expression.Status.Pushable;

import java.util.Map;

public class ConstantExpression extends Expression {

  private final boolean value;

  public ConstantExpression(boolean value) {
    super(Pushable);
    this.value = value;
  }

  @Override
  public <T> T convert(Converter<T> converter) {
    return converter.convert(this);
  }

  @Override
  public boolean evaluate(Map<String, Boolean> variables) {
    return value;
  }

  @Override
  public String toString() {
    return value ? "T" : "F";
  }

  public boolean isConstant() {
    return true;
  }

  public Expression negate() {
    return new ConstantExpression(!value);
  }

  @Override
  public int hashCode() {
    return value ? 1 : 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ConstantExpression) {
      ConstantExpression e = (ConstantExpression)obj;
      return (value && e.value || !value && !e.value);
    }
    return false;
  }
}
