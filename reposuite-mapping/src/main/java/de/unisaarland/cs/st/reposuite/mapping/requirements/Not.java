package de.unisaarland.cs.st.reposuite.mapping.requirements;

public class Not extends Expression {
	
	private final Expression expression;
	
	public Expression getExpression() {
		return expression;
	}
	
	public Not(Expression expression) {
		this.expression = expression;
	}
}
