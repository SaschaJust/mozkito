/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingsReader extends RepoSuiteSourceThread<RCSTransaction> {
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public MappingsReader(final RepoSuiteThreadGroup threadGroup, final String name, final RepoSuiteSettings settings) {
		super(threadGroup, name, settings);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}
	
}
