/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.nodes;

import de.unisaarland.cs.st.reposuite.output.formatting.BackgroundColor;
import de.unisaarland.cs.st.reposuite.output.formatting.Distance;
import de.unisaarland.cs.st.reposuite.output.formatting.FontSettings;
import de.unisaarland.cs.st.reposuite.output.formatting.Frame;
import de.unisaarland.cs.st.reposuite.output.formatting.Size;
import de.unisaarland.cs.st.reposuite.output.formatting.Style;
import de.unisaarland.cs.st.reposuite.output.formatting.StyleManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface Styleable {
	
	public void applyStyle(Style style);
	
	public void assignStyle(String stylename,
	                        StyleManager manager);
	
	public BackgroundColor getBackground();
	
	public Node[] getContent();
	
	public FontSettings getFontSettings();
	
	public Frame getFrame();
	
	public Size getHeight();
	
	public Distance getMargin();
	
	public Distance getPadding();
	
	public Style getStyle();
	
	public Size getWidth();
	
	public void setBackground(BackgroundColor color);
	
	public void setContent(Node[] node);
	
	public void setFontSettings(FontSettings settings);
	
	public void setFrame(Frame style);
	
	public void setHeight(Size size);
	
	public void setMargin(Distance margin);
	
	public void setPadding(Distance padding);
	
	public void setStyle(Style style);
	
	public void setWidth(Size size);
}
