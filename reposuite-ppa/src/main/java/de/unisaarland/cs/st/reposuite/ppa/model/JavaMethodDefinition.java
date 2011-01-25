package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

/**
 * The Class JavaMethodDefinition.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaMethodDefinition extends JavaElementDefinition implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6574764154587254697L;
	
	/**
	 * Compose full qualified name.
	 * 
	 * @param name
	 *            the name
	 * @param signature
	 *            the signature
	 * @return the string
	 */
	public static String composeFullQualifiedName(final String name, final List<String> signature){
		StringBuilder sb = new StringBuilder();
		sb.append(name);
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
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param signature
	 *            the signature
	 * @param file
	 *            the file
	 * @param timestamp
	 *            the timestamp
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 */
	protected JavaMethodDefinition(@NotNull final String fullQualifiedName, @NotNull final List<String> signature,
			@NotNull final String file, @NotNull final DateTime timestamp, @NotNull final JavaClassDefinition parent,
			@NonNegative final int startLine, @NonNegative final int endLine) {
		
		super(fullQualifiedName, file, timestamp, startLine, endLine, parent);
		if (parent != null) {
			Condition.check(parent instanceof JavaClassDefinition,
			"The parent of a method Definition has to be another class definition");
			parent.addChild(this);
		}
		this.setSignature(new ArrayList<String>(signature));
		this.fullQualifiedName = composeFullQualifiedName(super.getFullQualifiedName(), signature);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
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
		return signature;
	}
	
	@Override
	@Transient
	public JavaClassDefinition getTypedParent() {
		return (JavaClassDefinition) super.getParent();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getSignature() == null) ? 0 : getSignature().hashCode());
		return result;
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void setSignature(final List<String> signature) {
		this.signature = signature;
	}
	
}
