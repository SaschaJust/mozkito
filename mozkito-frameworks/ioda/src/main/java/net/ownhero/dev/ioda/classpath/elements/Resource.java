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

package net.ownhero.dev.ioda.classpath.elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.ownhero.dev.ioda.classpath.ClassPath;
import net.ownhero.dev.ioda.classpath.ClassPath.Element;
import net.ownhero.dev.ioda.classpath.exceptions.ElementLoadingException;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Resource extends Element {
	
	/**
	 * @param name
	 * @param path
	 * @param type
	 * @param source
	 * @param classPath
	 */
	public Resource(final String name, final String path,
	        final net.ownhero.dev.ioda.classpath.ClassPath.Element.Type type, final Source source,
	        final ClassPath classPath) {
		super(name, path, type, source, classPath);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.ioda.classpath.ClassPath.Element#load()
	 */
	@Override
	public InputStream load() throws ElementLoadingException {
		// PRECONDITIONS
		
		try {
			switch (getSource()) {
				case URL:
					try {
						final URL url = new URL(getPath());
						final InputStream stream = url.openStream();
						return stream;
					} catch (final IOException e) {
						throw new ElementLoadingException(e);
					}
					
				default:
					// TODO check JAR/file
					return null;
			}
			
		} finally {
			// POSTCONDITIONS
		}
	}
}
