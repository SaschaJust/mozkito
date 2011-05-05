/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.terminal;

import de.unisaarland.cs.st.reposuite.output.formatting.Distance;
import de.unisaarland.cs.st.reposuite.output.formatting.Size;
import de.unisaarland.cs.st.reposuite.output.formatting.Size.Unit;
import de.unisaarland.cs.st.reposuite.output.formatting.StyleManager;
import de.unisaarland.cs.st.reposuite.output.nodes.Node;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Cell;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class TerminalCell extends Cell {
	
	/**
	 * 
	 */
	public TerminalCell() {
		super();
		getStyle().setPadding(new Distance(new Size(0, Unit.UNIT), new Size(1, Unit.UNIT)));
	}
	
	/**
	 * @param node
	 */
	public TerminalCell(final Node node) {
		super(node);
		getStyle().setPadding(new Distance(new Size(0, Unit.UNIT), new Size(1, Unit.UNIT)));
	}
	
	/**
	 * @param string
	 */
	public TerminalCell(final String string) {
		super(string);
		getStyle().setPadding(new Distance(new Size(0, Unit.UNIT), new Size(1, Unit.UNIT)));
	}
	
	@Override
	public void assignStyle(final String stylename,
	                        final StyleManager manager) {
		applyStyle(manager.getStyle(stylename));
	}
	
	@Override
	public Node[] getContent() {
		return getNode();
	}
	
	@Override
	public void setContent(final Node[] nodes) {
		setNode(nodes);
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
