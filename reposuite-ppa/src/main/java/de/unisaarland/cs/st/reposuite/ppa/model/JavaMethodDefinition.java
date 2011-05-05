package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.jdom.Element;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class JavaMethodDefinition.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
@DiscriminatorValue ("JAVAMETHODDEFINITION")
public class JavaMethodDefinition extends JavaElement implements Annotated {
	
	public static final String FULL_QUALIFIED_NAME    = "fullQualifiedName";
	public static final String JAVA_METHOD_DEFINITION = "JavaMethodDefinition";
	
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
		StringBuilder sb = new StringBuilder();
		
		String localParentName = parentName;
		while (localParentName.endsWith(".")) {
			localParentName = localParentName.substring(0, localParentName.length() - 1);
		}
		sb.append(localParentName);
		sb.append(".");
		sb.append(methodName);
		sb.append(getSignatureString(signature));
		return sb.toString();
	}
	
	/**
	 * Creates a JavaMethodDefinition instance from XML.
	 * 
	 * @param element
	 *            the element
	 * @return the java method definition is successful, <code>null</code>
	 *         otherwise.
	 */
	public static JavaMethodDefinition fromXMLRepresentation(final org.jdom.Element element) {
		if (!element.getName().equals(JAVA_METHOD_DEFINITION)) {
			if (Logger.logWarn()) {
				Logger.warn("Unrecognized root element <" + element.getName() + ">. Returning null.");
			}
			return null;
		}
		
		org.jdom.Element nameElement = element.getChild(FULL_QUALIFIED_NAME);
		if (nameElement == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaMethodDefinfition.fullQualifidName. Returning null.");
			}
			return null;
		}
		String name = nameElement.getText();
		
		if ((name == null) || (!name.contains("(")) || (!name.contains(")"))) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaMethodDefinfition.fullQualifidName. Returning null.");
			}
			return null;
		}
		
		int dotIndex = name.indexOf(".");
		int index = name.indexOf("(");
		
		if ((dotIndex < 0) || (dotIndex > index)) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaMethodDefinfition.fullQualifidName. Returning null.");
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
		
		return new JavaMethodDefinition(parentName, methodName, argList);
	}
	
	public static String getSignatureString(final List<String> signature) {
		StringBuilder sb = new StringBuilder();
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
	protected JavaMethodDefinition(final String parentName, final String methodName, final List<String> signature) {
		super(methodName, JavaMethodDefinition.class.getCanonicalName());
		
		Condition.check(parentName.contains("."), "The parentName of a method call MUST contain at least one DOT.");
		Condition.check(!parentName.contains("("), "The parentName name of a method call must not contain '('.");
		Condition.check(!parentName.contains(")"), "The parentName name of a method call must not contain ')'.");
		Condition.check(!methodName.contains("."), "The methodName name of a method call MUST NOT contains any DOT.");
		Condition.check(!methodName.contains("("), "The methodName name of a method call must not contain '('.");
		Condition.check(!methodName.contains(")"), "The methodName name of a method call must not contain ')'.");
		
		setSignature(new ArrayList<String>(signature));
		setFullQualifiedName(composeFullQualifiedName(parentName, methodName, signature));
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition#equals
	 * (java.lang.Object)
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
		JavaMethodDefinition other = (JavaMethodDefinition) obj;
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
		return signature;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#getXMLRepresentation
	 * (org.w3c.dom.Document)
	 */
	@Override
	public Element getXMLRepresentation() {
		Element thisElement = new Element(JAVA_METHOD_DEFINITION);
		Element nameElement = new Element(FULL_QUALIFIED_NAME);
		nameElement.setText(getFullQualifiedName());
		thisElement.addContent(nameElement);
		return thisElement;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getSignature() == null)
		                                                   ? 0
		                                                   : getSignature().hashCode());
		return result;
	}
	
	/**
	 * Sets the signature. Used by persistence middleware only
	 * 
	 * @param signature
	 *            the new signature
	 */
	@NoneNull
	protected void setSignature(final List<String> signature) {
		this.signature = signature;
	}
	
}
