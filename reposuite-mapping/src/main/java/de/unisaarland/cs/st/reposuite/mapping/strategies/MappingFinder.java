/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.strategies;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingFinder {
	
	private final Map<String, MappingEngine>   engines    = new HashMap<String, MappingEngine>();
	private final Map<String, MappingStrategy> strategies = new HashMap<String, MappingStrategy>();
	
	/**
	 * @param engine
	 */
	public void addEngine(final MappingEngine engine) {
		this.engines.put(engine.getClass().getCanonicalName(), engine);
	}
	
	/**
	 * @param persistenceUtil
	 */
	public void addPersistenceUtil(final PersistenceUtil persistenceUtil) {
		for (String engineName : this.engines.keySet()) {
			MappingEngine mappingEngine = this.engines.get(engineName);
			mappingEngine.loadData(persistenceUtil);
		}
	}
	
	/**
	 * @param strategy
	 */
	public void addStrategy(final MappingStrategy strategy) {
		this.strategies.put(strategy.getClass().getCanonicalName(), strategy);
	}
	
	/**
	 * @param transaction
	 * @return
	 */
	public Set<Long> getCandidates(final RCSTransaction transaction) {
		Set<Long> candidates = new HashSet<Long>();
		
		Regex pattern = new Regex("({id}\\d{2,})");
		List<List<RegexGroup>> findAll = pattern.findAll(transaction.getMessage());
		
		if (findAll != null) {
			for (List<RegexGroup> match : findAll) {
				candidates.add(Long.parseLong(match.get(0).getMatch()));
			}
		}
		
		return candidates;
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
	
}
