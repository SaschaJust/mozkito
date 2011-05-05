/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.formatting;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Size {
	
	/**
	 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
	 *
	 */
	public enum Unit {
		UNIT, PERCENT;
	}
	
	private int  value = 0;
	private Unit unit  = Unit.UNIT;
	
	/**
	 * @param value
	 * @param unit
	 */
	public Size(final int value, final Unit unit) {
		setValue(value);
		setUnit(unit);
	}
	
	/**
	 * @param width
	 * @return
	 */
	public Size add(final Size width) {
		return new Size(getValue() + width.getValue(), getUnit());
	}
	
	/**
	 * @param unit
	 * @return
	 */
	public Size convert(final Unit unit) {
		if (this.unit == unit) {
			return this;
		} else {
			throw new RuntimeException("NOT IMPLEMENTED");
		}
	}
	
	/**
	 * @return the unit
	 */
	public Unit getUnit() {
		return this.unit;
	}
	
	/**
	 * @return the value
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(final Unit unit) {
		this.unit = unit;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(final int value) {
		this.value = value;
	}
	
}
