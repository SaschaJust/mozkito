/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
