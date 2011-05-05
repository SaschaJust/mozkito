/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output;

import de.unisaarland.cs.st.reposuite.output.formatting.BackgroundColor;
import de.unisaarland.cs.st.reposuite.output.formatting.FontColor;
import de.unisaarland.cs.st.reposuite.output.formatting.FontDecoration;
import de.unisaarland.cs.st.reposuite.output.formatting.FontSettings;
import de.unisaarland.cs.st.reposuite.output.formatting.FontStyle;
import de.unisaarland.cs.st.reposuite.output.formatting.FontWeight;
import de.unisaarland.cs.st.reposuite.output.formatting.Size;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Table;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface TextSink {
	
	/**
	 * @return the background
	 */
	public BackgroundColor getBackground();
	
	/**
	 * @return the foreground
	 */
	public FontColor getFontColor();
	
	/**
	 * @return
	 */
	public FontDecoration getFontDecoration();
	
	/**
	 * @return
	 */
	public FontSettings getFontSettings();
	
	/**
	 * @return
	 */
	public Size getFontSize();
	
	/**
	 * @return the style
	 */
	public FontStyle getFontStyle();
	
	/**
	 * @return
	 */
	public FontWeight getFontWeight();
	
	/**
	 * @param string
	 */
	public void print(final String string);
	
	/**
	 * @param string
	 */
	public void printLine(final String string);
	
	/**
	 * @param table
	 */
	public void printTable(final Table table);
	
	/**
	 * @param background the background to set
	 */
	public void setBackground(final BackgroundColor color);
	
	/**
	 * @param foreground the foreground to set
	 */
	public void setFontColor(final FontColor color);
	
	/**
	 * @param decoration
	 */
	public void setFontDecoration(final FontDecoration decoration);
	
	/**
	 * @param settings
	 */
	public void setFontSettings(final FontSettings settings);
	
	/**
	 * @param size
	 */
	public void setFontSize(Size size);
	
	/**
	 * @param style the style to set
	 */
	public void setFontStyle(final FontStyle style);
	
	/**
	 * @param weight
	 */
	public void setFontWeight(final FontWeight weight);
}
