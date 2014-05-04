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
package net.ownhero.dev.hiari.settings.requirements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.kanuni.conditions.Condition;

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
	Not(final Requirement requirement) {
		try {
			this.requirement = requirement;
		} finally {
			Condition.notNull(this.requirement, "Requirements in %s may never be null.", getHandle());
		}
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
	public Set<IOptions<?, ?>> getDependencies() {
		final Set<IOptions<?, ?>> dependencies = new HashSet<IOptions<?, ?>>();
		try {
			dependencies.addAll(this.requirement.getDependencies());
			return dependencies;
		} finally {
			Condition.notNull(dependencies, "Dependency values may never be null.");
		}
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
	public List<Requirement> getFailedChecks() {
		final List<Requirement> failureCause = this.requirement.getFailedChecks();
		
		if (failureCause.isEmpty()) {
			return check()
			              ? new ArrayList<Requirement>(0)
			              : new LinkedList<Requirement>() {
				              
				              private static final long serialVersionUID = 1L;
				              
				              {
					              add(Not.this);
				              }
			              };
		}
		return check()
		              ? new ArrayList<Requirement>(0)
		              : failureCause;
		
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
