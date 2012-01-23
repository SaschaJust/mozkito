/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import net.ownhero.dev.andama.utils.AndamaUtils;
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
 * @author just
 * 
 */
public class ClassLoadingError extends UnrecoverableError {
	
	private final String      classPath;
	private final String      className;
	static private String     defaultMessage   = "";
	static final private int  suggestionCount  = 5;
	static final private int  contextSize      = 3;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6022478069512988369L;
	
	/**
	 * @param cause
	 * @param className
	 */
	public ClassLoadingError(final ClassNotFoundException cause, final String className) {
		this(defaultMessage, cause, className, System.getProperty("java.class.path"));
	}
	
	/**
	 * @param cause
	 * @param className
	 * @param classPath
	 */
	public ClassLoadingError(final ClassNotFoundException cause, final String className, final String classPath) {
		this(defaultMessage, cause, className, classPath);
	}
	
	/**
	 * @param cause
	 * @param className
	 */
	public ClassLoadingError(final LinkageError cause, final String className) {
		this(defaultMessage, cause, className, System.getProperty("java.class.path"));
	}
	
	/**
	 * @param cause
	 * @param className
	 * @param classPath
	 */
	public ClassLoadingError(final LinkageError cause, final String className, final String classPath) {
		this(defaultMessage, cause, className, classPath);
	}
	
	/**
	 * @param message
	 * @param cause
	 * @param className
	 */
	public ClassLoadingError(final String message, final ClassNotFoundException cause, final String className) {
		this(message, cause, className, System.getProperty("java.class.path"));
	}
	
	/**
	 * @param message
	 * @param cause
	 * @param className
	 * @param classPath
	 */
	public ClassLoadingError(final String message, final ClassNotFoundException cause, final String className,
			final String classPath) {
		super(message, cause);
		this.className = className;
		this.classPath = classPath;
	}
	
	/**
	 * @param message
	 * @param cause
	 * @param className
	 */
	public ClassLoadingError(final String message, final LinkageError cause, final String className) {
		this(message, cause, className, System.getProperty("java.class.path"));
	}
	
	/**
	 * @param message
	 * @param cause
	 * @param className
	 * @param classPath
	 */
	public ClassLoadingError(final String message, final LinkageError cause, final String className,
			final String classPath) {
		super(message, cause);
		this.className = className;
		this.classPath = classPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.exceptions.UnrecoverableError#analyzeFailureCause
	 * ()
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
				.append(AndamaUtils.lineSeparator);
				builder.append("Origin: ").append(element.toString()).append(AndamaUtils.lineSeparator);
				
				final Iterator<File> iterator = FileUtils.findFiles(new File("."), element.getFileName());
				BufferedReader reader = null;
				File file = null;
				while (iterator.hasNext()) {
					file = iterator.next();
					try {
						reader = new BufferedReader(new FileReader(file));
					} catch (final FileNotFoundException e) {
						builder.append("Source code providing failed for: ").append(file.getAbsolutePath())
						.append(AndamaUtils.lineSeparator);
					}
					break;
					
				}
				
				if (reader != null) {
					builder.append("Source code: ").append(AndamaUtils.lineSeparator);
					
					try {
						int line = 1;
						String theLine = null;
						while ((line < (element.getLineNumber() - contextSize))
								&& ((theLine = reader.readLine()) != null)) {
							reader.readLine();
							++line;
						}
						
						final int charLength = (int) Math.log10(element.getLineNumber() + contextSize) + 1;
						
						while ((line <= (element.getLineNumber() + contextSize))
								&& ((theLine = reader.readLine()) != null)) {
							++line;
							builder.append(String.format(" %-" + charLength + "s:  ", line));
							builder.append(theLine);
							builder.append(AndamaUtils.lineSeparator);
						}
						
					} catch (final IOException e) {
						builder.append("Source code providing failed while reading from file: ")
						.append(file != null
						? file.getAbsolutePath()
								: "(null)").append(AndamaUtils.lineSeparator);
					}
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
				try{
					classNames = ClassFinder.getAllClassNames(getClassPath());
				} catch (IOException e1) {
					builder.append("Error while reading class names in class path: " + e1.getMessage());
				}
				boolean contained = false;
				Set<String> sameName = new HashSet<String>();
				for (final String fqClassName : classNames) {
					if (fqClassName.equals(getClassName())) {
						// the class is in the class path but can't be found by
						// the classloader (for some reason. this should
						// actually not happen).
						contained = true;
						
						break;
					} else {
						String[] split = getClassName().split("\\.");
						if (fqClassName.endsWith("." + (split.length > 1
								? split[split.length - 1]
										: split[0]))) {
							sameName.add(fqClassName);
						}
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
							.append(AndamaUtils.lineSeparator);
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
												AndamaUtils.lineSeparator)));
						spellChecker.indexDictionary(dictionary, indexWriterConfig, true);
						
						final String fqClassName = getClassName();
						final String[] split = fqClassName.split("\\.");
						final String simpleClassName = split.length > 1
								? split[split.length - 1]
										: split[0];
								final String[] suggestions = spellChecker.suggestSimilar(simpleClassName, suggestionCount);
								
								final Set<String> fqSuggestions = new HashSet<String>();
								for (final String suggestion : sameName) {
									builder.append("  ").append(suggestion).append(AndamaUtils.lineSeparator);
								}
								for (final String suggestion : suggestions) {
									for (final String theClassName : classNames) {
										
										if (theClassName.endsWith("." + suggestion)) {
											fqSuggestions.add(theClassName);
										}
									}
								}
								
								for (final String fqSuggestion : fqSuggestions) {
									builder.append("  ").append(fqSuggestion).append(AndamaUtils.lineSeparator);
								}
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		} else if (cause instanceof UnrecoverableError) {
			builder.append("Failure raised from another " + UnrecoverableError.class.getSimpleName() + ". Cause:")
			.append(AndamaUtils.lineSeparator);
			builder.append(((UnrecoverableError) cause).analyzeFailureCause());
			
		} else if (cause instanceof LinkageError) {
			// if cause == LinkageError
			builder.append("Linkage error: " + cause.getMessage()).append(AndamaUtils.lineSeparator);
			for (final StackTraceElement element : cause.getStackTrace()) {
				builder.append(element.toString()).append(AndamaUtils.lineSeparator);
			}
		} else {
			builder.append("Could not determine failure cause.");
		}
		
		return builder.append(AndamaUtils.lineSeparator).toString();
	}
	
	/**
	 * @return
	 */
	public final String getClassName() {
		return this.className;
	}
	
	/**
	 * @return
	 */
	public final String getClassPath() {
		return this.classPath;
	}
}
