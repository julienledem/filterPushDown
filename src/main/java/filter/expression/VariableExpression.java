package filter.expression;

import java.util.Map;


public class VariableExpression extends Expression {

  private final String name;

  public VariableExpression(Status status, String name) {
    super(status);
    this.name = name;
  }

  @Override
  public <T> T convert(Converter<T> converter) {
    return converter.convert(this);
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return
        //"[" + status + "]" +
        name;
  }

  @Override
  public boolean evaluate(Map<String, Boolean> variables) {
    return variables.get(name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof VariableExpression) {
      VariableExpression e = (VariableExpression)obj;
      return (e.name.equals(name));
    }
    return false;
  }
}
