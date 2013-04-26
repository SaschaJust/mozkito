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

package org.mozkito.utilities.loading.classpath.criteria;


import org.apache.commons.io.FilenameUtils;

import org.mozkito.utilities.loading.classpath.ClassPath.Element;
import org.mozkito.utilities.loading.classpath.ClassPath.Element.Criterion;

/**
 * The Class InPackage.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class InPackage extends Criterion {
	
	/** The pakkage. */
	private final Package pakkage;
	
	/** The recursive. */
	private final boolean recursive;
	
	/**
	 * Instantiates a new in package.
	 * 
	 * @param pakkage
	 *            the pakkage
	 * @param recursive
	 *            the recursive
	 */
	public InPackage(final Package pakkage, final boolean recursive) {
		this.pakkage = pakkage;
		this.recursive = recursive;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.loading.classpath.ClassPath.Element.Criterion#accept(org.mozkito.utilities.loading.classpath.ClassPath.Element)
	 */
	@Override
	public boolean accept(final Element element) {
		// PRECONDITIONS
		
		try {
			final String packageName = FilenameUtils.removeExtension(element.getName());
			assert packageName != null;
			
			if (this.recursive) {
				return packageName.equals(this.pakkage.getName())
				        || packageName.startsWith(this.pakkage.getName() + ".");
			} else {
				return packageName.equals(this.pakkage.getName());
			}
			
		} finally {
			// POSTCONDITIONS
		}
	}
}
