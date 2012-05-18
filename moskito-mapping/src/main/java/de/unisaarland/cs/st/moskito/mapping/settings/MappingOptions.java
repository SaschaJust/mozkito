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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine.Options;
import de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector;
import de.unisaarland.cs.st.moskito.mapping.splitters.MappingSplitter;
import de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.moskito.mapping.training.MappingTrainer;

/**
 * The Class MappingArguments.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MappingOptions extends ArgumentSetOptions<MappingFinder, ArgumentSet<MappingFinder, MappingOptions>> {
	
	private static final String        DESCRIPTION = "TODO";
	public static final String         NAME        = "mapping";
	
	/** The engines. */
	private final Set<MappingEngine>   engines     = new HashSet<MappingEngine>();
	
	/** The strategies. */
	private final Set<MappingStrategy> strategies  = new HashSet<MappingStrategy>();
	
	/** The filters. */
	private final Set<MappingFilter>   filters     = new HashSet<MappingFilter>();
	
	/** The selectors. */
	private final Set<MappingSelector> selectors   = new HashSet<MappingSelector>();
	
	/** The splitters. */
	private final Set<MappingSplitter> splitters   = new HashSet<MappingSplitter>();
	
	/** The trainers. */
	private final Set<MappingTrainer>  trainers    = new HashSet<MappingTrainer>();
	private Options                    engineOptions;
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param requirements
	 */
	public MappingOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, NAME, DESCRIPTION, requirements);
	}
	
	/**
	 * Gets the engines.
	 * 
	 * @return the engines
	 */
	public Set<MappingEngine> getEngines() {
		return this.engines;
	}
	
	/**
	 * Gets the filters.
	 * 
	 * @return the filters
	 */
	public final Set<MappingFilter> getFilters() {
		return this.filters;
	}
	
	/**
	 * Gets the selectors.
	 * 
	 * @return the selectors
	 */
	public final Set<MappingSelector> getSelectors() {
		return this.selectors;
	}
	
	/**
	 * Gets the splitters.
	 * 
	 * @return the splitters
	 */
	public final Set<MappingSplitter> getSplitters() {
		return this.splitters;
	}
	
	/**
	 * Gets the strategies.
	 * 
	 * @return the strategies
	 */
	public Set<MappingStrategy> getStrategies() {
		return this.strategies;
	}
	
	/**
	 * Gets the trainers.
	 * 
	 * @return the trainers
	 */
	public final Set<MappingTrainer> getTrainers() {
		return this.trainers;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#init()
	 */
	/**
	 * Inits the.
	 * 
	 * @return true, if successful
	 */
	@Override
	public MappingFinder init() {
		final MappingFinder finder = new MappingFinder();
		
		final ArgumentSet<Set<MappingEngine>, Options> engineArgument = getSettings().getArgumentSet(this.engineOptions);
		
		for (final MappingEngine engine : engineArgument.getValue()) {
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
		
		return finder;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final HashMap<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			this.engineOptions = MappingEngine.getOptions(set);
			map.put(this.engineOptions.getName(), this.engineOptions);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
