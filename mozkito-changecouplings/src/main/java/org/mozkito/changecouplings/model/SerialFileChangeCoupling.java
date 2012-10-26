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
 *******************************************************************************/

package org.mozkito.changecouplings.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSFile;


/**
 * The Class SerialFileChangeCoupling.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class SerialFileChangeCoupling implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5562424840980704091L;
	
	/** The premise. */
	private final Set<Long>   premise          = new HashSet<>();
	
	/** The implication. */
	private final Long        implication;
	
	/** The support. */
	private final Integer     support;
	
	/** The confidence. */
	private final Double      confidence;
	
	/**
	 * Instantiates a new serial file change coupling.
	 * 
	 * @param premise
	 *            the premise
	 * @param implication
	 *            the implication
	 * @param support
	 *            the support
	 * @param confidence
	 *            the confidence
	 */
	public SerialFileChangeCoupling(final FileChangeCoupling coupling) {
		for (final RCSFile file : coupling.getPremise()) {
			this.premise.add(file.getGeneratedId());
		}
		this.implication = coupling.getImplication().getGeneratedId();
		this.support = coupling.getSupport();
		this.confidence = coupling.getConfidence();
	}
	
	public FileChangeCoupling unserialize(final PersistenceUtil persistenceUtil) {
		return new FileChangeCoupling(this.premise.toArray(new Long[this.premise.size()]), this.implication,
		                              this.support, this.confidence, persistenceUtil);
	}
	
}
