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

import org.mozkito.causeeffect.kripke.KripkeStructure;
import org.mozkito.causeeffect.kripke.State;

/**
 * This is a singleton class, whose only instance represents the "true" CTL literal.
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLTrue extends CTLFormula {
	
	/** The one and only instance of CTL true. */
	private static CTLTrue instance = new CTLTrue();
	
	/**
	 * Returns the "true" CTL literal.
	 * 
	 * @return the cTL true
	 */
	public static CTLTrue get() {
		return CTLTrue.instance;
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLTrue getFromXMLRepresentation(final Element element) {
		assert "CTL-true".equals(element.getNodeName());
		return CTLTrue.get();
	}
	
	/**
	 * Creates an instance of a "true" CTL literal.
	 */
	private CTLTrue() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		return 83;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof CTLTrue) {
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
		return "true";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(final Document xml) {
		final Element ctlXML = xml.createElement("CTL-true");
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
			kripkeStruct.markEvaluatedFormula(state, this, true);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.causeeffect.ctl.CTLFormula#putAttomicFormulas(java.util.Collection)
	 */
	@Override
	public void putAttomicFormulas(final Collection<CTLAtomicFormula> atomicFormulas) {
		return;
	}
}
