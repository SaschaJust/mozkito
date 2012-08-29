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
package de.unisaarland.cs.st.moskito.mapping.finder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.filters.Filter;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.Composite;
import de.unisaarland.cs.st.moskito.mapping.model.Relation;
import de.unisaarland.cs.st.moskito.mapping.register.Node;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.selectors.Selector;
import de.unisaarland.cs.st.moskito.mapping.splitters.MappingSplitter;
import de.unisaarland.cs.st.moskito.mapping.storages.MappingStorage;
import de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.moskito.mapping.training.MappingTrainer;
import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * The Class MappingFinder.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MappingFinder {
	
	/** The engines. */
	private final Map<String, MappingEngine>                             engines    = new HashMap<String, MappingEngine>();
	
	/** The filters. */
	private final Map<Class<? extends Filter>, Filter>                   filters    = new HashMap<Class<? extends Filter>, Filter>();
	
	/** The selectors. */
	private final Map<Class<? extends Selector>, Selector>               selectors  = new HashMap<Class<? extends Selector>, Selector>();
	
	/** The splitters. */
	private final Map<Class<? extends MappingSplitter>, MappingSplitter> splitters  = new HashMap<Class<? extends MappingSplitter>, MappingSplitter>();
	
	/** The storages. */
	private final Map<Class<? extends MappingStorage>, MappingStorage>   storages   = new HashMap<Class<? extends MappingStorage>, MappingStorage>();
	
	/** The strategies. */
	private final Map<String, MappingStrategy>                           strategies = new HashMap<String, MappingStrategy>();
	
	/** The trainers. */
	private final Map<Class<? extends MappingTrainer>, MappingTrainer>   trainers   = new HashMap<Class<? extends MappingTrainer>, MappingTrainer>();
	
	/**
	 * Instantiates a new mapping finder.
	 */
	public MappingFinder() {
		
	}
	
	/**
	 * Adds the engine.
	 * 
	 * @param engine
	 *            the engine
	 */
	public void addEngine(final MappingEngine engine) {
		if (Logger.logInfo()) {
			Logger.info("Adding '%s' to available engines.", engine.getHandle());
		}
		
		this.engines.put(engine.getClass().getCanonicalName(), engine);
		
		provideStorages(engine);
	}
	
	/**
	 * Adds the filter.
	 * 
	 * @param filter
	 *            the filter
	 */
	public void addFilter(final Filter filter) {
		if (Logger.logInfo()) {
			Logger.info("Adding '%s' to available filters.", filter.getHandle());
		}
		
		this.filters.put(filter.getClass(), filter);
		
		provideStorages(filter);
	}
	
	/**
	 * Adds the selector.
	 * 
	 * @param selector
	 *            the selector
	 */
	public void addSelector(final Selector selector) {
		if (Logger.logInfo()) {
			Logger.info("Adding '%s' to available selectors.", selector.getHandle());
		}
		
		this.selectors.put(selector.getClass(), selector);
		provideStorages(selector);
	}
	
	/**
	 * Adds the splitter.
	 * 
	 * @param splitter
	 *            the splitter
	 */
	public void addSplitter(final MappingSplitter splitter) {
		if (Logger.logInfo()) {
			Logger.info("Adding '%s' to available splitters.", splitter.getHandle());
		}
		
		this.splitters.put(splitter.getClass(), splitter);
		provideStorages(splitter);
	}
	
	/**
	 * Adds the storage.
	 * 
	 * @param storage
	 *            the storage
	 */
	public void addStorage(final MappingStorage storage) {
		if (Logger.logInfo()) {
			Logger.info("Adding '%s' to available storages.", storage.getHandle());
		}
		
		this.storages.put(storage.getClass(), storage);
		provideStorages(storage);
	}
	
	/**
	 * Adds the strategy.
	 * 
	 * @param strategy
	 *            the strategy
	 */
	public void addStrategy(final MappingStrategy strategy) {
		if (Logger.logInfo()) {
			Logger.info("Adding '%s' to available strategies.", strategy.getHandle());
		}
		
		this.strategies.put(strategy.getClass().getCanonicalName(), strategy);
		provideStorages(strategy);
	}
	
	/**
	 * Adds the trainer.
	 * 
	 * @param trainer
	 *            the trainer
	 */
	public void addTrainer(final MappingTrainer trainer) {
		if (Logger.logInfo()) {
			Logger.info("Adding '%s' to available trainers.", trainer.getHandle());
		}
		
		this.trainers.put(trainer.getClass(), trainer);
		provideStorages(trainer);
	}
	
	/**
	 * Filter.
	 * 
	 * @param composite
	 *            the mapping
	 * @return the filtered mapping
	 */
	public FilteredMapping filter(final Composite composite) {
		final Set<Filter> triggeringFilters = new HashSet<Filter>();
		
		for (final Filter filter : this.filters.values()) {
			filter.filter(composite, triggeringFilters);
		}
		
		final FilteredMapping filteredMapping = new FilteredMapping(composite, triggeringFilters);
		return filteredMapping;
	}
	
	/**
	 * Find selectors.
	 * 
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param fromClazz
	 *            the from clazz
	 * @param toClazz
	 *            the to clazz
	 * @return the list
	 */
	private <K, V> List<Selector> findSelectors(final Class<K> fromClazz,
	                                            final Class<V> toClazz) {
		final List<Selector> list = new LinkedList<Selector>();
		
		if (Logger.logDebug()) {
			Logger.debug("Looking up selector for '%s'<->'%s'.", fromClazz.getSimpleName(), toClazz.getSimpleName());
		}
		for (final Class<? extends Selector> klass : this.selectors.keySet()) {
			try {
				if (Logger.logDebug()) {
					Logger.debug("Checking for compatibility: '%s'", klass.getSimpleName());
				}
				if (klass.newInstance().supports(fromClazz, toClazz)) {
					if (Logger.logDebug()) {
						Logger.debug("Adding selector '%s for '%s'<->'%s'.", klass.getSimpleName(),
						             fromClazz.getSimpleName(), toClazz.getSimpleName());
					}
					list.add(this.selectors.get(klass));
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Selector '%s' does not support '%s'<->'%s'.", klass.getSimpleName(),
						             fromClazz.getSimpleName(), toClazz.getSimpleName());
					}
				}
			} catch (final Exception e) {
				if (Logger.logWarn()) {
					Logger.warn("Omitting selector due to instantiation error: " + klass.getSimpleName() + " / "
					        + e.getMessage());
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Gets the candidates.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param source
	 *            the source
	 * @param targetClass
	 *            the target class
	 * @param util
	 *            the util
	 * @return the candidates
	 */
	public <T extends MappableEntity> Map<T, Set<Selector>> getCandidates(final MappableEntity source,
	                                                                      final Class<T> targetClass,
	                                                                      final PersistenceUtil util) {
		final Map<T, Set<Selector>> candidates = new HashMap<>();
		
		try {
			final List<Selector> activeSelectors = findSelectors(source.getBaseType(),
			                                                     ((MappableEntity) targetClass.newInstance()).getBaseType());
			
			for (final Selector selector : activeSelectors) {
				if (Logger.logDebug()) {
					Logger.debug("Using selector '%s' on '%s' with target type '%s'.", selector.getHandle(),
					             source.getHandle(), targetClass);
				}
				
				final List<T> parsingResults = selector.parse(source, targetClass, util);
				
				for (final T mappableEntity : parsingResults) {
					if (!candidates.containsKey(mappableEntity)) {
						candidates.put(mappableEntity, new HashSet<Selector>());
					}
					
					final Set<Selector> selectorSet = candidates.get(mappableEntity);
					selectorSet.add(selector);
				}
			}
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		}
		
		return candidates;
	}
	
	/**
	 * @return the engines
	 */
	public final Map<String, MappingEngine> getEngines() {
		// PRECONDITIONS
		
		try {
			return this.engines;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.engines, "Field '%s' in '%s'.", "engines", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the filters
	 */
	public final Map<Class<? extends Filter>, Filter> getFilters() {
		// PRECONDITIONS
		
		try {
			return this.filters;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.filters, "Field '%s' in '%s'.", "filters", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the selectors
	 */
	public final Map<Class<? extends Selector>, Selector> getSelectors() {
		// PRECONDITIONS
		
		try {
			return this.selectors;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.selectors, "Field '%s' in '%s'.", "selectors", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the splitters
	 */
	public final Map<Class<? extends MappingSplitter>, MappingSplitter> getSplitters() {
		// PRECONDITIONS
		
		try {
			return this.splitters;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.splitters, "Field '%s' in '%s'.", "splitters", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the storages
	 */
	public final Map<Class<? extends MappingStorage>, MappingStorage> getStorages() {
		// PRECONDITIONS
		
		try {
			return this.storages;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.storages, "Field '%s' in '%s'.", "storages", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the strategies
	 */
	public final Map<String, MappingStrategy> getStrategies() {
		// PRECONDITIONS
		
		try {
			return this.strategies;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.strategies, "Field '%s' in '%s'.", "strategies", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the trainers
	 */
	public final Map<Class<? extends MappingTrainer>, MappingTrainer> getTrainers() {
		// PRECONDITIONS
		
		try {
			return this.trainers;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.trainers, "Field '%s' in '%s'.", "trainers", getClass().getSimpleName());
		}
	}
	
	/**
	 * Load data.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public void loadData(final PersistenceUtil persistenceUtil) {
		for (final Class<? extends MappingStorage> key : this.storages.keySet()) {
			this.storages.get(key).loadData(persistenceUtil);
		}
	}
	
	/**
	 * Map.
	 * 
	 * @param strategy
	 *            the strategy
	 * @param composite
	 *            the mapping
	 * @return the mapping
	 */
	public Composite map(final MappingStrategy strategy,
	                     final Composite composite) {
		if (Logger.logDebug()) {
			Logger.debug("Mapping with strategy: " + strategy.getHandle());
		}
		strategy.map(composite);
		
		return composite;
		
	}
	
	/**
	 * Provide storages.
	 * 
	 * @param accessor
	 *            the accessor
	 */
	private void provideStorages(final Node accessor) {
		for (final Class<? extends MappingStorage> key : accessor.storageDependency()) {
			if (!this.storages.keySet().contains(key)) {
				try {
					final MappingStorage storage = key.newInstance();
					this.storages.put(key, storage);
				} catch (final InstantiationException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				} catch (final IllegalAccessException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
			
			accessor.provideStorage(this.storages.get(key));
		}
	}
	
	/**
	 * Score.
	 * 
	 * @param engine
	 *            the engine
	 * @param element1
	 *            the element1
	 * @param element2
	 *            the element2
	 * @return the computed scoring for transaction/report relation
	 */
	public Relation score(final MappingEngine engine,
	                      final Relation score) {
		
		if (Logger.logDebug()) {
			Logger.debug("Scoring with engine: " + engine.getHandle());
		}
		
		final Expression expression = engine.supported();
		if (expression == null) {
			throw new UnrecoverableError("Engine: " + engine.getHandle()
			        + " returns NULL when asked for supported fields.");
		}
		
		final MappableEntity element1 = score.getFrom();
		final MappableEntity element2 = score.getTo();
		
		final int check = expression.check(element1.getClass(), element2.getClass());
		
		if (check > 0) {
			engine.score(element1, element2, score);
		} else if (check < 0) {
			engine.score(element2, element1, score);
		} else if (Logger.logInfo()) {
			Logger.info("Skipping engine " + engine.getHandle() + " due to type incompatibility: "
			        + expression.toString());
		}
		
		return score;
	}
	
	/**
	 * Split.
	 * 
	 * @param data
	 *            the data
	 * @param util
	 *            the util
	 * @return the list
	 */
	public List<Annotated> split(final FilteredMapping data,
	                             final PersistenceUtil util) {
		final LinkedList<Annotated> list = new LinkedList<Annotated>();
		
		for (final Class<? extends MappingSplitter> key : this.splitters.keySet()) {
			final MappingSplitter splitter = this.splitters.get(key);
			
			list.addAll(splitter.process(util));
		}
		return list;
	}
	
}
