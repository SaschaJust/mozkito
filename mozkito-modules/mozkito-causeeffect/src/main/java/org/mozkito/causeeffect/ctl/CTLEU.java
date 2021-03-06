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
 * Instances of this class represent CTL EU formulas. "E (f U g)" means that
 * "along at least one path, f has to hold until at some position g holds". This implies that g will be verified in the
 * future.
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLEU extends CTLBilateralFormula {
	
	/**
	 * Returns an "E (f U g)" formula from given "f" and "g" formulas.
	 * 
	 * @param f
	 *            Formula before "U"
	 * @param g
	 *            Formula after "U"
	 * @return the ctleu
	 */
	public static CTLEU get(final CTLFormula f,
	                        final CTLFormula g) {
		return new CTLEU(f, g);
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLEU getFromXMLRepresentation(final Element element) {
		assert "CTL-EU".equals(element.getNodeName());
		CTLFormula left = null;
		CTLFormula right = null;
		final NodeList formulaNodes = element.getChildNodes();
		for (int i = 0; i < formulaNodes.getLength(); i++) {
			final Node node = formulaNodes.item(i);
			if (node instanceof Element) {
				final Element subformulaXML = (Element) node;
				if ("left".equals(subformulaXML.getTagName())) {
					assert left == null;
					left = getCTLFormulaFromXMLs(subformulaXML.getChildNodes());
				} else if ("right".equals(subformulaXML.getTagName())) {
					assert right == null;
					right = getCTLFormulaFromXMLs(subformulaXML.getChildNodes());
				}
			}
		}
		return CTLEU.get(left, right);
	}
	
	/** "f" --- formula before "U". */
	private final CTLFormula f;
	
	/** "g" --- formula after "U". */
	private final CTLFormula g;
	
	/**
	 * Creates an "E (f U g)" formula from given "f" and "g" formulas.
	 * 
	 * @param f
	 *            Formula before "U"
	 * @param g
	 *            Formula after "U"
	 */
	private CTLEU(final CTLFormula f, final CTLFormula g) {
		this.f = f;
		this.g = g;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 61;
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
	 * Returns the left hand-side of this formula. Specifically, for E (f U g) returns f.
	 * 
	 * @return The left-hand side of this formula.
	 */
	@Override
	public CTLFormula getLeft() {
		return this.f;
	}
	
	/**
	 * Returns the right hand-side of this formula. Specifically, for E (f U g) returns g.
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
		return "E (" + this.f.getTextRepresentation(verbosity) + " U " + this.g.getTextRepresentation(verbosity) + ")";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(final Document xml) {
		final Element ctlXML = xml.createElement("CTL-EU");
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
		
		this.f.modelCheckAllStates(kripkeStruct);
		this.g.modelCheckAllStates(kripkeStruct);
		
		// divide the set of states into two groups: ones where E (f U g) holds
		// and ones where E (f U g) does not hold. Do this by starting with
		// states where g holds and marking them as states where E (f U g)
		// holds, too. Iterate through all predecessors recursively and mark all
		// of them, for which f holds, as states where E (f U g) holds. When
		// done, all marked states are those where E (f U g) holds.
		final Set<State> trueStates = new HashSet<State>();
		final Set<State> falseStates = new HashSet<State>(kripkeStruct.getAllStates());
		final Queue<State> statesToConsider = new LinkedList<State>();
		for (final State state : kripkeStruct.getAllStates()) {
			if (kripkeStruct.isFormulaTrue(state, this.g)) {
				statesToConsider.add(state);
				trueStates.add(state);
			}
		}
		while (!statesToConsider.isEmpty()) {
			final State state = statesToConsider.poll();
			for (final State pred : kripkeStruct.getPredecessors(state)) {
				if (kripkeStruct.isFormulaTrue(pred, this.f) && !trueStates.contains(pred)) {
					statesToConsider.add(pred);
					trueStates.add(pred);
				}
			}
		}
		falseStates.removeAll(trueStates);
		
		// mark states in the Kripke structure according to the results
		for (final State state : trueStates) {
			kripkeStruct.markEvaluatedFormula(state, this, true);
		}
		for (final State state : falseStates) {
			kripkeStruct.markEvaluatedFormula(state, this, false);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
