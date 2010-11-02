/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import org.hibernate.SessionFactory;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.settings.BooleanArgument;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author just
 * 
 */
public class Core extends Thread {
	
	@Override
	public void run() {
		
		try {
			RepoSuiteSettings settings = new RepoSuiteSettings();
			RepositoryArguments repoSettings = settings.setRepositoryArg(true);
			DatabaseArguments databaseSettings = settings.setDatabaseArgs(true);
			LoggerArguments logSettings = settings.setLoggerArg(true);
			new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface",
			        "false", false);
			new LongArgument(settings, "repository.cachesize",
			        "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
			settings.parseArguments();
			
			Repository repository = repoSettings.getValue();
			logSettings.getValue();
			SessionFactory factory = databaseSettings.getValue();
			
			RepositoryReader reader = new RepositoryReader(repository, settings);
			reader.setName(RepositoryReader.getHandle());
			
			RepositoryAnalyzer analyzer = new RepositoryAnalyzer(reader);
			analyzer.setName(RepositoryAnalyzer.getHandle());
			
			RepositoryParser parser = new RepositoryParser(analyzer);
			parser.setName(RepositoryParser.getHandle());
			
			RepositoryPersister persister = new RepositoryPersister(parser, factory.openSession());
			persister.setName(RepositoryPersister.getHandle());
			
			reader.start();
			analyzer.start();
			parser.start();
			persister.start();
			
			reader.join();
			analyzer.join();
			parser.join();
			persister.join();
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
			throw new RuntimeException();
		}
	}
}
