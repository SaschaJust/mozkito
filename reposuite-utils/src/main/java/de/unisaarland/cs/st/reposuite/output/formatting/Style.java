/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.formatting;

import de.unisaarland.cs.st.reposuite.output.formatting.Size.Unit;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Style {
	
	FontSettings       fontSettings       = new FontSettings();
	Size               height;
	Size               width;
	Distance           margin             = new Distance(new Size(0, Unit.UNIT));
	Distance           padding            = new Distance(new Size(0, Unit.UNIT));
	HorizontalAligment horizontalAligment = HorizontalAligment.LEFT;
	VerticalAlignment  verticalAlignment  = VerticalAlignment.TOP;
	BackgroundColor    backgroundColor;
	Frame              frame              = new Frame(LineStyle.NONE);
	
	/**
	 * @return the backgroundColor
	 */
	public BackgroundColor getBackgroundColor() {
		return this.backgroundColor;
	}
	
	/**
	 * @return the fontSettings
	 */
	public FontSettings getFontSettings() {
		return this.fontSettings;
	}
	
	/**
	 * @return the frame
	 */
	public Frame getFrame() {
		return this.frame;
	}
	
	/**
	 * @return the height
	 */
	public Size getHeight() {
		return this.height;
	}
	
	/**
	 * @return the horizontalAligment
	 */
	public HorizontalAligment getHorizontalAligment() {
		return this.horizontalAligment;
	}
	
	/**
	 * @return the margin
	 */
	public Distance getMargin() {
		return this.margin;
	}
	
	/**
	 * @return the padding
	 */
	public Distance getPadding() {
		return this.padding;
	}
	
	/**
	 * @return the verticalAlignment
	 */
	public VerticalAlignment getVerticalAlignment() {
		return this.verticalAlignment;
	}
	
	/**
	 * @return the width
	 */
	public Size getWidth() {
		return this.width;
	}
	
	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(final BackgroundColor backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	/**
	 * @param fontSettings the fontSettings to set
	 */
	public void setFontSettings(final FontSettings fontSettings) {
		this.fontSettings = fontSettings;
	}
	
	/**
	 * @param frame the frame to set
	 */
	public void setFrame(final Frame frame) {
		this.frame = frame;
	}
	
	/**
	 * @param height the height to set
	 */
	public void setHeight(final Size height) {
		this.height = height;
	}
	
	/**
	 * @param horizontalAligment the horizontalAligment to set
	 */
	public void setHorizontalAligment(final HorizontalAligment horizontalAligment) {
		this.horizontalAligment = horizontalAligment;
	}
	
	/**
	 * @param margin the margin to set
	 */
	public void setMargin(final Distance margin) {
		this.margin = margin;
	}
	
	/**
	 * @param padding the padding to set
	 */
	public void setPadding(final Distance padding) {
		this.padding = padding;
	}
	
	/**
	 * @param verticalAlignment the verticalAlignment to set
	 */
	public void setVerticalAlignment(final VerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}
	
	/**
	 * @param width the width to set
	 */
	public void setWidth(final Size width) {
		this.width = width;
	}
	
}
