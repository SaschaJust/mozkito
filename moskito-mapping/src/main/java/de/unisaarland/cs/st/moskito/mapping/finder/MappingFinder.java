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
package de.unisaarland.cs.st.moskito.mapping.finder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.FilteredMapping;
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
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingFinder {
	
	private final Map<String, MappingEngine>                             engines    = new HashMap<String, MappingEngine>();
	private final Map<Class<? extends MappingFilter>, MappingFilter>     filters    = new HashMap<Class<? extends MappingFilter>, MappingFilter>();
	private final Map<Class<? extends MappingSelector>, MappingSelector> selectors  = new HashMap<Class<? extends MappingSelector>, MappingSelector>();
	private final Map<Class<? extends MappingSplitter>, MappingSplitter> splitters  = new HashMap<Class<? extends MappingSplitter>, MappingSplitter>();
	private final Map<Class<? extends MappingStorage>, MappingStorage>   storages   = new HashMap<Class<? extends MappingStorage>, MappingStorage>();
	private final Map<String, MappingStrategy>                           strategies = new HashMap<String, MappingStrategy>();
	private final Map<Class<? extends MappingTrainer>, MappingTrainer>   trainers   = new HashMap<Class<? extends MappingTrainer>, MappingTrainer>();
	
	/**
	 * @param engine
	 */
	public void addEngine(final MappingEngine engine) {
		this.engines.put(engine.getClass().getCanonicalName(), engine);
		
		provideStorages(engine);
	}
	
	/**
	 * @param filter
	 */
	public void addFilter(final MappingFilter filter) {
		this.filters.put(filter.getClass(), filter);
		
		provideStorages(filter);
	}
	
	/**
	 * @param selector
	 */
	public void addSelector(final MappingSelector selector) {
		this.selectors.put(selector.getClass(), selector);
		provideStorages(selector);
	}
	
	/**
	 * @param splitter
	 */
	public void addSplitter(final MappingSplitter splitter) {
		this.splitters.put(splitter.getClass(), splitter);
		provideStorages(splitter);
	}
	
	/**
	 * @param storage
	 */
	public void addStorage(final MappingStorage storage) {
		this.storages.put(storage.getClass(), storage);
		provideStorages(storage);
	}
	
	/**
	 * @param strategy
	 */
	public void addStrategy(final MappingStrategy strategy) {
		this.strategies.put(strategy.getClass().getCanonicalName(), strategy);
		provideStorages(strategy);
	}
	
	/**
	 * @param trainer
	 */
	public void addTrainer(final MappingTrainer trainer) {
		this.trainers.put(trainer.getClass(), trainer);
		provideStorages(trainer);
	}
	
	/**
	 * @param mapping
	 * @return
	 */
	public FilteredMapping filter(final Mapping mapping) {
		final Set<? extends MappingFilter> triggeringFilters = new HashSet<MappingFilter>();
		
		for (final MappingFilter filter : this.filters.values()) {
			filter.filter(mapping, triggeringFilters);
		}
		
		final FilteredMapping filteredMapping = new FilteredMapping(mapping, triggeringFilters);
		return filteredMapping;
	}
	
	/**
	 * @param <K>
	 * @param <V>
	 * @param fromClazz
	 * @param toClazz
	 * @return
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
					Logger.warn("Omitting selector " + klass.getSimpleName() + " due to instantiation error: "
					        + e.getMessage());
				}
			}
		}
		
		return list;
	}
	
	/**
	 * @param <K>
	 * @param <V>
	 * @param source
	 * @param targetClass
	 * @return
	 */
	public <T extends MappableEntity> Set<T> getCandidates(final MappableEntity source,
	                                                       final Class<T> targetClass) {
		final Set<T> candidates = new HashSet<T>();
		
		try {
			final List<MappingSelector> selectors = findSelectors(source.getBaseType(),
			                                                      ((MappableEntity) targetClass.newInstance()).getBaseType());
			
			for (final MappingSelector selector : selectors) {
				candidates.addAll(selector.parse(source, targetClass));
			}
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		}
		
		return candidates;
	}
	
	/**
	 * @param persistenceUtil
	 */
	public void loadData(final PersistenceUtil persistenceUtil) {
		for (final Class<? extends MappingStorage> key : this.storages.keySet()) {
			this.storages.get(key).loadData(persistenceUtil);
		}
	}
	
	/**
	 * @param score
	 * @return
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
	 * @param accessor
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
	 * @param transaction
	 * @param report
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
			Logger.info("Skipping engine " + engine.getHandle() + " due to type incompatibility.");
		}
		
		return score;
	}
	
	/**
	 * @return
	 */
	public List<Annotated> split(final FilteredMapping data) {
		final LinkedList<Annotated> list = new LinkedList<Annotated>();
		
		for (final Class<? extends MappingSplitter> key : this.splitters.keySet()) {
			final MappingSplitter splitter = this.splitters.get(key);
			
			list.addAll(splitter.process());
		}
		return list;
	}
	
}
