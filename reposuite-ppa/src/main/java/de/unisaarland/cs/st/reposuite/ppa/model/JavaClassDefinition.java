package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashMap;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor;

/**
 * The Class JavaClassDefinition.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaClassDefinition extends JavaElement implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long               serialVersionUID = 945704236316941413L;
	
	/** The super class name. */
	private String                          superClassName   = null;
	
	/** The Constant anonCheck. */
	private final static String             anonCheck        = ".*\\$\\d+";
	
	/** The anon counter. */
	private final HashMap<Integer, Integer> anonCounters     = new HashMap<Integer, Integer>();
	
	/** The anonym class. */
	private boolean                         anonymClass      = false;
	
	private JavaClassDefinition             parent;
	
	/**
	 * Instantiates a new java class definition.
	 */
	@SuppressWarnings ("unused")
	private JavaClassDefinition() {
		super();
	}
	
	/**
	 * Instantiates a new java class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param packageName
	 *            the package name
	 */
	@NoneNull
	protected JavaClassDefinition(final JavaClassDefinition parent, final String fullQualifiedName) {
		super(fullQualifiedName);
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			anonymClass = true;
		}
		setParent(parent);
	}
	
	/**
	 * Instantiates a new java class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param packageName
	 *            the package name
	 */
	@NoneNull
	protected JavaClassDefinition(final String fullQualifiedName, final String packageName) {
		
		super(fullQualifiedName);
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			throw new UnrecoverableError("Anonymous class must have parent!");
		}
	}
	
	/**
	 * @return the anonCounters
	 */
	@Transient
	private HashMap<Integer, Integer> getAnonCounters() {
		return anonCounters;
	}
	
	@ManyToOne (cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	public JavaClassDefinition getParent() {
		return parent;
	}
	
	/**
	 * Gets the super class name.
	 * 
	 * @return the super class name
	 */
	private String getSuperClassName() {
		return superClassName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#getXMLRepresentation
	 * (org.w3c.dom.Document)
	 */
	@Override
	@NoneNull
	public Element getXMLRepresentation(final Document document) {
		Element thisElement = document.createElement("JavaClassDefinition");
		
		Element nameElement = document.createElement("fullQualifiedName");
		Text textNode = document.createTextNode(getFullQualifiedName());
		nameElement.appendChild(textNode);
		thisElement.appendChild(nameElement);
		return thisElement;
	}
	
	/**
	 * Checks if is anonym class.
	 * 
	 * @return true, if is anonym class
	 */
	public boolean isAnonymClass() {
		return anonymClass;
	}
	
	/**
	 * Returns the next anonymous class counter. This might differ from
	 * anonymous class counters found in Java byte code.
	 * 
	 * @param v
	 *            the v
	 * @return the int
	 */
	@Transient
	@NoneNull
	public int nextAnonCounter(final PPATypeVisitor v) {
		if (isAnonymClass()) {
			return getParent().nextAnonCounter(v);
		} else {
			int vId = System.identityHashCode(v);
			if (!getAnonCounters().containsKey(vId)) {
				getAnonCounters().put(vId, 0);
			}
			getAnonCounters().put(vId, getAnonCounters().get(vId) + 1);
			return getAnonCounters().get(vId);
		}
	}
	
	/**
	 * Sets the anonym class.
	 * 
	 * @param anonymClass
	 *            the new anonym class
	 */
	@SuppressWarnings ("unused")
	@NoneNull
	private void setAnonymClass(final boolean anonymClass) {
		this.anonymClass = anonymClass;
	}
	
	/**
	 * @param parent
	 */
	private void setParent(final JavaClassDefinition parent) {
		this.parent = parent;
	}
	
	/**
	 * Sets the super class name.
	 * 
	 * @param superClassName
	 *            the new super class name
	 */
	@NoneNull
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
		sb.append(getSuperClassName());
		sb.append(", anonymClass=");
		sb.append(isAnonymClass());
		sb.append(", getFullQualifiedName()=");
		sb.append(getFullQualifiedName());
		sb.append(", getShortName()=");
		sb.append(getShortName());
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("JavaClassDefinition [superClassName=");
		sb.append(getSuperClassName());
		sb.append(", anonymClass=");
		sb.append(isAnonymClass());
		sb.append(", getFullQualifiedName()=");
		sb.append(getFullQualifiedName());
		sb.append(", getShortName()=");
		sb.append(getShortName());
		sb.append("]");
		return sb.toString();
	}
}
