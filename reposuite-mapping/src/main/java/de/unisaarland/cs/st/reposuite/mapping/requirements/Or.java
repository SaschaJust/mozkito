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
package de.unisaarland.cs.st.reposuite.mapping.requirements;

import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;

public class Or extends Expression {
	
	private final Expression e1;
	
	private final Expression e2;
	
	/**
	 * @param e1
	 * @param e2
	 */
	public Or(final Expression e1, final Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.requirements.Expression#check(
	 * java.lang.Class, java.lang.Class,
	 * de.unisaarland.cs.st.reposuite.mapping.requirements.Index)
	 */
	@Override
	public boolean check(final Class<? extends MappableEntity> target1,
	                     final Class<? extends MappableEntity> target2,
	                     final Index oneEquals) {
		return getE1().check(target1, target2, oneEquals) || getE2().check(target1, target2, oneEquals);
	}
	
	/**
	 * @return
	 */
	public Expression getE1() {
		return this.e1;
	}
	
	/**
	 * @return
	 */
	public Expression getE2() {
		return this.e2;
	}
}
