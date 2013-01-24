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

package net.ownhero.dev.ioda.classpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import net.ownhero.dev.ioda.classpath.ClassPath.Element.Criterion;
import net.ownhero.dev.ioda.classpath.criteria.And;
import net.ownhero.dev.ioda.classpath.criteria.IsClass;
import net.ownhero.dev.ioda.classpath.exceptions.ElementLoadingException;

import org.apache.commons.io.FilenameUtils;

/**
 * The Class ClassPath.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public final class ClassPath {
	
	/**
	 * The Class ClassPathElement.
	 */
	public static abstract class Element {
		
		/**
		 * The Class Criterion.
		 */
		public static abstract class Criterion {
			
			/**
			 * Accept.
			 * 
			 * @param element
			 *            the element
			 * @return true, if successful
			 */
			public abstract boolean accept(Element element);
		}
		
		/**
		 * The Enum SOURCE.
		 */
		public static enum Source {
			
			/** The local. */
			LOCAL,
			/** The url. */
			URL;
		}
		
		/**
		 * The Enum TYPE.
		 */
		public static enum Type {
			
			/** The compilation unit. */
			COMPILATION_UNIT,
			/** The resource. */
			RESOURCE;
		}
		
		/** The name. */
		private final String    name;
		
		/** The path. */
		private final String    path;
		
		/** The type. */
		private final Type      Type;
		
		/** The source. */
		private final Source    source;
		
		/** The class path. */
		private final ClassPath classPath;
		
		/**
		 * Instantiates a new element.
		 * 
		 * @param name
		 *            the name
		 * @param path
		 *            the path
		 * @param type
		 *            the t ype
		 * @param source
		 *            the source
		 * @param classPath
		 *            the class path
		 */
		public Element(final String name, final String path, final Type type, final Source source,
		        final ClassPath classPath) {
			super();
			this.name = name;
			this.path = path;
			this.Type = type;
			this.source = source;
			this.classPath = classPath;
		}
		
		/**
		 * Gets the class path.
		 * 
		 * @return the classPath
		 */
		public final ClassPath getClassPath() {
			// PRECONDITIONS
			
			try {
				return this.classPath;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public final String getName() {
			// PRECONDITIONS
			
			try {
				return this.name;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/**
		 * Gets the path.
		 * 
		 * @return the path
		 */
		public final String getPath() {
			// PRECONDITIONS
			
			try {
				return this.path;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/**
		 * Gets the source.
		 * 
		 * @return the source
		 */
		public final Source getSource() {
			// PRECONDITIONS
			
			try {
				return this.source;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/**
		 * Gets the type.
		 * 
		 * @return the tYPE
		 */
		public final Type getTYPE() {
			// PRECONDITIONS
			
			try {
				return this.Type;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/**
		 * Load.
		 * 
		 * @return the object
		 * @throws ElementLoadingException
		 *             the element loading exception
		 */
		public abstract Object load() throws ElementLoadingException;
	}
	
	/**
	 * The Interface ElementIterator.
	 */
	public static interface ElementIterator extends java.util.Iterator<Element> {
		
		/**
		 * Load.
		 * 
		 * @param element
		 *            the element
		 * @return the object
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		Class<?> loadCompilationUnit(Element element) throws IOException;
		
		/**
		 * Load resource.
		 * 
		 * @param element
		 *            the element
		 * @return the object
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		InputStream loadResource(Element element) throws IOException;
	}
	
	/**
	 * The Class ClassPathIterator.
	 */
	private static class Iterator implements java.util.Iterator<Element> {
		
		/** The iterator. */
		private final java.util.Iterator<ClassPath> iterator;
		
		/** The current. */
		private ClassPath                           current         = null;
		
		/** The current iterator. */
		private java.util.Iterator<Element>         currentIterator = null;
		
		/**
		 * Instantiates a new iterator.
		 */
		public Iterator() {
			this.iterator = SOURCES.iterator();
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
				while (true) {
					if ((this.currentIterator != null) && this.currentIterator.hasNext()) {
						return true;
					} else {
						if (this.iterator.hasNext()) {
							this.current = this.iterator.next();
							assert this.current != null;
							this.currentIterator = this.current.sourceIterator();
						} else {
							return false;
						}
					}
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
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				
				return this.currentIterator.next();
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
				throw new UnsupportedOperationException("Removing elements from the class path is not possible.");
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/**
	 * The Enum KIND.
	 */
	public static enum Kind {
		/** The directory. */
		LOCAL_DIRECTORY,
		/** The class. */
		URL_CLASS,
		/** The local jar. */
		LOCAL_JAR,
		/** The url jar. */
		URL_JAR;
	}
	
	/** The Constant CLASS_PATHS. */
	private static final Collection<ClassPath> SOURCES            = new HashSet<>();
	
	/** The Constant EXTERNAL_RESOURCES. */
	private static final Set<URL>              EXTERNAL_RESOURCES = new HashSet<>();
	
	static {
		final String[] CLASS_PATHS = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
		
		for (final String classPathString : CLASS_PATHS) {
			assert classPathString != null;
			
			if ("jar".equals(FilenameUtils.getExtension(classPathString).toLowerCase())) {
				// get elements from JAR resource
				ClassPath.SOURCES.add(new ClassPath(classPathString, Kind.LOCAL_JAR));
				
			} else {
				ClassPath.SOURCES.add(new ClassPath(classPathString, Kind.LOCAL_DIRECTORY));
				
			}
		}
		
		// this should be redundant
		for (final URL resource : EXTERNAL_RESOURCES) {
			final String path = resource.getPath();
			assert path != null;
			
			final Kind kind = urlToKind(resource);
			if (kind != null) {
				ClassPath.SOURCES.add(new ClassPath(path, kind));
			}
		}
	}
	
	/**
	 * Adds the external resource.
	 * 
	 * @param resource
	 *            the resource
	 * @return true, if successful
	 */
	public static final synchronized boolean addExternalResource(final URL resource) {
		boolean ret = EXTERNAL_RESOURCES.add(resource);
		if (ret) {
			final Kind kind = urlToKind(resource);
			if (kind != null) {
				ret &= SOURCES.add(new ClassPath(resource.getPath(), kind));
				if (!ret) {
					EXTERNAL_RESOURCES.remove(resource);
					return false;
				}
			} else {
				EXTERNAL_RESOURCES.remove(resource);
				return false;
			}
		}
		return ret;
	}
	
	/**
	 * Find classes.
	 * 
	 * @return the class[]
	 */
	public static Collection<Element> findClasses() {
		return findClasses(null);
	}
	
	/**
	 * Find classes.
	 * 
	 * @param criterion
	 *            the criterion
	 * @return the class[]
	 */
	public static Collection<Element> findClasses(final Criterion criterion) {
		final java.util.Iterator<Element> iterator = iterator();
		final List<Element> classes = new LinkedList<>();
		Criterion localCriterion = null;
		
		if (criterion != null) {
			localCriterion = new And(new IsClass(), criterion);
		} else {
			localCriterion = new IsClass();
		}
		
		while (iterator.hasNext()) {
			final Element element = iterator.next();
			assert element != null;
			if (localCriterion.accept(element)) {
				classes.add(element);
			}
		}
		
		return classes;
	}
	
	/**
	 * Iterator.
	 * 
	 * @return the iterator
	 */
	private static java.util.Iterator<Element> iterator() {
		return new Iterator();
	}
	
	/**
	 * Url to kind.
	 * 
	 * @param resource
	 *            the resource
	 * @return the kind
	 */
	private static Kind urlToKind(final URL resource) {
		if ("jar".equals(FilenameUtils.getExtension(resource.getPath()).toLowerCase())) {
			// get elements from a JAR file at the given URL
			return Kind.URL_JAR;
			
		} else if ("class".equals(FilenameUtils.getExtension(resource.getPath()).toLowerCase())) {
			// get element from class file at given URL
			return Kind.URL_CLASS;
		} else {
			// this is not supported.
		}
		
		return null;
	}
	
	/** The kind. */
	private final Kind   kind;
	
	/** The path. */
	private final String path;
	
	/**
	 * Instantiates a new class path.
	 * 
	 * @param path
	 *            the path
	 * @param kind
	 *            the kind
	 */
	private ClassPath(final String path, final Kind kind) {
		super();
		this.path = path;
		this.kind = kind;
	}
	
	/**
	 * Gets the kind.
	 * 
	 * @return the kind
	 */
	public final Kind getKind() {
		// PRECONDITIONS
		
		try {
			return this.kind;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public final String getPath() {
		// PRECONDITIONS
		
		try {
			return this.path;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Source iterator.
	 * 
	 * @return the java.util. iterator
	 */
	private final java.util.Iterator<Element> sourceIterator() {
		return null;
	}
	
}
