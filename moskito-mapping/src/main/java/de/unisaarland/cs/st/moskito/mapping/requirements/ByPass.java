/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package de.unisaarland.cs.st.moskito.mapping.requirements;

import java.util.LinkedList;
import java.util.List;

import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ByPass extends Expression {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#check(java.lang.Class, java.lang.Class,
	 * de.unisaarland.cs.st.moskito.mapping.requirements.Index)
	 */
	@Override
	public boolean check(final Class<? extends MappableEntity> target1,
	                     final Class<? extends MappableEntity> target2,
	                     final Index oneEquals) {
		// PRECONDITIONS
		
		try {
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#getFailureCause(java.lang.Class,
	 * java.lang.Class, de.unisaarland.cs.st.moskito.mapping.requirements.Index)
	 */
	@Override
	public List<Expression> getFailureCause(final Class<? extends MappableEntity> target1,
	                                        final Class<? extends MappableEntity> target2,
	                                        final Index oneEquals) {
		// PRECONDITIONS
		
		try {
			return new LinkedList<>();
		} finally {
			// POSTCONDITIONS
		}
	}
}
