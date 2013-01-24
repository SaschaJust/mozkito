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

package net.ownhero.dev.ioda.classpath.iterators;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import net.ownhero.dev.ioda.classpath.ClassPath;
import net.ownhero.dev.ioda.classpath.ClassPath.Element;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class LocalDirectoryIterator implements Iterator<Element> {
	
	private Enumeration<URL> resources = null;
	
	/**
 * 
 */
	public LocalDirectoryIterator(final ClassPath classPath) {
		// PRECONDITIONS
		
		try {
			try {
				final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				this.resources = classLoader.getResources(classPath.getPath());
				
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				if (Logger.logError()) {
					Logger.error(e);
				}
				
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return false;
			throw new RuntimeException("Method 'hasNext' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Element next() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'next' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'remove' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
