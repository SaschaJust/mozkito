/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingFinder {
	
	private static Map<String, MappingEngine> engines = new HashMap<String, MappingEngine>();
	
	static {
		try {
			Package package1 = MappingEngine.class.getPackage();
			Collection<Class<?>> classesExtendingClass = ClassFinder.getClassesOfInterface(package1,
			                                                                               MappingEngine.class);
			
			for (Class<?> klass : classesExtendingClass) {
				engines.put(klass.getCanonicalName(), (MappingEngine) klass.newInstance());
			}
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	public static List<Integer> getCandidates(final RCSTransaction transaction) {
		return new LinkedList<Integer>();
	}
	
}
