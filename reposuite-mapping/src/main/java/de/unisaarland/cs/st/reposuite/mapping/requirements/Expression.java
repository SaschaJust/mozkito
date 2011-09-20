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

public abstract class Expression {
	
	/**
	 * @param target1
	 * @param target2
	 * @return
	 */
	public int check(final Class<? extends MappableEntity> target1,
	                 final Class<? extends MappableEntity> target2) {
		if (check(target1, target2, Index.FROM) || check(target1, target2, Index.TO)) {
			return 1;
		} else if (check(target2, target1, Index.FROM) || check(target2, target1, Index.TO)) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * @param target1
	 * @param target2
	 * @param oneEquals
	 * @return
	 */
	public abstract boolean check(final Class<? extends MappableEntity> target1,
	                              final Class<? extends MappableEntity> target2,
	                              final Index oneEquals);
}
