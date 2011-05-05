/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.terminal;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.Matches;
import de.unisaarland.cs.st.reposuite.output.formatting.BasicColor;
import de.unisaarland.cs.st.reposuite.output.formatting.FontColor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class VT100TerminalFontColor extends FontColor {
	
	private String color;
	
	/**
	 * @param color
	 */
	public VT100TerminalFontColor(@NotNull final BasicColor color) {
		super(color);
	}
	
	/**
	 * @param code
	 */
	public VT100TerminalFontColor(@NotNull final String code) {
		super(code);
	}
	
	/**
	 * @return the color
	 */
	@Override
	public String getColor() {
		return this.color;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.formatting.FontColor#setColor(de
	 * .unisaarland.cs.st.reposuite.output.formatting.BasicColor)
	 */
	@Override
	public void setColor(@NotNull final BasicColor color) {
		switch (color) {
			case BLACK:
				setColor("30");
				break;
			case BLUE:
				setColor("34");
				break;
			case CYAN:
				setColor("36");
				break;
			case GREEN:
				setColor("32");
				break;
			case MAGENTA:
				setColor("35");
				break;
			case RED:
				setColor("31");
				break;
			case STANDARD:
				setColor("");
				break;
			case WHITE:
				setColor("37");
				break;
			case YELLOW:
				setColor("33");
				break;
			default:
				setColor(BasicColor.STANDARD);
				break;
		}
	}
	
	/**
	 * @param color the color to set
	 */
	@Override
	public void setColor(@NotNull @Matches (pattern = "3[0-7]") final String color) {
		this.color = color;
	}
	
}
