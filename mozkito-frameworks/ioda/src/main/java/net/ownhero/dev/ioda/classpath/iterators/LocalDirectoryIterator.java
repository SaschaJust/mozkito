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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.classpath.ClassPath;
import net.ownhero.dev.ioda.classpath.ClassPath.Element;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Source;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Type;
import net.ownhero.dev.ioda.classpath.elements.CompilationUnit;
import net.ownhero.dev.ioda.classpath.elements.Resource;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.io.FilenameUtils;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class LocalDirectoryIterator implements Iterator<Element> {
	
	private Enumeration<URL> resources = null;
	private ClassPath        classPath;
	private Element          current   = null; ;
	
	/**
 * 
 */
	public LocalDirectoryIterator(final ClassPath classPath) {
		// PRECONDITIONS
		
		try {
			try {
				final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				this.resources = classLoader.getResources(classPath.getPath());
				this.classPath = classPath;
				setNext();
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
			return this.current != null;
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
			final Element next = this.current;
			setNext();
			return next;
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
			throw new UnsupportedOperationException();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	private void setNext() {
		this.current = null;
		LOOP: while (this.resources.hasMoreElements()) {
			final URL url = this.resources.nextElement();
			final String path = url.getPath();
			final File file = new File(path);
			if (file.isFile()) {
				final String extension = FilenameUtils.getExtension(file.getAbsolutePath());
				switch (extension) {
					case ".class":
						this.current = new CompilationUnit(url.getFile().replace(FileUtils.fileSeparator, "."),
						                                   file.getAbsolutePath(), Type.COMPILATION_UNIT, Source.LOCAL,
						                                   this.classPath);
						break;
					default:
						this.current = new Resource(FilenameUtils.getBaseName(url.getFile()), file.getAbsolutePath(),
						                            Type.RESOURCE, Source.LOCAL, this.classPath);
						break;
				}
				break LOOP;
			}
		}
	}
	
}
