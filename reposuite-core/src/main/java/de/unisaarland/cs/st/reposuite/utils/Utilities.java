/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

/**
 * @author just
 * 
 */
public final class Utilities {
	
	public static Integer getLineNumber() {
		Throwable throwable = new Throwable();
		assert (throwable != null);
		
		throwable.fillInStackTrace();
		assert (throwable.getStackTrace().length > 1);
		
		Integer i = throwable.getStackTrace()[1].getLineNumber();
		
		assert (i != null);
		assert (i > 0);
		
		return i;
	}
}
