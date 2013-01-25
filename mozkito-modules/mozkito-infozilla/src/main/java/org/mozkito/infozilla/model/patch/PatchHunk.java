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
package org.mozkito.infozilla.model.patch;

/**
 * The Class PatchHunk.
 */
public class PatchHunk {
	
	/** The text. */
	private final String text;
	
	/**
	 * Instantiates a new patch hunk.
	 */
	public PatchHunk() {
		this.text = "";
	}
	
	/**
	 * Instantiates a new patch hunk.
	 * 
	 * @param text
	 *            the text
	 */
	public PatchHunk(final String text) {
		this.text = text;
	}
	
	/**
	 * Gets the added.
	 * 
	 * @return the added
	 */
	public String getAdded() {
		throw new RuntimeException();
	}
	
	/**
	 * Gets the deleted.
	 * 
	 * @return the deleted
	 */
	public String getDeleted() {
		throw new RuntimeException();
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}
}
