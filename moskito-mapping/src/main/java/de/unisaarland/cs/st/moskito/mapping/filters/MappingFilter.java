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

import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.register.Node;
import de.unisaarland.cs.st.moskito.mapping.settings.MappingSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingFilter extends Node {
	
	private final boolean   initialized = false;
	private final boolean   registered  = false;
	private MappingSettings settings;
	
	public MappingFilter() {
		
	}
	
	/**
	 * @param transaction
	 * @param report
	 * @param score
	 */
	@NoneNull
	public abstract Set<? extends MappingFilter> filter(final Mapping mapping,
	                                                    Set<? extends MappingFilter> triggeringFilters);
	
	/**
	 * @return
	 */
	@Override
	public abstract String getDescription();
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Registered#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		if (getSettings() != null) {
			final ListArgument setting = (ListArgument) getSettings().getSetting("mapping.filters");
			return setting.getValue().contains(this.getClass().getSimpleName());
		} else {
			return true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("MappingFilter [settings=");
		builder.append(this.settings);
		builder.append(", registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append("]");
		return builder.toString();
	}
	
}
