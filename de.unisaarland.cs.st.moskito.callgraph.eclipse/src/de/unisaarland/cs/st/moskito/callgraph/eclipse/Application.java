package de.unisaarland.cs.st.moskito.callgraph.eclipse;

import net.ownhero.dev.ioda.FileUtils;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		String baseDir = System.getProperty("user.home") + FileUtils.fileSeparator + ".m2" + FileUtils.fileSeparator
		+ "moskito" + FileUtils.fileSeparator + "de" + FileUtils.fileSeparator + "unisaarland"
		+ FileUtils.fileSeparator + "cs" + FileUtils.fileSeparator + "st" + FileUtils.fileSeparator
		+ "moskito";
		
		String utils = baseDir + "-utils" + FileUtils.fileSeparator + "0.2-SNAPSHOT" + FileUtils.fileSeparator
		+ "moskito-utils-0.2-SNAPSHOT.jar";
		String core = baseDir + "-rcs" + FileUtils.fileSeparator + "0.2-SNAPSHOT" + FileUtils.fileSeparator
		        + "moskito-rcs-0.2-SNAPSHOT.jar";
		String ppaStr = baseDir + "-callgraph" + FileUtils.fileSeparator + "0.2-SNAPSHOT" + FileUtils.fileSeparator
		+ "moskito-callgraph-0.2-SNAPSHOT.jar";
		
		System.setProperty("reposuiteClassLookup", utils + ":" + core + ":" + ppaStr);
		
		callgraph.Main.main(new String[0]);
		return IApplication.EXIT_OK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		// nothing to do
	}
}
