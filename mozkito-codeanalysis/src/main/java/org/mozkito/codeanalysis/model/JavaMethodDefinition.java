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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.StringCondition;

import org.jdom2.Element;
import org.mozkito.persistence.Annotated;


/**
 * The Class JavaMethodDefinition.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
@Entity
@DiscriminatorValue ("JAVAMETHODDEFINITION")
public class JavaMethodDefinition extends JavaElement implements Annotated, Serializable {
	
	public static final String FULL_QUALIFIED_NAME    = "fullQualifiedName";   //$NON-NLS-1$
	public static final String JAVA_METHOD_DEFINITION = "JavaMethodDefinition"; //$NON-NLS-1$
	public static final String ANNOTATED_OVERRIDE     = "annotatedOverride";   //$NON-NLS-1$
	                                                                            
	/** The Constant serialVersionUID. */
	private static final long  serialVersionUID       = -6574764154587254697L;
	
	/**
	 * Compose full qualified name.
	 * 
	 * @param parent
	 *            the parent
	 * @param methodName
	 *            the method name
	 * @param signature
	 *            the signature
	 * @return the string
	 */
	@NoneNull
	public static String composeFullQualifiedName(final String parentName,
	                                              final String methodName,
	                                              final List<String> signature) {
		final StringBuilder sb = new StringBuilder();
		
		String localParentName = parentName;
		while (localParentName.endsWith(".")) { //$NON-NLS-1$
			localParentName = localParentName.substring(0, localParentName.length() - 1);
		}
		sb.append(localParentName);
		sb.append("."); //$NON-NLS-1$
		sb.append(methodName);
		sb.append(getSignatureString(signature));
		return sb.toString();
	}
	
	/**
	 * Creates a JavaMethodDefinition instance from XML.
	 * 
	 * @param element
	 *            the element
	 * @return the java method definition is successful, <code>null</code> otherwise.
	 */
	@NoneNull
	public static JavaMethodDefinition fromXMLRepresentation(final Element element) {
		StringCondition.equals(element.getName(), JAVA_METHOD_DEFINITION,
		                       Messages.JavaMethodDefinition_unrecognized_root_element, element.getName());
		
		final Element nameElement = element.getChild(FULL_QUALIFIED_NAME);
		Condition.notNull(nameElement, Messages.JavaMethodDefinition_fullQualifiedName_extract_error);
		
		final String name = nameElement.getText();
		
		Condition.notNull(name, Messages.JavaMethodDefinition_fullQualifiedName_extract_error);
		
		// FIXME why do we have to use escapes in find but not in matches?
		StringCondition.matches(name, ".*(.*)", Messages.JavaMethodDefinition_fullQualifiedName_extract_error);
		
		final int dotIndex = name.indexOf("."); //$NON-NLS-1$
		final int index = name.indexOf("("); //$NON-NLS-1$
		
		CompareCondition.greater(dotIndex, 0, Messages.JavaMethodDefinition_fullQualifiedName_extract_error);
		CompareCondition.less(dotIndex, index, Messages.JavaMethodDefinition_fullQualifiedName_extract_error);
		
		final Element overrideElement = element.getChild(ANNOTATED_OVERRIDE);
		Condition.notNull(overrideElement, Messages.JavaMethodDefinition_fullQualifiedName_extract_error);
		
		final boolean override = Boolean.valueOf(overrideElement.getText());
		
		final String tmpName = name.substring(0, index);
		final int lastDotIndex = tmpName.lastIndexOf("."); //$NON-NLS-1$
		final String parentName = tmpName.substring(0, lastDotIndex);
		final String methodName = tmpName.substring(lastDotIndex + 1, tmpName.length());
		
		final String argString = name.substring(index + 1, name.indexOf(")")); //$NON-NLS-1$
		final String[] args = argString.split(","); //$NON-NLS-1$
		final List<String> argList = new ArrayList<String>(args.length);
		for (final String arg : args) {
			argList.add(arg);
		}
		
		return new JavaMethodDefinition(parentName, methodName, argList, override);
	}
	
	@NoneNull
	public static String getSignatureString(@NotEmpty final List<String> signature) {
		final StringBuilder sb = new StringBuilder();
		sb.append("("); //$NON-NLS-1$
		if (!signature.isEmpty()) {
			sb.append(signature.get(0));
		}
		for (int i = 1; i < signature.size(); ++i) {
			sb.append(","); //$NON-NLS-1$
			sb.append(signature.get(i));
		}
		sb.append(")"); //$NON-NLS-1$
		return sb.toString();
	}
	
	private boolean      annotatedOverride = false;
	
	/** The signature. */
	private List<String> signature;
	
	/**
	 * Instantiates a new java method definition.
	 */
	@Deprecated
	public JavaMethodDefinition() {
		super();
	}
	
	/**
	 * Instantiates a new java method definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param signature
	 *            the signature
	 */
	@NoneNull
	protected JavaMethodDefinition(final String parentName, final String methodName, final List<String> signature,
	        final boolean override) {
		super(methodName, JavaMethodDefinition.class.getCanonicalName());
		
		setSignature(new ArrayList<String>(signature));
		setFullQualifiedName(composeFullQualifiedName(parentName, methodName, signature));
		setAnnotatedOverride(override);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.model.JavaElementDefinition#equals (java.lang.Object)
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
		final JavaMethodDefinition other = (JavaMethodDefinition) obj;
		if (getSignature() == null) {
			if (other.getSignature() != null) {
				return false;
			}
		} else if (!getSignature().equals(other.getSignature())) {
			return false;
		}
		return true;
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
	 * @see org.mozkito.ppa.model.JavaElement#getXMLRepresentation (org.w3c.dom.Document)
	 */
	@Override
	@Transient
	public Element getXMLRepresentation() {
		final Element thisElement = new Element(JAVA_METHOD_DEFINITION);
		final Element nameElement = new Element(FULL_QUALIFIED_NAME);
		final Element overrideElement = new Element(ANNOTATED_OVERRIDE);
		nameElement.setText(getFullQualifiedName());
		thisElement.addContent(nameElement);
		overrideElement.setText(String.valueOf(isAnnotatedOverride()));
		thisElement.addContent(overrideElement);
		return thisElement;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.model.JavaElementDefinition#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((getSignature() == null)
		                                                     ? 0
		                                                     : getSignature().hashCode());
		return result;
	}
	
	public boolean isAnnotatedOverride() {
		// PRECONDITIONS
		
		try {
			return this.annotatedOverride;
		} finally {
			// POSTCONDITIONS
			
		}
	}
	
	public void setAnnotatedOverride(final boolean annotatedOverride) {
		// PRECONDITIONS
		
		try {
			this.annotatedOverride = annotatedOverride;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the signature. Used by persistence middleware only
	 * 
	 * @param signature
	 *            the new signature
	 */
	protected void setSignature(final List<String> signature) {
		this.signature = signature;
	}
	
}
