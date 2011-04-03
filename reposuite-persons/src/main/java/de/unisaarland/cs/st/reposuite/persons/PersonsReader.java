/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import java.util.List;
import java.util.ListIterator;

import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersonsReader extends RepoSuiteSourceThread<PersonContainer> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param persistenceUtil
	 */
	public PersonsReader(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, PersonsReader.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
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
			
			PersonContainer personContainer;
			Criteria<PersonContainer> criteria = this.persistenceUtil.createCriteria(PersonContainer.class);
			List<PersonContainer> containerList = this.persistenceUtil.load(criteria);
			ListIterator<PersonContainer> iterator = containerList.listIterator();
			
			while (!isShutdown() && iterator.hasNext() && ((personContainer = iterator.next()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Providing " + personContainer);
				}
				
				write(personContainer);
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
