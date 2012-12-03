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
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.mozkito.causeeffect.kripke.KripkeStructure;
import org.mozkito.causeeffect.kripke.State;

/**
 * Instances of this class represent CTL AU formulas. "A (f U g)" means that
 * "along all paths, f has to hold until at some position g holds". This implies that g will be verified in the future.
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLAU extends CTLBilateralFormula {
	
	/**
	 * Returns an "A (f U g)" formula from given "f" and "g" formulas.
	 * 
	 * @param f
	 *            Formula before "U"
	 * @param g
	 *            Formula after "U"
	 * @return the ctlau
	 */
	public static CTLAU get(final CTLFormula f,
	                        final CTLFormula g) {
		return new CTLAU(f, g);
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLAU getFromXMLRepresentation(final Element element) {
		assert element.getNodeName().equals("CTL-AU");
		CTLFormula left = null;
		CTLFormula right = null;
		final NodeList formulaNodes = element.getChildNodes();
		for (int i = 0; i < formulaNodes.getLength(); i++) {
			final Node node = formulaNodes.item(i);
			if (node instanceof Element) {
				final Element subformulaXML = (Element) node;
				if (subformulaXML.getTagName().equals("left")) {
					assert left == null;
					left = getCTLFormulaFromXMLs(subformulaXML.getChildNodes());
				} else if (subformulaXML.getTagName().equals("right")) {
					assert right == null;
					right = getCTLFormulaFromXMLs(subformulaXML.getChildNodes());
				}
			}
		}
		return CTLAU.get(left, right);
	}
	
	/** "f" --- formula before "U". */
	private final CTLFormula f;
	
	/** "g" --- formula after "U". */
	private final CTLFormula g;
	
	/**
	 * Creates an "A (f U g)" formula from given "f" and "g" formulas.
	 * 
	 * @param f
	 *            Formula before "U"
	 * @param g
	 *            Formula after "U"
	 */
	private CTLAU(final CTLFormula f, final CTLFormula g) {
		this.f = f;
		this.g = g;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 29;
		int result = 1;
		result = (prime * result) + ((this.f == null)
		                                             ? 0
		                                             : this.f.hashCode());
		result = (prime * result) + ((this.g == null)
		                                             ? 0
		                                             : this.g.hashCode());
		return result;
	}
	
	/**
	 * Returns the left hand-side of this formula. Specifically, for A (f U g) returns f.
	 * 
	 * @return The left-hand side of this formula.
	 */
	@Override
	public CTLFormula getLeft() {
		return this.f;
	}
	
	/**
	 * Returns the right hand-side of this formula. Specifically, for A (f U g) returns g.
	 * 
	 * @return The right-hand side of this formula.
	 */
	@Override
	public CTLFormula getRight() {
		return this.g;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getTextRepresentation(org.softevo .tikanga.ops.OutputVerbosity)
	 */
	@Override
	public String getTextRepresentation(final OutputVerbosity verbosity) {
		return "A (" + this.f.getTextRepresentation(verbosity) + " U " + this.g.getTextRepresentation(verbosity) + ")";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(final Document xml) {
		final Element ctlXML = xml.createElement("CTL-AU");
		final Element leftXML = xml.createElement("left");
		leftXML.appendChild(this.f.getXMLRepresentation(xml));
		ctlXML.appendChild(leftXML);
		final Element rightXML = xml.createElement("right");
		rightXML.appendChild(this.g.getXMLRepresentation(xml));
		ctlXML.appendChild(rightXML);
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
		
		// A (f U g) = not (E ((not g) U ((not f) and (not g))) or EG (not g))
		// First check E ((not g) U ((not f) and (not g))).
		final CTLFormula sub1 = CTLEU.get(CTLNegation.get(this.g),
		                                  CTLConjunction.get(CTLNegation.get(this.f), CTLNegation.get(this.g)));
		sub1.modelCheckAllStates(kripkeStruct);
		
		// Now check not g.
		final CTLFormula sub2 = CTLNegation.get(this.g);
		sub2.modelCheckAllStates(kripkeStruct);
		
		// Now check EG (not g): having fairness means that we ask if there
		// exists a path that reaches the final state and for which g does not
		// hold anywhere. We do this by search from the final state
		final Set<State> egHolds = new HashSet<State>();
		final Queue<State> toConsider = new LinkedList<State>();
		if (kripkeStruct.isFormulaTrue(kripkeStruct.getFinalState(), sub2)) {
			toConsider.add(kripkeStruct.getFinalState());
		}
		while (!toConsider.isEmpty()) {
			final State state = toConsider.poll();
			egHolds.add(state);
			for (final State s : kripkeStruct.getPredecessors(state)) {
				if (kripkeStruct.isFormulaTrue(s, sub2) && !egHolds.contains(s) && !toConsider.contains(s)) {
					toConsider.add(s);
				}
			}
		}
		
		// Now mark all states where neither sub1 nor egHolds hold.
		for (final State state : kripkeStruct.getAllStates()) {
			final boolean thisHolds = !(kripkeStruct.isFormulaTrue(state, sub1) | egHolds.contains(state));
			kripkeStruct.markEvaluatedFormula(state, this, thisHolds);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
