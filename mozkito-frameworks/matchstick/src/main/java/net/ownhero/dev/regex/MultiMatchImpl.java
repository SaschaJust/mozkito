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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class MultiMatchImpl.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MultiMatchImpl implements MultiMatch {
	
	/** The matches. */
	private final List<Match> matches = new LinkedList<Match>();
	
	/**
	 * Instantiates a new {@link MultiMatchImpl}.
	 */
	MultiMatchImpl() {
		
	}
	
	/**
	 * Adds the match to the local list.
	 * 
	 * @param match
	 *            the match
	 */
	void add(@NotNull final Match match) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			
			this.matches.add(match);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#get(int)
	 */
	@Override
	public Match get(@NotNegative final int index) {
		// PRECONDITIONS
		
		try {
			return getMatch(index);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#get(int, int)
	 */
	@Override
	public Group get(@NotNegative final int index,
	                 @NotNegative final int id) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			final Match match = this.matches.get(index);
			if (match == null) {
				throw new ArrayIndexOutOfBoundsException("Invalid index: " + index);
			}
			
			return match.getGroup(id);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#get(int, java.lang.String)
	 */
	@Override
	public Group get(@NotNegative final int index,
	                 @NotNull @NotEmptyString final String name) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			final Match match = this.matches.get(index);
			if (match == null) {
				throw new ArrayIndexOutOfBoundsException("Invalid index: " + index);
			}
			
			return match.getGroup(name);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#getGroup(int)
	 */
	@Override
	public Group[] getGroup(@Positive final int id) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		final List<Group> groups = new ArrayList<Group>(this.matches.size());
		
		try {
			for (final Match match : this.matches) {
				final Group group = match.getGroup(id);
				
				if (group != null) {
					groups.add(group);
				}
			}
			
			return groups.toArray(new Group[0]);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(groups, "Local variable '%s' in '%s:%s'.", "groups", getHandle(), "getGroup(int)"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#getGroup(java.lang.String)
	 */
	@Override
	public Group[] getGroup(@NotNull @NotEmptyString final String name) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		final List<Group> groups = new ArrayList<Group>(this.matches.size());
		
		try {
			for (final Match match : this.matches) {
				groups.add(match.getGroup(name));
			}
			
			return groups.toArray(new Group[0]);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(groups, "Local variable '%s' in '%s:%s'.", "groups", getHandle(), "getGroup(int)"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public final String getHandle() {
		// PRECONDITIONS
		
		try {
			return getClass().getSimpleName();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#getMatch(int)
	 */
	@Override
	public Match getMatch(@NotNegative final int index) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.matches.get(index);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#hasGroups()
	 */
	@Override
	public boolean hasGroups() {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.matches.isEmpty()
			                             ? false
			                             : this.matches.iterator().next().hasGroups();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#hasNamedGroups()
	 */
	@Override
	public boolean hasNamedGroups() {
		// PRECONDITIONS
		
		try {
			return this.matches.isEmpty()
			                             ? false
			                             : this.matches.iterator().next().hasNamedGroups();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.matches.isEmpty();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Match> iterator() {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return new Iterator<Match>() {
				
				private final Iterator<Match> matchIterator = MultiMatchImpl.this.matches.iterator();
				
				@Override
				public boolean hasNext() {
					// PRECONDITIONS
					Condition.notNull(this.matchIterator, "Field '%s' in '%s'.", "matchIterator", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
					
					try {
						return this.matchIterator.hasNext();
					} finally {
						// POSTCONDITIONS
					}
				}
				
				@Override
				public Match next() {
					// PRECONDITIONS
					Condition.notNull(this.matchIterator, "Field '%s' in '%s'.", "matchIterator", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
					
					try {
						return this.matchIterator.next();
					} finally {
						// POSTCONDITIONS
					}
				}
				
				/**
				 * @throws UnsupportedOperationException
				 *             guaranteed.
				 */
				@Override
				public void remove() {
					// PRECONDITIONS
					
					try {
						throw new UnsupportedOperationException();
					} finally {
						// POSTCONDITIONS
					}
				}
			};
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#size()
	 */
	@Override
	public int size() {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.matches.size();
		} finally {
			// POSTCONDITIONS
		}
	}
}
