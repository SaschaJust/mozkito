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
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

/**
 * The Class JavaClassDefinition.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaClassDefinition extends JavaElementDefinition implements Annotated {
	
	/** The Constant serialVersionUID. */
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
	 */
	@SuppressWarnings("unused")
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
	protected JavaClassDefinition(final String fullQualifiedName, final String packageName) {
		
		super(fullQualifiedName);
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			this.anonymClass = true;
		}
	}
	
	/**
	 * Adds the method.
	 * 
	 * @param methodDef
	 *            the method def
	 * @return
	 * @return the java method definition
	 */
	@Transient
	@NoneNull
	public JavaElementRelation addMethod(final JavaMethodDefinition methodDef) {
		return methodDef.addParent(this);
	}
	
	@Override
	public JavaElementRelation addParent(JavaElement parent){
		if(this.getParentRelations().isEmpty()){
			return super.addParent(parent);
		}else{
			if (Logger.logWarn()) {
				Logger.warn("Cannot add parent to JavaElement "+this.toString()+". Parent set already.");
			}
			return getParentRelations().get(0);
		}
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
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition#getParent
	 * ()
	 */
	@Override
	@Transient
	public JavaClassDefinition getTypedParent() {
		if (this.getParentRelations().isEmpty()) {
			return null;
		}
		JavaElementRelation parent = this.getParentRelations().values().iterator().next();
		return (JavaClassDefinition) parent.getParent();
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#getXMLRepresentation(org.w3c.dom.Document)
	 */
	@Override
	@NoneNull
	public Element getXMLRepresentation(final Document document) {
		Element thisElement = document.createElement("JavaClassDefinition");
		
		Element nameElement = document.createElement("fullQualifiedName");
		Text textNode = document.createTextNode(this.getFullQualifiedName());
		nameElement.appendChild(textNode);
		
		thisElement.appendChild(nameElement);
		
		Element parentElement = document.createElement("parent");
		if (!this.getParentRelations().isEmpty()) {
			JavaElementRelation relation = this.getParentRelations().values().iterator().next();
			parentElement.appendChild(relation.getXMLRepresentation(document));
		}
		thisElement.appendChild(parentElement);
		
		Element childElement = document.createElement("children");
		for (JavaElementRelation rel : getChildRelations().values()) {
			childElement.appendChild(rel.getXMLRepresentation(document));
		}
		
		
		thisElement.appendChild(childElement);
		
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
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
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
	@NoneNull
	private void setAnonymClass(final boolean anonymClass) {
		this.anonymClass = anonymClass;
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
		sb.append(this.superClassName);
		sb.append(", anonymClass=");
		sb.append(this.anonymClass);
		sb.append(", childrenSize=");
		sb.append(super.getChildRelations().size());
		
		sb.append(", getFullQualifiedName()=");
		sb.append(getFullQualifiedName());
		sb.append(", getShortName()=");
		sb.append(getShortName());
		sb.append(", parent=");
		if (getTypedParent() != null) {
			sb.append(getTypedParent().getFullQualifiedName());
		} else {
			sb.append("null");
		}
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
		sb.append(super.getChildRelations().size());
		sb.append(", getFullQualifiedName()=");
		sb.append(getFullQualifiedName());
		sb.append(", getShortName()=");
		sb.append(getShortName());
		sb.append("]");
		return sb.toString();
	}
}
