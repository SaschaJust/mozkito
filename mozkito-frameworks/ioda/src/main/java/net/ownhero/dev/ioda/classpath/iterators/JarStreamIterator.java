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
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import net.ownhero.dev.ioda.classpath.ClassPath;
import net.ownhero.dev.ioda.classpath.ClassPath.Element;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Source;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Type;
import net.ownhero.dev.ioda.classpath.ClassPath.ElementIterator;
import net.ownhero.dev.ioda.classpath.elements.CompilationUnit;
import net.ownhero.dev.ioda.classpath.elements.Resource;
import net.ownhero.dev.ioda.classpath.exceptions.ElementLoadingException;
import net.ownhero.dev.ioda.classpath.exceptions.GenericClassPathException;

import org.apache.commons.io.FilenameUtils;

/**
 * The Class JarStreamIterator.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class JarStreamIterator implements ElementIterator {
	
	/** The input stream. */
	private JarInputStream inputStream;
	
	/** The current. */
	private JarEntry       current;
	
	/** The class path. */
	private ClassPath      classPath;
	
	/** The entry name. */
	private String         entryName;
	
	/**
	 * Instantiates a new jar stream iterator.
	 * 
	 * @param classPath
	 *            the class path
	 * @param entryName
	 *            the entry name
	 * @param inputStream
	 *            the input stream
	 */
	public JarStreamIterator(final ClassPath classPath, final String entryName, final JarInputStream inputStream) {
		// PRECONDITIONS
		
		try {
			this.inputStream = inputStream;
			this.classPath = classPath;
			this.entryName = entryName;
			setNext();
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
			return this.current != null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.ioda.classpath.ClassPath.ElementIterator#loadCompilationUnit(net.ownhero.dev.ioda.classpath.ClassPath.Element)
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
				return unit.load();
			} catch (final ElementLoadingException e) {
				throw new IOException(e);
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
			
			final Resource resource = (Resource) element;
			
			try {
				return resource.load();
			} catch (final ElementLoadingException e) {
				throw new IOException(e);
			}
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
			if (this.current == null) {
				throw new NoSuchElementException();
			}
			
			final JarEntry next = this.current;
			setNext();
			final String name = next.getName();
			final String extension = FilenameUtils.getExtension(name);
			switch (extension) {
				case ".class":
					return new CompilationUnit(name, this.classPath.getPath() + "!" + this.entryName,
					                           Type.COMPILATION_UNIT, Source.STREAM, this.classPath);
				default:
					return new Resource(name, this.classPath.getPath() + "!" + this.entryName, Type.RESOURCE,
					                    Source.STREAM, this.classPath);
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
			throw new UnsupportedOperationException("Removing entities from the classpath is not supported.");
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the next.
	 */
	private void setNext() {
		
		try {
			this.current = this.inputStream.getNextJarEntry();
		} catch (final IOException e) {
			throw new GenericClassPathException("Could not fetch next JAR element from input stream.", e);
		}
	}
}
