/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package de.unisaarland.cs.st.moskito.changecouplings.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class SerialFileChangeCoupling.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class SerialFileChangeCoupling implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long       serialVersionUID = -5562424840980704091L;
	
	/** The premise. */
	private final ArrayList<String> premise          = new ArrayList<String>();
	
	/** The implication. */
	private final String            implication;
	
	/** The support. */
	private final Integer           support;
	
	/** The confidence. */
	private final Double            confidence;
	
	/**
	 * Instantiates a new serial file change coupling.
	 *
	 * @param premise the premise
	 * @param implication the implication
	 * @param support the support
	 * @param confidence the confidence
	 */
	protected SerialFileChangeCoupling(List<String> premise, final String implication, final Integer support,
	        final Double confidence) {
		this.getPremise().addAll(premise);
		this.implication = new String(implication);
		this.confidence = new Double(confidence);
		this.support = new Integer(support);
	}
	
	/**
	 * Gets the confidence.
	 *
	 * @return the confidence
	 */
	public Double getConfidence() {
		return confidence;
	}
	
	/**
	 * Gets the implication.
	 *
	 * @return the implication
	 */
	public String getImplication() {
		return implication;
	}
	
	/**
	 * Gets the premise.
	 *
	 * @return the premise
	 */
	public ArrayList<String> getPremise() {
		return premise;
	}
	
	/**
	 * Gets the support.
	 *
	 * @return the support
	 */
	public Integer getSupport() {
		return support;
	}
}
