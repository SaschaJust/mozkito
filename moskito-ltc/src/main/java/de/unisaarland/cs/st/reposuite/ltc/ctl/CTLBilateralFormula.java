package de.unisaarland.cs.st.reposuite.ltc.ctl;

import java.util.Collection;

/**
 * @author Kim Herzig <kim@cs.uni-saarland.de>
 * 
 */
public abstract class CTLBilateralFormula extends CTLFormula {
	
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		CTLBilateralFormula other = (CTLBilateralFormula) obj;
		if (this.getLeft() == null) {
			if (other.getLeft() != null) {
				return false;
			}
		} else if (!this.getLeft().equals(other.getLeft())) {
			return false;
		}
		if (this.getRight() == null) {
			if (other.getRight() != null) {
				return false;
			}
		} else if (!this.getRight().equals(other.getRight())) {
			return false;
		}
		return true;
	}
	
	public abstract CTLFormula getLeft();
	
	public abstract CTLFormula getRight();
	
	@Override
	public void putAttomicFormulas(Collection<CTLAtomicFormula> atomicFormulas) {
		this.getLeft().putAttomicFormulas(atomicFormulas);
		this.getRight().putAttomicFormulas(atomicFormulas);
	}
	
}
