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
package net.ownhero.dev.andama.exceptions;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * The Class ClassLoadingError.
 * 
 * @author just
 */
public class ClassLoadingError extends UnrecoverableError {
	
	/** The class path. */
	private final String      classPath;
	
	/** The class name. */
	private final String      className;
	
	/** The default message. */
	static private String     defaultMessage   = "";
	
	/** The Constant suggestionCount. */
	static final private int  suggestionCount  = 5;
	
	/** The Constant contextSize. */
	static final private int  contextSize      = 3;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6022478069512988369L;
	
	/**
	 * Instantiates a new class loading error.
	 * 
	 * @param cause
	 *            the cause
	 * @param className
	 *            the class name
	 */
	public ClassLoadingError(final ClassNotFoundException cause, final String className) {
		this(defaultMessage, cause, className, System.getProperty("java.class.path"));
	}
	
	/**
	 * Instantiates a new class loading error.
	 * 
	 * @param cause
	 *            the cause
	 * @param className
	 *            the class name
	 * @param classPath
	 *            the class path
	 */
	public ClassLoadingError(final ClassNotFoundException cause, final String className, final String classPath) {
		this(defaultMessage, cause, className, classPath);
	}
	
	/**
	 * Instantiates a new class loading error.
	 * 
	 * @param cause
	 *            the cause
	 * @param className
	 *            the class name
	 */
	public ClassLoadingError(final LinkageError cause, final String className) {
		this(defaultMessage, cause, className, System.getProperty("java.class.path"));
	}
	
	/**
	 * Instantiates a new class loading error.
	 * 
	 * @param cause
	 *            the cause
	 * @param className
	 *            the class name
	 * @param classPath
	 *            the class path
	 */
	public ClassLoadingError(final LinkageError cause, final String className, final String classPath) {
		this(defaultMessage, cause, className, classPath);
	}
	
	/**
	 * Instantiates a new class loading error.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param className
	 *            the class name
	 */
	public ClassLoadingError(final String message, final ClassNotFoundException cause, final String className) {
		this(message, cause, className, System.getProperty("java.class.path"));
	}
	
	/**
	 * Instantiates a new class loading error.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param className
	 *            the class name
	 * @param classPath
	 *            the class path
	 */
	public ClassLoadingError(final String message, final ClassNotFoundException cause, final String className,
	        final String classPath) {
		super(message, cause);
		this.className = className;
		this.classPath = classPath;
	}
	
	/**
	 * Instantiates a new class loading error.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param className
	 *            the class name
	 */
	public ClassLoadingError(final String message, final LinkageError cause, final String className) {
		this(message, cause, className, System.getProperty("java.class.path"));
	}
	
