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

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.mozkito.causeeffect.kripke.KripkeStructure;
import org.mozkito.causeeffect.kripke.State;

/**
 * This is a singleton class, whose only instance represents the "false" CTL literal.
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLFalse extends CTLFormula {
	
	/** The one and only instance of CTL false. */
	private static CTLFalse instance = new CTLFalse();
	
	/**
	 * Returns the "false" CTL literal.
	 *
	 * @return the cTL false
	 */
	public static CTLFalse get() {
		return CTLFalse.instance;
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLFalse getFromXMLRepresentation(final Element element) {
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
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		return 71;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof CTLFalse) {
			return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getTextRepresentation(org.softevo .tikanga.ops.OutputVerbosity)
	 */
	@Override
	public String getTextRepresentation(final OutputVerbosity verbosity) {
		return "false";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(final Document xml) {
		final Element ctlXML = xml.createElement("CTL-false");
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
		
		for (final State state : kripkeStruct.getAllStates()) {
			kripkeStruct.markEvaluatedFormula(state, this, false);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.causeeffect.ctl.CTLFormula#putAttomicFormulas(java.util.Collection)
	 */
	@Override
	public void putAttomicFormulas(final Collection<CTLAtomicFormula> atomicFormulas) {
		return;
	}
}
