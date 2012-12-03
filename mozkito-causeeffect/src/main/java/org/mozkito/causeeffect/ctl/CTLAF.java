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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.mozkito.causeeffect.kripke.KripkeStructure;
import org.mozkito.causeeffect.kripke.State;

/**
 * Instances of this class represent CTL AF formulas. "AF f" means that "along all paths, finally f"
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLAF extends CTLComposedFormula {
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(CTLAF.class);
	
	/**
	 * Returns an "AF f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with AF.
	 * @return the ctlaf
	 */
	public static CTLAF get(final CTLFormula formula) {
		return new CTLAF(formula);
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLAF getFromXMLRepresentation(final Element element) {
		assert element.getNodeName().equals("CTL-AF");
		final CTLFormula formula = getCTLFormulaFromXMLs(element.getChildNodes());
		return CTLAF.get(formula);
	}
	
	/** Encapsulated formula. */
	private final CTLFormula formula;
	
	/**
	 * Creates an "AF f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with AF.
	 */
	private CTLAF(final CTLFormula formula) {
		this.formula = formula;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 17;
		int result = 1;
		result = (prime * result) + ((this.formula == null)
		                                                   ? 0
		                                                   : this.formula.hashCode());
		return result;
	}
	
	/**
	 * Gets the parent states.
	 * 
	 * @param <V>
	 *            the value type
	 * @param kripkeStruct
	 *            the kripke struct
	 * @param states
	 *            the states
	 * @return the parent states
	 * @author Kim Herzig <kim@mozkito.org>
	 */
	private <V> Set<State> getParentStates(final KripkeStructure<V> kripkeStruct,
	                                       final Set<State> states) {
		final Set<State> parents = new HashSet<State>();
		Set<State> children = states;
		while (!children.isEmpty()) {
			final Set<State> newChildren = new HashSet<State>();
			for (final State child : children) {
				parents.addAll(kripkeStruct.getPredecessors(child));
				newChildren.addAll(kripkeStruct.getPredecessors(child));
			}
			children = newChildren;
		}
		return parents;
	}
	
	/**
	 * Returns the subformula of this formula. Specifically, for AF f returns f.
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
		return "AF " + this.formula.getTextRepresentation(verbosity);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(final Document xml) {
		final Element ctlXML = xml.createElement("CTL-AF");
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
		
		final Set<State> trueStates = new HashSet<State>();
		final Set<State> seenStates = new HashSet<State>();
		
		for (final State state : kripkeStruct.getAllStates()) {
			if (kripkeStruct.isFormulaTrue(state, this.formula)) {
				trueStates.add(state);
				seenStates.add(state);
				kripkeStruct.markEvaluatedFormula(state, this, true);
			}
		}
		Set<State> trueParentStates = this.getParentStates(kripkeStruct, trueStates);
		while (!trueParentStates.isEmpty()) {
			final Set<State> newTrueParentStates = new HashSet<State>();
			for (final State parent : trueParentStates) {
				final Set<State> parentChildren = kripkeStruct.getSuccessors(parent);
				
				if (trueStates.containsAll(parentChildren)) {
					// if all children are true the formula holds
					trueStates.add(parent);
					seenStates.add(parent);
				} else if (seenStates.containsAll(parentChildren)) {
					// if not all children are true but all children were
					// validated already, the formula holds not
					seenStates.add(parent);
				} else {
					// not all children have been validated. Wait.
					newTrueParentStates.add(parent);
				}
			}
			if (trueParentStates.size() == newTrueParentStates.size()) {
				CTLAF.logger.fatal("CTLAF would end up in an endless loop. Abort!");
				throw new RuntimeException();
			}
			trueParentStates = newTrueParentStates;
		}
		
		for (final State trueState : trueStates) {
			kripkeStruct.markEvaluatedFormula(trueState, this, true);
		}
		final Set<State> falseStates = kripkeStruct.getAllStates();
		falseStates.removeAll(trueStates);
		for (final State falseState : falseStates) {
			kripkeStruct.markEvaluatedFormula(falseState, this, false);
		}
		
		// !!! OLD FAIR CTL implementation
		// // model check A(True U f) instead
		// CTLFormula sub = CTLAU.get(CTLTrue.get(), this.formula);
		// sub.modelCheckAllStates(kripkeStruct);
		//
		// // mark states where AF F holds
		// for (State state : kripkeStruct.getAllStates()) {
		// kripkeStruct.markEvaluatedFormula(state, this, kripkeStruct
		// .isFormulaTrue(state, sub));
		// }
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
