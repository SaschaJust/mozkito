/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
package org.mozkito.versions.elements;

import java.util.HashMap;
import java.util.Map;

import org.mozkito.versions.model.RCSFile;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class RCSFileManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class RCSFileManager {
	
	/** The current files. */
	private final Map<String, RCSFile> currentFiles = new HashMap<String, RCSFile>();
	
	/**
	 * @param file
	 *            the file to be registered
	 */
	public void addFile(final RCSFile file) {
		this.currentFiles.put(file.getLatestPath(), file);
	}
	
	/**
	 * Creates and registers a file with the provided path and transaction.
	 * 
	 * @param path
	 *            the path of the file to be created
	 * @param transaction
	 *            the transaction
	 * @return the {@link RCSFile}
	 */
	public RCSFile createFile(final String path,
	                          final RCSTransaction transaction) {
		final RCSFile rCSFile = new RCSFile(path, transaction);
		this.currentFiles.put(path, rCSFile);
		return rCSFile;
	}
	
	/**
	 * Get the file registered with the given path.
	 * 
	 * @param path
	 *            the path
	 * @return the {@link RCSFile} if exists, null otherwise.
	 */
	public RCSFile getFile(final String path) {
		return this.currentFiles.get(path);
	}
}
