/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.settings;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.requirements.Required;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector;
import de.unisaarland.cs.st.moskito.mapping.splitters.MappingSplitter;
import de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.moskito.mapping.training.MappingTrainer;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingArguments extends ArgumentSet<MappingFinder> {
	
	private final Set<MappingEngine>   engines    = new HashSet<MappingEngine>();
	private final Set<MappingStrategy> strategies = new HashSet<MappingStrategy>();
	private final Set<MappingFilter>   filters    = new HashSet<MappingFilter>();
	private final Set<MappingSelector> selectors  = new HashSet<MappingSelector>();
	private final Set<MappingSplitter> splitters  = new HashSet<MappingSplitter>();
	private final Set<MappingTrainer>  trainers   = new HashSet<MappingTrainer>();
	
	/**
	 * @param argumentSet
	 * @param requirement
	 * @throws ArgumentRegistrationException
	 */
	public MappingArguments(final ArgumentSet<?> argumentSet, final Requirement requirement)
	        throws ArgumentRegistrationException {
		super(argumentSet, "Definies mapping specific settings.", requirement);
		
		this.engines.addAll(ArgumentSet.provideDynamicArguments(argumentSet, MappingEngine.class, "bleh blub",
		                                                        new Required(), null, "Mapping", "Engines", true));
		
		this.filters.addAll(ArgumentSet.provideDynamicArguments(argumentSet, MappingFilter.class, "bleh blub",
		                                                        new Required(), null, "Mapping", "Filters", true));
		
		this.selectors.addAll(ArgumentSet.provideDynamicArguments(argumentSet, MappingSelector.class, "bleh blub",
		                                                          new Required(), null, "Mapping", "Selectors", true));
		
		this.splitters.addAll(ArgumentSet.provideDynamicArguments(argumentSet, MappingSplitter.class, "bleh blub",
		                                                          new Required(), null, "Mapping", "Splitters", true));
		
		this.strategies.addAll(ArgumentSet.provideDynamicArguments(argumentSet, MappingStrategy.class, "bleh blub",
		                                                           new Required(), null, "Mapping", "Strategies", true));
		
		this.trainers.addAll(ArgumentSet.provideDynamicArguments(argumentSet, MappingTrainer.class, "bleh blub",
		                                                         new Required(), null, "Mapping", "Trainers", true));
	}
	
	/**
	 * @return
	 */
	public Set<MappingEngine> getEngines() {
		return this.engines;
	}
	
	/**
	 * @return the filters
	 */
	public final Set<MappingFilter> getFilters() {
		return this.filters;
	}
	
	/**
	 * @return the selectors
	 */
	public final Set<MappingSelector> getSelectors() {
		return this.selectors;
	}
	
	/**
	 * @return the splitters
	 */
	public final Set<MappingSplitter> getSplitters() {
		return this.splitters;
	}
	
	/**
	 * @return
	 */
	public Set<MappingStrategy> getStrategies() {
		return this.strategies;
	}
	
	/**
	 * @return the trainers
	 */
	public final Set<MappingTrainer> getTrainers() {
		return this.trainers;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#init()
	 */
	@Override
	protected boolean init() {
		final MappingFinder finder = new MappingFinder();
		
		for (final MappingEngine engine : this.engines) {
			finder.addEngine(engine);
		}
		
		for (final MappingStrategy strategy : this.strategies) {
			finder.addStrategy(strategy);
		}
		
		for (final MappingFilter filter : this.filters) {
			finder.addFilter(filter);
		}
		
		for (final MappingSelector selector : this.selectors) {
			finder.addSelector(selector);
		}
		
		for (final MappingSplitter splitter : this.splitters) {
			finder.addSplitter(splitter);
		}
		
		for (final MappingTrainer trainer : this.trainers) {
			finder.addTrainer(trainer);
		}
		
		setCachedValue(finder);
		return true;
	}
	
}
