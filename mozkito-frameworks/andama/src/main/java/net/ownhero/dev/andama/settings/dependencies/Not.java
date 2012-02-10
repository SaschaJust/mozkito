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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgumentInterface;

/**
 * The not expression evaluates to true if the innerexpression evaluates to false. Evaluates to true otherwise.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Not extends Requirement {
	
	private final Requirement requirement;
	
	/**
	 * @param requirement
	 *            the inner expression used in the evaluation
	 */
	public Not(final Requirement requirement) {
		this.requirement = requirement;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean check() {
		return !this.requirement.check();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<AndamaArgumentInterface<?>> getDependencies() {
		return this.requirement.getDependencies();
	}
	
	/**
	 * @return the inner expression
	 */
	public Requirement getExpression() {
		return this.requirement;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getFailureCause()
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		final List<Requirement> failureCause = this.requirement.getMissingRequirements();
		
		if (failureCause == null) {
			return check()
			              ? null
			              : new LinkedList<Requirement>() {
				              
				              private static final long serialVersionUID = 1L;
				              
				              {
					              add(Not.this);
				              }
			              };
		} else {
			return check()
			              ? null
			              : failureCause;
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#toString()
	 */
	@Override
	public String toString() {
		return "!" + this.requirement.toString();
	}
}
