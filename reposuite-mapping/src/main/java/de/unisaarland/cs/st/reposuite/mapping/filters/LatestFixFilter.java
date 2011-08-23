package de.unisaarland.cs.st.reposuite.mapping.filters;

import java.util.Set;

import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LatestFixFilter extends MappingFilter {
	
	@Override
	public Set<MappingFilter> filter(final PersistentMapping mapping, final Set<MappingFilter> triggeringFilters) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getDescription() {
		return "Filters a mapping if the fix in the transaction wasn't the final fix (e.g. partial fix, reverted change, etc...)";
	}
	
}
