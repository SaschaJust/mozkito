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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgumentInterface;

/**
 * The and expression evaluates to true if and only if both inner expressions evaluate to true. Evaluates to false
 * otherwise.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class And extends Requirement {
	
	private final Requirement expression1;
	private final Requirement expression2;
	
	/**
	 * @param expression1
	 * @param expression2
	 */
	public And(final Requirement expression1, final Requirement expression2) {
		this.expression1 = expression1;
		this.expression2 = expression2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#check( java.lang.Class, java.lang.Class,
	 * de.unisaarland.cs.st.moskito.mapping.requirements.Index)
	 */
	@Override
	public boolean check() {
		return getExpression1().check() && getExpression2().check();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<AndamaArgumentInterface<?>> getDependencies() {
		HashSet<AndamaArgumentInterface<?>> dependencies = new HashSet<AndamaArgumentInterface<?>>();
		dependencies.addAll(this.expression1.getDependencies());
		dependencies.addAll(this.expression2.getDependencies());
		
		return dependencies;
	}
	
	/**
	 * @return the first expression
	 */
	public Requirement getExpression1() {
		return this.expression1;
	}
	
	/**
	 * @return the second expression
	 */
	public Requirement getExpression2() {
		return this.expression2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#getFailureCause (java.lang.Class,
	 * java.lang.Class, de.unisaarland.cs.st.moskito.mapping.requirements.Index)
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		List<Requirement> failureCause = this.expression1.getMissingRequirements();
		final boolean check = check();
		if (!check) {
			if (failureCause == null) {
				failureCause = this.expression2.getMissingRequirements();
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.expression1.toString() + " && " + this.expression2.toString() + ")";
	}
}
