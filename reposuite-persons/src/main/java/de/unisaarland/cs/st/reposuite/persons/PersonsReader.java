/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import java.util.List;
import java.util.ListIterator;

import org.hibernate.Criteria;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
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
	
	private final HibernateUtil hibernateUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param hibernateUtil
	 */
	public PersonsReader(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	                     final HibernateUtil hibernateUtil) {
		super(threadGroup, PersonsReader.class.getSimpleName(), settings);
		this.hibernateUtil = hibernateUtil;
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
			Criteria criteria = this.hibernateUtil.createCriteria(PersonContainer.class);
			@SuppressWarnings ("unchecked")
			List<PersonContainer> containerList = criteria.list();
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
