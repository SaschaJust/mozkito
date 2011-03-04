package de.unisaarland.cs.st.reposuite.ppa.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

@Embeddable
public class JavaElementPrimaryKey implements Annotated, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3377913237574041596L;
	
	private String            fullQualifiedName;
	private String            elementType;
	
	protected JavaElementPrimaryKey() {
	}
	
	public JavaElementPrimaryKey(final String fullQualifiedName, final Class<? extends JavaElement> clazz) {
		this.setFullQualifiedName(fullQualifiedName);
		this.setElementType(clazz.toString());
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if ((o != null) && (o instanceof JavaElementPrimaryKey) && (o.hashCode() == this.hashCode())) {
			JavaElementPrimaryKey anotherKey = (JavaElementPrimaryKey) o;
			return anotherKey.fullQualifiedName.equals(this.fullQualifiedName)
			&& anotherKey.elementType.equals(this.elementType);
		} else {
			return false;
		}
	}
	
	public String getElementType() {
		return this.elementType;
	}
	
	public String getFullQualifiedName() {
		return this.fullQualifiedName;
	}
	
	@Override
	public int hashCode() {
		return this.getFullQualifiedName().hashCode() + 13 * this.getElementType().hashCode();
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	private void setElementType(final String type) {
		this.elementType = type;
	}
	
	protected void setFullQualifiedName(final String name) {
		this.fullQualifiedName = name;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
		.append("fullQualifiedName", this.getFullQualifiedName()).append("elementType", this.getElementType())
		.toString();
	}
}
