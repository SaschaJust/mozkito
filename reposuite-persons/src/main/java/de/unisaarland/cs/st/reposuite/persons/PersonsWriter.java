/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import java.util.HashMap;
import java.util.HashSet;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersonsWriter extends RepoSuiteSinkThread<HashMap<Person, HashSet<PersonContainer>>> {
	
	private final HibernateUtil hibernateUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param hibernateUtil
	 */
	public PersonsWriter(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final HibernateUtil hibernateUtil) {
		super(threadGroup, PersonsWriter.class.getSimpleName(), settings);
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
			
			HashMap<Person, HashSet<PersonContainer>> remap;
			this.hibernateUtil.beginTransaction();
			while (!isShutdown() && ((remap = read()) != null)) {
				
				for (Person person : remap.keySet()) {
					HashSet<PersonContainer> set = remap.get(person);
					
					if (Logger.logInfo()) {
						Logger.info("Updating personcontainers (" + set.size() + ") for " + person + ".");
					}
					
					for (PersonContainer container2 : set) {
						this.hibernateUtil.update(container2);
					}
				}
			}
			this.hibernateUtil.commitTransaction();
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
		
	}
	
}
