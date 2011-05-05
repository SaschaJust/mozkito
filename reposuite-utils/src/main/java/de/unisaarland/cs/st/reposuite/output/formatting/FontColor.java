package de.unisaarland.cs.st.reposuite.output.formatting;

public abstract class FontColor {
	
	/**
	 * @param color
	 */
	public FontColor(final BasicColor color) {
		setColor(color);
	}
	
	/**
	 * @param code
	 */
	public FontColor(final String code) {
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
