/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import org.hibernate.Session;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryPersister extends Thread {
	
	/**
	 * @return the simple class name
	 */
	public static String getHandle() {
		return RepositoryPersister.class.getSimpleName();
	}
	
	private final RepositoryParser parser;
	
	private final Session          hibernateSession;
	
	public RepositoryPersister(final RepositoryParser parser, final Session hibernateSession) {
		this.parser = parser;
		this.hibernateSession = hibernateSession;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		RCSTransaction currentTransaction;
		
		while ((currentTransaction = this.parser.getNext()) != null) {
			
			if (Logger.logInfo()) {
				Logger.info("Saving " + currentTransaction);
			}
			this.hibernateSession.saveOrUpdate(currentTransaction);
		}
	}
	
}
