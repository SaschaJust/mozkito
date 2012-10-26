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
 * @author Sascha Just <sascha.just@mozkito.org>
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
	public RCSFile createFile(final String path,
	                          final RCSTransaction transaction) {
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
