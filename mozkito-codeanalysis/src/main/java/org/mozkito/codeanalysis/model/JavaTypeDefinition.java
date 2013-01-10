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
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

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
	
	/** The Constant FULL_QUALIFIED_NAME. */
	public static final String FULL_QUALIFIED_NAME   = "fullQualifiedName";
	
	/** The Constant JAVA_CLASS_DEFINITION. */
	public static final String JAVA_CLASS_DEFINITION = "JavaClassDefinition";
	
	/** The Constant JAVA_CLASS_ANONYMOUS. */
	public static final String JAVA_CLASS_ANONYMOUS  = "anonymous";
	
	/** The Constant JAVA_CLASS_INTERFACE. */
	public static final String JAVA_CLASS_INTERFACE  = "interface";
	
	/** The Constant JAVA_CLASS_PARENT. */
	public static final String JAVA_CLASS_PARENT     = "parent";
	
	/** The Constant serialVersionUID. */
	private static final long  serialVersionUID      = 945704236316941413L;
	
	/**
	 * Creates a JavaClassDefinition instance from XML.
	 * 
	 * @param element
	 *            the element
	 * @param factory
	 *            the factory
	 * @return the java class definition is successful, <code>null</code> otherwise.
	 */
	public static JavaTypeDefinition fromXMLRepresentation(final Element element,
	                                                       final JavaElementFactory factory) {
		
		if (!element.getName().equals(JavaTypeDefinition.JAVA_CLASS_DEFINITION)) {
			throw new UnrecoverableError(String.format("Unrecognized root element <%s>. Returning null.",
			                                           element.getName()));
			
		}
		
		final Element nameElement = element.getChild(JavaTypeDefinition.FULL_QUALIFIED_NAME);
		Condition.notNull(nameElement, "Could not extract JavaClassDefinfition.fullQualifidName. Returning null.");
		
		final String name = nameElement.getText();
		
		final Element anonElement = element.getChild(JavaTypeDefinition.JAVA_CLASS_ANONYMOUS);
		Condition.notNull(anonElement, "Could not extract JavaClassDefinfition.anonymous.");
		
		final Boolean anonymous = Boolean.valueOf(anonElement.getText());
		
		final Element interfaceElement = element.getChild(JavaTypeDefinition.JAVA_CLASS_INTERFACE);
		Condition.notNull(interfaceElement, "Could not extract JavaClassDefinfition.interface.");
		
		final Boolean isInterface = Boolean.valueOf(interfaceElement.getText());
		
		final Element parentElement = element.getChild(JavaTypeDefinition.JAVA_CLASS_PARENT);
		if (parentElement != null) {
			final JavaTypeDefinition parent = JavaTypeDefinition.fromXMLRepresentation(parentElement.getChild(JavaTypeDefinition.JAVA_CLASS_DEFINITION),
			                                                                           factory);
			if (anonymous) {
				return factory.getAnonymousClassDefinition(parent, name);
			}
		}
		
		net.ownhero.dev.kanuni.conditions.Condition.check(!anonymous,
		                                                  "Anonymous classes MUST have a parent! XML file contained no parent of anonymous class.");
		
		if (isInterface) {
			return factory.getInterfaceDefinition(name);
		}
		
		return factory.getClassDefinition(name);
	}
	
	/** The super class name. */
	private String                          superClassName = null;
	
	/** The Constant anonCheck. */
	private static final String             ANON_CHECK     = ".*\\$\\d+";
	
	/** The anon counter. */
	private final HashMap<Integer, Integer> anonCounters   = new HashMap<Integer, Integer>();
	
	/** The anonym class. */
	private boolean                         anonymClass    = false;
	
	/** The parent. */
	private JavaTypeDefinition              parent;
	
	/** The interfaze. */
	private boolean                         interfaze      = false;
	
	/**
	 * Instantiates a new java class definition.
	 * 
	 * @deprecated should only be used by the persistence util if dynamic enhancement is nescessary
	 */
	@Deprecated
	public JavaTypeDefinition() {
		super();
	}
	
	/**
	 * Instantiates a new java class definition.
	 * 
	 * @param parent
	 *            the parent
	 * @param fullQualifiedName
	 *            the full qualified name
	 */
	@NoneNull
	protected JavaTypeDefinition(final JavaTypeDefinition parent, final String fullQualifiedName) {
		super(fullQualifiedName, JavaTypeDefinition.class.getCanonicalName());
		if (Pattern.matches(JavaTypeDefinition.ANON_CHECK, fullQualifiedName)) {
			this.anonymClass = true;
		}
		setParent(parent);
	}
	
	/**
	 * Instantiates a new java class definition.
	 * 
	 * @param parent
	 *            the parent
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param isInterface
	 *            the is interface
	 */
	@NoneNull
	protected JavaTypeDefinition(final JavaTypeDefinition parent, final String fullQualifiedName,
	        final boolean isInterface) {
		super(fullQualifiedName, JavaTypeDefinition.class.getCanonicalName());
		if (Pattern.matches(JavaTypeDefinition.ANON_CHECK, fullQualifiedName)) {
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
	 */
	@NoneNull
	protected JavaTypeDefinition(final String fullQualifiedName) {
		super(fullQualifiedName, JavaTypeDefinition.class.getCanonicalName());
		if (Pattern.matches(JavaTypeDefinition.ANON_CHECK, fullQualifiedName)) {
			throw new UnrecoverableError("Anonymous class must have parent!");
		}
	}
	
	/**
	 * Instantiates a new java class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param isInterface
	 *            the is interface
	 */
	@NoneNull
	protected JavaTypeDefinition(final String fullQualifiedName, final boolean isInterface) {
		super(fullQualifiedName, JavaTypeDefinition.class.getCanonicalName());
		if (Pattern.matches(JavaTypeDefinition.ANON_CHECK, fullQualifiedName)) {
			throw new UnrecoverableError("Anonymous class must have parent!");
		}
		setInterfaze(isInterface);
	}
	
	/**
	 * Gets the anon counters.
	 * 
	 * @return the anonCounters
	 */
	@Transient
	private HashMap<Integer, Integer> getAnonCounters() {
		return this.anonCounters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(JavaTypeDefinition.class);
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
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
		final Element thisElement = new Element(JavaTypeDefinition.JAVA_CLASS_DEFINITION);
		final Element nameElement = new Element(JavaTypeDefinition.FULL_QUALIFIED_NAME);
		final Element anonElement = new Element(JavaTypeDefinition.JAVA_CLASS_ANONYMOUS);
		final Element interfaceElement = new Element(JavaTypeDefinition.JAVA_CLASS_INTERFACE);
		final Element parentElement = new Element(JavaTypeDefinition.JAVA_CLASS_PARENT);
		nameElement.setText(getFullQualifiedName());
		thisElement.addContent(nameElement);
		anonElement.setText(String.valueOf(isAnonymClass()));
		thisElement.addContent(anonElement);
		interfaceElement.setText(String.valueOf(isInterface()));
		thisElement.addContent(interfaceElement);
		if (getParent() != null) {
			parentElement.addContent(getParent().getXMLRepresentation());
			thisElement.addContent(parentElement);
		}
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
	
	/**
	 * Checks if is interface.
	 * 
	 * @return true, if is interface
	 */
	@Transient
	public boolean isInterface() {
		return isInterfaze();
	}
	
	/**
	 * Checks if is interfaze.
	 * 
	 * @return true, if is interfaze
	 */
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
	
	/**
	 * Sets the interfaze.
	 * 
	 * @param interfaze
	 *            the new interfaze
	 */
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
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the new parent
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
