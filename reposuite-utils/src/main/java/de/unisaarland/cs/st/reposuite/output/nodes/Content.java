package de.unisaarland.cs.st.reposuite.output.nodes;

import de.unisaarland.cs.st.reposuite.output.formatting.BackgroundColor;
import de.unisaarland.cs.st.reposuite.output.formatting.Distance;
import de.unisaarland.cs.st.reposuite.output.formatting.FontSettings;
import de.unisaarland.cs.st.reposuite.output.formatting.Frame;
import de.unisaarland.cs.st.reposuite.output.formatting.LineStyle;
import de.unisaarland.cs.st.reposuite.output.formatting.Size;
import de.unisaarland.cs.st.reposuite.output.formatting.Style;
import de.unisaarland.cs.st.reposuite.output.formatting.StyleManager;

public abstract class Content implements Styleable {
	
	private Style style = new Style();
	
	public Content() {
		super();
	}
	
	@Override
	public void applyStyle(final Style style) {
		this.style = style;
	}
	
	@Override
	public abstract void assignStyle(final String stylename,
	                                 final StyleManager manager);
	
	@Override
	public BackgroundColor getBackground() {
		return this.style.getBackgroundColor();
	}
	
	@Override
	public abstract Node[] getContent();
	
	@Override
	public FontSettings getFontSettings() {
		return this.style.getFontSettings();
	}
	
	@Override
	public Frame getFrame() {
		return this.style.getFrame();
	}
	
	@Override
	public Size getHeight() {
		if (this.style.getHeight() != null) {
			return this.style.getHeight();
		} else {
			Size height = getContent().getHeight();
			if (getStyle().getFrame().getBottom() != LineStyle.NONE) {
				height = height.add(getStyle().getFrame().getWidth());
			}
			
			if (getStyle().getFrame().getTop() != LineStyle.NONE) {
				height = height.add(getStyle().getFrame().getWidth());
			}
			
			height = height.add(getStyle().getPadding().getBottom());
			height = height.add(getStyle().getPadding().getTop());
			
			height = height.add(getStyle().getMargin().getBottom());
			height = height.add(getStyle().getMargin().getTop());
			
			return height;
		}
	}
	
	/**
	 * @return
	 */
	public int getLength() {
		int length = 0;
		for (Node node : getContent()) {
			length = Math.max(length, node.getLength());
		}
		return length;
	}
	
	@Override
	public Distance getMargin() {
		return this.style.getMargin();
	}
	
	@Override
	public Distance getPadding() {
		return this.style.getPadding();
	}
	
	@Override
	public Style getStyle() {
		return this.style;
	}
	
	@Override
	public Size getWidth() {
		if (this.style.getWidth() != null) {
			return this.style.getWidth();
		} else {
			Size width = getContent().getWidth();
			if (getStyle().getFrame().getLeft() != LineStyle.NONE) {
				width = width.add(getStyle().getFrame().getWidth());
			}
			
			if (getStyle().getFrame().getRight() != LineStyle.NONE) {
				width = width.add(getStyle().getFrame().getWidth());
			}
			
			width = width.add(getStyle().getPadding().getLeft());
			width = width.add(getStyle().getPadding().getRight());
			
			width = width.add(getStyle().getMargin().getLeft());
			width = width.add(getStyle().getMargin().getRight());
			
			return width;
		}
	}
	
	@Override
	public void setBackground(final BackgroundColor color) {
		this.style.setBackgroundColor(color);
	}
	
	@Override
	public abstract void setContent(Node[] node);
	
	@Override
	public void setFontSettings(final FontSettings settings) {
		this.style.setFontSettings(settings);
	}
	
	@Override
	public void setFrame(final Frame frame) {
		this.style.setFrame(frame);
	}
	
	@Override
	public void setHeight(final Size size) {
		this.style.setHeight(size);
	}
	
	@Override
	public void setMargin(final Distance margin) {
		this.style.setMargin(margin);
	}
	
	@Override
	public void setPadding(final Distance padding) {
		this.style.setPadding(padding);
	}
	
	@Override
	public void setStyle(final Style style) {
		this.style = style;
	}
	
	@Override
	public void setWidth(final Size size) {
		this.style.setWidth(size);
	}
	
	/**
	 * @return
	 */
	public abstract String toText();
	
}
