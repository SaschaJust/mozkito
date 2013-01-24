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
import java.net.URL;
import java.net.URLClassLoader;

import net.ownhero.dev.ioda.classpath.ClassPath;
import net.ownhero.dev.ioda.classpath.ClassPath.Element;
import net.ownhero.dev.ioda.classpath.exceptions.ElementLoadingException;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class CompilationUnit extends Element {
	
	Class<?> clazz = null;
	
	/**
	 * @param name
	 * @param path
	 * @param type
	 * @param source
	 * @param classPath
	 */
	public CompilationUnit(final String name, final String path, final Type type, final Source source,
	        final ClassPath classPath) {
		super(name, path, type, source, classPath);
		
	}
	
	/**
	 * Load.
	 * 
	 * @return the class
	 * @throws ElementLoadingException
	 *             the element loading exception
	 */
	@Override
	public Class<?> load() throws ElementLoadingException {
		if (this.clazz == null) {
			switch (getSource()) {
				case URL:
					try {
						final URLClassLoader loader = new URLClassLoader(new URL[] { new URL(getPath()) });
						this.clazz = loader.loadClass(getName());
						loader.close();
					} catch (final IOException | ClassNotFoundException e) {
						throw new ElementLoadingException("Invalid URL for class " + getName() + ".", e);
					}
					break;
				default:
					try {
						this.clazz = Class.forName(getName());
					} catch (final ClassNotFoundException e) {
						throw new ElementLoadingException(e);
					}
			}
		}
		
		return this.clazz;
	}
	
	/**
	 * Sets the class.
	 * 
	 * @param clazz
	 *            the new class
	 */
	public void setClass(final Class<?> clazz) {
		this.clazz = clazz;
	}
}
