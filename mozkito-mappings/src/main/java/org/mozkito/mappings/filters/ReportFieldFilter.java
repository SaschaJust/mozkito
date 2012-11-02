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
package org.mozkito.mappings.filters;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.issues.tracker.elements.Type;
import org.mozkito.issues.tracker.model.EnhancedReport;
import org.mozkito.issues.tracker.settings.Messages;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;

/**
 * The Class ReportFieldFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportFieldFilter extends Filter {
	
	public static final class Options extends
	        ArgumentSetOptions<ReportFieldFilter, ArgumentSet<ReportFieldFilter, Options>> {
		
		private static final String        DESCRIPTION = Messages.getString("ReportFieldFilter.optionSetDescription");
		private static final String        TAG         = "reportField";
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
	 * @see org.mozkito.mappings.filters.Filter#filter(org.mozkito.mappings.model.Mapping)
	 */
	@Override
	public Mapping filter(final Mapping mapping) {
		// PRECONDITIONS
		
		try {
			if (!mapping.getComposite().getTo().get(FieldKey.TYPE).equals(this.type)) {
				mapping.addFilter(this);
			}
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.filters.MappingFilter#getDescription ()
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.filters.MappingFilter#supported()
	 */
	@Override
	public Expression supported() {
		return new Atom(Index.FROM, EnhancedReport.class);
	}
	
}
