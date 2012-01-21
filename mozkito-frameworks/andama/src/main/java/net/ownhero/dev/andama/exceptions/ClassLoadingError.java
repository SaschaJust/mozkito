/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ownhero.dev.andama.utils.AndamaUtils;
import net.ownhero.dev.ioda.ClassFinder;

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
	
	public ClassLoadingError(final String message, final Throwable t, final String className) {
		this(message, t, className, System.getProperty("java.class.path"));
	}
	
	public ClassLoadingError(final String message, final Throwable t, final String className, final String classPath) {
		super(message, t);
		this.className = className;
		this.classPath = classPath;
	}
	
	public ClassLoadingError(final Throwable t, final String className) {
		this(defaultMessage, t, className, System.getProperty("java.class.path"));
	}
	
	public ClassLoadingError(final Throwable t, final String className, final String classPath) {
		this(defaultMessage, t, className, classPath);
	}
	
	@Override
	public String analyzeFailureCause() {
		final StringBuilder builder = new StringBuilder();
		
		final Throwable cause = getCause();
		
		// LinkageError - if the linkage fails
		// ExceptionInInitializerError - if the initialization provoked by this
		// method fails
		// ClassNotFoundException - if the class cannot be located
		
		if (cause instanceof ExceptionInInitializerError) {
			final StackTraceElement[] trace = cause.getStackTrace();
			Throwable t = cause;
			final LinkedList<StackTraceElement> relevants = new LinkedList<StackTraceElement>();
			
			while (t != null) {
				final LinkedList<StackTraceElement> localRelevants = new LinkedList<StackTraceElement>();
				for (final StackTraceElement element : trace) {
					if (element.getClassName().equals(getClassName())) {
						localRelevants.addFirst(element);
					}
				}
				relevants.addAll(localRelevants);
				t = t.getCause();
			}
			
			final StackTraceElement element = relevants.pollLast();
			builder.append("The initialization provoked by loading the class '" + getClassName() + "' failed.")
			       .append(AndamaUtils.lineSeparator);
			builder.append("Origin: ").append(element.toString()).append(AndamaUtils.lineSeparator);
			
			final InputStream stream = Thread.currentThread().getContextClassLoader()
			                                 .getResourceAsStream(element.getFileName());
			if (stream != null) {
				builder.append("Source code: ").append(AndamaUtils.lineSeparator);
				
				final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				try {
					int line = 1;
					String theLine = null;
					while ((line < (element.getLineNumber() - contextSize)) && ((theLine = reader.readLine()) != null)) {
						reader.readLine();
						++line;
					}
					
					final int charLength = (int) Math.log10(element.getLineNumber() + contextSize);
					
					while ((line <= (element.getLineNumber() + contextSize)) && ((theLine = reader.readLine()) != null)) {
						builder.append("Line ").append(String.format("%0" + charLength + "d", line)).append(theLine)
						       .append(AndamaUtils.lineSeparator);
						++line;
					}
					
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				final Set<String> classNames = ClassFinder.getAllClassNames(getClassPath());
				boolean contained = false;
				for (final String fqClassName : classNames) {
					if (fqClassName.equals(getClassName())) {
						// the class is in the class path but can't be found by
						// the classloader (for some reason. this should
						// actually not happen).
						contained = true;
						
						break;
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
						for (final String suggestion : suggestions) {
							for (final String theClassName : classNames) {
								if (theClassName.endsWith("." + suggestion)) {
									fqSuggestions.add(theClassName);
								}
							}
						}
						
						for (final String fqSuggestion : fqSuggestions) {
							builder.append(fqSuggestion).append(AndamaUtils.lineSeparator);
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
