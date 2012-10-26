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
package org.mozkito.changecouplings.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.persistence.PersistenceUtil;

import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@mozkito.org>
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
