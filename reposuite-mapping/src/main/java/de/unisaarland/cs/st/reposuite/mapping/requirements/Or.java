package de.unisaarland.cs.st.reposuite.mapping.requirements;

public class Or extends Expression {
	
	private final Expression e1;
	
	public Expression getE1() {
		return e1;
	}
	
	public Expression getE2() {
		return e2;
	}
	
	private final Expression e2;
	
	public Or(Expression e1, Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
}
