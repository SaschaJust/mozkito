/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.codeanalysis.model;

import java.util.HashMap;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kisa.Logger;

import org.jdom2.Element;
import org.mozkito.codeanalysis.visitors.PPATypeVisitor;
import org.mozkito.persistence.Annotated;


/**
 * The Class JavaClassDefinition.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
@Entity
@DiscriminatorValue ("JAVATYPEDEFINITION")
public class JavaTypeDefinition extends JavaElement implements Annotated {
	
	public static final String FULL_QUALIFIED_NAME   = "fullQualifiedName";
	public static final String JAVA_CLASS_DEFINITION = "JavaClassDefinition";
	
	/** The Constant serialVersionUID. */
	private static final long  serialVersionUID      = 945704236316941413L;
	
	/**
	 * Creates a JavaClassDefinition instance from XML.
	 * 
	 * @param element
	 *            the element
	 * @return the java class definition is successful, <code>null</code> otherwise.
	 */
	public static JavaTypeDefinition fromXMLRepresentation(final Element element) {
		
		if (!element.getName().equals(JAVA_CLASS_DEFINITION)) {
			if (Logger.logWarn()) {
				Logger.warn("Unrecognized root element <" + element.getName() + ">. Returning null.");
			}
			return null;
		}
		
		final Element nameElement = element.getChild(FULL_QUALIFIED_NAME);
		if (nameElement == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaClassDefinfition.fullQualifidName. Returning null.");
			}
			return null;
		}
		final String name = nameElement.getText();
		
		return new JavaTypeDefinition(name);
	}
	
	/** The super class name. */
	private String                          superClassName = null;
	
	/** The Constant anonCheck. */
	private final static String             anonCheck      = ".*\\$\\d+";
	
	/** The anon counter. */
	private final HashMap<Integer, Integer> anonCounters   = new HashMap<Integer, Integer>();
	
	/** The anonym class. */
	private boolean                         anonymClass    = false;
	
	private JavaTypeDefinition              parent;
	
	private boolean                         interfaze      = false;
	
	/**
	 * Instantiates a new java class definition.
	 */
	@Deprecated
	public JavaTypeDefinition() {
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
	protected JavaTypeDefinition(final JavaTypeDefinition parent, final String fullQualifiedName) {
		super(fullQualifiedName, JavaTypeDefinition.class.getCanonicalName());
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			this.anonymClass = true;
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
	protected JavaTypeDefinition(final JavaTypeDefinition parent, final String fullQualifiedName,
	        final boolean isInterface) {
		super(fullQualifiedName, JavaTypeDefinition.class.getCanonicalName());
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			this.anonymClass = true;
		}
		setParent(parent);
		setInterfaze(isInterface);
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
	protected JavaTypeDefinition(final String fullQualifiedName) {
		super(fullQualifiedName, JavaTypeDefinition.class.getCanonicalName());
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			throw new UnrecoverableError("Anonymous class must have parent!");
		}
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
	protected JavaTypeDefinition(final String fullQualifiedName, final boolean isInterface) {
		super(fullQualifiedName, JavaTypeDefinition.class.getCanonicalName());
		if (Pattern.matches(anonCheck, fullQualifiedName)) {
			throw new UnrecoverableError("Anonymous class must have parent!");
		}
		setInterfaze(isInterface);
	}
	
	/**
	 * @return the anonCounters
	 */
	@Transient
	private HashMap<Integer, Integer> getAnonCounters() {
		return this.anonCounters;
	}
	
	@ManyToOne (cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	public JavaTypeDefinition getParent() {
		return this.parent;
	}
	
	/**
	 * Gets the super class name.
	 * 
	 * @return the super class name
	 */
	public String getSuperClassName() {
		return this.superClassName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.model.JavaElement#getXMLRepresentation (org.w3c.dom.Document)
	 */
	@Override
	@NoneNull
	@Transient
	public Element getXMLRepresentation() {
		final Element thisElement = new Element(JAVA_CLASS_DEFINITION);
		final Element nameElement = new Element(FULL_QUALIFIED_NAME);
		nameElement.setText(getFullQualifiedName());
		thisElement.addContent(nameElement);
		return thisElement;
	}
	
	/**
	 * Checks if is anonym class.
	 * 
	 * @return true, if is anonym class
	 */
	public boolean isAnonymClass() {
		return this.anonymClass;
	}
	
	@Transient
	public boolean isInterface() {
		return isInterfaze();
	}
	
	public boolean isInterfaze() {
		// PRECONDITIONS
		
		try {
			return this.interfaze;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Returns the next anonymous class counter. This might differ from anonymous class counters found in Java byte
	 * code.
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
		}
		final int vId = System.identityHashCode(v);
		if (!getAnonCounters().containsKey(vId)) {
			getAnonCounters().put(vId, 0);
		}
		getAnonCounters().put(vId, getAnonCounters().get(vId) + 1);
		return getAnonCounters().get(vId);
	}
	
	/**
	 * Sets the anonym class.
	 * 
	 * @param anonymClass
	 *            the new anonym class
	 */
	@NoneNull
	protected void setAnonymClass(final boolean anonymClass) {
		this.anonymClass = anonymClass;
	}
	
	public void setInterfaze(final boolean interfaze) {
		// PRECONDITIONS
		try {
			this.interfaze = interfaze;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.interfaze, interfaze,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	/**
	 * @param parent
	 */
	protected void setParent(final JavaTypeDefinition parent) {
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
		final StringBuilder sb = new StringBuilder();
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
	 * @see org.mozkito.ppa.model.JavaElement#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
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
