package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * The Class JavaMethodDefinition.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaMethodDefinition extends JavaElementDefinition implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6574764154587254697L;
	
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
	public static String composeFullQualifiedName(final JavaClassDefinition parent,
	                                              final String methodName,
	                                              final List<String> signature) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(parent.getFullQualifiedName());
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
	
	/** The signature. */
	private List<String> signature;
	
	/**
	 * Instantiates a new java method definition.
	 */
	@SuppressWarnings ("unused")
	private JavaMethodDefinition() {
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
	protected JavaMethodDefinition(final String fullQualifiedName, final List<String> signature) {
		// TODO add condition check that fullQualifiedName contains (,) and .
		super(fullQualifiedName);
		this.setSignature(new ArrayList<String>(signature));
		this.setFullQualifiedName(fullQualifiedName);
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
		if (getClass() != obj.getClass()) {
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
		return this.signature;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition#getTypedParent
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#getXMLRepresentation
	 * (org.w3c.dom.Document)
	 */
	@Override
	public Element getXMLRepresentation(final Document document) {
		Element thisElement = document.createElement("JavaMethodDefinition");
		
		Element nameElement = document.createElement("fullQualifiedName");
		Text textNode = document.createTextNode(this.getFullQualifiedName());
		nameElement.appendChild(textNode);
		
		Element parentElement = document.createElement("parent");
		if (!this.getParentRelations().isEmpty()) {
			JavaElementRelation relation = this.getParentRelations().values().iterator().next();
			parentElement.appendChild(relation.getXMLRepresentation(document));
		}
		
		Element childElement = document.createElement("children");
		for (JavaElementRelation rel : getChildRelations().values()) {
			childElement.appendChild(rel.getXMLRepresentation(document));
		}
		
		thisElement.appendChild(nameElement);
		thisElement.appendChild(parentElement);
		thisElement.appendChild(childElement);
		
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	public Collection<Annotated> saveFirst() {
		HashSet<Annotated> set = new HashSet<Annotated>();
		set.add(this.getTypedParent());
		return set;
	}
	
	/**
	 * Sets the signature. Used by Hibernate only
	 * 
	 * @param signature
	 *            the new signature
	 */
	@NoneNull
	private void setSignature(final List<String> signature) {
		this.signature = signature;
	}
	
}
