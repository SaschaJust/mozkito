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
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.ownhero.dev.ioda.classpath.ClassPath;
import net.ownhero.dev.ioda.classpath.ClassPath.Element;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Source;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Type;
import net.ownhero.dev.ioda.classpath.elements.CompilationUnit;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * The Class UrlClassIterator.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class UrlClassIterator implements Iterator<Element> {
	
	/**
	 * The Class ABC.
	 */
	private static class URLSingleClassLoader extends ClassLoader {
		
		/**
		 * Load class.
		 * 
		 * @param url
		 *            the url
		 * @return the class
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		public Class<?> loadClass(final URL url) throws IOException {
			if (url == null) {
				throw new NullPointerException();
			}
			if (url.getPath() == null) {
				throw new NullPointerException();
			}
			
			final String path = url.getPath();
			assert path != null;
			final int i = path.lastIndexOf('/');
			assert i > 0;
			final String classFileName = path.substring(i);
			assert FilenameUtils.isExtension(classFileName, ".class");
			final String className = FilenameUtils.removeExtension(classFileName);
			final InputStream stream = url.openStream();
			final byte[] bs = IOUtils.toByteArray(stream);
			final Class<?> definedClass = defineClass(className, bs, 0, bs.length);
			return definedClass;
			
		}
	}
	
	/** The class path. */
	private ClassPath classPath;
	
	/** The url. */
	private URL       url;
	
	/** The clazz. */
	private Class<?>  clazz;
	
	/** The has next. */
	private boolean   hasNext = false;
	
	/**
	 * Instantiates a new url class iterator.
	 * 
	 * @param classPath
	 *            the class path
	 */
	public UrlClassIterator(final ClassPath classPath) {
		// PRECONDITIONS
		
		try {
			this.classPath = classPath;
			
			try {
				this.url = new URL(classPath.getPath());
				this.clazz = new URLSingleClassLoader().loadClass(this.url);
				this.hasNext = this.clazz != null;
			} catch (final IOException e) {
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
			return this.hasNext;
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
			if (!hasNext()) {
				throw new NoSuchElementException();
			} else {
				return new CompilationUnit(this.clazz.getName(), this.url.toString(), Type.COMPILATION_UNIT,
				                           Source.URL, this.classPath);
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
			throw new UnsupportedOperationException("Removing classes from the class path is not supported.");
		} finally {
			// POSTCONDITIONS
		}
	}
}
