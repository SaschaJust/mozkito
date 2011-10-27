package de.unisaarland.cs.st.reposuite.ltc.ctl;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.State;

/**
 * This is a singleton class, whose only instance represents the "false" CTL
 * literal.
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLFalse extends CTLFormula {
	
	/** The one and only instance of CTL false. */
	private static CTLFalse instance = new CTLFalse();
	
	/**
	 * Returns the "false" CTL literal.
	 */
	public static CTLFalse get() {
		return instance;
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
	public static CTLFalse getFromXMLRepresentation(Element element) {
		assert element.getNodeName().equals("CTL-false");
		return CTLFalse.get();
	}
	
	/**
	 * Creates an instance of a "false" CTL literal.
	 */
	private CTLFalse() {
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		return 71;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softevo.ctl.ctl.CTLFormula#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof CTLFalse) {
			return true;
		} else {
			return false;
		}
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
		return "false";
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
		Element ctlXML = xml.createElement("CTL-false");
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
		
		for (State state : kripkeStruct.getAllStates()) {
			kripkeStruct.markEvaluatedFormula(state, this, false);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
	
	@Override
	public void putAttomicFormulas(Collection<CTLAtomicFormula> atomicFormulas) {
		
	}
}