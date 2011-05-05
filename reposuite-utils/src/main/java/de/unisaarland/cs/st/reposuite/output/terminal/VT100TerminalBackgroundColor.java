/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.terminal;

import de.unisaarland.cs.st.reposuite.output.formatting.BackgroundColor;
import de.unisaarland.cs.st.reposuite.output.formatting.BasicColor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class VT100TerminalBackgroundColor extends BackgroundColor {
	
	private String color;
	
	/**
	 * @param color
	 */
	public VT100TerminalBackgroundColor(final BasicColor color) {
		super(color);
	}
	
	/**
	 * @param code
	 */
	public VT100TerminalBackgroundColor(final String code) {
		super(code);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.formatting.BackgroundColor#getColor
	 * ()
	 */
	@Override
	public String getColor() {
		return this.color;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.formatting.BackgroundColor#setColor
	 * (de.unisaarland.cs.st.reposuite.output.formatting.BasicColor)
	 */
	@Override
	public void setColor(final BasicColor color) {
		switch (color) {
			case BLACK:
				setColor("40");
				break;
			case BLUE:
				setColor("44");
				break;
			case CYAN:
				setColor("46");
				break;
			case GREEN:
				setColor("42");
				break;
			case MAGENTA:
				setColor("45");
				break;
			case RED:
				setColor("41");
				break;
			case STANDARD:
				setColor("");
				break;
			case WHITE:
				setColor("47");
				break;
			case YELLOW:
				setColor("43");
				break;
			default:
				setColor(BasicColor.STANDARD);
				break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.formatting.BackgroundColor#setColor
	 * (java.lang.String)
	 */
	@Override
	public void setColor(final String code) {
		this.color = code;
	}
	
}
