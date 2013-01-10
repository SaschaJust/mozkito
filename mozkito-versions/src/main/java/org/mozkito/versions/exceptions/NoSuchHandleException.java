package org.mozkito.versions.exceptions;

/**
 * The Class NoSuchHandleException.
 */
public class NoSuchHandleException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = 7902755533383802066L;
	
	/**
	 * Create exception using format string.
	 * 
	 * @param formatString
	 *            the format string
	 * @param args
	 *            the args
	 * @return the no such handle exception
	 */
	public static NoSuchHandleException format(final String formatString,
	                                           final Object... args) {
		return new NoSuchHandleException(String.format(formatString, args));
	}
	
	/**
	 * Create exception using format string.
	 * 
	 * @param cause
	 *            the cause
	 * @param formatString
	 *            the format string
	 * @param args
	 *            the args
	 * @return the no such handle exception
	 */
	public static NoSuchHandleException format(final Throwable cause,
	                                           final String formatString,
	                                           final Object... args) {
		return new NoSuchHandleException(String.format(formatString, args), cause);
	}
	
	/**
	 * Instantiates a new no such handle exception.
	 */
	public NoSuchHandleException() {
		super();
	}
	
	/**
	 * Instantiates a new no such handle exception.
	 * 
	 * @param message
	 *            the message
	 */
	public NoSuchHandleException(final String message) {
		super(message);
	}
	
	/**
	 * Instantiates a new no such handle exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public NoSuchHandleException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Instantiates a new no such handle exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param enableSuppression
	 *            the enable suppression
	 * @param writableStackTrace
	 *            the writable stack trace
	 */
	public NoSuchHandleException(final String message, final Throwable cause, final boolean enableSuppression,
	        final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	/**
	 * Instantiates a new no such handle exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public NoSuchHandleException(final Throwable cause) {
		super(cause);
	}
	
}
