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
package net.ownhero.dev.regex;

import java.util.Set;

/**
 * The Interface Match.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public interface Match extends Iterable<RegexGroup> {
	
	/**
	 * Gets the.
	 * 
	 * @param id
	 *            the id
	 * @return the regex group
	 */
	RegexGroup get(final int id);
	
	/**
	 * Gets the.
	 * 
	 * @param name
	 *            the name
	 * @return the regex group
	 */
	RegexGroup get(final String name);
	
	/**
	 * Gets the group names.
	 * 
	 * @return the group names
	 */
	Set<String> getGroupNames();
	
	/**
	 * Gets the groups.
	 * 
	 * @return the groups
	 */
	RegexGroup[] getGroups();
	
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 * @deprecated Empty {@link Match} instances can't exist per definition. The {@link Regex} instance will either
	 *             return a non-empty {@link Match} or null.
	 */
	@Deprecated
	boolean isEmpty();
	
	/**
	 * Size.
	 * 
	 * @return the int
	 */
	int size();
}
