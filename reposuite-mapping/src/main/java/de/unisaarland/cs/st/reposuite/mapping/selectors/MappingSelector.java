/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.selectors;

import java.util.List;

import de.unisaarland.cs.st.reposuite.mapping.elements.Candidate;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.register.Registered;

/**
 * Selectors analyze a {@link MappableEntity} and find possible candidates that
 * can be mapped to the entity, due to some relation.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingSelector extends Registered {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.register.Registered#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return isEnabled("mapping.selectors", this.getClass().getSimpleName());
	}
	
	/**
	 * @param entity
	 *            the element under subject
	 * @param targetType
	 *            the target entity type of the candidate
	 * @return a list of {@link Candidate}s that might be mapped to the given
	 *         entity
	 */
	public abstract <T extends MappableEntity> List<T> parse(MappableEntity entity,
	                                                         Class<T> targetType);
	
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
