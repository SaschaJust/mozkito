package de.unisaarland.cs.st.reposuite.output.formatting;

import de.unisaarland.cs.st.reposuite.output.formatting.Size.Unit;

public class Frame {
	
	LineStyle top, right, bottom, left;
	FontColor color;
	Size      width = new Size(1, Unit.UNIT);
	
	/**
	 * @param color
	 */
	public Frame(final FontColor color) {
		setColor(color);
	}
	
	public Frame(final FontColor color, final Size width) {
		setColor(color);
		setWidth(width);
	}
	
	public Frame(final LineStyle style) {
		this(style, style, style, style);
	}
	
	public Frame(final LineStyle style, final FontColor color) {
		this(style, style, style, style, color);
	}
	
	public Frame(final LineStyle style, final FontColor color, final Size width) {
		this(style, style, style, style, color);
		setWidth(width);
	}
	
	public Frame(final LineStyle top, final LineStyle right, final LineStyle bottom, final LineStyle left) {
		setTop(top);
		setBottom(bottom);
		setRight(right);
		setLeft(left);
	}
	
	public Frame(final LineStyle top, final LineStyle right, final LineStyle bottom, final LineStyle left,
	        final FontColor color) {
		this(top, right, bottom, left);
		setColor(color);
	}
	
	public Frame(final LineStyle top, final LineStyle right, final LineStyle bottom, final LineStyle left,
	        final FontColor color, final Size width) {
		this(top, right, bottom, left);
		setColor(color);
		setWidth(width);
	}
	
	public Frame(final LineStyle top, final LineStyle right, final LineStyle bottom, final LineStyle left,
	        final Size width) {
		this(top, right, bottom, left);
		setWidth(width);
	}
	
	public Frame(final LineStyle style, final Size width) {
		this(style, style, style, style);
		setWidth(width);
	}
	
	public Frame(final Size width) {
		setWidth(width);
	}
	
	/**
	 * @return the bottom
	 */
	public LineStyle getBottom() {
		return this.bottom;
	}
	
	/**
	 * @return the color
	 */
	public FontColor getColor() {
		return this.color;
	}
	
	/**
	 * @return the left
	 */
	public LineStyle getLeft() {
		return this.left;
	}
	
	/**
	 * @return the right
	 */
	public LineStyle getRight() {
		return this.right;
	}
	
	/**
	 * @return the top
	 */
	public LineStyle getTop() {
		return this.top;
	}
	
	/**
	 * @return the width
	 */
	public Size getWidth() {
		return this.width;
	}
	
	/**
	 * @param bottom the bottom to set
	 */
	public void setBottom(final LineStyle bottom) {
		this.bottom = bottom;
	}
	
	/**
	 * @param color the color to set
	 */
	public void setColor(final FontColor color) {
		this.color = color;
	}
	
	/**
	 * @param left the left to set
	 */
	public void setLeft(final LineStyle left) {
		this.left = left;
	}
	
	/**
	 * @param right the right to set
	 */
	public void setRight(final LineStyle right) {
		this.right = right;
	}
	
	/**
	 * @param top the top to set
	 */
	public void setTop(final LineStyle top) {
		this.top = top;
	}
	
	/**
	 * @param width the width to set
	 */
	public void setWidth(final Size width) {
		this.width = width;
	}
}
