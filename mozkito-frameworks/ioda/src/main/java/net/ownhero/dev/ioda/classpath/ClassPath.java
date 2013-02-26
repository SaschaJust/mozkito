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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import jregex.REFlags;
import net.ownhero.dev.ioda.classpath.ClassPath.Element.Criterion;
import net.ownhero.dev.ioda.classpath.classloaders.BootstrapClassloader;
import net.ownhero.dev.ioda.classpath.criteria.And;
import net.ownhero.dev.ioda.classpath.criteria.IsClass;
import net.ownhero.dev.ioda.classpath.criteria.Not;
import net.ownhero.dev.ioda.classpath.elements.CompilationUnit;
import net.ownhero.dev.ioda.classpath.elements.Resource;
import net.ownhero.dev.ioda.classpath.exceptions.ElementLoadingException;
import net.ownhero.dev.ioda.classpath.exceptions.GenericClassPathException;
import net.ownhero.dev.ioda.classpath.iterators.FileJarIterator;
import net.ownhero.dev.ioda.classpath.iterators.LocalDirectoryIterator;
import net.ownhero.dev.ioda.classpath.iterators.UrlClassIterator;
import net.ownhero.dev.ioda.classpath.iterators.UrlJarIterator;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

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
			URL,
			
			/** The stream. */
			STREAM;
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
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("Element [name=");
			builder.append(this.name);
			builder.append(", path=");
			builder.append(this.path);
			builder.append(", Type=");
			builder.append(this.Type);
			builder.append(", source=");
			builder.append(this.source);
			builder.append(", classPath=");
			builder.append(this.classPath);
			builder.append("]");
			return builder.toString();
		}
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
	
	/** The Constant MAVEN_VERSION_PATTERN. */
	private static final String                MAVEN_VERSION_PATTERN = "({ARTIFACTID}.+)-({MAJOR}[0-9]+)(\\.({MINOR}[0-9]+))?(\\.({INCREMENTAL}[0-9]+))?(-(({BUILD}[0-9]+)|({QUALIFIER}SNAPSHOT|RELEASE)))?";
	
	/** The jar in jar enabled. */
	public static boolean                      JAR_IN_JAR_ENABLED    = true;
	
	/** The Constant CLASS_PATHS. */
	private static final Collection<ClassPath> SOURCES               = new HashSet<>();
	
	/** The Constant EXTERNAL_RESOURCES. */
	private static final Set<URL>              EXTERNAL_RESOURCES    = new HashSet<>();
	
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
	public static Collection<CompilationUnit> findClasses() {
		return findClasses(null);
	}
	
	/**
	 * Find classes.
	 * 
	 * @param criterion
	 *            the criterion
	 * @return the class[]
	 */
	public static Collection<CompilationUnit> findClasses(final Criterion criterion) {
		final java.util.Iterator<Element> iterator = iterator();
		final List<CompilationUnit> classes = new LinkedList<>();
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
				assert element instanceof CompilationUnit;
				classes.add((CompilationUnit) element);
			}
		}
		
		return classes;
	}
	
	/**
	 * Gets the declaring class.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the declaring class
	 */
	private static Class<?> getDeclaringClass(final Class<?> clazz) {
		Class<?> declaring = clazz;
		while (declaring.getDeclaringClass() != null) {
			declaring = declaring.getDeclaringClass();
		}
		
		while (declaring.getEnclosingClass() != null) {
			declaring = declaring.getEnclosingClass();
		}
		
		return declaring;
	}
	
	/**
	 * @return
	 */
	public static Collection<Resource> getResources() {
		// PRECONDITIONS
		
		try {
			return getResources(null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param object
	 * @return
	 */
	private static Collection<Resource> getResources(final Criterion criterion) {
		final java.util.Iterator<Element> iterator = iterator();
		final List<Resource> resources = new LinkedList<>();
		Criterion localCriterion = null;
		
		if (criterion != null) {
			localCriterion = new And(new Not(new IsClass()), criterion);
		} else {
			localCriterion = new Not(new IsClass());
		}
		
		while (iterator.hasNext()) {
			final Element element = iterator.next();
			assert element != null;
			
			if (localCriterion.accept(element)) {
				assert element instanceof Resource;
				resources.add((Resource) element);
			}
		}
		
		return resources;
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
	
	/**
	 * Version.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the string
	 */
	public static String version(final Class<?> clazz) {
		final URL url = where(clazz);
		final String path = url.getPath();
		if (path.contains(".jar")) {
			final int to = path.lastIndexOf(".jar");
			final String lastJarPart = path.substring(0, to + ".jar".length());
			final String jarName = lastJarPart.substring(lastJarPart.lastIndexOf('/') + 1);
			
			final String baseName = FilenameUtils.getBaseName(jarName);
			final Regex regex = new Regex(MAVEN_VERSION_PATTERN, REFlags.IGNORE_CASE);
			final Match match = regex.find(baseName);
			if (match != null) {
				final StringBuilder builder = new StringBuilder();
				builder.append(match.getGroup("ARTIFACTID").getMatch()).append('-');
				builder.append(match.getGroup("MAJOR").getMatch());
				if (match.getGroup("MINOR") != null) {
					builder.append('.').append(match.getGroup("MINOR").getMatch());
					if (match.getGroup("INCREMENTAL") != null) {
						builder.append('.').append(match.getGroup("INCREMENTAL").getMatch());
					}
				}
				
				if (match.getGroup("BUILD") != null) {
					builder.append('-').append(match.getGroup("BUILD").getMatch());
				} else if (match.getGroup("QUALIFIER") != null) {
					builder.append('.').append(match.getGroup("QUALIFIER").getMatch());
				}
				
				return builder.toString();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Where.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the url
	 */
	public static URL where(final Class<?> clazz) {
		final Class<?> declaredClass = getDeclaringClass(clazz);
		final String fqn = declaredClass.getCanonicalName();
		assert fqn != null;
		
		final ClassLoader loader = who(clazz);
		assert loader != null : "ClassLoader must not be null for loaded classes.";
		return loader.getResource(fqn.replace('.', '/') + ".class");
		
	}
	
	/**
	 * Who.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the class loader
	 */
	public static ClassLoader who(final Class<?> clazz) {
		final ClassLoader loader = clazz.getClassLoader();
		if (loader == null) {
			return BootstrapClassloader.getInstance();
		} else {
			return loader;
		}
	}
	
	/** The loader. */
	private ClassLoader  loader;
	
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
		try {
			switch (kind) {
				case URL_JAR:
					this.loader = URLClassLoader.newInstance(new URL[] { new URL(path) });
					break;
				case LOCAL_DIRECTORY:
				case LOCAL_JAR:
					this.loader = URLClassLoader.newInstance(new URL[] { new URL("file://" + path) });
					break;
				case URL_CLASS:
					throw new GenericClassPathException("Not yet supported");
				default:
					throw new GenericClassPathException("Not yet supported");
			}
		} catch (final MalformedURLException e) {
			throw new GenericClassPathException(e);
		}
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
	 * Gets the loader.
	 * 
	 * @return the loader
	 */
	public final ClassLoader getLoader() {
		// PRECONDITIONS
		
		try {
			return this.loader;
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
		switch (this.kind) {
			case LOCAL_DIRECTORY:
				return new LocalDirectoryIterator(this);
			case LOCAL_JAR:
				return new FileJarIterator(this);
			case URL_JAR:
				return new UrlJarIterator(this);
			case URL_CLASS:
				return new UrlClassIterator(this);
			default:
				throw new IllegalArgumentException();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ClassPath [kind=");
		builder.append(this.kind);
		builder.append(", path=");
		builder.append(this.path);
		builder.append("]");
		return builder.toString();
	}
	
}
