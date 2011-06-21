/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.jdom.Element;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * The Class JavaMethodCall.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
@DiscriminatorValue ("JAVAMETHODCALL")
public class JavaMethodCall extends JavaElement implements Annotated {
	
	public static final String FULL_QUALIFIED_NAME = "fullQualifiedName";
	public static final String JAVA_METHOD_CALL    = "JavaMethodCall";
	
	/** The Constant serialVersionUID. */
	private static final long  serialVersionUID    = -2885710604331995125L;
	
	/**
	 * Compose full qualified name.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param signature
	 *            the signature
	 * @return the string
	 */
	@NoneNull
	public static String composeFullQualifiedName(final String objectName,
	                                              final String methodName,
	                                              final List<String> signature) {
		StringBuilder sb = new StringBuilder();
		String localParentName = objectName;
		while (localParentName.endsWith(".")) {
			localParentName = localParentName.substring(0, localParentName.length() - 1);
		}
		sb.append(localParentName);
		sb.append(".");
		sb.append(methodName);
		sb.append("(");
		if (!signature.isEmpty()) {
			sb.append(signature.get(0));
		}
		for (int i = 1; i < signature.size(); ++i) {
			sb.append(",");
			sb.append(signature.get(i));
		}
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * Creates a JavaMethodCall instance from XML.
	 * 
	 * @param element
	 *            the element
	 * @return the java method call is successful, <code>null</code> otherwise.
	 */
	public static JavaMethodCall fromXMLRepresentation(final org.jdom.Element element) {
		if (!element.getName().equals(JAVA_METHOD_CALL)) {
			if (Logger.logWarn()) {
				Logger.warn("Unrecognized root element <" + element.getName() + ">. Returning null.");
			}
			return null;
		}
		
		org.jdom.Element nameElement = element.getChild(FULL_QUALIFIED_NAME);
		if (nameElement == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaMethodCall.fullQualifidName. Returning null.");
			}
			return null;
		}
		String name = nameElement.getText();
		
		if ((name == null) || (!name.contains("(")) || (!name.contains(")"))) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaMethodCall.fullQualifidName. Returning null.");
			}
			return null;
		}
		
		int dotIndex = name.indexOf(".");
		int index = name.indexOf("(");
		
		if ((dotIndex < 0) || (dotIndex > index)) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaMethodCall.fullQualifidName. Returning null.");
			}
			return null;
		}
		
		String tmpName = name.substring(0, index);
		int lastDotIndex = tmpName.lastIndexOf(".");
		String parentName = tmpName.substring(0, lastDotIndex);
		String methodName = tmpName.substring(lastDotIndex + 1, tmpName.length());
		
		String argString = name.substring(index + 1, name.indexOf(")"));
		String[] args = argString.split(",");
		List<String> argList = new ArrayList<String>(args.length);
		for (String arg : args) {
			argList.add(arg);
		}
		
		return new JavaMethodCall(parentName, methodName, argList);
	}
	
	/** The signature. */
	private List<String> signature;
	
	/** The called package name. */
	private String       calledPackageName = "<unknown>";
	
	/** The called class name. */
	private String       calledClassName   = "<unknown>";
	
	/**
	 * Instantiates a new java method call.
	 */
	@Deprecated
	public JavaMethodCall() {
		super();
	}
	
	/**
	 * Instantiates a new java method call.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param signature
	 *            the signature
	 */
	@NoneNull
	protected JavaMethodCall(final String objectName, final String methodName, final List<String> signature) {
		super(methodName, JavaMethodCall.class.getCanonicalName());
		
		Condition.check(objectName.contains("."), "The objectName of a method call MUST contain at least one DOT.");
		Condition.check(!objectName.contains("("), "The objectName name of a method call must not contain '('.");
		Condition.check(!objectName.contains(")"), "The objectName name of a method call must not contain ')'.");
		Condition.check(!methodName.contains("."), "The methodName name of a method call MUST NOT contains any DOT.");
		Condition.check(!methodName.contains("("), "The methodName name of a method call must not contain '('.");
		Condition.check(!methodName.contains(")"), "The methodName name of a method call must not contain ')'.");
		
		this.signature = new ArrayList<String>(signature);
		this.setFullQualifiedName(composeFullQualifiedName(objectName, methodName, signature));
		int index = objectName.lastIndexOf(".");
		if (index < 0) {
			this.calledPackageName = "";
			this.calledClassName = objectName.substring(0, objectName.length());
		} else {
			this.calledPackageName = objectName.substring(0, index);
			this.calledClassName = objectName.substring(index + 1, objectName.length());
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#equals(java.lang
	 * .Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		JavaMethodCall other = (JavaMethodCall) obj;
		if (this.getSignature() == null) {
			if (other.getSignature() != null) {
				return false;
			}
		} else if (!this.getSignature().equals(other.getSignature())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the called class name full qualified.
	 * 
	 * @return the called class name full qualified
	 */
	@Transient
	public String getCalledClassNameFullQualified() {
		return this.getFullQualifiedName();
	}
	
	/**
	 * Gets the called class name short.
	 * 
	 * @return the called class name short
	 */
	@Transient
	public String getCalledClassNameShort() {
		return this.calledClassName;
	}
	
	/**
	 * Gets the called package name.
	 * 
	 * @return the called package name
	 */
	public String getCalledPackageName() {
		return this.calledPackageName;
	}
	
	/**
	 * Gets the signature.
	 * 
	 * @return the signature
	 */
	@ElementCollection
	public List<String> getSignature() {
		return this.signature;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#getXMLRepresentation
	 * ()
	 */
	@Override
	@NoneNull
	public Element getXMLRepresentation() {
		Element thisElement = new Element(JAVA_METHOD_CALL);
		Element nameElement = new Element(FULL_QUALIFIED_NAME);
		nameElement.setText(this.getFullQualifiedName());
		thisElement.addContent(nameElement);
		return thisElement;
	}
	
	/**
	 * Sets the called class name.
	 * 
	 * @param calledClassName
	 *            the new called class name
	 */
	@NoneNull
	protected void setCalledClassName(final String calledClassName) {
		this.calledClassName = calledClassName;
	}
	
	/**
	 * Sets the called package name.
	 * 
	 * @param calledPackageName
	 *            the new called package name
	 */
	@NoneNull
	protected void setCalledPackageName(final String calledPackageName) {
		this.calledPackageName = calledPackageName;
	}
	
	/**
	 * Sets the signature.
	 * 
	 * @param signature
	 *            the new signature
	 */
	@NoneNull
	protected void setSignature(final List<String> signature) {
		this.signature = signature;
	}
}
