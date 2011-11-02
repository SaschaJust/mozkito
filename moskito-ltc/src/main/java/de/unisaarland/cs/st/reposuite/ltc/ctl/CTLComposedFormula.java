package de.unisaarland.cs.st.reposuite.ltc.ctl;

import java.util.Collection;

/**
 * @author Kim Herzig <kim@cs.uni-saarland.de>
 * 
 */
public abstract class CTLComposedFormula extends CTLFormula {
	
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
		CTLComposedFormula other = (CTLComposedFormula) obj;
		if (this.getSubformula() == null) {
			if (other.getSubformula() != null) {
				return false;
			}
		} else if (!this.getSubformula().equals(other.getSubformula())) {
			return false;
		}
		return true;
	}
	
	public abstract CTLFormula getSubformula();
	
	@Override
	public void putAttomicFormulas(Collection<CTLAtomicFormula> atomicFormulas) {
		this.getSubformula().putAttomicFormulas(atomicFormulas);
	}
	
}
