/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.settings;

import java.io.File;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DirectoryArgument extends RepoSuiteArgument {
	
	private boolean create = false;
	
	/**
	 * This is similar to FileArgument but requires the file to be a directory
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @param create
	 *            Attempts to create directory if not exist
	 */
	public DirectoryArgument(final RepoSuiteSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired, final boolean create) {
		super(settings, name, description, defaultValue, isRequired);
		this.create = create;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public File getValue() {
		if (this.stringValue == null) {
			return null;
		}
		
		File file = new File(this.stringValue.trim());
		
		if (!file.exists()) {
			if (!this.create) {
				if (Logger.logError()) {
					Logger.error("The file `" + this.stringValue + "` specified for argument `" + getName()
					        + "` does not exist.");
				}
				throw new Shutdown();
			} else {
				if (!file.mkdirs()) {
					if (Logger.logError()) {
						Logger.error("The file `" + this.stringValue + "` specified for argument `" + getName()
						        + "` does not exist and cannot be created.");
					}
					throw new Shutdown();
				}
			}
		}
		if (!file.isDirectory()) {
			if (Logger.logError()) {
				Logger.error("The directory `" + this.stringValue + "` specified for argument `" + getName()
				        + "` is not a directory. Please remove file or choose different argument value.");
			}
			throw new Shutdown();
		}
		return file;
	}
}
