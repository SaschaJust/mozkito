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
 * The all expression evaluates to true if and only if all checks on the inner expressions evaluate to true. Evaluates
 * to false otherwise.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class All extends Requirement {
	
	private final Set<Requirement> requirements = new HashSet<Requirement>();
	
	/**
	 * @param requirements
	 *            a collection of inner expressions
	 */
	public All(final Collection<Requirement> requirements) {
		this.requirements.addAll(this.requirements);
	}
	
	/**
	 * @param expressions
	 *            a collection of inner expressions
	 */
	public All(final Requirement... expressions) {
		CollectionUtils.addAll(this.requirements, expressions);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Requirement#check()
	 */
	@Override
	public boolean check() {
		for (final Requirement requirement : this.requirements) {
			if (!requirement.check()) {
				return false;
			}
		}
		return true;
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
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Requirement# getMissingRequirements()
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		if (!check()) {
			final List<Requirement> list = new LinkedList<Requirement>();
			for (final Requirement requirement : this.requirements) {
				final List<Requirement> failureCause = requirement.getMissingRequirements();
				if (failureCause != null) {
					list.addAll(failureCause);
				}
			}
			
			return list;
			
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
		return "(∀ [true] : " + JavaUtils.collectionToString(this.requirements) + ")";
	}
	
}
