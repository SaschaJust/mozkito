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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.State;

/**
 * Instances of this class represent CTL AF formulas. "AF f" means that "along all paths, finally f"
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLAF extends CTLComposedFormula {
	
	private static Logger logger = Logger.getLogger(CTLAF.class);
	
	/**
	 * Returns an "AF f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with AF.
	 */
	public static CTLAF get(CTLFormula formula) {
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
	public static CTLAF getFromXMLRepresentation(Element element) {
		assert element.getNodeName().equals("CTL-AF");
		CTLFormula formula = getCTLFormulaFromXMLs(element.getChildNodes());
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
	private CTLAF(CTLFormula formula) {
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
	 * @param trueStates
	 * @return
	 * @author Kim Herzig <kim@cs.uni-saarland.de>
	 * @param <V>
	 */
	private <V> Set<State> getParentStates(KripkeStructure<V> kripkeStruct,
	                                       Set<State> states) {
		Set<State> parents = new HashSet<State>();
		Set<State> children = states;
		while (!children.isEmpty()) {
			Set<State> newChildren = new HashSet<State>();
			for (State child : children) {
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
	public String getTextRepresentation(OutputVerbosity verbosity) {
		return "AF " + this.formula.getTextRepresentation(verbosity);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(Document xml) {
		Element ctlXML = xml.createElement("CTL-AF");
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
		
		Set<State> trueStates = new HashSet<State>();
		Set<State> seenStates = new HashSet<State>();
		
		for (State state : kripkeStruct.getAllStates()) {
			if (kripkeStruct.isFormulaTrue(state, this.formula)) {
				trueStates.add(state);
				seenStates.add(state);
				kripkeStruct.markEvaluatedFormula(state, this, true);
			}
		}
		Set<State> trueParentStates = this.getParentStates(kripkeStruct, trueStates);
		while (!trueParentStates.isEmpty()) {
			Set<State> newTrueParentStates = new HashSet<State>();
			for (State parent : trueParentStates) {
				Set<State> parentChildren = kripkeStruct.getSuccessors(parent);
				
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
				logger.fatal("CTLAF would end up in an endless loop. Abort!");
				throw new RuntimeException();
			}
			trueParentStates = newTrueParentStates;
		}
		
		for (State trueState : trueStates) {
			kripkeStruct.markEvaluatedFormula(trueState, this, true);
		}
		Set<State> falseStates = kripkeStruct.getAllStates();
		falseStates.removeAll(trueStates);
		for (State falseState : falseStates) {
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
