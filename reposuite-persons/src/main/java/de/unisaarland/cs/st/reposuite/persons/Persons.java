/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import de.unisaarland.cs.st.reposuite.Core;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadPool;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Persons extends RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool;
	private final DatabaseArguments   databaseArguments;
	private final LoggerArguments     logSettings;
	
	/**
	 * 
	 */
	public Persons() {
		super(new RepositorySettings());
		this.threadPool = new RepoSuiteThreadPool(Core.class.getSimpleName(), this);
		
		RepoSuiteSettings settings = getSettings();
		this.databaseArguments = ((RepositorySettings) settings).setDatabaseArgs(true);
		this.logSettings = settings.setLoggerArg(true);
		
		settings.parseArguments();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		setup();
		this.threadPool.execute();
		
		if (Logger.logInfo()) {
			Logger.info("Terminating.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		this.logSettings.getValue();
		HibernateUtil hibernateUtil = null;
		
		if (this.databaseArguments.getValue() != null) {
			try {
				hibernateUtil = HibernateUtil.getInstance(this);
			} catch (UninitializedDatabaseException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				
				shutdown();
			}
			
		} else {
			if (Logger.logError()) {
				Logger.error("Database arguments are not set (required when merging persons).");
			}
			
			shutdown();
		}
		
		new PersonsReader(this.threadPool.getThreadGroup(), getSettings(), hibernateUtil);
		new PersonsMerger(this.threadPool.getThreadGroup(), getSettings(), hibernateUtil);
		new PersonsWriter(this.threadPool.getThreadGroup(), getSettings(), hibernateUtil);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#shutdown()
	 */
	@Override
	public void shutdown() {
		this.threadPool.shutdown();
	}
	
}
