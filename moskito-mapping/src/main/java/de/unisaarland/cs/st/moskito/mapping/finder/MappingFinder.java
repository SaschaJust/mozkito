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
import de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.FilteredMapping;
import de.unisaarland.cs.st.moskito.mapping.model.IMapping;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.register.Node;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector;
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
	private final Map<Class<? extends MappingFilter>, MappingFilter>     filters    = new HashMap<Class<? extends MappingFilter>, MappingFilter>();
	
	/** The selectors. */
	private final Map<Class<? extends MappingSelector>, MappingSelector> selectors  = new HashMap<Class<? extends MappingSelector>, MappingSelector>();
	
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
		this.engines.put(engine.getClass().getCanonicalName(), engine);
		
		provideStorages(engine);
	}
	
	/**
	 * Adds the filter.
	 * 
	 * @param filter
	 *            the filter
	 */
	public void addFilter(final MappingFilter filter) {
		this.filters.put(filter.getClass(), filter);
		
		provideStorages(filter);
	}
	
	/**
	 * Adds the selector.
	 * 
	 * @param selector
	 *            the selector
	 */
	public void addSelector(final MappingSelector selector) {
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
		this.trainers.put(trainer.getClass(), trainer);
		provideStorages(trainer);
	}
	
	/**
	 * Filter.
	 * 
	 * @param mapping
	 *            the mapping
	 * @return the filtered mapping
	 */
	public FilteredMapping filter(final IMapping mapping) {
		final Set<? extends MappingFilter> triggeringFilters = new HashSet<MappingFilter>();
		
		for (final MappingFilter filter : this.filters.values()) {
			filter.filter(mapping, triggeringFilters);
		}
		
		final FilteredMapping filteredMapping = new FilteredMapping(mapping, triggeringFilters);
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
	private <K, V> List<MappingSelector> findSelectors(final Class<K> fromClazz,
	                                                   final Class<V> toClazz) {
		final List<MappingSelector> list = new LinkedList<MappingSelector>();
		
		for (final Class<? extends MappingSelector> klass : this.selectors.keySet()) {
			try {
				if (klass.newInstance().supports(fromClazz, toClazz)) {
					list.add(this.selectors.get(klass));
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
	public <T extends MappableEntity> Set<T> getCandidates(final MappableEntity source,
	                                                       final Class<T> targetClass,
	                                                       final PersistenceUtil util) {
		final Set<T> candidates = new HashSet<T>();
		
		try {
			final List<MappingSelector> activeSelectors = findSelectors(source.getBaseType(),
			                                                            ((MappableEntity) targetClass.newInstance()).getBaseType());
			
			for (final MappingSelector selector : activeSelectors) {
				candidates.addAll(selector.parse(source, targetClass, util));
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
	public final Map<Class<? extends MappingFilter>, MappingFilter> getFilters() {
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
	public final Map<Class<? extends MappingSelector>, MappingSelector> getSelectors() {
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
	 * @param mapping
	 *            the mapping
	 * @return the mapping
	 */
	public Mapping map(final MappingStrategy strategy,
	                   final Mapping mapping) {
		if (Logger.logDebug()) {
			Logger.debug("Mapping with strategy: " + strategy.getHandle());
		}
		strategy.map(mapping);
		
		return mapping;
		
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
	public Mapping score(final MappingEngine engine,
	                     final MappableEntity element1,
	                     final MappableEntity element2) {
		final Mapping score = new Mapping(element1, element2);
		
		if (Logger.logDebug()) {
			Logger.debug("Scoring with engine: " + engine.getHandle());
		}
		
		final Expression expression = engine.supported();
		if (expression == null) {
			throw new UnrecoverableError("Engine: " + engine.getHandle()
			        + " returns NULL when asked for supported fields.");
		}
		
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
