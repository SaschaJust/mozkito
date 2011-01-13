package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.List;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

public class JavaClassDefinition extends JavaElementDefinition {
	
	private String superClassName = null;
	private final static String anonCheck = ".*\\$\\d+";
	private int anonCounter = 0;
	private boolean anonymClass = false;
	
	public JavaClassDefinition(@NotNull final String fullQualifiedName, @NotNull final String file,
			@NotNull final DateTime timestamp, final JavaClassDefinition parent, @NonNegative final int startLine, @NonNegative final int endLine) {
		
		super(fullQualifiedName, file, timestamp, startLine, endLine, parent);
		
		if (parent != null) {
			Condition.check(parent instanceof JavaClassDefinition, "The parent of a class Definition has to be another class definition");
			parent.addChild(this);
		}
		
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			anonymClass = true;
		}
	}
	
	public JavaMethodDefinition addMethod(final String methodName, final List<String> arguments, final DateTime timestamp, final int startLine, final int endLine) {
		JavaMethodDefinition mDef = new JavaMethodDefinition(this.getFullQualifiedName() + "." + methodName, arguments, this.getFilePath(), timestamp, this, startLine, endLine);
		if (children.add(mDef)) {
			return mDef;
		} else {
			return null;
		}
	}
	
	@Override
	public JavaClassDefinition getParent() {
		return (JavaClassDefinition) super.getParent();
	}
	
	public String getSuperclass() {
		return superClassName;
	}
	
	public int nextAnonCounter() {
		if (anonymClass) {
			return this.getParent().nextAnonCounter();
		} else {
			return ++anonCounter;
		}
	}
	
	public void setSuperclass(final String superClassName) {
		this.superClassName = superClassName;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("JavaClassDefinition [superClassName=");
		sb.append(superClassName);
		sb.append(", anonymClass=");
		sb.append(anonymClass);
		sb.append(", childrenSize=");
		sb.append(children.size());
		
		sb.append(", getFullQualifiedName()=");
		sb.append(getFullQualifiedName());
		sb.append(", getShortName()=");
		sb.append(getShortName());
		sb.append(", getFilePath()=");
		sb.append(getFilePath());
		sb.append(", getTimestamp()=");
		sb.append(getTimestamp());
		sb.append(", getStartLine()=");
		sb.append(getStartLine());
		sb.append(", getEndLine()=");
		sb.append(getEndLine());
		sb.append(", parent=");
		if (getParent() != null) {
			sb.append(getParent().getFullQualifiedName());
		} else {
			sb.append("null");
		}
		sb.append(", children=[");
		for (JavaElement elem : children) {
			sb.append(elem.toString());
			sb.append(",");
		}
		sb.append("]]");
		return sb.toString();
	}
}
