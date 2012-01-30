package net.ownhero.dev.ioda;

public class StringUtils {
	
	/**
	 * @param string
	 * @return
	 */
	public static final String truncate(final String string) {
		return truncate(string, 254);
	}
	
	/**
	 * @param string
	 * @param length
	 * @return
	 */
	public static final String truncate(final String string,
	                                    final int length) {
		return (string != null)
		                       ? string.substring(0, Math.min(string.length(), length))
		                       : "";
		
	}
}
