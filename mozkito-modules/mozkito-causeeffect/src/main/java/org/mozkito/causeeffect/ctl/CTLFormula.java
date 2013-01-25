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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.mozkito.causeeffect.kripke.KripkeStructure;
import org.mozkito.causeeffect.kripke.State;

/**
 * This class is the base class of all classes that represent CTL formulas.
 * 
 * @author Andrzej Wasylkowski
 */
public abstract class CTLFormula {
	
	/**
	 * Returns the CTL formula that occurs as the top level element in the given node list.
	 * 
	 * @param nodes
	 *            List to search through.
	 * @return CTL formula that occurs as the top level element in the node list.
	 */
	protected static CTLFormula getCTLFormulaFromXMLs(final NodeList nodes) {
		CTLFormula formula = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			final Node node = nodes.item(i);
			if (node instanceof Element) {
				final Element element = (Element) node;
				final CTLFormula f = CTLFormula.getFromXMLRepresentation(element);
				if (f != null) {
					assert formula == null;
					formula = f;
				}
			}
		}
		return formula;
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLFormula getFromXMLRepresentation(final Element element) {
		if ("CTL-AF".equals(element.getNodeName())) {
			return CTLAF.getFromXMLRepresentation(element);
		} else if ("CTL-AG".equals(element.getNodeName())) {
			return CTLAG.getFromXMLRepresentation(element);
		} else if ("CTL-atomic".equals(element.getNodeName())) {
			return CTLAtomicFormula.getFromXMLRepresentation(element);
		} else if ("CTL-AU".equals(element.getNodeName())) {
			return CTLAU.getFromXMLRepresentation(element);
		} else if ("CTL-AX".equals(element.getNodeName())) {
			return CTLAX.getFromXMLRepresentation(element);
		} else if ("CTL-and".equals(element.getNodeName())) {
			return CTLConjunction.getFromXMLRepresentation(element);
		} else if ("CTL-or".equals(element.getNodeName())) {
			return CTLDisjunction.getFromXMLRepresentation(element);
		} else if ("CTL-EF".equals(element.getNodeName())) {
			return CTLEF.getFromXMLRepresentation(element);
		} else if ("CTL-EG".equals(element.getNodeName())) {
			return CTLEG.getFromXMLRepresentation(element);
		} else if ("CTL-iff".equals(element.getNodeName())) {
			return CTLEquivalence.getFromXMLRepresentation(element);
		} else if ("CTL-EU".equals(element.getNodeName())) {
			return CTLEU.getFromXMLRepresentation(element);
		} else if ("CTL-EX".equals(element.getNodeName())) {
			return CTLEX.getFromXMLRepresentation(element);
		} else if ("CTL-false".equals(element.getNodeName())) {
			return CTLFalse.getFromXMLRepresentation(element);
		} else if ("CTL-if".equals(element.getNodeName())) {
			return CTLImplication.getFromXMLRepresentation(element);
		} else if ("CTL-not".equals(element.getNodeName())) {
			return CTLNegation.getFromXMLRepresentation(element);
		} else if ("CTL-true".equals(element.getNodeName())) {
			return CTLTrue.getFromXMLRepresentation(element);
		} else {
			return null;
		}
	}
	
	/**
	 * Calculates the hash code of this CTL formula.
	 * 
	 * @return Hash code of this CTL formula.
	 */
	protected abstract int calculateHashCode();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public abstract boolean equals(Object o);
	
	/**
	 * Returns a text representation of this CTL formula. Length of the representation is determined by the verbosity
	 * given.
	 * 
	 * @param verbosity
	 *            Verbosity of the representation.
	 * @return Text representation of this CTL formula.
	 */
	public abstract String getTextRepresentation(OutputVerbosity verbosity);
	
	/**
	 * Returns the XML representation of this CTL formula.
	 * 
	 * @param xml
	 *            XML document to use.
	 * @return XML representation of this CTL formula.
	 */
	public abstract Element getXMLRepresentation(Document xml);
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		return calculateHashCode();
	}
	
	/**
	 * Model-checks this formula against a given Kripke structure. Returns the result of model-checking (i.e.,
	 * <code>true</code> if the formula is true in the given structure and <code>false</code> if it is not).
	 * 
	 * @param <V>
	 *            the value type
	 * @param kripkeStruct
	 *            Kripke structure to model-check the formula against.
	 * @return <code>true</code> if the formula is true in the given structure; <code>false</code> otherwise.
	 */
	public final <V> boolean modelCheck(final KripkeStructure<V> kripkeStruct) {
		this.modelCheckAllStates(kripkeStruct);
		for (final State state : kripkeStruct.getInitialStates()) {
			assert kripkeStruct.wasFormulaEvaluated(state, this);
			if (kripkeStruct.isFormulaFalse(state, this)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Model-checks this formula against all states in the given Kripke structure. This updates the states with
	 * information on whether the formula holds in them or not.
	 * 
	 * @param <V>
	 *            the value type
	 * @param kripkeStruct
	 *            Kripke structure to model-check the formula against.
	 */
	public abstract <V> void modelCheckAllStates(KripkeStructure<V> kripkeStruct);
	
	/**
	 * Put attomic formulas.
	 * 
	 * @param atomicFormulas
	 *            the atomic formulas
	 */
	public abstract void putAttomicFormulas(Collection<CTLAtomicFormula> atomicFormulas);
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final String toString() {
		return getTextRepresentation(OutputVerbosity.FULL);
	}
}
