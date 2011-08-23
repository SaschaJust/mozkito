/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteTransformerThread;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingProcessor extends RepoSuiteTransformerThread<MapScore, PersistentMapping> {
	
	private final MappingFinder mappingFinder;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public MappingProcessor(final RepoSuiteThreadGroup threadGroup, final MappingSettings settings,
	        final MappingFinder finder) {
		super(threadGroup, MappingProcessor.class.getSimpleName(), settings);
		this.mappingFinder = finder;
	}
	
	@Override
	public void run() {
		try {
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			MapScore score = null;
			
			while (!isShutdown() && ((score = read()) != null)) {
				PersistentMapping mapping = this.mappingFinder.map(score);
				if (mapping != null) {
					if (Logger.logInfo()) {
						Logger.info("Providing for store operation: " + mapping);
					}
					write(mapping);
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Discarding " + mapping + " due to non-positive score (" + score + ").");
					}
				}
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
