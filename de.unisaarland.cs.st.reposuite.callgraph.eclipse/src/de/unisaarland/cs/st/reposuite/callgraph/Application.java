package de.unisaarland.cs.st.reposuite.callgraph;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import de.unisaarland.cs.st.reposuite.utils.FileUtils;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception {
		String baseDir = System.getProperty("user.home") + FileUtils.fileSeparator + ".m2" + FileUtils.fileSeparator
		+ "repository" + FileUtils.fileSeparator + "de" + FileUtils.fileSeparator + "unisaarland"
		+ FileUtils.fileSeparator + "cs" + FileUtils.fileSeparator + "st" + FileUtils.fileSeparator
		+ "reposuite";
		
		String utils = baseDir + "-utils" + FileUtils.fileSeparator + "0.1-SNAPSHOT" + FileUtils.fileSeparator
		+ "reposuite-utils-0.1-SNAPSHOT.jar";
		String core = baseDir + "-core" + FileUtils.fileSeparator + "0.1-SNAPSHOT" + FileUtils.fileSeparator
		+ "reposuite-core-0.1-SNAPSHOT.jar";
		String ppaStr = baseDir + "-ppa" + FileUtils.fileSeparator + "0.1-SNAPSHOT" + FileUtils.fileSeparator
		+ "reposuite-callgraph-0.1-SNAPSHOT.jar";
		
		System.setProperty("reposuiteClassLookup", utils + ":" + core + ":" + ppaStr);
		
		callgraph.Main.main(new String[0]);
		return IApplication.EXIT_OK;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		// nothing to do
	}
}
