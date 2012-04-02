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
public interface MultiMatch {
	
	public abstract RegexGroup get(final int index,
	                               final int id);
	
	public abstract RegexGroup get(final int index,
	                               final String name);
	
	public abstract int getCount();
	
	public abstract RegexGroup[] getGroup(final int id);
	
	public abstract RegexGroup[] getGroup(final String name);
	
	public abstract Match getMatch(final int index);
	
	/**
	 * @return
	 */
	public abstract int size();
	
}
