/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.formatting;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class BackgroundColor {
	
	/**
	 * @param color
	 */
	public BackgroundColor(final BasicColor color) {
		setColor(color);
	}
	
	/**
	 * @param code
	 */
	public BackgroundColor(final String code) {
		setColor(code);
	}
	
	/**
	 * @return
	 */
	public abstract String getColor();
	
	/**
	 * @param color
	 */
	public abstract void setColor(final BasicColor color);
	
	/**
	 * @param code
	 */
	public abstract void setColor(final String code);
}
