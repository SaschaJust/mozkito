/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.causeeffect.ctl;

import java.util.Collection;

/**
 * The Class CTLComposedFormula.
 * 
 * @author Kim Herzig <kim@mozkito.org>
 */
public abstract class CTLComposedFormula extends CTLFormula {
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.causeeffect.ctl.CTLFormula#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final CTLComposedFormula other = (CTLComposedFormula) obj;
		if (this.getSubformula() == null) {
			if (other.getSubformula() != null) {
				return false;
			}
		} else if (!this.getSubformula().equals(other.getSubformula())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the subformula.
	 * 
	 * @return the subformula
	 */
	public abstract CTLFormula getSubformula();
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.causeeffect.ctl.CTLFormula#putAttomicFormulas(java.util.Collection)
	 */
	@Override
	public void putAttomicFormulas(final Collection<CTLAtomicFormula> atomicFormulas) {
		this.getSubformula().putAttomicFormulas(atomicFormulas);
	}
	
}
