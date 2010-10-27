package de.unisaarland.cs.st.reposuite.utils;

import java.util.regex.Pattern;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RegexUtils {
	
	/**
	 * @param string
	 * @return
	 */
	public static boolean matches(String string) {
		return true;
	}
	
	private Pattern pattern;
	
	/**
	 * @return
	 */
	public int getGroupCount() {
		return 0;
	}
	
	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return this.pattern;
	}
	
	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
}
