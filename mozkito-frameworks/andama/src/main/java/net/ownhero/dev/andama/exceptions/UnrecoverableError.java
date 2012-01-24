/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import net.ownhero.dev.andama.utils.AndamaUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UnrecoverableError extends Error {
	
	private static final long serialVersionUID = -8156028538555027087L;
	
	/**
     * 
     */
	public UnrecoverableError() {
		super();
	}
	
	/**
	 * @param arg0
	 */
	public UnrecoverableError(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public UnrecoverableError(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public UnrecoverableError(final Throwable arg0) {
		super(arg0);
	}
	
	/**
	 * 
	 */
	public String analyzeFailureCause() {
		return null;
	}
	
	/**
	 * @param types
	 * @return
	 */
	protected String getArgumentString(final Class<?>[] types) {
		StringBuilder builder = new StringBuilder();
		
		for (Class<?> clazz : types) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(clazz.getCanonicalName());
		}
		
		return builder.toString();
	}
	
	/**
	 * @param types
	 * @return
	 */
	protected String getArgumentString(final Object[] types) {
		StringBuilder builder = new StringBuilder();
		
		for (Object object : types) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(object.getClass().getCanonicalName());
		}
		
		return builder.toString();
	}
	
	/**
	 * @param types
	 * @return
	 */
	protected String getArgumentString(final Type[] types) {
		StringBuilder builder = new StringBuilder();
		
		for (Type type : types) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(type);
		}
		
		return builder.toString();
	}
	
	/**
	 * @param constructor
	 * @return
	 */
	protected String getConstructorString(final Constructor<?> constructor) {
		StringBuilder builder = new StringBuilder();
		
		String modifier = getModifierString(constructor.getModifiers());
		if (modifier.length() > 0) {
			builder.append(modifier).append(" ");
		}
		
		builder.append(constructor.getName()).append('(');
		String arguments = getArgumentString(constructor.getGenericParameterTypes());
		builder.append(arguments).append(')');
		
		String exceptions = getArgumentString(constructor.getGenericExceptionTypes());
		if (exceptions.length() > 0) {
			builder.append(" throws ").append(exceptions);
		}
		
		return builder.toString();
	}
	
	/**
	 * @param name
	 * @param modifier
	 * @param arguments
	 * @return
	 */
	protected String getConstructorString(final String name,
	                                      final String modifier,
	                                      final Object[] arguments) {
		StringBuilder builder = new StringBuilder();
		
		if (modifier.length() > 0) {
			builder.append(modifier).append(" ");
		}
		
		builder.append(name).append('(');
		String argumentString = getArgumentString(arguments);
		builder.append(argumentString).append(')');
		
		return builder.toString();
	}
	
	protected String getModifierString(final int modifiers) {
		final StringBuilder builder = new StringBuilder();
		
		if ((modifiers & Modifier.PRIVATE) != 0) {
			builder.append("private ");
		} else if ((modifiers & Modifier.PUBLIC) != 0) {
			builder.append("public ");
		} else if ((modifiers & Modifier.PROTECTED) != 0) {
			builder.append("static ");
		}
		
		if ((modifiers & Modifier.STATIC) != 0) {
			builder.append("static ");
		}
		
		if ((modifiers & Modifier.FINAL) != 0) {
			builder.append("final ");
		}
		
		if ((modifiers & Modifier.ABSTRACT) != 0) {
			builder.append("abstract ");
		}
		
		if ((modifiers & Modifier.SYNCHRONIZED) != 0) {
			builder.append("synchronized ");
		}
		
		if ((modifiers & Modifier.VOLATILE) != 0) {
			builder.append("volatile ");
		}
		
		if ((modifiers & Modifier.TRANSIENT) != 0) {
			builder.append("transient ");
		}
		
		if ((modifiers & Modifier.NATIVE) != 0) {
			builder.append("native ");
		}
		
		return builder.toString();
	}
	
	protected String getSourceCode(final File file,
	                               final int lineNumber,
	                               final int contextSize) {
		final StringBuilder builder = new StringBuilder();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			
			builder.append("Source code: ").append(AndamaUtils.lineSeparator);
			
			try {
				int line = 1;
				String theLine = null;
				while ((line < (lineNumber - contextSize)) && ((theLine = reader.readLine()) != null)) {
					reader.readLine();
					++line;
				}
				
				final int charLength = (int) Math.log10(lineNumber + contextSize) + 1;
				
				while ((line <= (lineNumber + contextSize)) && ((theLine = reader.readLine()) != null)) {
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
		} catch (final FileNotFoundException e1) {
		}
		
		return builder.toString();
	}
	
}
