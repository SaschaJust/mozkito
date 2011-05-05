package de.unisaarland.cs.st.reposuite.output.nodes.table;

import java.util.ArrayList;

import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import de.unisaarland.cs.st.reposuite.output.formatting.StyleManager;
import de.unisaarland.cs.st.reposuite.output.nodes.Node;

public abstract class Row extends Node {
	
	private final int       columnCount;
	private ArrayList<Cell> cells = new ArrayList<Cell>();
	
	/**
	 * @param columns
	 */
	public Row(final int columns) {
		this.columnCount = columns;
		this.cells = new ArrayList<Cell>(columns);
		for (int i = 0; i < columns; ++i) {
			this.cells.add(createCell());
		}
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
	 * @param column
	 * @return
	 */
	public Cell getCell(final int column) {
		CollectionCondition.validIndex(this.cells, column, "Requested column with index %s.", column);
		return this.cells.get(column);
	}
	
	/**
	 * @return
	 */
	protected ArrayList<Cell> getCells() {
		return this.cells;
	}
	
	/**
	 * @return
	 */
	public int getColumnCount() {
		return this.columnCount;
	}
	
	/**
	 * @param column
	 * @param content
	 */
	public void setCell(final int column,
	                    final Cell content) {
		CollectionCondition.validIndex(this.cells, column, "Requested column with index %s.", column);
		this.cells.set(column, content);
	}
}
