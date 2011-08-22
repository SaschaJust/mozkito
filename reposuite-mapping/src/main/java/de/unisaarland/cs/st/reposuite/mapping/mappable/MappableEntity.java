/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.mappable;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author just
 * 
 */
public abstract class MappableEntity implements Annotated {
	
	/**
     * 
     */
	private static final long serialVersionUID = 2350328785752088197L;
	
	public abstract Class<?> getBaseType();
	
	public abstract String getBodyText();
	
}
