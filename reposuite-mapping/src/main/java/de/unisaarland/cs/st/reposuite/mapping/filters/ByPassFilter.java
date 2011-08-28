/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.filters;

import java.util.Set;

import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ByPassFilter extends MappingFilter {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.filters.MappingFilter#filter(de
	 * .unisaarland.cs.st.reposuite.mapping.model.PersistentMapping,
	 * java.util.Set)
	 */
	@Override
	public Set<MappingFilter> filter(final PersistentMapping mapping,
	                                 final Set<MappingFilter> triggeringFilters) {
		return triggeringFilters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.filters.MappingFilter#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Does not filter at all (dummy).";
		
	}
	
}
