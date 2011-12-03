package de.unisaarland.cs.st.reposuite.ltc.ctl;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.State;

/**
 * Instances of this class represent CTL EF formulas. "EF f" means that "along
 * at least one path, eventually f"
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLEF extends CTLComposedFormula {
	
	/**
	 * Returns an "EF f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with EF.
	 */
	public static CTLEF get(CTLFormula formula) {
		return new CTLEF(formula);
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
	public static CTLEF getFromXMLRepresentation(Element element) {
		assert element.getNodeName().equals("CTL-EF");
		CTLFormula formula = getCTLFormulaFromXMLs(element.getChildNodes());
		return CTLEF.get(formula);
	}
	
	/** Encapsulated formula. */
	private CTLFormula formula;
	
	/**
	 * Creates an "EF f" formula from a given "f" formula.
	 * 
	 * @param formula
	 *            Formula to surround with EF.
	 */
	private CTLEF(CTLFormula formula) {
		this.formula = formula;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 43;
		int result = 1;
		result = (prime * result) + ((this.formula == null) ? 0 : this.formula.hashCode());
		return result;
	}
	
	/**
	 * Returns the subformula of this formula. Specifically, for EF f returns f.
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
		return "EF " + this.formula.getTextRepresentation(verbosity);
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
		Element ctlXML = xml.createElement("CTL-EF");
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
		
		Set<State> allTrueStates = new HashSet<State>();
		
		Set<State> trueStates = new HashSet<State>();
		for (State state : kripkeStruct.getAllStates()) {
			if (kripkeStruct.isFormulaTrue(state, this.formula)) {
				trueStates.add(state);
				kripkeStruct.markEvaluatedFormula(state, this, true);
			}
		}
		allTrueStates.addAll(trueStates);
		
		while (!trueStates.isEmpty()) {
			Set<State> newTrueStates = new HashSet<State>();
			for (State trueState : trueStates) {
				Set<State> pres = kripkeStruct.getPredecessors(trueState);
				for (State pre : pres) {
					newTrueStates.add(pre);
					kripkeStruct.markEvaluatedFormula(pre, this, true);
				}
			}
			trueStates = newTrueStates;
			allTrueStates.addAll(newTrueStates);
		}
		
		Set<State> falseStates = new HashSet<State>(kripkeStruct.getAllStates());
		falseStates.removeAll(allTrueStates);
		for (State falseState : falseStates) {
			kripkeStruct.markEvaluatedFormula(falseState, this, false);
		}
		
		// !!! OLD Version for failr CTL
		// model check E(True U f) instead
		// CTLFormula sub = CTLEU.get(CTLTrue.get(), this.formula);
		// sub.modelCheckAllStates(kripkeStruct);
		//
		// // mark states where EF F holds
		// for (State state : kripkeStruct.getAllStates()) {
		// kripkeStruct.markEvaluatedFormula(state, this, kripkeStruct
		// .isFormulaTrue(state, sub));
		// }
		//
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
