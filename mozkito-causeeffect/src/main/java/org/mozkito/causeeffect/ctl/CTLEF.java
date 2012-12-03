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

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.mozkito.causeeffect.kripke.KripkeStructure;
import org.mozkito.causeeffect.kripke.State;

/**
 * Instances of this class represent CTL EF formulas. "EF f" means that "along at least one path, eventually f"
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLEF extends CTLComposedFormula {
	
	/**
	 * Returns an "EF f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with EF.
	 * @return the ctlef
	 */
	public static CTLEF get(final CTLFormula formula) {
		return new CTLEF(formula);
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLEF getFromXMLRepresentation(final Element element) {
		assert element.getNodeName().equals("CTL-EF");
		final CTLFormula formula = getCTLFormulaFromXMLs(element.getChildNodes());
		return CTLEF.get(formula);
	}
	
	/** Encapsulated formula. */
	private final CTLFormula formula;
	
	/**
	 * Creates an "EF f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with EF.
	 */
	private CTLEF(final CTLFormula formula) {
		this.formula = formula;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 43;
		int result = 1;
		result = (prime * result) + ((this.formula == null)
		                                                   ? 0
		                                                   : this.formula.hashCode());
		return result;
	}
	
	/**
	 * Returns the subformula of this formula. Specifically, for EF f returns f.
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
	public String getTextRepresentation(final OutputVerbosity verbosity) {
		return "EF " + this.formula.getTextRepresentation(verbosity);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(final Document xml) {
		final Element ctlXML = xml.createElement("CTL-EF");
		ctlXML.appendChild(this.formula.getXMLRepresentation(xml));
		return ctlXML;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#modelCheckAllStates(org.softevo.ctl.kripke .KripkeStructure)
	 */
	@Override
	public <V> void modelCheckAllStates(final KripkeStructure<V> kripkeStruct) {
		if (kripkeStruct.wasFormulaEvaluated(this)) {
			return;
		}
		
		this.formula.modelCheckAllStates(kripkeStruct);
		
		final Set<State> allTrueStates = new HashSet<State>();
		
		Set<State> trueStates = new HashSet<State>();
		for (final State state : kripkeStruct.getAllStates()) {
			if (kripkeStruct.isFormulaTrue(state, this.formula)) {
				trueStates.add(state);
				kripkeStruct.markEvaluatedFormula(state, this, true);
			}
		}
		allTrueStates.addAll(trueStates);
		
		while (!trueStates.isEmpty()) {
			final Set<State> newTrueStates = new HashSet<State>();
			for (final State trueState : trueStates) {
				final Set<State> pres = kripkeStruct.getPredecessors(trueState);
				for (final State pre : pres) {
					newTrueStates.add(pre);
					kripkeStruct.markEvaluatedFormula(pre, this, true);
				}
			}
			trueStates = newTrueStates;
			allTrueStates.addAll(newTrueStates);
		}
		
		final Set<State> falseStates = new HashSet<State>(kripkeStruct.getAllStates());
		falseStates.removeAll(allTrueStates);
		for (final State falseState : falseStates) {
			kripkeStruct.markEvaluatedFormula(falseState, this, false);
		}
		
		// !!! OLD Version for failr CTL
		// model check E(True U f) instead
		// CTLFormula sub = CTLEU.get(CTLTrue.get(), this.formula);
		// sub.modelCheckAllStates(kripkeStruct);
		//
		// // mark states where EF F holds
		// for (State state : kripkeStruct.getAllStates()) {
		// kripkeStruct.markEvaluatedFormula(state, this, kripkeStruct
		// .isFormulaTrue(state, sub));
		// }
		//
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
