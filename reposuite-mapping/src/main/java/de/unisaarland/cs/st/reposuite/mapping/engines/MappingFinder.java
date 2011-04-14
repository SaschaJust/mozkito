/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingFinder {
	
	private final Map<String, MappingEngine> engines = new HashMap<String, MappingEngine>();
	
	public MappingFinder(final MappingSettings settings) {
		try {
			Package package1 = MappingEngine.class.getPackage();
			Collection<Class<?>> classesExtendingClass = ClassFinder.getClassesExtendingClass(package1,
			                                                                                  MappingEngine.class);
			
			for (Class<?> klass : classesExtendingClass) {
				
				if (Logger.logInfo()) {
					Logger.info("Adding new MappingEngine " + klass.getCanonicalName());
				}
				
				Constructor<?> constructor = klass.getConstructor(MappingSettings.class);
				this.engines.put(klass.getCanonicalName(), (MappingEngine) constructor.newInstance(settings));
			}
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
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
	 * @param transaction
	 * @param report
	 * @return the computed scoring for transaction/report relation
	 */
	public MapScore score(final RCSTransaction transaction,
	                      final Report report) {
		MapScore score = new MapScore(transaction, report);
		
		for (String engineName : this.engines.keySet()) {
			MappingEngine mappingEngine = this.engines.get(engineName);
			mappingEngine.score(transaction, report, score);
		}
		return score;
	}
	
}
