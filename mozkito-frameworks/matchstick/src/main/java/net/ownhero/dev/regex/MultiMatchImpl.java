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
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MultiMatchImpl implements MultiMatch {
	
	private final List<Match> matches = new LinkedList<Match>();
	
	MultiMatchImpl() {
		
	}
	
	/**
	 * @param matches
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
	 * @see net.ownhero.dev.regex.MultiMatch#get(int, int)
	 */
	@Override
	public RegexGroup get(@NotNegative final int index,
	                      @NotNegative final int id) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			final Match match = this.matches.get(index);
			if (match == null) {
				throw new ArrayIndexOutOfBoundsException("Invalid index: " + index);
			}
			
			return match.get(id);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#get(int, java.lang.String)
	 */
	@Override
	public RegexGroup get(final int index,
	                      final String name) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			final Match match = this.matches.get(index);
			if (match == null) {
				throw new ArrayIndexOutOfBoundsException("Invalid index: " + index);
			}
			
			return match.get(name);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.MultiMatch#getGroup(int)
	 */
	@Override
	public RegexGroup[] getGroup(final int id) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		final List<RegexGroup> groups = new ArrayList<RegexGroup>(this.matches.size());
		
		try {
			for (final Match match : this.matches) {
				groups.add(match.get(id));
			}
			
			return groups.toArray(new RegexGroup[0]);
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
	public RegexGroup[] getGroup(final String name) {
		// PRECONDITIONS
		Condition.notNull(this.matches, "Field '%s' in '%s'.", "matches", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		final List<RegexGroup> groups = new ArrayList<RegexGroup>(this.matches.size());
		
		try {
			for (final Match match : this.matches) {
				groups.add(match.get(name));
			}
			
			return groups.toArray(new RegexGroup[0]);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(groups, "Local variable '%s' in '%s:%s'.", "groups", getHandle(), "getGroup(int)"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * @return
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
	public Match getMatch(final int index) {
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
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Match> iterator() {
		// PRECONDITIONS
		
		try {
			return new Iterator<Match>() {
				
				private final Iterator<Match> it = MultiMatchImpl.this.matches.iterator();
				
				@Override
				public boolean hasNext() {
					// PRECONDITIONS
					
					try {
						return this.it.hasNext();
					} finally {
						// POSTCONDITIONS
					}
				}
				
				@Override
				public Match next() {
					// PRECONDITIONS
					
					try {
						return this.it.next();
					} finally {
						// POSTCONDITIONS
					}
				}
				
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
