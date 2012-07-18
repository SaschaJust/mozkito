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
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class SerialMethodChangeCoupling implements Serializable {
	
	/**
     * 
     */
	private static final long serialVersionUID = 8861359294708634130L;
	
	private final Set<Long>   premise          = new HashSet<>();
	private Long              implication      = null;
	private final Integer     support;
	private final Double      confidence;
	
	public SerialMethodChangeCoupling(final MethodChangeCoupling coupling) {
		for (final JavaMethodDefinition mDef : coupling.getPremise()) {
			this.premise.add(mDef.getGeneratedId());
		}
		this.implication = coupling.getImplication().getGeneratedId();
		this.support = coupling.getSupport();
		this.confidence = coupling.getConfidence();
	}
	
	public MethodChangeCoupling unserialize(final PersistenceUtil persistenceUtil) {
		final Set<JavaMethodDefinition> unserPremise = new HashSet<>();
		for (final Long id : this.premise) {
			final JavaMethodDefinition mDef = persistenceUtil.loadById(id, JavaMethodDefinition.class);
			if (mDef == null) {
				if (Logger.logError()) {
					Logger.error("Could not load JavaMethodDefinition with id %s,", id);
				}
			} else {
				unserPremise.add(mDef);
			}
		}
		final JavaMethodDefinition unserImplication = persistenceUtil.loadById(this.implication,
		                                                                       JavaMethodDefinition.class);
		return new MethodChangeCoupling(unserPremise, unserImplication, this.support, this.confidence);
	}
	
}
