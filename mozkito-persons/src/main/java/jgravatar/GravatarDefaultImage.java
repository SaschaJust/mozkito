/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
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
package jgravatar;

/**
 * The Enum GravatarDefaultImage.
 */
public enum GravatarDefaultImage {
	
	/** The gravatar icon. */
	GRAVATAR_ICON (""),
	
	/** The identicon. */
	IDENTICON ("identicon"),
	
	/** The monsterid. */
	MONSTERID ("monsterid"),
	
	/** The wavatar. */
	WAVATAR ("wavatar"),
	
	/** The HTT p_404. */
	HTTP_404 ("404");
	
	/** The code. */
	private String code;
	
	/**
	 * Instantiates a new gravatar default image.
	 * 
	 * @param code
	 *            the code
	 */
	private GravatarDefaultImage(final String code) {
		this.code = code;
	}
	
	/**
	 * Gets the code.
	 * 
	 * @return the code
	 */
	public String getCode() {
		return this.code;
	}
	
}
