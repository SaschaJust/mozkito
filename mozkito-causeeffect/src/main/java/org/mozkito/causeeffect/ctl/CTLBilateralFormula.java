/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.mozkito.causeeffect.ctl;

import java.util.Collection;

/**
 * The Class CTLBilateralFormula.
 * 
 * @author Kim Herzig <kim@mozkito.org>
 */
public abstract class CTLBilateralFormula extends CTLFormula {
	
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
		final CTLBilateralFormula other = (CTLBilateralFormula) obj;
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
	
	/**
	 * Gets the left.
	 * 
	 * @return the left
	 */
	public abstract CTLFormula getLeft();
	
	/**
	 * Gets the right.
	 * 
	 * @return the right
	 */
	public abstract CTLFormula getRight();
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.causeeffect.ctl.CTLFormula#putAttomicFormulas(java.util.Collection)
	 */
	@Override
	public void putAttomicFormulas(final Collection<CTLAtomicFormula> atomicFormulas) {
		this.getLeft().putAttomicFormulas(atomicFormulas);
		this.getRight().putAttomicFormulas(atomicFormulas);
	}
	
}
