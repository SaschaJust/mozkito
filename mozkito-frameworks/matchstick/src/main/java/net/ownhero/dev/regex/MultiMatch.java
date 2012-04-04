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

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;

/**
 * The Interface MultiMatch.
 * 
 * This interface is used in {@link Regex} when returning multiple {@link Match}es like in {@link Regex#findAll(String)}
 * .
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
	 * @since 0.1
	 */
	@Deprecated
	Match get(@NotNegative final int index);
	
	/**
	 * Gets the {@link Group} with id <code>id</code> in the {@link Match} with index <code>index</code>. If you have a
	 * pattern <code>"(a) (b)"</code> and a string <code>"a b a b"</code>, <code>get(1, 1)</code> will return the
	 * 
	 * @param index
	 *            the {@link NotNegative} index
	 * @param id
	 *            the {@link Positive} id
	 * @return the {@link Group} with id <code>id</code> of the <code>index</code>th match or <code>null</code> if the
	 *         id is invalid. {@link Group} for the second a in the string since it is the 1st {@link Group} (
	 *         <code>id = 1</code>) within the 2nd {@link Match} (<code>index = 1</code>).
	 * 
	 *         Note: Please keep in mind that <code>id = 0</code> is not valid and won't return a {@link Group} that
	 *         corresponds to the complete match of the pattern. Requesting <code>id = 0</code> will result in the
	 *         function returning null (if the index is valid).
	 * @since 0.2
	 */
	Group get(@NotNegative final int index,
	          @Positive final int id);
	
	/**
	 * Gets the {@link Group} with name <code>name</code> in the {@link Match} with index <code>index</code>. If you
	 * have a pattern <code>"({x}a) ({y}b)"</code> and a string <code>"a b a b"</code>, <code>get(1, "x")</code> will
	 * return the {@link Group} for the second a in the string since it is the {@link Group} with name "x" within the
	 * 2nd {@link Match} (<code>index = 1</code>).
	 * 
	 * @param index
	 *            the {@link NotNegative} index
	 * @param name
	 *            the {@link NotNull} name
	 * @return the {@link Group} with name <code>name</code> of the <code>index</code>th match or <code>null</code> if
	 *         the id is invalid.
	 * @since 0.2
	 */
	Group get(@NotNegative final int index,
	          @NotNull @NotEmptyString final String name);
	
	/**
	 * Returns an array containing all {@link Group}s that corresponds to the given <code>id</code> of all {@link Match}
	 * es. Will return an empty array if the <code>id</code> is invalid.
	 * 
	 * @param id
	 *            the {@link Positive} id
	 * @return all {@link Group}s for the given id.
	 * @since 0.2
	 */
	Group[] getGroup(@Positive final int id);
	
	/**
	 * Returns an array containing all {@link Group}s that corresponds to the given <code>name</code> of all.
	 * 
	 * @param name
	 *            the {@link NotNull} {@link NotEmptyString} name
	 * @return all {@link Group}s for the given id. {@link Match}es. Will return an empty array if there are no
	 *         {@link Group}s with that <code>name</code> is invalid.
	 * @since 0.2
	 */
	Group[] getGroup(@NotNull @NotEmptyString final String name);
	
	/**
	 * Gets the match with the given index.
	 * 
	 * @param index
	 *            the {@link NotNegative} index
	 * @return the match with that corresponds to the index
	 * @since 0.2
	 */
	Match getMatch(@NotNegative final int index);
	
	/**
	 * Checks if there are any {@link Group}s in the pattern that matched.
	 * 
	 * @return true, if successful
	 * @since 0.2
	 */
	boolean hasGroups();
	
	/**
	 * Checks if there are any names {@link Group}s in the pattern that matched.
	 * 
	 * @return true, if successful
	 * @since 0.2
	 */
	boolean hasNamedGroups();
	
	/**
	 * Checks if there aren't any groups aside the full match of the pattern.
	 * 
	 * @return true, if is empty
	 * @since 0.1
	 * @deprecated You want to use {@link MultiMatch#hasGroups()} or {@link MultiMatch#hasNamedGroups()} instead. This
	 *             will be removed in the 0.2 release.
	 */
	@Deprecated
	boolean isEmpty();
	
	/**
	 * Returns the number of {@link Match}es in the {@link MultiMatch}.
	 * 
	 * @return the number of {@link Match}es.
	 * @since 0.1
	 */
	int size();
	
}
