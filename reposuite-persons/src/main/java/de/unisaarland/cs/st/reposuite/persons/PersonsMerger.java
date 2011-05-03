/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.persons.processing.MergingProcessor;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersonsMerger extends RepoSuiteSinkThread<PersonContainer> {
	
	PersistenceUtil                persistenceUtil = null;
	private final MergingProcessor processor;
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param persistenceUtil
	 * @param processor
	 */
	public PersonsMerger(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil, final MergingProcessor processor) {
		super(threadGroup, PersonsMerger.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
		this.processor = processor;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			PersonContainer container = null;
			
			while (!isShutdown() && ((container = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Processing " + container + ".");
				}
				
				this.processor.process(container);
				
			}
			
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
