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
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MultiMatchImpl implements MultiMatch {
	
	MultiMatchImpl() {
		
	}
	
	/**
	 * @param matches
	 */
	void add(final Match match) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/* (non-Javadoc)
     * @see net.ownhero.dev.regex.MultiMatch#get(int, int)
     */
	@Override
    public RegexGroup get(final int index,
	                      final int id) {
		return null;
	}
	
	/* (non-Javadoc)
     * @see net.ownhero.dev.regex.MultiMatch#get(int, java.lang.String)
     */
	@Override
    public RegexGroup get(final int index,
	                      final String name) {
		return null;
	}
	
	/* (non-Javadoc)
     * @see net.ownhero.dev.regex.MultiMatch#getCount()
     */
	@Override
    public int getCount() {
		return 0;
	}
	
	/* (non-Javadoc)
     * @see net.ownhero.dev.regex.MultiMatch#getGroup(int)
     */
	@Override
    public RegexGroup[] getGroup(final int id) {
		return null;
	}
	
	/* (non-Javadoc)
     * @see net.ownhero.dev.regex.MultiMatch#getGroup(java.lang.String)
     */
	@Override
    public RegexGroup[] getGroup(final String name) {
		return null;
	}
	
	/* (non-Javadoc)
     * @see net.ownhero.dev.regex.MultiMatch#getMatch(int)
     */
	@Override
    public Match getMatch(final int index) {
		return null;
	}
	
	/* (non-Javadoc)
     * @see net.ownhero.dev.regex.MultiMatch#size()
     */
	@Override
    public int size() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return 0;
		} finally {
			// POSTCONDITIONS
		}
	}
}
