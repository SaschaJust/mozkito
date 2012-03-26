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
package de.unisaarland.cs.st.moskito.mapping.selectors;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.mapping.elements.Candidate;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.register.Node;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * Selectors analyze a {@link MappableEntity} and find possible candidates that can be mapped to the entity, due to some
 * relation.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingSelector extends Node {
	
	static class Options extends ArgumentSetOptions<Set<MappingSelector>, ArgumentSet<Set<MappingSelector>, Options>> {
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final String name, final String description,
		        final Requirement requirements) {
			super(argumentSet, name, description, requirements);
			// PRECONDITIONS
			
			try {
				// TODO Auto-generated constructor stub
				
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public Set<MappingSelector> init() {
			// PRECONDITIONS
			
			try {
				// TODO Auto-generated method stub
				return null;
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
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
		                                                                            SettingsParseError {
			// PRECONDITIONS
			
			try {
				// TODO Auto-generated method stub
				return null;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/**
	 * @param entity
	 *            the element under subject
	 * @param targetType
	 *            the target entity type of the candidate
	 * @return a list of {@link Candidate}s that might be mapped to the given entity
	 */
	public abstract <T extends MappableEntity> List<T> parse(MappableEntity entity,
	                                                         Class<T> targetType,
	                                                         PersistenceUtil util);
	
	/**
	 * @param from
	 *            the 'from' entity
	 * @param to
	 *            the 'to' entity
	 * @return true if the selector supports this combination of entities
	 */
	public abstract boolean supports(Class<?> from,
	                                 Class<?> to);
}
