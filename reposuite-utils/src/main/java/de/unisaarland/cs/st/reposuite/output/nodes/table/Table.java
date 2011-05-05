package de.unisaarland.cs.st.reposuite.output.nodes.table;

import java.util.LinkedList;

import de.unisaarland.cs.st.reposuite.output.formatting.StyleManager;
import de.unisaarland.cs.st.reposuite.output.nodes.Node;
import de.unisaarland.cs.st.reposuite.output.terminal.TerminalRow;

public abstract class Table extends Node {
	
	private final LinkedList<Row> rows = new LinkedList<Row>();
	private int                   columnCount;
	private Caption               caption;
	private Header                header;
	private Footer                footer;
	
	public void addRow(final Row row) {
		this.rows.add(row);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.Styleable#assignStyle(java
	 * .lang.String,
	 * de.unisaarland.cs.st.reposuite.output.formatting.StyleManager)
	 */
	@Override
	public abstract void assignStyle(final String stylename,
	                                 final StyleManager manager);
	
	/**
	 * @return
	 */
	public abstract Cell createCell();
	
	/**
	 * @param node
	 * @return
	 */
	public abstract Cell createCell(Node node);
	
	/**
	 * @param string
	 * @return
	 */
	public abstract Cell createCell(String string);
	
	/**
	 * @return
	 */
	public abstract Row createRow();
	
	/**
	 * @return the caption
	 */
	public Caption getCaption() {
		return this.caption;
	}
	
	public Cell getCell(final int row,
	                    final int column) {
		return ((row < this.rows.size()) && (column < this.columnCount))
		                                                                ? this.rows.get(row).getCell(column)
		                                                                : null;
	}
	
	/**
	 * @return the columnCount
	 */
	public int getColumnCount() {
		return this.columnCount;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.nodes.Content#getContent()
	 */
	@Override
	public abstract Node[] getContent();
	
	/**
	 * @return the footer
	 */
	public Footer getFooter() {
		return this.footer;
	}
	
	/**
	 * @return the header
	 */
	public Header getHeader() {
		return this.header;
	}
	
	/**
	 * @param rowNumber
	 * @return
	 */
	public Row getRow(final int rowNumber) {
		return this.rows.size() > rowNumber
		                                   ? this.rows.get(rowNumber)
		                                   : null;
	}
	
	/**
	 * @return the rows
	 */
	public LinkedList<Row> getRows() {
		return this.rows;
	}
	
	/**
	 * @param caption
	 */
	public void setCaption(final Caption caption) {
		this.caption = caption;
	}
	
	/**
	 * @param row
	 * @param column
	 * @param content
	 */
	public void setCell(final int row,
	                    final int column,
	                    final Cell content) {
		if (column >= this.columnCount) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		// fill array
		while (row >= this.rows.size()) {
			this.rows.add(new TerminalRow(this.columnCount));
		}
		
		this.rows.get(row).setCell(column, content);
		
	}
	
	/**
	 * @param size
	 */
	public void setColumnCount(final int size) {
		this.columnCount = size;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.output.nodes.Content#setContent(de.unisaarland
	 * .cs.st.reposuite.output.nodes.Content)
	 */
	@Override
	public abstract void setContent(Node[] node);
	
	/**
	 * @param footer
	 */
	public void setFooter(final Footer footer) {
		this.footer = footer;
	}
	
	/**
	 * @param header
	 */
	public void setHeader(final Header header) {
		this.header = header;
	}
	
	/**
	 * @param rowNumber
	 * @param row
	 */
	public void setRow(final int rowNumber,
	                   final Row row) {
		while (this.rows.size() <= rowNumber) {
			this.rows.add(new TerminalRow(this.columnCount));
		}
		
		this.rows.set(rowNumber, row);
	}
	
	/**
	 * @param size
	 */
	public void setRowCount(final int size) {
		while (this.rows.size() > size) {
			this.rows.removeLast();
		}
		
		while (this.rows.size() < size) {
			this.rows.add(new TerminalRow(this.columnCount));
		}
	}
}
