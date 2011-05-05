/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.formatting;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Distance {
	
	Size top, right, bottom, left;
	
	/**
	 * @param all
	 */
	public Distance(final Size all) {
		this(all, all, all, all);
	}
	
	/**
	 * @param vertical
	 * @param horizontal
	 */
	public Distance(final Size vertical, final Size horizontal) {
		this(vertical, horizontal, vertical, horizontal);
	}
	
	/**
	 * @param top
	 * @param right
	 * @param bottom
	 * @param left
	 */
	public Distance(final Size top, final Size right, final Size bottom, final Size left) {
		setTop(top);
		setRight(right);
		setBottom(bottom);
		setLeft(left);
	}
	
	/**
	 * @return the bottom
	 */
	public Size getBottom() {
		return this.bottom;
	}
	
	/**
	 * @return the left
	 */
	public Size getLeft() {
		return this.left;
	}
	
	/**
	 * @return the right
	 */
	public Size getRight() {
		return this.right;
	}
	
	/**
	 * @return the top
	 */
	public Size getTop() {
		return this.top;
	}
	
	/**
	 * @param bottom the bottom to set
	 */
	public void setBottom(final Size bottom) {
		this.bottom = bottom;
	}
	
	/**
	 * @param left the left to set
	 */
	public void setLeft(final Size left) {
		this.left = left;
	}
	
	/**
	 * @param right the right to set
	 */
	public void setRight(final Size right) {
		this.right = right;
	}
	
	/**
	 * @param top the top to set
	 */
	public void setTop(final Size top) {
		this.top = top;
	}
	
}
