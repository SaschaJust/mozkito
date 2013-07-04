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
package org.mozkito.mappings.settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.TupleArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Highlighter;
import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.issues.model.Report;
import org.mozkito.mappings.engines.Engine;
import org.mozkito.mappings.filters.Filter;
import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.register.Node;
import org.mozkito.mappings.selectors.Selector;
import org.mozkito.mappings.splitters.Splitter;
import org.mozkito.mappings.storages.GraphStorage;
import org.mozkito.mappings.storages.LuceneStorage;
import org.mozkito.mappings.storages.PersistenceStorage;
import org.mozkito.mappings.storages.RepositoryStorage;
import org.mozkito.mappings.storages.Storage;
import org.mozkito.mappings.strategies.Strategy;
import org.mozkito.mappings.training.Trainer;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.utilities.datastructures.Tuple;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class MappingArguments.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class MappingOptions extends ArgumentSetOptions<Finder, ArgumentSet<Finder, MappingOptions>> {
	
	/** The Constant DESCRIPTION. */
	private static final String   DESCRIPTION        = Messages.getString("MappingOptions.description"); //$NON-NLS-1$
	                                                                                                     
	/** The Constant NAME. */
	public static final String    TAG                = "mappings";                                      //$NON-NLS-1$
	                                                                                                     
	static {
		Logger.addHighlighter(new Highlighter(LogLevel.ERROR, LogLevel.DEBUG) {
			
			@Override
			public boolean matches(final String message,
			                       final LogLevel level,
			                       final String prefix) {
				return message.matches("Found no persistent.*"); //$NON-NLS-1$
			}
		});
	}
	
	/** The engine options. */
	private EngineOptions         engineOptions;
	
	/** The engines. */
	private final Set<Engine>     engines            = new HashSet<Engine>();
	
	/** The filters. */
	private final Set<Filter>     filters            = new HashSet<Filter>();
	
	/** The selector options. */
	private SelectorOptions       selectorOptions;
	
	/** The selectors. */
	private final Set<Selector>   selectors          = new HashSet<Selector>();
	
	/** The source options. */
	private TupleArgument.Options sourceOptions;
	/** The splitters. */
	private final Set<Splitter>   splitters          = new HashSet<Splitter>();
	
	/** The splitter options. */
	private SplitterOptions       splitterOptions;
	
	/** The strategies. */
	private final Set<Strategy>   strategies         = new HashSet<Strategy>();
	
	/** The trainers. */
	private final Set<Trainer>    trainers           = new HashSet<Trainer>();
	
	/** The strategy options. */
	private StrategyOptions       strategyOptions;
	
	/** The filter options. */
	private FilterOptions         filterOptions;
	
	/** The database options. */
	private final DatabaseOptions databaseOptions;
	
	/** The graph options. */
	private final GraphOptions    graphOptions;
	
	/** The persistence storage. */
	private PersistenceStorage    persistenceStorage = null;
	
	/** The graph storage. */
	private GraphStorage          graphStorage       = null;
	
	/** The lucene storage. */
	private LuceneStorage         luceneStorage      = null;
	
	/** The repository storage. */
	private RepositoryStorage     repositoryStorage  = null;
	
	/**
	 * Instantiates a new mapping options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 * @param databaseOptions
	 *            the database options
	 * @param graphOptions
	 *            the graph options
	 */
	public MappingOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
	        final DatabaseOptions databaseOptions, final GraphOptions graphOptions) {
		super(argumentSet, MappingOptions.TAG, MappingOptions.DESCRIPTION, requirements);
		this.databaseOptions = databaseOptions;
		this.graphOptions = graphOptions;
	}
	
	/**
	 * Gets the engines.
	 * 
	 * @return the engines
	 */
	public Set<Engine> getEngines() {
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
	public final Set<Splitter> getSplitters() {
		return this.splitters;
	}
	
	/**
	 * Gets the strategies.
	 * 
	 * @return the strategies
	 */
	public Set<Strategy> getStrategies() {
		return this.strategies;
	}
	
	/**
	 * Gets the trainers.
	 * 
	 * @return the trainers
	 */
	public final Set<Trainer> getTrainers() {
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
	public Finder init() {
		this.persistenceStorage = null;
		this.graphStorage = null;
		this.luceneStorage = null;
		this.repositoryStorage = null;
		
		// check for available storage provider
		if (this.databaseOptions != null) {
			final PersistenceUtil persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
			if (persistenceUtil != null) {
				this.persistenceStorage = new PersistenceStorage(persistenceUtil);
			} else {
				// TODO error
			}
		}
		
		final Finder finder = new Finder();
		
		final ArgumentSet<Set<Selector>, SelectorOptions> selectorArgument = getSettings().getArgumentSet(this.selectorOptions);
		
		for (final Selector selector : selectorArgument.getValue()) {
			provideStorages(selector);
			finder.addSelector(selector);
		}
		
		final ArgumentSet<Set<Engine>, EngineOptions> engineArgument = getSettings().getArgumentSet(this.engineOptions);
		
		for (final Engine engine : engineArgument.getValue()) {
			provideStorages(engine);
			finder.addEngine(engine);
		}
		
		final ArgumentSet<Set<Strategy>, StrategyOptions> strategyArgument = getSettings().getArgumentSet(this.strategyOptions);
		
		for (final Strategy strategy : strategyArgument.getValue()) {
			provideStorages(strategy);
			finder.addStrategy(strategy);
		}
		
		final ArgumentSet<Set<Filter>, FilterOptions> filterArgument = getSettings().getArgumentSet(this.filterOptions);
		
		for (final Filter filter : filterArgument.getValue()) {
			provideStorages(filter);
			finder.addFilter(filter);
		}
		
		final ArgumentSet<Set<Splitter>, SplitterOptions> splitterArgument = getSettings().getArgumentSet(this.splitterOptions);
		
		for (final Splitter splitter : splitterArgument.getValue()) {
			provideStorages(splitter);
			finder.addSplitter(splitter);
		}
		
		return finder;
	}
	
	/**
	 * Provide storages.
	 * 
	 * @param node
	 *            the node
	 */
	private void provideStorages(final Node node) {
		for (final Class<? extends Storage> clazz : node.storageDependency()) {
			if (clazz.equals(PersistenceStorage.class)) {
				if (this.persistenceStorage != null) {
					node.provideStorage(this.persistenceStorage);
				} else {
					// TODO error
				}
			} else if (clazz.equals(GraphStorage.class)) {
				if (this.graphStorage != null) {
					node.provideStorage(this.graphStorage);
				} else {
					// TODO error
				}
			} else if (clazz.equals(LuceneStorage.class)) {
				if (this.luceneStorage != null) {
					node.provideStorage(this.luceneStorage);
				} else {
					// TODO error
				}
			} else if (clazz.equals(RepositoryStorage.class)) {
				if (this.repositoryStorage != null) {
					node.provideStorage(this.repositoryStorage);
				} else {
					// TODO error
				}
			} else {
				// TODO error: unsupported storage provider
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final HashMap<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			this.sourceOptions = new TupleArgument.Options(set, "sourceTypes", //$NON-NLS-1$
			                                               Messages.getString("optionSourceTypes"), //$NON-NLS-1$
			                                               new Tuple<String, String>(ChangeSet.class.getSimpleName(),
			                                                                         Report.class.getSimpleName()),
			                                               Requirement.required);
			map.put(this.sourceOptions.getName(), this.sourceOptions);
			
			this.selectorOptions = new SelectorOptions(set, Requirement.required);
			map.put(this.selectorOptions.getName(), this.selectorOptions);
			
			this.engineOptions = new EngineOptions(set, Requirement.required);
			map.put(this.engineOptions.getName(), this.engineOptions);
			
			this.strategyOptions = new StrategyOptions(set, Requirement.required);
			map.put(this.strategyOptions.getName(), this.strategyOptions);
			
			this.filterOptions = new FilterOptions(set, Requirement.required);
			map.put(this.filterOptions.getName(), this.filterOptions);
			
			this.splitterOptions = new SplitterOptions(set, Requirement.required);
			map.put(this.splitterOptions.getName(), this.splitterOptions);
			
			if (this.databaseOptions != null) {
				map.put(this.databaseOptions.getName(), this.databaseOptions);
			}
			
			if (this.graphOptions != null) {
				map.put(this.graphOptions.getName(), this.graphOptions);
			}
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
