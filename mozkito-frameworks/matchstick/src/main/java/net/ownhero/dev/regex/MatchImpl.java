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
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.MapCondition;

/**
 * The Class MatchImpl.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
class MatchImpl implements Match {
	
	/** The map. */
	Map<Integer, RegexGroup> map     = new HashMap<Integer, RegexGroup>();
	
	/** The name map. */
	Map<String, Integer>     nameMap = new HashMap<String, Integer>();
	
	/**
	 * Instantiates a new match impl.
	 */
	MatchImpl() {
		
	}
	
	/**
	 * Adds the.
	 * 
	 * @param group
	 *            the group
	 */
	void add(final RegexGroup group) {
		this.map.put(group.getIndex(), group);
		
		if (group.getName() != null) {
			this.nameMap.put(group.getName(), group.getIndex());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.IMatch#get(int)
	 */
	@Override
	public RegexGroup get(@NotNegative final int id) {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		MapCondition.containsKey(this.map, id, "Field '%s' in '%s' lags key '%s'.", "map", getHandle(), "id"); //$NON-NLS-1$ //$NON-NLS-2$
		
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
	public RegexGroup get(@NotNull final String name) {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		MapCondition.containsKey(this.nameMap, name, "Field '%s' in '%s' lags key '%s'.", "nameMap", getHandle(), name); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			final Integer index = this.nameMap.get(name);
			Condition.notNull(index, "Local variable '%s' in '%s:%s'.", "index", getHandle(), "get(String)"); //$NON-NLS-1$ //$NON-NLS-2$
			MapCondition.containsKey(this.map, index, "Field '%s' in '%s' lags key '%s'.", "map", getHandle(), index); //$NON-NLS-1$ //$NON-NLS-2$
			
			return this.map.get(index);
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
		
		return this.nameMap.keySet();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.regex.IMatch#getGroups()
	 */
	@Override
	public RegexGroup[] getGroups() {
		// PRECONDITIONS
		Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		return this.map.values().toArray(new RegexGroup[0]);
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
	 * @see net.ownhero.dev.regex.Match#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// PRECONDITIONS
		
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
	public Iterator<RegexGroup> iterator() {
		// PRECONDITIONS
		
		try {
			return new Iterator<RegexGroup>() {
				
				private final Iterator<RegexGroup> it = MatchImpl.this.map.values().iterator();
				
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
				public RegexGroup next() {
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
	 * @see net.ownhero.dev.regex.Match#size()
	 */
	@Override
	public int size() {
		// PRECONDITIONS
		
		try {
			// PRECONDITIONS
			Condition.notNull(this.map, "Field '%s' in '%s'.", "map", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			
			return this.map.size();
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
