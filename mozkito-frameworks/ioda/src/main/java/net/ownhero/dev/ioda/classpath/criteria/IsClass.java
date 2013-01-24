/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package net.ownhero.dev.ioda.classpath.criteria;

import net.ownhero.dev.ioda.classpath.ClassPath.Element;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Type;

/**
 * The Class IsClass.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class IsClass extends Element.Criterion {
	
	/**
	 * Accept.
	 * 
	 * @param element
	 *            the element
	 * @return true, if successful
	 */
	@Override
	public boolean accept(final Element element) {
		// PRECONDITIONS
		
		try {
			return (Type.COMPILATION_UNIT.equals(element.getTYPE()));
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
