package de.unisaarland.cs.st.reposuite.output.formatting;

public class FontSettings {
	
	private FontColor      color;
	private FontDecoration decoration;
	private Size           size;
	private FontStyle      style;
	private FontWeight     weight;
	
	public FontColor getColor() {
		return this.color;
	}
	
	public FontDecoration getDecoration() {
		return this.decoration;
	}
	
	public Size getSize() {
		return this.size;
	}
	
	public FontStyle getStyle() {
		return this.style;
	}
	
	public FontWeight getWeight() {
		return this.weight;
	}
	
	/**
	 * @param color the color to set
	 */
	public void setColor(final FontColor color) {
		this.color = color;
	}
	
	/**
	 * @param decoration the decoration to set
	 */
	public void setDecoration(final FontDecoration decoration) {
		this.decoration = decoration;
	}
	
	/**
	 * @param size the size to set
	 */
	public void setSize(final Size size) {
		this.size = size;
	}
	
	/**
	 * @param style the style to set
	 */
	public void setStyle(final FontStyle style) {
		this.style = style;
	}
	
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(final FontWeight weight) {
		this.weight = weight;
	}
}
