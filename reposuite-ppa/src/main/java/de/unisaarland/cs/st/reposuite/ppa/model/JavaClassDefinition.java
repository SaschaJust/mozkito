package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

/**
 * The Class JavaClassDefinition.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaClassDefinition extends JavaElementDefinition implements Annotated {
	
	/**
	 * 
	 */
	private static final long   serialVersionUID = 945704236316941413L;
	
	/** The super class name. */
	private String superClassName = null;
	
	/** The Constant anonCheck. */
	private final static String anonCheck      = ".*\\$\\d+";
	
	
	/** The anon counter. */
	@Transient
	private int          anonCounter = 0;
	
	/** The anonym class. */
	private boolean      anonymClass = false;
	
	
	/**
	 * Instantiates a new java class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param file
	 *            the file
	 * @param timestamp
	 *            the timestamp
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param packageName
	 *            the package name
	 */
	protected JavaClassDefinition(@NotNull final String fullQualifiedName, @NotNull final String file,
			@NotNull final DateTime timestamp, final JavaClassDefinition parent, @NonNegative final int startLine,
			@NonNegative final int endLine, @NotNull final String packageName) {
		
		super(fullQualifiedName, file, timestamp, startLine, endLine, parent);
		if (parent != null) {
			Condition.check(parent instanceof JavaClassDefinition,
			"The parent of a class Definition has to be another class definition");
			parent.addChild(this);
		}
		
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			anonymClass = true;
		}
	}
	
	/**
	 * Adds the method.
	 * 
	 * @param methodName
	 *            the method name
	 * @param arguments
	 *            the arguments
	 * @param timestamp
	 *            the timestamp
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @return the java method definition
	 */
	public JavaMethodDefinition addMethod(final String methodName, final List<String> arguments,
			final DateTime timestamp, final int startLine, final int endLine) {
		
		return JavaElementDefinitionCache.getMethodDefinition(this.getFullQualifiedName() + "." + methodName,
				arguments, this.getFilePath(), timestamp, this, startLine, endLine);
	}
	
	/**
	 * Gets the super class name.
	 * 
	 * @return the super class name
	 */
	@SuppressWarnings("unused")
	private String getSuperClassName() {
		return superClassName;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition#getParent
	 * ()
	 */
	@Override
	@Transient
	public JavaClassDefinition getTypedParent() {
		return (JavaClassDefinition) super.getParent();
	}
	
	/**
	 * Checks if is anonym class.
	 * 
	 * @return true, if is anonym class
	 */
	@SuppressWarnings("unused")
	private boolean isAnonymClass() {
		return anonymClass;
	}
	
	/**
	 * Next anon counter.
	 * 
	 * @return the int
	 */
	@Transient
	public int nextAnonCounter() {
		if (anonymClass) {
			return this.getTypedParent().nextAnonCounter();
		} else {
			return ++anonCounter;
		}
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Sets the anonym class.
	 * 
	 * @param anonymClass
	 *            the new anonym class
	 */
	@SuppressWarnings("unused")
	private void setAnonymClass(final boolean anonymClass) {
		this.anonymClass = anonymClass;
	}
	
	/**
	 * Sets the super class name.
	 * 
	 * @param superClassName
	 *            the new super class name
	 */
	public void setSuperClassName(final String superClassName) {
		this.superClassName = superClassName;
	}
	
	/**
	 * To long string.
	 * 
	 * @return the string
	 */
	public String toLongString() {
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
		for (JavaElement elem : super.getChildren()) {
			sb.append(elem.toString());
			sb.append(",");
		}
		sb.append("]]");
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#toString()
	 */
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
		sb.append("]");
		return sb.toString();
	}
}
