/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.formatting;

import java.util.HashMap;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class StyleManager {
	
	private final HashMap<String, Style> styles = new HashMap<String, Style>();
	
	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public Style addStyle(final String key,
	                      final Style value) {
		return this.styles.put(key, value);
	}
	
	/**
	 * @param string
	 * @return
	 */
	public Style getStyle(final String string) {
		return this.styles.get(string);
	}
}
