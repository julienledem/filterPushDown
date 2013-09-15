package filter.expression;


public interface Converter<T> {

  T convert(BinaryExpression binaryExpression);

  T convert(NotExpression notExpression);

  T convert(VariableExpression variableExression);

  T convert(ConstantExpression constantExpression);

}