	/**
	 * Instantiates a new class loading error.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param className
	 *            the class name
	 * @param classPath
	 *            the class path
	 */
	public ClassLoadingError(final String message, final LinkageError cause, final String className,
	        final String classPath) {
		super(message, cause);
		this.className = className;
		this.classPath = classPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.exceptions.UnrecoverableError#analyzeFailureCause ()
	 */
	@Override
	public String analyzeFailureCause() {
		final StringBuilder builder = new StringBuilder();
		
		final Throwable cause = getCause();
		
		// LinkageError - if the linkage fails
		// ExceptionInInitializerError - if the initialization provoked by this
		// method fails
		// ClassNotFoundException - if the class cannot be located
		
		if (cause instanceof ExceptionInInitializerError) {
			Throwable t = cause;
			final LinkedList<StackTraceElement> relevants = new LinkedList<StackTraceElement>();
			
			while (t != null) {
				final LinkedList<StackTraceElement> localRelevants = new LinkedList<StackTraceElement>();
				for (final StackTraceElement element : t.getStackTrace()) {
					final String elementClassName = element.getClassName();
					if (elementClassName.equals(getClassName())) {
						localRelevants.addFirst(element);
					}
				}
				relevants.addAll(localRelevants);
				t = t.getCause();
			}
			
			final StackTraceElement element = relevants.pollLast();
			
			if (element != null) {
				builder.append("The initialization provoked by loading the class '" + getClassName() + "' failed.")
				       .append(FileUtils.lineSeparator);
				builder.append("Origin: ").append(element.toString()).append(FileUtils.lineSeparator);
				
				final Iterator<File> iterator = FileUtils.findFiles(new File("."), element.getFileName());
				File file = null;
				
				if (iterator.hasNext()) {
					file = iterator.next();
					builder.append(getSourceCode(file, element.getLineNumber(), contextSize));
				}
			}
		} else if (cause instanceof ClassNotFoundException) {
			// try to find matching class in the classpath
			// if not found, list alternatives using minimal string distance
			// against all class names in the classpath
			// if found, then the class is in the class path but can't be found
			// by the classloader (for some reason. this should actually not
			// happen).
			
			if ((getClassName() == null) || getClassName().isEmpty()) {
				builder.append("ClassName '" + this.className + "' is invalid.");
			} else {
				Set<String> classNames = new HashSet<String>();
				try {
					classNames = ClassFinder.getAllClassNames(getClassPath());
				} catch (final IOException e1) {
					builder.append("Error while reading class names in class path: " + e1.getMessage());
				}
				boolean contained = false;
				final Set<String> sameName = new HashSet<String>();
				for (final String fqClassName : classNames) {
					if (fqClassName.equals(getClassName())) {
						// the class is in the class path but can't be found by
						// the classloader (for some reason. this should
						// actually not happen).
						contained = true;
						
						break;
					}
					final String[] split = getClassName().split("\\.");
					if (fqClassName.endsWith("." + (split.length > 1
					                                                ? split[split.length - 1]
					                                                : split[0]))) {
						sameName.add(fqClassName);
					}
				}
				
				if (contained) {
					builder.append("Class '"
					        + getClassName()
					        + "' is contained in the classpath but could not be found by the classloader (for some reason). Classpath: "
					        + getClassPath());
				} else {
					builder.append("Class '" + getClassName()
					                       + "' is not contained in the classpath. Did you mean one of these: ")
					       .append(FileUtils.lineSeparator);
					final Directory directory = new RAMDirectory();
					try {
						final Set<String> simpleClassNames = new HashSet<String>();
						for (final String fqClassName : classNames) {
							String simpleClassName;
							final String[] split = fqClassName.split("\\.");
							if (split.length > 1) {
								simpleClassName = split[split.length - 1];
							} else {
								simpleClassName = split[0];
							}
							
							simpleClassNames.add(simpleClassName);
						}
						final SpellChecker spellChecker = new SpellChecker(directory);
						final Analyzer analyzer = new org.apache.lucene.analysis.en.EnglishAnalyzer(Version.LUCENE_35);
						final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);
						final Dictionary dictionary = new PlainTextDictionary(
						                                                      new StringReader(
						                                                                       StringUtils.join(simpleClassNames,
						                                                                                        FileUtils.lineSeparator)));
						spellChecker.indexDictionary(dictionary, indexWriterConfig, true);
						
						final String fqClassName = getClassName();
						final String[] split = fqClassName.split("\\.");
						final String simpleClassName = split.length > 1
						                                               ? split[split.length - 1]
						                                               : split[0];
						final String[] suggestions = spellChecker.suggestSimilar(simpleClassName, suggestionCount);
						spellChecker.close();
						
						final Set<String> fqSuggestions = new HashSet<String>();
						for (final String suggestion : sameName) {
							builder.append("  ").append(suggestion).append(FileUtils.lineSeparator);
						}
						for (final String suggestion : suggestions) {
							for (final String theClassName : classNames) {
								
								if (theClassName.endsWith("." + suggestion)) {
									fqSuggestions.add(theClassName);
								}
							}
						}
						
						for (final String fqSuggestion : fqSuggestions) {
							builder.append("  ").append(fqSuggestion).append(FileUtils.lineSeparator);
						}
					} catch (final IOException e) {
						builder.append(e.getMessage());
					}
					
				}
				
			}
		} else if (cause instanceof UnrecoverableError) {
			builder.append("Failure raised from another " + UnrecoverableError.class.getSimpleName() + ". Cause:")
			       .append(FileUtils.lineSeparator);
			builder.append(((UnrecoverableError) cause).analyzeFailureCause());
			
		} else if (cause instanceof LinkageError) {
			// if cause == LinkageError
			builder.append("Linkage error: " + cause.getMessage()).append(FileUtils.lineSeparator);
			for (final StackTraceElement element : cause.getStackTrace()) {
				builder.append(element.toString()).append(FileUtils.lineSeparator);
			}
		} else {
			builder.append("Could not determine failure cause.");
		}
		
		return builder.append(FileUtils.lineSeparator).toString();
	}
	
	/**
	 * Gets the class name.
	 * 
	 * @return the class name
	 */
	public final String getClassName() {
		return this.className;
	}
	
	/**
	 * Gets the class path.
	 * 
	 * @return the class path
	 */
	public final String getClassPath() {
		return this.classPath;
	}
}
