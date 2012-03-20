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

package de.unisaarland.cs.st.reposuite.ltc.ctl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.State;

/**
 * Instances of this class represent CTL AX formulas. "AX f" means that "along all paths, next f"
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLAX extends CTLComposedFormula {
	
	/**
	 * Returns an "AX f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with AX.
	 */
	public static CTLAX get(CTLFormula formula) {
		return new CTLAX(formula);
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLAX getFromXMLRepresentation(Element element) {
		assert element.getNodeName().equals("CTL-AX");
		CTLFormula formula = getCTLFormulaFromXMLs(element.getChildNodes());
		return CTLAX.get(formula);
	}
	
	/** Encapsulated formula. */
	private final CTLFormula formula;
	
	/**
	 * Creates an "AX f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with AX.
	 */
	private CTLAX(CTLFormula formula) {
		this.formula = formula;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.formula == null)
		                                                   ? 0
		                                                   : this.formula.hashCode());
		return result;
	}
	
	/**
	 * Returns the subformula of this formula. Specifically, for AX f returns f.
	 * 
	 * @return The subformula of this formula.
	 */
	@Override
	public CTLFormula getSubformula() {
		return this.formula;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getTextRepresentation(org.softevo .tikanga.ops.OutputVerbosity)
	 */
	@Override
	public String getTextRepresentation(OutputVerbosity verbosity) {
		return "AX " + this.formula.getTextRepresentation(verbosity);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(Document xml) {
		Element ctlXML = xml.createElement("CTL-AX");
		ctlXML.appendChild(this.formula.getXMLRepresentation(xml));
		return ctlXML;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#modelCheckAllStates(org.softevo.ctl.kripke .KripkeStructure)
	 */
	@Override
	public <V> void modelCheckAllStates(KripkeStructure<V> kripkeStruct) {
		if (kripkeStruct.wasFormulaEvaluated(this)) {
			return;
		}
		
		this.formula.modelCheckAllStates(kripkeStruct);
		for (State state : kripkeStruct.getAllStates()) {
			boolean thisHolds = true;
			for (State succ : kripkeStruct.getSuccessors(state)) {
				boolean formulaHolds = kripkeStruct.isFormulaTrue(succ, this.formula);
				thisHolds &= formulaHolds;
			}
			kripkeStruct.markEvaluatedFormula(state, this, thisHolds);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
