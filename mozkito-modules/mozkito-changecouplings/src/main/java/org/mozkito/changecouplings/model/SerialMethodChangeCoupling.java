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
package org.mozkito.changecouplings.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class SerialMethodChangeCoupling.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class SerialMethodChangeCoupling implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8861359294708634130L;
	
	/** The premise. */
	private final Set<Long>   premise          = new HashSet<>();
	
	/** The implication. */
	private Long              implication      = null;
	
	/** The support. */
	private final Integer     support;
	
	/** The confidence. */
	private final Double      confidence;
	
	/**
	 * Instantiates a new serial method change coupling.
	 * 
	 * @param coupling
	 *            the coupling
	 */
	public SerialMethodChangeCoupling(final MethodChangeCoupling coupling) {
		for (final JavaMethodDefinition mDef : coupling.getPremise()) {
			this.premise.add(mDef.getGeneratedId());
		}
		this.implication = coupling.getImplication().getGeneratedId();
		this.support = coupling.getSupport();
		this.confidence = coupling.getConfidence();
	}
	
	/**
	 * Unserialize.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the method change coupling
	 */
	public MethodChangeCoupling unserialize(final PersistenceUtil persistenceUtil) {
		final Set<JavaMethodDefinition> unserPremise = new HashSet<>();
		for (final Long id : this.premise) {
			final JavaElement elem = persistenceUtil.loadById(id, JavaElement.class);
			final JavaMethodDefinition mDef = (JavaMethodDefinition) elem;
			if (mDef == null) {
				if (Logger.logError()) {
					Logger.error("Could not load JavaMethodDefinition with id %s,", id);
				}
			} else {
				unserPremise.add(mDef);
			}
		}
		final JavaElement elem = persistenceUtil.loadById(this.implication, JavaElement.class);
		final JavaMethodDefinition unserImplication = (JavaMethodDefinition) elem;
		return new MethodChangeCoupling(unserPremise, unserImplication, this.support, this.confidence);
	}
	
}
