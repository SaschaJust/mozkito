package de.unisaarland.cs.st.reposuite.ltc.ctl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.State;

/**
 * Instances of this class represent negations of CTL formulas.
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLNegation extends CTLComposedFormula {
	
	/**
	 * Returns a negation of a given CTL formula.
	 * 
	 * @param formula
	 *            Formula to create negation of.
	 */
	public static CTLNegation get(CTLFormula formula) {
		return new CTLNegation(formula);
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
	public static CTLNegation getFromXMLRepresentation(Element element) {
		assert element.getNodeName().equals("CTL-not");
		CTLFormula formula = getCTLFormulaFromXMLs(element.getChildNodes());
		return CTLNegation.get(formula);
	}
	
	/** Formula that is to be negated. */
	private final CTLFormula formula;
	
	/**
	 * Creates a negation of a given CTL formula.
	 * 
	 * @param formula
	 *            Formula to create negation of.
	 */
	private CTLNegation(CTLFormula formula) {
		this.formula = formula;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 79;
		int result = 1;
		result = (prime * result) + ((this.formula == null) ? 0 : this.formula.hashCode());
		return result;
	}
	
	/**
	 * Returns the subformula of this formula. Specifically, for not f returns
	 * f.
	 * 
	 * @return The subformula of this formula.
	 */
	@Override
	public CTLFormula getSubformula() {
		return this.formula;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.softevo.tikanga.ops.ctl.CTLFormula#getTextRepresentation(org.softevo
	 * .tikanga.ops.OutputVerbosity)
	 */
	@Override
	public String getTextRepresentation(OutputVerbosity verbosity) {
		return "(not " + this.formula.getTextRepresentation(verbosity) + ")";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom
	 * .Document)
	 */
	@Override
	public Element getXMLRepresentation(Document xml) {
		Element ctlXML = xml.createElement("CTL-not");
		ctlXML.appendChild(this.formula.getXMLRepresentation(xml));
		return ctlXML;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.softevo.ctl.ctl.CTLFormula#modelCheckAllStates(org.softevo.ctl.kripke
	 * .KripkeStructure)
	 */
	@Override
	public <V> void modelCheckAllStates(KripkeStructure<V> kripkeStruct) {
		if (kripkeStruct.wasFormulaEvaluated(this)) {
			return;
		}
		
		this.formula.modelCheckAllStates(kripkeStruct);
		for (State state : kripkeStruct.getAllStates()) {
			boolean formulaHolds = kripkeStruct.isFormulaTrue(state, this.formula);
			boolean thisHolds = !formulaHolds;
			kripkeStruct.markEvaluatedFormula(state, this, thisHolds);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
