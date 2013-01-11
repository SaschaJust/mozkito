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
package net.ownhero.dev.ioda;

/**
 * The Class StringUtils.
 */
public class StringUtils {
	
	/**
	 * Truncate.
	 * 
	 * @param string
	 *            the string
	 * @return the string
	 */
	public static final String truncate(final String string) {
		return truncate(string, 254);
	}
	
	/**
	 * Truncate.
	 * 
	 * @param string
	 *            the string
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static final String truncate(final String string,
	                                    final int length) {
		return (string != null)
		                       ? string.substring(0, Math.min(string.length(), length))
		                       : "";
		
	}
}
