/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

/**
 *
 * @author kim
 */
public abstract class JavaElementDefinition extends JavaElement {
	
	private JavaElementDefinition parent = null;
	protected Set<JavaElementDefinition> children = new HashSet<JavaElementDefinition>();
	
	public JavaElementDefinition(@NotNull final String fullQualifiedName, @NotNull final String filePath, @NotNull final DateTime timestamp, @NonNegative final int startLine, @NonNegative final int endLine, final JavaElementDefinition parent) {
		super(fullQualifiedName, filePath, timestamp, startLine, endLine);
		this.parent = parent;
	}
	
	public boolean addChild(final JavaElementDefinition child){
		return children.add(child);
	}
	
	/**
	 * @return the children
	 */
	public Set<JavaElementDefinition> getChildren() {
		return children;
	}
	
	/**
	 * @return the parent
	 */
	public JavaElementDefinition getParent() {
		return parent;
	}

	public boolean setParent(final JavaClassDefinition parent) {
		if (this.parent == null) {
			this.parent = parent;
			return true;
		} else {
			return false;
		}
	}
}
