/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.finder;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.reposuite.mapping.model.FilteredMapping;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping;
import de.unisaarland.cs.st.reposuite.mapping.selectors.MappingSelector;
import de.unisaarland.cs.st.reposuite.mapping.splitters.MappingSplitter;
import de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage;
import de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingFinder {
	
	private final Map<String, MappingEngine>                                         engines    = new HashMap<String, MappingEngine>();
	
	private final Map<String, MappingStrategy>                                       strategies = new HashMap<String, MappingStrategy>();
	
	private final Map<Class<? extends MappingStorage>, MappingStorage>               storages   = new HashMap<Class<? extends MappingStorage>, MappingStorage>();
	
	private final Map<Class<? extends MappingFilter>, MappingFilter>                 filters    = new HashMap<Class<? extends MappingFilter>, MappingFilter>();
	private final Map<Class<? extends MappingSplitter>, MappingSplitter>             splitters  = new HashMap<Class<? extends MappingSplitter>, MappingSplitter>();
	private final Map<Class<? extends MappingSelector<?, ?>>, MappingSelector<?, ?>> selectors  = new HashMap<Class<? extends MappingSelector<?, ?>>, MappingSelector<?, ?>>();
	
	/**
	 * @param engine
	 */
	public void addEngine(final MappingEngine engine) {
		this.engines.put(engine.getClass().getCanonicalName(), engine);
		for (Class<? extends MappingStorage> key : engine.storageDependency()) {
			if (!this.storages.keySet().contains(key)) {
				try {
					MappingStorage storage = key.newInstance();
					this.storages.put(key, storage);
				} catch (InstantiationException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
			engine.provideStorage(this.storages.get(key));
		}
	}
	
	/**
	 * @param filter
	 */
	public void addFilter(final MappingFilter filter) {
		this.filters.put(filter.getClass(), filter);
	}
	
	/**
	 * @param selector
	 */
	@SuppressWarnings ("unchecked")
	public void addSelector(final MappingSelector<?, ?> selector) {
		this.selectors.put((Class<? extends MappingSelector<?, ?>>) selector.getClass(), selector);
		for (Class<? extends MappingStorage> key : selector.storageDependency()) {
			if (!this.storages.keySet().contains(key)) {
				try {
					MappingStorage storage = key.newInstance();
					this.storages.put(key, storage);
				} catch (InstantiationException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
			selector.provideStorage(this.storages.get(key));
		}
	}
	
	/**
	 * @param splitter
	 */
	public void addSplitter(final MappingSplitter splitter) {
		this.splitters.put(splitter.getClass(), splitter);
	}
	
	/**
	 * @param storage
	 */
	public void addStorage(final MappingStorage storage) {
		this.storages.put(storage.getClass(), storage);
	}
	
	/**
	 * @param strategy
	 */
	public void addStrategy(final MappingStrategy strategy) {
		this.strategies.put(strategy.getClass().getCanonicalName(), strategy);
	}
	
	/**
	 * @param mapping
	 * @return
	 */
	public FilteredMapping filter(final RCSBugMapping mapping) {
		// TODO
		return null;
	}
	
	/**
	 * @param <K>
	 * @param <V>
	 * @param fromClazz
	 * @param toClazz
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	private <K, V> List<MappingSelector<K, V>> findSelectors(final Class<K> fromClazz,
	                                                         final Class<V> toClazz) {
		List<MappingSelector<K, V>> list = new LinkedList<MappingSelector<K, V>>();
		
		for (Class<? extends MappingSelector<?, ?>> klass : this.selectors.keySet()) {
			ParameterizedType type = (ParameterizedType) klass.getGenericSuperclass();
			if ((type.getActualTypeArguments()[0] == fromClazz) && (type.getActualTypeArguments()[1] == toClazz)) {
				list.add((MappingSelector<K, V>) this.selectors.get(klass));
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
	public <K, V> Set<V> getCandidates(final K source,
	                                   final Class<V> targetClass) {
		Set<V> candidates = new HashSet<V>();
		
		@SuppressWarnings ("unchecked")
		List<MappingSelector<K, V>> selectors = findSelectors((Class<K>) source.getClass(), targetClass);
		
		for (MappingSelector<K, V> selector : selectors) {
			candidates.addAll(selector.parse(source));
		}
		
		return candidates;
	}
	
	/**
	 * @param persistenceUtil
	 */
	public void loadData(final PersistenceUtil persistenceUtil) {
		for (Class<? extends MappingStorage> key : this.storages.keySet()) {
			this.storages.get(key).loadData(persistenceUtil);
		}
	}
	
	/**
	 * @param score
	 * @return
	 */
	public RCSBugMapping map(final MapScore score) {
		RCSBugMapping mapping = new RCSBugMapping(score);
		for (String key : this.strategies.keySet()) {
			MappingStrategy strategy = this.strategies.get(key);
			mapping = strategy.map(mapping);
		}
		
		if ((mapping.getValid() != null) && (mapping.getValid() == true)) {
			return mapping;
		} else {
			return null;
		}
	}
	
	/**
	 * @param transaction
	 * @param report
	 * @return the computed scoring for transaction/report relation
	 */
	public MapScore score(final RCSTransaction transaction,
	                      final Report report) {
		MapScore score = new MapScore(transaction, report);
		
		if (Logger.logDebug()) {
			Logger.debug("Scoring with " + this.engines.size() + " engines: "
			        + JavaUtils.collectionToString(this.engines.values()));
		}
		
		for (String engineName : this.engines.keySet()) {
			MappingEngine mappingEngine = this.engines.get(engineName);
			mappingEngine.score(transaction, report, score);
		}
		return score;
	}
	
	/**
	 * @return
	 */
	public List<Annotated> split() {
		return new LinkedList<Annotated>();
	}
	
}
