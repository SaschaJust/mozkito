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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.EnhancedReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.Messages;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.model.Composite;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;

/**
 * The Class ReportFieldFilter.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ReportFieldFilter extends Filter {
	
	public static final class Options extends
	        ArgumentSetOptions<ReportFieldFilter, ArgumentSet<ReportFieldFilter, Options>> {
		
		private static final String        TAG         = "reportField";
		private static final String        DESCRIPTION = "...";
		private EnumArgument.Options<Type> typeOption;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, TAG, DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public ReportFieldFilter init() {
			// PRECONDITIONS
			
			try {
				final EnumArgument<Type> typeArgument = getSettings().getArgument(this.typeOption);
				return new ReportFieldFilter(typeArgument.getValue());
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			
			try {
				final Map<String, IOptions<?, ?>> map = new HashMap<>();
				
				this.typeOption = new EnumArgument.Options<Type>(
				                                                 argumentSet,
				                                                 "type",
				                                                 Messages.getString("ReportFieldFilter.typeDescription"),
				                                                 getDefaultType(), Requirement.required);
				map.put(this.typeOption.getName(), this.typeOption);
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	private static final String DESCRIPTION = "Requires certain field values on reports to be mapped";
	
	/**
	 * @return
	 */
	public static final Type getDefaultType() {
		return Type.BUG;
	}
	
	private final Type type;
	
	/**
	 * @param type
	 */
	public ReportFieldFilter(final Type type) {
		this.type = type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#filter(de
	 * .unisaarland.cs.st.reposuite.mapping.model.PersistentMapping, java.util.Set)
	 */
	@Override
	public Set<? extends Filter> filter(final Composite composite,
	                                    final Set<Filter> triggeringFilters) {
		if (composite.getTo().get(FieldKey.TYPE).equals(this.type)) {
			triggeringFilters.add(this);
		}
		return triggeringFilters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#getDescription ()
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#supported()
	 */
	@Override
	public Expression supported() {
		return new Atom(Index.FROM, EnhancedReport.class);
	}
	
}
