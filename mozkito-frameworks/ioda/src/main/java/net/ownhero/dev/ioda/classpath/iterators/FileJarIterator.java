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
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.classpath.ClassPath;
import net.ownhero.dev.ioda.classpath.ClassPath.Element;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Source;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Type;
import net.ownhero.dev.ioda.classpath.ClassPath.ElementIterator;
import net.ownhero.dev.ioda.classpath.elements.CompilationUnit;
import net.ownhero.dev.ioda.classpath.elements.Resource;
import net.ownhero.dev.ioda.classpath.exceptions.GenericClassPathException;

import org.apache.commons.io.FilenameUtils;

/**
 * The Class FileJarIterator.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class FileJarIterator implements ElementIterator {
	
	/** The class path. */
	private final ClassPath       classPath;
	
	/** The jar file. */
	private JarFile               jarFile;
	
	/** The next entry. */
	private JarEntry              nextEntry = null;
	
	/** The entries. */
	private Enumeration<JarEntry> entries;
	
	private final ClassLoader     classLoader;
	
	private JarStreamIterator     streamIterator;
	
	/**
	 * Instantiates a new file jar iterator.
	 * 
	 * @param classPath
	 *            the class path
	 */
	public FileJarIterator(final ClassPath classPath) {
		this.classPath = classPath;
		try {
			this.classLoader = URLClassLoader.newInstance(new URL[] { new URL("file://" + classPath.getPath()) });
			this.jarFile = new JarFile(classPath.getPath());
			this.entries = this.jarFile.entries();
			setNext();
		} catch (final IOException e) {
			throw new GenericClassPathException("Could not access JAR file at " + classPath.getPath() + ".", e);
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
	 * @throws IOException
	 * @see net.ownhero.dev.ioda.classpath.ClassPath.ElementIterator#load(net.ownhero.dev.ioda.classpath.ClassPath.Element)
	 */
	@Override
	public Class<?> loadCompilationUnit(final Element element) throws IOException {
		// PRECONDITIONS
		
		try {
			if (!(element instanceof CompilationUnit)) {
				throw new IllegalArgumentException();
			}
			
			final CompilationUnit unit = (CompilationUnit) element;
			
			try {
				
				final Class<?> clazz = this.classLoader.loadClass(unit.getName());
				unit.setClass(clazz);
				return clazz;
			} catch (final ClassNotFoundException e) {
				// class not found on the class path. try loading it from the JAr file manually
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.ioda.classpath.ClassPath.ElementIterator#loadResource(net.ownhero.dev.ioda.classpath.ClassPath.Element)
	 */
	@Override
	public InputStream loadResource(final Element element) throws IOException {
		// PRECONDITIONS
		
		try {
			if (!(element instanceof Resource)) {
				throw new IllegalArgumentException();
			}
			
			return this.classLoader.getResourceAsStream(element.getPath());
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
			final String fqn = FilenameUtils.removeExtension(currentName).replace(FileUtils.fileSeparator, ".");
			
			switch (extension) {
				case "jar":
					assert ClassPath.JAR_IN_JAR_ENABLED;
					assert this.streamIterator.hasNext();
					final Element retval = this.streamIterator.next();
					if (!this.streamIterator.hasNext()) {
						this.streamIterator = null;
					}
					return retval;
				case "class":
					return new CompilationUnit(fqn, this.classPath.getPath() + "!" + fqn, Type.COMPILATION_UNIT,
					                           Source.LOCAL, this.classPath);
				default:
					// resource
					return new Resource(currentName, this.classPath.getPath() + "/" + currentName, Type.RESOURCE,
					                    Source.LOCAL, this.classPath);
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
		assert this.entries != null;
		boolean found = false;
		
		// this means we either were not processing a JAR in JAR or there were no remaining entries.
		// look up the next entry in this JAR
		if (!found) {
			FIND_NEXT: while (this.entries.hasMoreElements()) {
				this.nextEntry = this.entries.nextElement();
				if (!this.nextEntry.isDirectory()) {
					final String extension = FilenameUtils.getExtension(this.nextEntry.getName()).toLowerCase();
					if ("jar".equals(extension)) {
						if (ClassPath.JAR_IN_JAR_ENABLED) {
							System.err.println("PROCESSING JAR IN JAR: " + this.classPath + " --> "
							        + this.nextEntry.getName());
							found = true;
							JarInputStream jarInputStream;
							try {
								jarInputStream = new JarInputStream(this.jarFile.getInputStream(this.nextEntry));
								this.streamIterator = new JarStreamIterator(this.classPath, this.nextEntry.getName(),
								                                            jarInputStream);
							} catch (final IOException e) {
								throw new GenericClassPathException("Cannot load JAR in JAR: "
								        + this.nextEntry.getName() + " in " + this.classPath.getPath(), e);
							}
							
							break FIND_NEXT;
						}
					} else {
						found = true;
						break FIND_NEXT;
					}
				}
			}
		}
		
		if (!found) {
			this.nextEntry = null;
		}
		
	}
	
}
