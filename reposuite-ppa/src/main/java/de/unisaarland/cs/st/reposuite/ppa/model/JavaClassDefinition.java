package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.reposuite.utils.Condition;
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
	private String              superClassName   = null;
	
	/** The Constant anonCheck. */
	private final static String anonCheck        = ".*\\$\\d+";
	
	/** The anon counter. */
	@Transient
	private final HashMap<Integer, Integer> anonCounters     = new HashMap<Integer, Integer>();
	
	/** The anonym class. */
	private boolean             anonymClass      = false;
	
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
	protected JavaClassDefinition(@NotNull final String fullQualifiedName, final JavaClassDefinition parent,
			@NotNull final String packageName) {
		
		super(fullQualifiedName, parent);
		if (parent != null) {
			Condition.check(parent instanceof JavaClassDefinition,
			"The parent of a class Definition has to be another class definition");
			parent.addChild(this);
		}
		
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			this.anonymClass = true;
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
	public void addMethod(final JavaMethodDefinition methodDef) {
		super.addChild(methodDef);
	}
	
	/**
	 * Gets the super class name.
	 * 
	 * @return the super class name
	 */
	@SuppressWarnings("unused")
	private String getSuperClassName() {
		return this.superClassName;
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
	
	@Override
	public Element getXMLRepresentation(final Document document) {
		Element thisElement = document.createElement("JavaClassDefinition");
		
		Element nameElement = document.createElement("fullQualifiedName");
		Text textNode = document.createTextNode(this.getFullQualifiedName());
		nameElement.appendChild(textNode);
		thisElement.appendChild(nameElement);
		
		return thisElement;
	}
	
	/**
	 * Checks if is anonym class.
	 * 
	 * @return true, if is anonym class
	 */
	@SuppressWarnings("unused")
	private boolean isAnonymClass() {
		return this.anonymClass;
	}
	
	/**
	 * Next anon counter.
	 * 
	 * @return the int
	 */
	@Transient
	public int nextAnonCounter(final PPATypeVisitor v) {
		if (this.anonymClass) {
			return this.getTypedParent().nextAnonCounter(v);
		} else {
			int vId = System.identityHashCode(v);
			if(!this.anonCounters.containsKey(vId)){
				this.anonCounters.put(vId, 0);
			}
			this.anonCounters.put(vId, this.anonCounters.get(vId) + 1);
			return this.anonCounters.get(vId);
		}
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		return new HashSet<Annotated>();
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
		sb.append(this.superClassName);
		sb.append(", anonymClass=");
		sb.append(this.anonymClass);
		sb.append(", childrenSize=");
		sb.append(this.children.size());
		
		sb.append(", getFullQualifiedName()=");
		sb.append(getFullQualifiedName());
		sb.append(", getShortName()=");
		sb.append(getShortName());
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
		sb.append(this.superClassName);
		sb.append(", anonymClass=");
		sb.append(this.anonymClass);
		sb.append(", childrenSize=");
		sb.append(this.children.size());
		
		sb.append(", getFullQualifiedName()=");
		sb.append(getFullQualifiedName());
		sb.append(", getShortName()=");
		sb.append(getShortName());
		sb.append("]");
		return sb.toString();
	}
}
