/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.terminal;

import de.unisaarland.cs.st.reposuite.output.formatting.StyleManager;
import de.unisaarland.cs.st.reposuite.output.nodes.Node;
import de.unisaarland.cs.st.reposuite.output.nodes.Text;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Cell;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Row;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class TerminalRow extends Row {
	
	/**
	 * 
	 */
	public TerminalRow(final int columns) {
		super(columns);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.Styleable#assignStyle(java
	 * .lang.String,
	 * de.unisaarland.cs.st.reposuite.output.formatting.StyleManager)
	 */
	@Override
	public void assignStyle(final String stylename,
	                        final StyleManager manager) {
		applyStyle(manager.getStyle(stylename));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.table.Row#createCell()
	 */
	@Override
	public Cell createCell() {
		return new TerminalCell();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.table.Row#createCell(de.
	 * unisaarland.cs.st.reposuite.output.nodes.Node)
	 */
	@Override
	public Cell createCell(final Node node) {
		return new TerminalCell(node);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.table.Row#createCell(java
	 * .lang.String)
	 */
	@Override
	public Cell createCell(final String string) {
		return new TerminalCell(string);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.table.Row#getContent()
	 */
	@Override
	public Node[] getContent() {
		StringBuilder builder = new StringBuilder();
		for (Cell cell : getCells()) {
			// TODO apply style
			builder.append(cell.toText());
		}
		return new Node[] { new Text(builder.toString()) };
	}
	
	@Override
	public void setContent(final Node[] nodes) {
		// TODO 1 cell row or first cell
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.Content#toText()
	 */
	@Override
	public String toText() {
		return Terminal.toText(this);
	}
	
}
