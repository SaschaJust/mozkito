/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.utils.RepositoryFactory;

/**
 * @author just
 * 
 */
public class Core extends Thread {
	
	@Override
	public void run() {
		
		try {
			RepositoryFactory.getRepositoryHandler(RepositoryType.GIT);
		} catch (UnregisteredRepositoryTypeException e) {
			e.printStackTrace();
		}
	}
}
