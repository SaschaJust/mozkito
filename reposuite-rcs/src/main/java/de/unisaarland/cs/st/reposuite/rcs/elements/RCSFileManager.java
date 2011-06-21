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
package de.unisaarland.cs.st.reposuite.rcs.elements;

import java.util.HashMap;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RCSFileManager {
	
	private final Map<String, RCSFile> currentFiles = new HashMap<String, RCSFile>();
	
	/**
	 * @param file
	 */
	public void addFile(final RCSFile file) {
		this.currentFiles.put(file.getLatestPath(), file);
	}
	
	/**
	 * @param path
	 * @param transaction
	 * @return
	 */
	public RCSFile createFile(final String path, final RCSTransaction transaction) {
		RCSFile file = new RCSFile(path, transaction);
		this.currentFiles.put(path, file);
		return file;
	}
	
	/**
	 * @param path
	 * @return
	 */
	public RCSFile getFile(final String path) {
		return this.currentFiles.get(path);
	}
}
