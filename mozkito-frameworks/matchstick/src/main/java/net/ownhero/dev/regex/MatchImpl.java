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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.MapCondition;

/**
 * The Class MatchImpl.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
class MatchImpl implements Match {
	
	/** The map. */
	private final Map<Integer, Group>  map     = new HashMap<Integer, Group>();
	
	/** The name map. */
	private final Map<String, Integer> nameMap = new HashMap<String, Integer>();
	
	/** The full match. */
	private final Group                fullMatch;
	
	/**
	 * Instantiates a new match impl.
	 * 
	 * @param fullMatch
	 *            the full match
	 */
	MatchImpl(@NotNull final Group fullMatch) {
		// PRECONDITIONS
		CompareCondition.equals(fullMatch.getIndex(), 0, "");
		Condition.isNull(fullMatch.getName(), "");
		
		try {
			this.fullMatch = fullMatch;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Adds the.
	 * 
	 * @param group
	 *            the group
	 */
	void add(@NotNull final Group group) {
		// PRECONDITIONS
		Condition.notNull(this.fullMatch, "Field '%s' in '%s'.", "fullMatch", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		Condition.notNull(this.nameMap, "Field '%s' in '%s'.", "nameMap", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		CompareCondition.positive(group.getIndex(),
		                          "Parameter '%s' in '%s:%s'.", "group.getIndex()", getHandle(), "add(RegexGroup)");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		try {
			this.map.put(group.getIndex(), group);
			
			if (group.getName() != null) {
				this.nameMap.put(group.getName(), group.getIndex());
			}
		} finally {
			// POSTCONDITIONS
			// TODO description
			MapCondition.containsKey(this.map, group.getIndex(), "");
			if (group.getName() != null) {
				// TODO description
				MapCondition.containsKey(this.nameMap, group.getName(), "");
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.Match#get(int)
	 */
	@Override
	public Group get(@Positive final int id) {
		// PRECONDITIONS
		
		try {
			return getGroup(id);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.Match#getFullMatch()
	 */
	@Override
	public Group getFullMatch() {
		// PRECONDITIONS
		
		try {
			return this.fullMatch;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.IMatch#get(int)
	 */
	@Override
	public Group getGroup(@Positive final int id) {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.map.get(id);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.IMatch#get(java.lang.String)
	 */
	@Override
	public Group getGroup(@NotNull @NotEmptyString final String name) {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			final Integer index = this.nameMap.get(name);
			return index == null
			                    ? null
			                    : this.map.get(index);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.Match#size()
	 */
	@Override
	public int getGroupCount() {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.map.size();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.IMatch#getGroupNames()
	 */
	@Override
	public Set<String> getGroupNames() {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		// PRECONDITIONS
		
		try {
			return this.nameMap.keySet();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.IMatch#getGroups()
	 */
	@Override
	public Group[] getGroups() {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		// PRECONDITIONS
		
		try {
			return this.map.values().toArray(new Group[0]);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public final String getHandle() {
		return getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.Match#getNamedGroupCount()
	 */
	@Override
	public int getNamedGroupCount() {
		// PRECONDITIONS
		Condition.notNull(this.nameMap, "Field '%s' in '%s'.", "nameMap", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.nameMap.size();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the named groups.
	 * 
	 * @return the named groups
	 */
	@Override
	public Group[] getNamedGroups() {
		// PRECONDITIONS
		
		try {
			final LinkedList<Group> list = new LinkedList<Group>();
			
			for (final Group group : this) {
				if (group.getName() != null) {
					list.add(group);
				}
			}
			
			return list.toArray(new Group[0]);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.Match#hasGroups()
	 */
	@Override
	public boolean hasGroups() {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return !this.map.isEmpty();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.Match#hasNamesGroups()
	 */
	@Override
	public boolean hasNamesGroups() {
		// PRECONDITIONS
		Condition.notNull(this.nameMap, "Field '%s' in '%s'.", "nameMap", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return !this.nameMap.isEmpty();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.Match#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.map.isEmpty();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Group> iterator() {
		// PRECONDITIONS
		
		try {
			return new Iterator<Group>() {
				
				private final Iterator<Group> groupIterator = MatchImpl.this.map.values().iterator();
				
				@Override
				public boolean hasNext() {
					// PRECONDITIONS
					Condition.notNull(this.groupIterator, "Field '%s' in '%s'.", "groupIterator", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
					
					try {
						return this.groupIterator.hasNext();
					} finally {
						// POSTCONDITIONS
					}
				}
				
				@Override
				public Group next() {
					// PRECONDITIONS
					Condition.notNull(this.groupIterator, "Field '%s' in '%s'.", "groupIterator", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
					
					try {
						return this.groupIterator.next();
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("MatchImpl [map={");
		
		StringBuilder builder2 = new StringBuilder();
		for (final Integer key : this.map.keySet()) {
			if (builder2.length() > 0) {
				builder2.append(", ");
			}
			
			builder2.append(key).append(" => ").append(this.map.get(key));
		}
		builder.append(builder2);
		builder.append("}, names={");
		
		builder2 = new StringBuilder();
		for (final String key : this.nameMap.keySet()) {
			if (builder2.length() > 0) {
				builder2.append(", ");
			}
			
			builder2.append(key).append(" => ").append(this.map.get(key));
		}
		builder.append(builder2);
		builder.append("}]");
		
		return builder.toString();
	}
	
}
