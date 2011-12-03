package de.unisaarland.cs.st.reposuite.ltc.ctl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.State;

/**
 * Instances of this class represent CTL EX formulas. "EX f" means that "along
 * at least one path, next f"
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLEX extends CTLComposedFormula {
	
	/**
	 * Returns an "EX f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with EX.
	 */
	public static CTLEX get(CTLFormula formula) {
		return new CTLEX(formula);
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
	public static CTLEX getFromXMLRepresentation(Element element) {
		assert element.getNodeName().equals("CTL-EX");
		CTLFormula formula = getCTLFormulaFromXMLs(element.getChildNodes());
		return CTLEX.get(formula);
	}
	
	/** Encapsulated formula. */
	private CTLFormula formula;
	
	/**
	 * Creates an "EX f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with EX.
	 */
	private CTLEX(CTLFormula formula) {
		this.formula = formula;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 67;
		int result = 1;
		result = (prime * result) + ((this.formula == null) ? 0 : this.formula.hashCode());
		return result;
	}
	
	/**
	 * Returns the subformula of this formula. Specifically, for EX f returns f.
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
		return "EX " + this.formula.getTextRepresentation(verbosity);
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
		Element ctlXML = xml.createElement("CTL-EX");
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
			boolean thisHolds = false;
			for (State succ : kripkeStruct.getSuccessors(state)) {
				boolean formulaHolds = kripkeStruct.isFormulaTrue(succ, this.formula);
				thisHolds |= formulaHolds;
				if (thisHolds) {
					break;
				}
			}
			kripkeStruct.markEvaluatedFormula(state, this, thisHolds);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
