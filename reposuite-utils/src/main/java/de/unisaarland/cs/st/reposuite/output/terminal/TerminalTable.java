/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.terminal;

import java.util.LinkedList;

import de.unisaarland.cs.st.reposuite.output.formatting.Frame;
import de.unisaarland.cs.st.reposuite.output.formatting.LineStyle;
import de.unisaarland.cs.st.reposuite.output.formatting.Size;
import de.unisaarland.cs.st.reposuite.output.formatting.Size.Unit;
import de.unisaarland.cs.st.reposuite.output.formatting.StyleManager;
import de.unisaarland.cs.st.reposuite.output.nodes.Node;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Cell;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Row;
import de.unisaarland.cs.st.reposuite.output.nodes.table.Table;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class TerminalTable extends Table {
	
	public enum Span {
		STATIC, DYNAMIC;
	}
	
	int                columnCount = -1;
	private final Span span        = Span.DYNAMIC;
	
	/**
	 * @param columns
	 */
	public TerminalTable(final int columns) {
		if (this.span == Span.DYNAMIC) {
			getStyle().setWidth(new Size(120, Unit.UNIT));
			getStyle().setHeight(new Size(40, Unit.UNIT));
		}
		getStyle().setFrame(new Frame(LineStyle.SOLID));
		this.columnCount = columns;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.table.Table#assignStyle(java
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
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.table.Table#createCell()
	 */
	@Override
	public Cell createCell() {
		return new TerminalCell();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.table.Table#createCell(de
	 * .unisaarland.cs.st.reposuite.output.nodes.Node)
	 */
	@Override
	public Cell createCell(final Node node) {
		return new TerminalCell(node);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.table.Table#createCell(java
	 * .lang.String)
	 */
	@Override
	public Cell createCell(final String string) {
		return new TerminalCell(string);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.table.Table#createRow()
	 */
	@Override
	public Row createRow() {
		return new TerminalRow(this.columnCount);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.table.Table#getContent()
	 */
	@Override
	public Node[] getContent() {
		LinkedList<Node> nodes = new LinkedList<Node>();
		
		nodes.add(getCaption());
		nodes.add(getHeader());
		nodes.addAll(getRows());
		nodes.add(getFooter());
		
		return nodes.toArray(new Node[0]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.table.Table#setContent(de
	 * .unisaarland.cs.st.reposuite.output.nodes.Content)
	 */
	@Override
	public void setContent(final Node[] node) {
		// TODO one cell table
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
