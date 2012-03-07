/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

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
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app. IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception {
		
		String baseDir = System.getProperty("user.home") + FileUtils.fileSeparator + ".m2" + FileUtils.fileSeparator
		        + "repository" + FileUtils.fileSeparator + "de" + FileUtils.fileSeparator + "unisaarland"
		        + FileUtils.fileSeparator + "cs" + FileUtils.fileSeparator + "st" + FileUtils.fileSeparator + "moskito";
		
		String utils = baseDir + "-utils" + FileUtils.fileSeparator + "0.3-SNAPSHOT" + FileUtils.fileSeparator
		        + "moskito-utils-0.3-SNAPSHOT.jar";
		String core = baseDir + "-rcs" + FileUtils.fileSeparator + "0.3-SNAPSHOT" + FileUtils.fileSeparator
		        + "moskito-rcs-0.3-SNAPSHOT.jar";
		String ppaStr = baseDir + "-ppa" + FileUtils.fileSeparator + "0.3-SNAPSHOT" + FileUtils.fileSeparator
		        + "moskito-ppa-0.3-SNAPSHOT.jar";
		
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
