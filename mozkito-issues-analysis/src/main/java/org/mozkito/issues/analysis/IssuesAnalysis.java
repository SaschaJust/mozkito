package org.mozkito.issues.analysis;

import net.ownhero.dev.hiari.settings.Settings;

import org.mozkito.persistence.PersistenceUtil;

/**
 * The Interface IssuesAnalysis.
 */
public interface IssuesAnalysis {
	
	/**
	 * Perform the actual analysis.
	 * 
	 * @param persistenceUtil
	 *            the PersistenceUtil
	 */
	void performAnalysis(PersistenceUtil persistenceUtil);
	
	/**
	 * Method called before calling performAnalysis. Here you can request additional settings.
	 * 
	 * @param settings
	 *            the new up
	 */
	void setup(Settings settings);
	
	/**
	 * A method allowing the implementer to clean up and to perform closing actions (e.g. committing and closing open
	 * transactions).
	 */
	void tearDown();
	
}
