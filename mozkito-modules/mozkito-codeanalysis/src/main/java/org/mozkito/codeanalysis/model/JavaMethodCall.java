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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.jdom2.Element;

import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class JavaMethodCall.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
@Entity
@DiscriminatorValue ("JAVAMETHODCALL")
public class JavaMethodCall extends JavaElement implements Annotated {
	
	/** The Constant FULL_QUALIFIED_NAME. */
	public static final String FULL_QUALIFIED_NAME = "fullQualifiedName";
	
	/** The Constant JAVA_METHOD_CALL. */
	public static final String JAVA_METHOD_CALL    = "JavaMethodCall";
	
	/** The Constant serialVersionUID. */
	private static final long  serialVersionUID    = -2885710604331995125L;
	
	/**
	 * Compose full qualified name.
	 * 
	 * @param objectName
	 *            the object name
	 * @param methodName
	 *            the method name
	 * @param signature
	 *            the signature
	 * @return the string
	 */
	@NoneNull
	public static String composeFullQualifiedName(final String objectName,
	                                              final String methodName,
	                                              final List<String> signature) {
		
		String localParentName = objectName;
		
		if (objectName.startsWith("src.")) {
			localParentName = localParentName.substring(4);
		}
		
		final StringBuilder sb = new StringBuilder();
		
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
	public static JavaMethodCall fromXMLRepresentation(final Element element) {
		if (!element.getName().equals(JavaMethodCall.JAVA_METHOD_CALL)) {
			if (Logger.logWarn()) {
				Logger.warn("Unrecognized root element <" + element.getName() + ">. Returning null.");
			}
			return null;
		}
		
		final Element nameElement = element.getChild(JavaMethodCall.FULL_QUALIFIED_NAME);
		if (nameElement == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaMethodCall.fullQualifidName. Returning null.");
			}
			return null;
		}
		final String name = nameElement.getText();
		
		if ((name == null) || (!name.contains("(")) || (!name.contains(")"))) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaMethodCall.fullQualifidName. Returning null.");
			}
			return null;
		}
		
		final int dotIndex = name.indexOf(".");
		final int index = name.indexOf("(");
		
		if ((dotIndex < 0) || (dotIndex > index)) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaMethodCall.fullQualifidName. Returning null.");
			}
			return null;
		}
		
		final String tmpName = name.substring(0, index);
		final int lastDotIndex = tmpName.lastIndexOf(".");
		final String parentName = tmpName.substring(0, lastDotIndex);
		final String methodName = tmpName.substring(lastDotIndex + 1, tmpName.length());
		
		final String argString = name.substring(index + 1, name.indexOf(")"));
		final String[] args = argString.split(",");
		final List<String> argList = new ArrayList<String>(args.length);
		for (final String arg : args) {
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
	 * 
	 * @deprecated shoud only be used by the persistence util in case dynamic enhancement is nescessary
	 */
	@Deprecated
	public JavaMethodCall() {
		super();
	}
	
	/**
	 * Instantiates a new java method call.
	 * 
	 * @param objectName
	 *            the object name
	 * @param methodName
	 *            the method name
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
		setFullQualifiedName(composeFullQualifiedName(objectName, methodName, signature));
		final int index = objectName.lastIndexOf(".");
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
	 * @see org.mozkito.codeanalysis.model.JavaElement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JavaMethodCall other = (JavaMethodCall) obj;
		if (this.calledClassName == null) {
			if (other.calledClassName != null) {
				return false;
			}
		} else if (!this.calledClassName.equals(other.calledClassName)) {
			return false;
		}
		if (this.calledPackageName == null) {
			if (other.calledPackageName != null) {
				return false;
			}
		} else if (!this.calledPackageName.equals(other.calledPackageName)) {
			return false;
		}
		if (this.signature == null) {
			if (other.signature != null) {
				return false;
			}
		} else if (!this.signature.equals(other.signature)) {
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
		return getFullQualifiedName();
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
	 * Gets the called method name.
	 * 
	 * @return the called method name
	 */
	@Transient
	public String getCalledMethodName() {
		return extractMethodName(getFullQualifiedName());
	}
	
	/**
	 * Gets the called package name.
	 * 
	 * @return the called package name
	 */
	public String getCalledPackageName() {
		return this.calledPackageName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(JavaMethodCall.class);
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
	 * @see org.mozkito.ppa.model.JavaElement#getXMLRepresentation ()
	 */
	@Override
	@NoneNull
	@Transient
	public Element getXMLRepresentation() {
		final Element thisElement = new Element(JavaMethodCall.JAVA_METHOD_CALL);
		final Element nameElement = new Element(JavaMethodCall.FULL_QUALIFIED_NAME);
		nameElement.setText(getFullQualifiedName());
		thisElement.addContent(nameElement);
		return thisElement;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.codeanalysis.model.JavaElement#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((this.calledClassName == null)
		                                                           ? 0
		                                                           : this.calledClassName.hashCode());
		result = (prime * result) + ((this.calledPackageName == null)
		                                                             ? 0
		                                                             : this.calledPackageName.hashCode());
		result = (prime * result) + ((this.signature == null)
		                                                     ? 0
		                                                     : this.signature.hashCode());
		return result;
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
