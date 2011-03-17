package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.conditions.StringCondition;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * The Class JavaMethodCall.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaMethodCall extends JavaElement implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2885710604331995125L;
	
	/**
	 * Compose full qualified name.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param signature
	 *            the signature
	 * @return the string
	 */
	public static String composeFullQualifiedName(final String fullQualifiedName, final List<String> signature){
		StringBuilder sb = new StringBuilder();
		sb.append(fullQualifiedName);
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
	private List<String>          signature;
	
	/** The called package name. */
	private String                calledPackageName = "<unknown>";
	
	/** The called class name. */
	private String                calledClassName   = "<unknown>";
	
	/**
	 * Instantiates a new java method call.
	 */
	@SuppressWarnings("unused")
	private JavaMethodCall() {
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
	protected JavaMethodCall(final String fullQualifiedName, final List<String> signature) {
		super(fullQualifiedName);
		this.signature = new ArrayList<String>(signature);
		this.setFullQualifiedName(composeFullQualifiedName(fullQualifiedName, signature));
		int index = fullQualifiedName.lastIndexOf(".");
		StringCondition.minLength(fullQualifiedName,
		                          index + 1,
		                          "Could not determine called class name. Last index of `.` is not less than length of string: %s",
		                          fullQualifiedName);
		this.calledPackageName = fullQualifiedName.substring(0, index);
		this.calledClassName = fullQualifiedName.substring(index+1, fullQualifiedName.length());
		
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#equals(java.lang.Object)
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
		JavaMethodCall other = (JavaMethodCall) obj;
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
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#getXMLRepresentation(org.w3c.dom.Document)
	 */
	@Override
	public Element getXMLRepresentation(final Document document) {
		Element thisElement = document.createElement("JavaMethodCall");
		
		Element nameElement = document.createElement("fullQualifiedName");
		Text textNode = document.createTextNode(this.getFullQualifiedName());
		nameElement.appendChild(textNode);
		thisElement.appendChild(nameElement);
		
		return thisElement;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	public Collection<Annotated> saveFirst() {
		return new HashSet<Annotated>();
	}
	
	/**
	 * Sets the called class name.
	 * 
	 * @param calledClassName
	 *            the new called class name
	 */
	@SuppressWarnings("unused")
	private void setCalledClassName(final String calledClassName) {
		this.calledClassName = calledClassName;
	}
	
	/**
	 * Sets the called package name.
	 * 
	 * @param calledPackageName
	 *            the new called package name
	 */
	@SuppressWarnings("unused")
	private void setCalledPackageName(final String calledPackageName) {
		this.calledPackageName = calledPackageName;
	}
	
	/**
	 * Sets the signature.
	 * 
	 * @param signature
	 *            the new signature
	 */
	@SuppressWarnings("unused")
	private void setSignature(final List<String> signature) {
		this.signature = signature;
	}
}
