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

/**
 * The Interface MultiMatch.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public interface MultiMatch extends Iterable<Match> {
	
	/**
	 * Gets the match with the given index.
	 * 
	 * @param index
	 *            the index
	 * @return the match
	 * @deprecated use {@link MultiMatch#getMatch(int)} instead.
	 */
	@Deprecated
	Match get(final int index);
	
	/**
	 * Gets the.
	 * 
	 * @param index
	 *            the index
	 * @param id
	 *            the id
	 * @return the regex group with id 'id' of the 'index'th match or null if the id is invalid.
	 */
	RegexGroup get(final int index,
	               final int id);
	
	/**
	 * Gets the.
	 * 
	 * @param index
	 *            the index
	 * @param name
	 *            the name
	 * @return the regex group with name 'name' of the 'index'th match or null if the id is invalid.
	 */
	RegexGroup get(final int index,
	               final String name);
	
	/**
	 * Gets the group.
	 * 
	 * @param id
	 *            the id
	 * @return the all {@link RegexGroup}s for the given id.
	 */
	RegexGroup[] getGroup(final int id);
	
	/**
	 * Gets the group.
	 * 
	 * @param name
	 *            the name
	 * @return the all {@link RegexGroup}s for the given name.
	 */
	RegexGroup[] getGroup(final String name);
	
	/**
	 * Gets the match with the given index.
	 * 
	 * @param index
	 *            the index
	 * @return the match
	 */
	Match getMatch(final int index);
	
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 * @deprecated Empty MultiMatch instances can't exist by definition. Either regex.findAll() returns a non-empty
	 *             instance or null.
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
