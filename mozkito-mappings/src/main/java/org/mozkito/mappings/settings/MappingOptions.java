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
package org.mozkito.mappings.settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.engines.MappingEngine;
import org.mozkito.mappings.engines.MappingEngine.Options;
import org.mozkito.mappings.filters.Filter;
import org.mozkito.mappings.finder.MappingFinder;
import org.mozkito.mappings.selectors.Selector;
import org.mozkito.mappings.splitters.MappingSplitter;
import org.mozkito.mappings.strategies.MappingStrategy;
import org.mozkito.mappings.training.MappingTrainer;
import org.mozkito.versions.model.RCSTransaction;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.TupleArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Highlighter;
import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class MappingArguments.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MappingOptions extends ArgumentSetOptions<MappingFinder, ArgumentSet<MappingFinder, MappingOptions>> {
	
	static {
		Logger.addHighlighter(new Highlighter(LogLevel.ERROR, LogLevel.DEBUG) {
			
			@Override
			public boolean matches(final String message,
			                       final LogLevel level,
			                       final String prefix) {
				return message.matches("Adding new mapping engines dependency.*");
			}
		});
	}
	
	private static final String        DESCRIPTION = "TODO";
	public static final String         NAME        = "mapping";
	
	/** The engines. */
	private final Set<MappingEngine>   engines     = new HashSet<MappingEngine>();
	
	/** The strategies. */
	private final Set<MappingStrategy> strategies  = new HashSet<MappingStrategy>();
	
	/** The filters. */
	private final Set<Filter>   filters     = new HashSet<Filter>();
	
	/** The selectors. */
	private final Set<Selector> selectors   = new HashSet<Selector>();
	
	/** The splitters. */
	private final Set<MappingSplitter> splitters   = new HashSet<MappingSplitter>();
	
	/** The trainers. */
	private final Set<MappingTrainer>  trainers    = new HashSet<MappingTrainer>();
	private MappingEngine.Options      engineOptions;
	private TupleArgument.Options      sourceOptions;
	private Selector.Options    selectorOptions;
	
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
	public final Set<Filter> getFilters() {
		return this.filters;
	}
	
	/**
	 * Gets the selectors.
	 * 
	 * @return the selectors
	 */
	public final Set<Selector> getSelectors() {
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
		
		for (final Filter filter : this.filters) {
			finder.addFilter(filter);
		}
		
		final ArgumentSet<Set<Selector>, Selector.Options> selectorArgument = getSettings().getArgumentSet(this.selectorOptions);
		for (final Selector selector : selectorArgument.getValue()) {
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
			
			this.sourceOptions = new TupleArgument.Options(
			                                               set,
			                                               "sourceTypes",
			                                               "Determines what kind of stuff you want to map. E.g. =RCSTransaction,Report",
			                                               new Tuple<String, String>(
			                                                                         RCSTransaction.class.getSimpleName(),
			                                                                         Report.class.getSimpleName()),
			                                               Requirement.required);
			map.put(this.sourceOptions.getName(), this.sourceOptions);
			
			this.engineOptions = MappingEngine.getOptions(set);
			map.put(this.engineOptions.getName(), this.engineOptions);
			
			this.selectorOptions = Selector.getOptions(set);
			map.put(this.selectorOptions.getName(), this.selectorOptions);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
