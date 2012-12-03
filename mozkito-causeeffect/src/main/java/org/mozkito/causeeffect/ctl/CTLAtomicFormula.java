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
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.mozkito.causeeffect.kripke.KripkeStructure;
import org.mozkito.causeeffect.kripke.Label;
import org.mozkito.causeeffect.kripke.State;

/**
 * Instances of this class are used to represent atomic CTL formulas.
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLAtomicFormula extends CTLFormula {
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(CTLAtomicFormula.class);
	
	/**
	 * Returns an atomic formula that represents the given proposition.
	 *
	 * @param proposition Proposition to be represented by the atomic formula.
	 * @return the cTL atomic formula
	 */
	public static CTLAtomicFormula get(final Long proposition) {
		return new CTLAtomicFormula(proposition);
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLAtomicFormula getFromXMLRepresentation(final Element element) {
		assert element.getNodeName().equals("CTL-atomic");
		final NodeList formulaNodes = element.getChildNodes();
		Long id = null;
		for (int i = 0; i < formulaNodes.getLength(); i++) {
			final Node node = formulaNodes.item(i);
			if (node.getNodeName().equals("id")) {
				if (node.getChildNodes().getLength() < 1) {
					CTLAtomicFormula.logger.fatal("Could not get CTLAtmoicFormula from XML! Wrong XML format!");
					throw new RuntimeException();
				}
				id = new Long(node.getChildNodes().item(0).getNodeValue());
			}
		}
		if (id == null) {
			CTLAtomicFormula.logger.fatal("Could not get CTLAtmoicFormula from XML!");
			throw new RuntimeException();
		}
		return new CTLAtomicFormula(id);
	}
	
	/** Proposition represented by this atomic formula. */
	private final Long id;
	
	/**
	 * Creates a new atomic formula that represents the given proposition.
	 *
	 * @param file the file
	 */
	private CTLAtomicFormula(final Long file) {
		this.id = file;
	}
	
	/**
	 * Instantiates a new cTL atomic formula.
	 *
	 * @param id the id
	 * @param path the path
	 */
	private CTLAtomicFormula(final Long id, final String path) {
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.causeeffect.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	public int calculateHashCode() {
		final int prime = 97;
		int result = 1;
		result = (prime * result) + ((this.id == null)
		                                              ? 0
		                                              : this.id.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.causeeffect.ctl.CTLFormula#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final CTLAtomicFormula other = (CTLAtomicFormula) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the proposition represented by this atomic formula.
	 * 
	 * @return Proposition represented by this atomic formula.
	 */
	public Long getProposition() {
		return this.id;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getTextRepresentation(org.softevo .tikanga.ops.OutputVerbosity)
	 */
	@Override
	public String getTextRepresentation(final OutputVerbosity verbosity) {
		return this.id.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(final Document xml) {
		final Element ctlXML = xml.createElement("CTL-atomic");
		final Element idXML = xml.createElement("id");
		idXML.appendChild(xml.createTextNode(this.id.toString()));
		ctlXML.appendChild(idXML);
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
			final Set<Label> labels = kripkeStruct.getStateLabels(state);
			boolean holds = false;
			for (final Label label : labels) {
				if (label.getContent().equals(this.id)) {
					holds = true;
					break;
				}
			}
			kripkeStruct.markEvaluatedFormula(state, this, holds);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.causeeffect.ctl.CTLFormula#putAttomicFormulas(java.util.Collection)
	 */
	@Override
	public void putAttomicFormulas(final Collection<CTLAtomicFormula> atomicFormulas) {
		atomicFormulas.add(this);
	}
}
