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
 * The Enum GravatarRating.
 */
public enum GravatarRating {
	
	/** The general audiences. */
	GENERAL_AUDIENCES ("g"),
	
	/** The parental guidance suggested. */
	PARENTAL_GUIDANCE_SUGGESTED ("pg"),
	
	/** The restricted. */
	RESTRICTED ("r"),
	
	/** The xplicit. */
	XPLICIT ("x");
	
	/** The code. */
	private String code;
	
	/**
	 * Instantiates a new gravatar rating.
	 * 
	 * @param code
	 *            the code
	 */
	private GravatarRating(final String code) {
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
