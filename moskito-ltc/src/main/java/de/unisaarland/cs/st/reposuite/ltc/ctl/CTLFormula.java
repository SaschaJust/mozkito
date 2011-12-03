package de.unisaarland.cs.st.reposuite.ltc.ctl;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.State;


/**
 * This class is the base class of all classes that represent CTL formulas.
 * 
 * @author Andrzej Wasylkowski
 */
public abstract class CTLFormula {
	
	/**
	 * Returns the CTL formula that occurs as the top level element in the given
	 * node list.
	 * 
	 * @param nodes
	 *            List to search through.
	 * @return CTL formula that occurs as the top level element in the node
	 *         list.
	 */
	protected static CTLFormula getCTLFormulaFromXMLs(NodeList nodes) {
		CTLFormula formula = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				CTLFormula f = CTLFormula.getFromXMLRepresentation(element);
				if (f != null) {
					assert formula == null;
					formula = f;
				}
			}
		}
		return formula;
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the
	 * given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or
	 *         <code>null</code>, if the element was not recognized.
	 */
	public static CTLFormula getFromXMLRepresentation(Element element) {
		if (element.getNodeName().equals("CTL-AF")) {
			return CTLAF.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-AG")) {
			return CTLAG.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-atomic")) {
			return CTLAtomicFormula.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-AU")) {
			return CTLAU.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-AX")) {
			return CTLAX.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-and")) {
			return CTLConjunction.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-or")) {
			return CTLDisjunction.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-EF")) {
			return CTLEF.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-EG")) {
			return CTLEG.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-iff")) {
			return CTLEquivalence.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-EU")) {
			return CTLEU.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-EX")) {
			return CTLEX.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-false")) {
			return CTLFalse.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-if")) {
			return CTLImplication.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-not")) {
			return CTLNegation.getFromXMLRepresentation(element);
		} else if (element.getNodeName().equals("CTL-true")) {
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
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public abstract boolean equals(Object o);
	
	/**
	 * Returns a text representation of this CTL formula. Length of the
	 * representation is determined by the verbosity given.
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
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		return this.calculateHashCode();
	}
	
	/**
	 * Model-checks this formula against a given Kripke structure. Returns the
	 * result of model-checking (i.e., <code>true</code> if the formula is true
	 * in the given structure and <code>false</code> if it is not).
	 * 
	 * @param kripkeStruct
	 *            Kripke structure to model-check the formula against.
	 * @return <code>true</code> if the formula is true in the given structure;
	 *         <code>false</code> otherwise.
	 */
	public final <V> boolean modelCheck(KripkeStructure<V> kripkeStruct) {
		this.modelCheckAllStates(kripkeStruct);
		for (State state : kripkeStruct.getInitialStates()) {
			assert kripkeStruct.wasFormulaEvaluated(state, this);
			if (kripkeStruct.isFormulaFalse(state, this)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Model-checks this formula against all states in the given Kripke
	 * structure. This updates the states with information on whether the
	 * formula holds in them or not.
	 * 
	 * @param kripkeStruct
	 *            Kripke structure to model-check the formula against.
	 */
	public abstract <V> void modelCheckAllStates(KripkeStructure<V> kripkeStruct);
	
	public abstract void putAttomicFormulas(Collection<CTLAtomicFormula> atomicFormulas);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final String toString() {
		return this.getTextRepresentation(OutputVerbosity.FULL);
	}
}
