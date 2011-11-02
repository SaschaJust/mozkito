package de.unisaarland.cs.st.moskito.ppa.eclipse;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import net.ownhero.dev.ioda.FileUtils;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception {
		
		String baseDir = System.getProperty("user.home") + FileUtils.fileSeparator + ".m2" + FileUtils.fileSeparator
		        + "repository" + FileUtils.fileSeparator + "de" + FileUtils.fileSeparator + "unisaarland"
		        + FileUtils.fileSeparator + "cs" + FileUtils.fileSeparator + "st" + FileUtils.fileSeparator
		        + "moskito";
		
		String utils = baseDir + "-utils" + FileUtils.fileSeparator + "0.2-SNAPSHOT" + FileUtils.fileSeparator
		        + "moskito-utils-0.2-SNAPSHOT.jar";
		String core = baseDir + "-rcs" + FileUtils.fileSeparator + "0.2-SNAPSHOT" + FileUtils.fileSeparator
		        + "moskito-rcs-0.2-SNAPSHOT.jar";
		String ppaStr = baseDir + "-ppa" + FileUtils.fileSeparator + "0.2-SNAPSHOT" + FileUtils.fileSeparator
		        + "moskito-ppa-0.2-SNAPSHOT.jar";
		
		System.setProperty("reposuiteClassLookup", utils + ":" + core + ":" + ppaStr);
		
		ppa.Main.main(new String[0]);
		return IApplication.EXIT_OK;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		// nothing to do
	}
}
