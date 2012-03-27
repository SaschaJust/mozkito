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
package de.unisaarland.cs.st.moskito.mapping.filters;

import java.util.Set;

import net.ownhero.dev.hiari.settings.DynamicArgumentSet;
import de.unisaarland.cs.st.moskito.mapping.model.IMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class TransactionFieldFilter.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class TransactionFieldFilter extends MappingFilter {
	
	/**
	 * After parse.
	 */
	@Override
	public void afterParse() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#filter(de
	 * .unisaarland.cs.st.reposuite.mapping.model.PersistentMapping, java.util.Set)
	 */
	@Override
	public Set<? extends MappingFilter> filter(final IMapping mapping,
	                                           final Set<? extends MappingFilter> triggeringFilters) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#getDescription ()
	 */
	@Override
	public String getDescription() {
		return "Requires certain field values on transactions to be mapped";
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
	 */
	/**
	 * Inits the settings.
	 *
	 * @param set the set
	 * @return true, if successful
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
