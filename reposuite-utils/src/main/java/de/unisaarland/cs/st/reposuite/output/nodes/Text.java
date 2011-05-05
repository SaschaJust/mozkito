/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.nodes;

import de.unisaarland.cs.st.reposuite.output.formatting.Size;
import de.unisaarland.cs.st.reposuite.output.formatting.Size.Unit;
import de.unisaarland.cs.st.reposuite.output.formatting.StyleManager;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Text extends Node {
	
	String content; ;
	
	/**
	 * @param string 
	 * 
	 */
	public Text(final String string) {
		this.content = string;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.Content#assignStyle(java.
	 * lang.String,
	 * de.unisaarland.cs.st.reposuite.output.formatting.StyleManager)
	 */
	@Override
	public void assignStyle(final String stylename,
	                        final StyleManager manager) {
		applyStyle(manager.getStyle(stylename));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.Content#getContent()
	 */
	@Override
	public Node[] getContent() {
		return new Node[0];
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.Content#getHeight()
	 */
	@Override
	public Size getHeight() {
		return new Size(1, Unit.UNIT);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.Content#getLength()
	 */
	@Override
	public int getLength() {
		return this.content.length();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.Content#getWidth()
	 */
	@Override
	public Size getWidth() {
		return new Size(this.content.length(), Unit.UNIT);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.Content#setContent(de.unisaarland
	 * .cs.st.reposuite.output.nodes.Node)
	 */
	@Override
	public void setContent(final Node[] nodes) {
		StringBuilder builder = new StringBuilder();
		
		for (Node node : nodes) {
			if (builder.length() > 0) {
				builder.append(FileUtils.lineSeparator);
			}
			builder.append(node.toText());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.Content#toText()
	 */
	@Override
	public String toText() {
		return this.content;
	}
	
}
