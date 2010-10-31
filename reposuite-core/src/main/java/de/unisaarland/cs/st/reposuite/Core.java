/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.rcs.model.RepositoryAnalyzer;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author just
 * 
 */
public class Core extends Thread {
	
	@Override
	public void run() {
		
		try {
			RepositoryAnalyzer analyzer = new RepositoryAnalyzer();
			analyzer.setName(RepositoryAnalyzer.getHandle());
			analyzer.start();
			analyzer.join();
		} catch (InterruptedException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
			throw new RuntimeException();
		}
	}
}
