/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryPersister extends RepositoryThread {
	
	/**
	 * @return the simple class name
	 */
	public static String getHandle() {
		return RepositoryPersister.class.getSimpleName();
	}
	
	private final RepositoryParser parser;
	
	private final HibernateUtil    hibernateUtil;
	
	private final boolean          shutdown = false;
	
	public RepositoryPersister(final ThreadGroup threadGroup, final RepositoryParser parser,
	        final HibernateUtil hibernateUtil) {
		super(threadGroup, getHandle());
		this.parser = parser;
		this.hibernateUtil = hibernateUtil;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!this.shutdown) {
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			this.hibernateUtil.beginTransaction();
			RCSTransaction currentTransaction;
			int i = 0;
			while (!this.shutdown && ((currentTransaction = this.parser.getNext()) != null)) {
				
				if (Logger.logError()) {
					Logger.error("Saving " + currentTransaction);
				}
				
				if (++i % 1000 == 0) {
					this.hibernateUtil.commitTransaction();
					this.hibernateUtil.beginTransaction();
				}
				this.hibernateUtil.saveOrUpdate(currentTransaction);
			}
			this.hibernateUtil.commitTransaction();
			
		}
	}
}
