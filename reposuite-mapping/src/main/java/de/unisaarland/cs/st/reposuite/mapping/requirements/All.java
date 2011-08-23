package de.unisaarland.cs.st.reposuite.mapping.requirements;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class All extends Expression {
	
	private final Set<Expression> expressions = new HashSet<Expression>();
	
	public All(Expression... expressions) {
		CollectionUtils.addAll(this.expressions, expressions);
	}
	
	public All(Collection<Expression> expressions) {
		this.expressions.addAll(this.expressions);
	}
}
