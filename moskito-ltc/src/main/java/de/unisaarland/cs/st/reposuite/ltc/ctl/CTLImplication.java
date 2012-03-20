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

package de.unisaarland.cs.st.reposuite.ltc.ctl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.State;

/**
 * Instances of this class represent implications of CTL formulas.
 * 
 * @author Andrzej Wasylkowski
 */
public class CTLImplication extends CTLBilateralFormula {
	
	/**
	 * Returns an implication between two given CTL formulas.
	 * 
	 * @param left
	 *            Left side of the implication.
	 * @param right
	 *            Right side of the implication.
	 */
	public static CTLImplication get(CTLFormula f,
	                                 CTLFormula g) {
		return new CTLImplication(f, g);
	}
	
	/**
	 * Returns (creating it, if necessary) the CTL formula represented by the given XML element.
	 * 
	 * @param element
	 *            XML representation of the CTL formula to create.
	 * @return CTL formula, as represented by the given XML element, or <code>null</code>, if the element was not
	 *         recognized.
	 */
	public static CTLImplication getFromXMLRepresentation(Element element) {
		assert element.getNodeName().equals("CTL-if");
		CTLFormula left = null;
		CTLFormula right = null;
		NodeList formulaNodes = element.getChildNodes();
		for (int i = 0; i < formulaNodes.getLength(); i++) {
			Node node = formulaNodes.item(i);
			if (node instanceof Element) {
				Element subformulaXML = (Element) node;
				if (subformulaXML.getTagName().equals("left")) {
					assert left == null;
					left = getCTLFormulaFromXMLs(subformulaXML.getChildNodes());
				} else if (subformulaXML.getTagName().equals("right")) {
					assert right == null;
					right = getCTLFormulaFromXMLs(subformulaXML.getChildNodes());
				}
			}
		}
		return CTLImplication.get(left, right);
	}
	
	/** Left side of the implication. */
	private CTLFormula left;
	
	/** Right side of the implication. */
	private CTLFormula right;
	
	/**
	 * Creates an implication between two given CTL formulas.
	 * 
	 * @param left
	 *            Left side of the implication.
	 * @param right
	 *            Right side of the implication.
	 */
	private CTLImplication(CTLFormula left, CTLFormula right) {
		this.left = left;
		this.right = right;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#calculateHashCode()
	 */
	@Override
	protected int calculateHashCode() {
		final int prime = 73;
		int result = 1;
		result = (prime * result) + ((this.left == null)
		                                                ? 0
		                                                : this.left.hashCode());
		result = (prime * result) + ((this.right == null)
		                                                 ? 0
		                                                 : this.right.hashCode());
		return result;
	}
	
	/**
	 * Returns the left hand-side of this formula. Specifically, for (f => g) returns f.
	 * 
	 * @return The left-hand side of this formula.
	 */
	@Override
	public CTLFormula getLeft() {
		return this.left;
	}
	
	/**
	 * Returns the right hand-side of this formula. Specifically, for (f => g) returns g.
	 * 
	 * @return The right-hand side of this formula.
	 */
	@Override
	public CTLFormula getRight() {
		return this.right;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getTextRepresentation(org.softevo .tikanga.ops.OutputVerbosity)
	 */
	@Override
	public String getTextRepresentation(OutputVerbosity verbosity) {
		return "(" + this.left.getTextRepresentation(verbosity) + " => " + this.right.getTextRepresentation(verbosity)
		        + ")";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.tikanga.ops.ctl.CTLFormula#getXMLRepresentation(org.w3c.dom .Document)
	 */
	@Override
	public Element getXMLRepresentation(Document xml) {
		Element ctlXML = xml.createElement("CTL-if");
		Element leftXML = xml.createElement("left");
		leftXML.appendChild(this.left.getXMLRepresentation(xml));
		ctlXML.appendChild(leftXML);
		Element rightXML = xml.createElement("right");
		rightXML.appendChild(this.right.getXMLRepresentation(xml));
		ctlXML.appendChild(rightXML);
		return ctlXML;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.softevo.ctl.ctl.CTLFormula#modelCheckAllStates(org.softevo.ctl.kripke .KripkeStructure)
	 */
	@Override
	public <V> void modelCheckAllStates(KripkeStructure<V> kripkeStruct) {
		if (kripkeStruct.wasFormulaEvaluated(this)) {
			return;
		}
		
		this.left.modelCheckAllStates(kripkeStruct);
		this.right.modelCheckAllStates(kripkeStruct);
		for (State state : kripkeStruct.getAllStates()) {
			boolean leftHolds = kripkeStruct.isFormulaTrue(state, this.left);
			boolean rightHolds = kripkeStruct.isFormulaTrue(state, this.right);
			boolean thisHolds = !leftHolds | rightHolds;
			kripkeStruct.markEvaluatedFormula(state, this, thisHolds);
		}
		
		kripkeStruct.markEvaluatedFormula(this);
	}
}
