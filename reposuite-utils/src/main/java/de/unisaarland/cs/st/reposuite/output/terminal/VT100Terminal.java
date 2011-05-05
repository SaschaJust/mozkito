package de.unisaarland.cs.st.reposuite.output.terminal;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.reposuite.output.formatting.BackgroundColor;
import de.unisaarland.cs.st.reposuite.output.formatting.BasicColor;
import de.unisaarland.cs.st.reposuite.output.formatting.FontColor;
import de.unisaarland.cs.st.reposuite.output.formatting.FontDecoration;
import de.unisaarland.cs.st.reposuite.output.formatting.FontSettings;
import de.unisaarland.cs.st.reposuite.output.formatting.FontStyle;
import de.unisaarland.cs.st.reposuite.output.formatting.FontWeight;
import de.unisaarland.cs.st.reposuite.output.formatting.Size;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Table;

public class VT100Terminal extends Terminal {
	
	public enum Style {
		NONE ("0"), BRIGHT ("1"), DIM ("2"), UNDERSCORE ("4"), BLINK ("5"), REVERSE ("7"), HIDDEN ("8");
		
		private final String code;
		
		private Style(final String code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code;
		}
		
	}
	
	private FontColor       fontColor       = new VT100TerminalFontColor(BasicColor.STANDARD);
	private Style           style           = Style.NONE;
	private BackgroundColor backgroundColor = new VT100TerminalBackgroundColor(BasicColor.STANDARD);
	private final String    pattern         = "\u001b[%sm";
	
	private final String    end             = "\u001b[m";
	
	private String          start           = "";
	private FontWeight      fontWeight;
	private FontDecoration  fontDecoration;
	private FontStyle       fontStyle;
	private Size            fontSize;
	private FontSettings    fontSettings;
	
	private void changeStyle() {
		String[] s = new String[] { this.style.toString(), this.backgroundColor.getColor(), this.fontColor.getColor() };
		this.start = String.format(this.pattern, StringUtils.join(s, ';'));
	}
	
	private String convertStyle(final FontSettings settings) {
		if (settings.getDecoration() == FontDecoration.UNDERSCORE) {
			return Style.UNDERSCORE.toString();
		} else if (settings.getWeight() == FontWeight.BOLD) {
			return Style.BRIGHT.toString();
		} else if (settings.getStyle() == FontStyle.ITALIC) {
			return Style.DIM.toString();
		} else {
			return Style.NONE.toString();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.Terminal#getBackground()
	 */
	@Override
	public BackgroundColor getBackground() {
		return this.backgroundColor;
	}
	
	/**
	 * @return the end
	 */
	public String getEnd() {
		return this.end;
	}
	
	@Override
	public FontColor getFontColor() {
		return this.fontColor;
	}
	
	@Override
	public FontDecoration getFontDecoration() {
		return this.fontDecoration;
	}
	
	@Override
	public FontSettings getFontSettings() {
		return this.fontSettings;
	}
	
	@Override
	public Size getFontSize() {
		return this.fontSize;
	}
	
	@Override
	public FontStyle getFontStyle() {
		return this.fontStyle;
	}
	
	@Override
	public FontWeight getFontWeight() {
		return this.fontWeight;
	}
	
	@Override
	public String getString(final de.unisaarland.cs.st.reposuite.output.formatting.Style style,
	                        final String string) {
		String[] s = new String[] { convertStyle(style.getFontSettings()),
		        style.getBackgroundColor() != null
		                                          ? style.getBackgroundColor().getColor()
		                                          : new VT100TerminalBackgroundColor(BasicColor.STANDARD).getColor(),
		        style.getFontSettings().getColor() != null
		                                                  ? style.getFontSettings().getColor().getColor()
		                                                  : new VT100TerminalFontColor(BasicColor.STANDARD).getColor() };
		String start = String.format(this.pattern, StringUtils.join(s, ';'));
		return start + string + this.end;
	}
	
	@Override
	public void print(final String string) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.Terminal#println(java.lang.String)
	 */
	@Override
	public void printLine(final String string) {
		System.out.println(this.start + string + this.end);
	}
	
	@Override
	public void printTable(final Table table) {
		System.out.println(table.toText());
	}
	
	@Override
	public void setBackground(final BackgroundColor color) {
		this.backgroundColor = color;
		changeStyle();
	}
	
	@Override
	public void setFontColor(final FontColor color) {
		this.fontColor = color;
		changeStyle();
	}
	
	@Override
	public void setFontDecoration(final FontDecoration decoration) {
		this.fontDecoration = decoration;
		switch (decoration) {
			case UNDERSCORE:
				this.style = Style.UNDERSCORE;
				break;
			default:
				if (this.style == Style.UNDERSCORE) {
					this.style = Style.NONE;
				}
				break;
		}
		changeStyle();
		
	}
	
	@Override
	public void setFontSettings(final FontSettings settings) {
		super.setFontSettings(settings);
		this.fontSettings = settings;
	}
	
	@Override
	public void setFontSize(final Size size) {
		this.fontSize = size;
	}
	
	@Override
	public void setFontStyle(final FontStyle style) {
		this.fontStyle = style;
		switch (style) {
			case ITALIC:
				this.style = Style.DIM;
				break;
			default:
				if (this.style == Style.DIM) {
					this.style = Style.NONE;
				}
				break;
		}
		changeStyle();
	}
	
	@Override
	public void setFontWeight(final FontWeight weight) {
		this.fontWeight = weight;
		switch (weight) {
			case BOLD:
				this.style = Style.BRIGHT;
				break;
			default:
				if (this.style == Style.BRIGHT) {
					this.style = Style.NONE;
				}
				break;
		}
		changeStyle();
	}
}
