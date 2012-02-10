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
package net.ownhero.dev.andama.settings.dependencies;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgumentInterface;
import net.ownhero.dev.ioda.JavaUtils;

import org.apache.commons.collections.CollectionUtils;

/**
 * The any expression evaluates to true if any of the inner expressions evaluate to true. Evaluates to false otherwise.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class Any extends Requirement {
	
	private final Set<Requirement> requirements = new HashSet<Requirement>();
	
	/**
	 * @param requirements
	 *            a collection of inner expressions
	 */
	public Any(final Collection<Requirement> requirements) {
		this.requirements.addAll(this.requirements);
	}
	
	/**
	 * @param expressions
	 *            sa collection of inner expressions
	 */
	public Any(final Requirement... expressions) {
		CollectionUtils.addAll(this.requirements, expressions);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#check( java.lang.Class, java.lang.Class,
	 * de.unisaarland.cs.st.moskito.mapping.requirements.Index)
	 */
	@Override
	public boolean check() {
		for (final Requirement requirement : this.requirements) {
			if (requirement.check()) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<AndamaArgumentInterface<?>> getDependencies() {
		HashSet<AndamaArgumentInterface<?>> dependencies = new HashSet<AndamaArgumentInterface<?>>();
		for (Requirement requirement : this.requirements) {
			dependencies.addAll(requirement.getDependencies());
		}
		return dependencies;
	}
	
	/**
	 * @return the expressions
	 */
	public final Set<Requirement> getExpressions() {
		return this.requirements;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#getFailureCause (java.lang.Class,
	 * java.lang.Class, de.unisaarland.cs.st.moskito.mapping.requirements.Index)
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		if (!check()) {
			return new LinkedList<Requirement>() {
				
				private static final long serialVersionUID = 1L;
				
				{
					addAll(getExpressions());
				}
			};
		} else {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#toString()
	 */
	@Override
	public String toString() {
		return "(∃ [true] : " + JavaUtils.collectionToString(this.requirements) + ")";
	}
}
