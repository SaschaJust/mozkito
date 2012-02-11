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
package net.ownhero.dev.andama.settings.requirements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.IArgument;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The and expression evaluates to true if and only if both inner expressions
 * evaluate to true. Evaluates to false otherwise.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class And extends Requirement {
	
	private final Requirement requirement1;
	private final Requirement requirement2;
	
	/**
	 * @param requirement1
	 * @param requirement2
	 */
	public And(@NotNull final Requirement requirement1, @NotNull final Requirement requirement2) {
		try {
			this.requirement1 = requirement1;
			this.requirement2 = requirement2;
		} finally {
			Condition.notNull(this.requirement1, "Requirements in %s may never be null.", getHandle());
			Condition.notNull(this.requirement2, "Requirements in %s may never be null.", getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Requirement#check()
	 */
	@Override
	public boolean required() {
		return getRequirement1().required() && getRequirement2().required();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<IArgument<?>> getDependencies() {
		HashSet<IArgument<?>> dependencies = new HashSet<IArgument<?>>();
		try {
			dependencies.addAll(this.requirement1.getDependencies());
			dependencies.addAll(this.requirement2.getDependencies());
			
			return dependencies;
		} finally {
			Condition.notNull(dependencies, "Dependency values may never be null.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.requirements.Expression#getFailureCause
	 * (java.lang.Class, java.lang.Class,
	 * de.unisaarland.cs.st.moskito.mapping.requirements.Index)
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		List<Requirement> failureCause = this.requirement1.getMissingRequirements();
		final boolean check = required();
		if (!check) {
			if (failureCause == null) {
				failureCause = this.requirement2.getMissingRequirements();
				if (failureCause == null) {
					return check
					            ? null
					            : new LinkedList<Requirement>() {
						            
						            private static final long serialVersionUID = 1L;
						            
						            {
							            add(And.this);
						            }
					            };
				}
			}
		} else {
			return null;
		}
		
		return failureCause;
	}
	
	/**
	 * @return the first expression
	 */
	public Requirement getRequirement1() {
		return this.requirement1;
	}
	
	/**
	 * @return the second expression
	 */
	public Requirement getRequirement2() {
		return this.requirement2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.requirements.Expression#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.requirement1.toString() + " && " + this.requirement2.toString() + ")";
	}
}
