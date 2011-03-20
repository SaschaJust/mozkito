/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingsPersister extends RepoSuiteSinkThread {
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public MappingsPersister(RepoSuiteThreadGroup threadGroup, String name, RepoSuiteSettings settings) {
		super(threadGroup, name, settings);
		// TODO Auto-generated constructor stub
	}
	
}
