package de.unisaarland.cs.st.reposuite.ltc.ctl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.State;

/**
 * Instances of this class represent CTL AG formulas. "AG f" means that "along
 * all paths, globally f"
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLAG extends CTLComposedFormula {
	
	/**
	 * Returns an "AG f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with AG.
	 */
	public static CTLAG get(CTLFormula formula) {
		return new CTLAG(formula);
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
	public static CTLAG getFromXMLRepresentation(Element element) {
		assert element.getNodeName().equals("CTL-AG");
		CTLFormula formula = getCTLFormulaFromXMLs(element.getChildNodes());
		return CTLAG.get(formula);
	}
	
	/** Encapsulated formula. */
	private final CTLFormula formula;
	
	/**
	 * Creates an "AG f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with AG.
	 */
	private CTLAG(CTLFormula formula) {
		this.formula = formula;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 23;
		int result = 1;
		result = (prime * result) + ((this.formula == null) ? 0 : this.formula.hashCode());
		return result;
	}
	
	/**
	 * Returns the subformula of this formula. Specifically, for AG f returns f.
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
		return "AG " + this.formula.getTextRepresentation(verbosity);
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
		Element ctlXML = xml.createElement("CTL-AG");
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
	public void modelCheckAllStates(KripkeStructure kripkeStruct) {
		if (kripkeStruct.wasFormulaEvaluated(this)) {
			return;
		}
		
		// model check (not EF (not f)) instead
		CTLFormula sub = CTLNegation.get(CTLEF.get(CTLNegation.get(this.formula)));
		sub.modelCheckAllStates(kripkeStruct);
		
		// mark states where AG f holds
		for (State state : kripkeStruct.getAllStates()) {
			kripkeStruct.markEvaluatedFormula(state, this, kripkeStruct.isFormulaTrue(state, sub));
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
