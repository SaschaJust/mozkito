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

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;

/**
 * The Interface Match.
 * 
 * Used when {@link Regex} returns matches like in {@link Regex#find(String)}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public interface Match extends Iterable<Group> {
	
	/**
	 * Gets the {@link Group}s that corresponds to the <code>id</code>. Keep in mind that actual groups within a pattern
	 * are enumerated starting at 1, not at 0. Thus, 0 is not a valid <code>id</code>.
	 * 
	 * @param id
	 *            the {@link Positive} id
	 * @return the {@link Group}
	 * @deprecated use {@link Match#getGroup(int)} instead. This will be removed in the 0.2 release.
	 * @since 0.1
	 */
	@Deprecated
	Group get(@Positive final int id);
	
	/**
	 * Gets the full match of the pattern. This is guaranteed to not return null.
	 * 
	 * @return the full match
	 */
	Group getFullMatch();
	
	/**
	 * Gets the {@link Group}s that corresponds to the <code>id</code>. Keep in mind that actual groups within a pattern
	 * are enumerated starting at 1, not at 0. Thus, 0 is not a valid <code>id</code>.
	 * 
	 * @param id
	 *            the {@link Positive} id
	 * @return the {@link Group} if <code>id</code> is valid; null otherwise
	 * @since 0.2
	 */
	Group getGroup(@Positive final int id);
	
	/**
	 * Gets the {@link Group}s that corresponds to the <code>name</code>. Returns <code>null</code> if there is no such
	 * group.
	 * 
	 * @param name
	 *            the {@link NotNull} {@link NotEmptyString} name
	 * @return the {@link Group} if <code>name</code> is valid; null otherwise
	 * @since 0.2
	 */
	Group getGroup(@NotNull @NotEmptyString final String name);
	
	/**
	 * Gets the number of {@link Group}s in the {@link Match}.
	 * 
	 * @return the number of {@link Group}s
	 * @since 0.2
	 */
	int getGroupCount();
	
	/**
	 * Gets name of all groups in the {@link Match}.
	 * 
	 * @return a {@link Set} containing all names of the {@link Group}s in the {@link Match}. Guaranteed to not be
	 *         <code>null</code>.
	 * @since 0.2
	 */
	Set<String> getGroupNames();
	
	/**
	 * Gets all {@link Group}s in the {@link Match}.
	 * 
	 * @return all {@link Group}s in the {@link Match}. Will return an empty array if there are none. Guaranteed to not
	 *         return <code>null</code>.
	 * @since 0.2
	 */
	Group[] getGroups();
	
	/**
	 * Gets the number of named {@link Group}s in the {@link Match}.
	 * 
	 * @return the number of named {@link Group}s
	 * @since 0.2
	 */
	int getNamedGroupCount();
	
	/**
	 * Gets all named {@link Group}s in the {@link Match}.
	 * 
	 * @return all named {@link Group}s in the {@link Match}. Will return an empty array if there are none. Guaranteed
	 *         to not return <code>null</code>.
	 * @since 0.2
	 */
	Group[] getNamedGroups();
	
	/**
	 * Checks for the group with <code>id</code>.
	 * 
	 * @param id
	 *            the id
	 * @return true, if successful
	 */
	boolean hasGroup(@Positive int id);
	
	/**
	 * Checks for any {@link Group}s in the {@link Match}.
	 * 
	 * @return true, if there is at least one.
	 * @since 0.2
	 */
	boolean hasGroups();
	
	/**
	 * Checks for the named group <code>name</code>.
	 * 
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	boolean hasNamedGroup(@NotNull @NotEmptyString String name);
	
	/**
	 * Checks for any named {@link Group}s in the {@link Match}.
	 * 
	 * @return true, if there is at least one.
	 * @since 0.2
	 */
	boolean hasNamedGroups();
	
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 * @deprecated Use {@link Match#hasGroups()} or {@link Match#hasNamedGroups()} instead. This will be removed with
	 *             the 0.2 release.
	 * @since 0.2
	 */
	@Deprecated
	boolean isEmpty();
}
