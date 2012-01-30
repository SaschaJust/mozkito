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

package de.unisaarland.cs.st.moskito.changecouplings.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerialFileChangeCoupling implements Serializable {
	
	/**
	 * 
	 */
	private static final long       serialVersionUID = -5562424840980704091L;
	
	private final ArrayList<String> premise          = new ArrayList<String>();
	private final String            implication;
	private final Integer           support;
	private final Double            confidence;
	
	protected SerialFileChangeCoupling(List<String> premise, final String implication, final Integer support,
	        final Double confidence) {
		this.getPremise().addAll(premise);
		this.implication = new String(implication);
		this.confidence = new Double(confidence);
		this.support = new Integer(support);
	}
	
	public Double getConfidence() {
		return confidence;
	}
	
	public String getImplication() {
		return implication;
	}
	
	public ArrayList<String> getPremise() {
		return premise;
	}
	
	public Integer getSupport() {
		return support;
	}
}
