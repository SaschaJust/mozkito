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

/**
 * The Class Or.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Or extends Element.Criterion {
	
	/** The criteria. */
	private final Element.Criterion[] criteria;
	
	/**
	 * Instantiates a new or.
	 * 
	 * @param criteria
	 *            the criteria
	 */
	public Or(final Element.Criterion... criteria) {
		this.criteria = criteria;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.ioda.classpath.ClassPath.Element.Criterion#accept(net.ownhero.dev.ioda.classpath.ClassPath.Element)
	 */
	@Override
	public boolean accept(final Element element) {
		if (this.criteria != null) {
			for (final Element.Criterion criterion : this.criteria) {
				if (criterion.accept(element)) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
}
