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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.classpath.ClassPath;
import net.ownhero.dev.ioda.classpath.ClassPath.Element;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Source;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Type;
import net.ownhero.dev.ioda.classpath.elements.CompilationUnit;
import net.ownhero.dev.ioda.classpath.elements.Resource;

import org.apache.commons.io.FilenameUtils;

/**
 * The Class UrlJarIterator.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class UrlJarIterator implements Iterator<Element> {
	
	/** The url. */
	private URL             url;
	
	/** The stream. */
	private JarInputStream  stream;
	
	/** The next entry. */
	private JarEntry        nextEntry = null;
	
	/** The jar in jar. */
	private final boolean   jarInJar  = false;
	
	/** The class path. */
	private final ClassPath classPath;
	
	/**
	 * Instantiates a new url jar iterator.
	 * 
	 * @param classPath
	 *            the class path
	 */
	public UrlJarIterator(final ClassPath classPath) {
		this.classPath = classPath;
		try {
			this.url = new URL(classPath.getPath());
			this.stream = new JarInputStream(this.url.openStream());
			setNext();
		} catch (final IOException e) {
			// TODO error
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
			return this.nextEntry != null;
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
			
			final JarEntry current = this.nextEntry;
			if (current == null) {
				throw new NoSuchElementException();
			}
			
			setNext();
			final String currentName = current.getName();
			
			final String extension = FilenameUtils.getExtension(currentName).toLowerCase();
			final String fqn = FilenameUtils.removeExtension(currentName).replace(FileUtils.pathSeparator, ".");
			
			if ("jar".equals(extension)) {
				assert this.jarInJar;
				// TODO process this
				return null;
			} else if ("class".equals(extension)) {
				return new CompilationUnit(fqn, this.url.toString() + "$" + fqn, Type.COMPILATION_UNIT, Source.URL,
				                           this.classPath);
			} else {
				// resource
				return new Resource(currentName, this.url.toString() + "/" + currentName, Type.RESOURCE, Source.URL,
				                    this.classPath);
			}
			
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
			throw new UnsupportedOperationException("Removing elements from the class path is not possible");
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the next.
	 */
	private void setNext() {
		try {
			FIND_NEXT: while ((this.nextEntry = this.stream.getNextJarEntry()) != null) {
				if (!this.nextEntry.isDirectory()) {
					final String extension = FilenameUtils.getExtension(this.nextEntry.getName()).toLowerCase();
					if ("jar".equals(extension)) {
						if (this.jarInJar) {
							break FIND_NEXT;
						}
					} else {
						break FIND_NEXT;
					}
				}
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
		}
	}
	
}
