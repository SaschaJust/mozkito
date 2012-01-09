/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.strategies;

import de.unisaarland.cs.st.moskito.mapping.model.PersistentMapping;
import de.unisaarland.cs.st.moskito.mapping.register.StorageAccessor;

/**
 * 
 * A strategy determines the way reposuite decides whether a mapping is valid or
 * not. In a TotalAgreement strategy all engines have to agree on a valid
 * mapping.
 * 
 * Reposuite relies on a strategy to be used when computing the actual mappings.
 * In this step, reposuite fetches all MapScores from the previous step from the
 * persistence provider and evaluates the feature vector according to the
 * selected strategy. E.g. if a TotalConfidence strategy is used, reposuite will
 * only consider mappings as valid, if and only if the total confidence (the sum
 * of all individual scores from the engines) yields a positive result. In a
 * veto strategies, all mappings that have at least one negative value in the
 * feature vector are dropped. Certain strategies rely on storages. E.g. the SVM
 * strategy uses a model that has been build beforehand by having a support
 * vector machine train on already mapped and verified data. If a mapping has
 * passed the strategy checks it is persisted in the database.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingStrategy extends StorageAccessor {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Registered#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return isEnabled("mapping.strategies", this.getClass().getSimpleName());
	}
	
	/**
	 * @param mapping
	 * @return
	 */
	public abstract PersistentMapping map(PersistentMapping mapping);
}
