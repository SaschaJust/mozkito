package de.unisaarland.cs.st.reposuite.mapping;

import de.unisaarland.cs.st.reposuite.bugs.Bugs;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.settings.BooleanArgument;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadPool;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class Mapping extends RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool;
	private final DatabaseArguments   databaseArguments;
	private final LoggerArguments     logSettings;
	private final MappingArguments    mappingArguments;
	
	/**
	 * 
	 */
	public Mapping() {
		super(new MappingSettings());
		this.threadPool = new RepoSuiteThreadPool(Bugs.class.getSimpleName(), this);
		MappingSettings settings = getSettings();
		this.databaseArguments = settings.setDatabaseArgs(false, this.getClass().getSimpleName().toLowerCase());
		this.logSettings = settings.setLoggerArg(true);
		this.mappingArguments = settings.setMappingArgs(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		                    false);
		new LongArgument(settings, "cache.size",
		                 "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		
		settings.parseArguments();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#getSettings()
	 */
	@Override
	public MappingSettings getSettings() {
		return (MappingSettings) super.getSettings();
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
		
		MappingFinder finder = this.mappingArguments.getValue();
		
		if (finder == null) {
			if (Logger.logError()) {
				Logger.error("MappingFinder initialization failed. Aborting...");
				shutdown();
			}
		}
		
		if (this.databaseArguments.getValue() != null) {
			PersistenceUtil persistenceUtil;
			try {
				persistenceUtil = PersistenceManager.getUtil();
				new MappingsReader(this.threadPool.getThreadGroup(), getSettings(), persistenceUtil);
				new MappingsProcessor(this.threadPool.getThreadGroup(), getSettings(), finder, persistenceUtil);
				new MappingsPersister(this.threadPool.getThreadGroup(), getSettings(), persistenceUtil);
			} catch (UninitializedDatabaseException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				shutdown();
			}
		} else {
			if (Logger.logError()) {
				Logger.error("Database arguments not valid. Aborting...");
			}
			shutdown();
		}
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
