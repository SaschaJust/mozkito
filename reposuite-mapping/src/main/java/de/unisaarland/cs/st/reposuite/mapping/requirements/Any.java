package de.unisaarland.cs.st.reposuite.mapping.requirements;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class Any extends Expression {
	
	private final Set<Expression> expressions = new HashSet<Expression>();
	
	public Any(Expression... expressions) {
		CollectionUtils.addAll(this.expressions, expressions);
	}
	
	public Any(Collection<Expression> expressions) {
		this.expressions.addAll(this.expressions);
	}
}
