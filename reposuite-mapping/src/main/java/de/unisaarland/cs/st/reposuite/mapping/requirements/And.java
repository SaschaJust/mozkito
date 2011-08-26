package de.unisaarland.cs.st.reposuite.mapping.requirements;

/**
 * @author just
 * 
 */
public class And extends Expression {
	
	private final Object     e1;
	private final Expression e2;
	
	public And(Expression e1, Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	
	public Object getE1() {
		return e1;
	}
	
	public Expression getE2() {
		return e2;
	}
}
