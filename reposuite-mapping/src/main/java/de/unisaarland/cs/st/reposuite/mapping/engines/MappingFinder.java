/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
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
	
	private static Map<String, MappingEngine> engines = new HashMap<String, MappingEngine>();
	
	static {
		try {
			Package package1 = MappingEngine.class.getPackage();
			Collection<Class<?>> classesExtendingClass = ClassFinder.getClassesExtendingClass(package1,
			                                                                                  MappingEngine.class);
			
			for (Class<?> klass : classesExtendingClass) {
				
				if (Logger.logInfo()) {
					Logger.info("Adding new MappingEngine " + klass.getCanonicalName());
				}
				engines.put(klass.getCanonicalName(), (MappingEngine) klass.newInstance());
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
	public static Set<Long> getCandidates(final RCSTransaction transaction) {
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
	 * @return
	 */
	public static MapScore score(final RCSTransaction transaction,
	                             final Report report) {
		MapScore score = new MapScore(transaction, report);
		
		for (String engineName : engines.keySet()) {
			MappingEngine mappingEngine = engines.get(engineName);
			mappingEngine.score(transaction, report, score);
		}
		return score;
	}
	
}
