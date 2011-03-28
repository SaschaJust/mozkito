/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import javax.persistence.Entity;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.hibernate.annotations.ForeignKey;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * The Class JavaElementDefinition.
 * 
 * @author Kim Herzig <kim@cs.uni-saarland.de>
 */
@Entity
@ForeignKey (name = "JAVA_ELEM_DEF")
public abstract class JavaElementDefinition extends JavaElement implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long   serialVersionUID = 1535115107166147270L;
	
	/**
	 * Instantiates a new java element definition.
	 */
	protected JavaElementDefinition() {
		super();
	}
	
	/**
	 * Instantiates a new java element definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 */
	@NoneNull
	public JavaElementDefinition(final String fullQualifiedName) {
		super(fullQualifiedName);
	}
	
	/**
	 * Gets the package name.
	 * 
	 * @return the package name
	 */
	@Override
	@Transient
	public String getPackageName() {
		if (!super.getFullQualifiedName().contains(".")) {
			return "";
		} else {
			int index = super.getFullQualifiedName().lastIndexOf(".");
			return super.getFullQualifiedName().substring(0, index);
		}
	}
	
	/**
	 * Gets the typed parent.
	 * 
	 * @return the typed parent
	 */
	@Transient
	public abstract JavaElementDefinition getTypedParent();
}
