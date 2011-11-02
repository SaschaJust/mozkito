/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.rcs.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;


public class RevDependency {
	
	private final String                    revId;
	
	/** The map of children to their branches. */
	private Set<String>  parents = new HashSet<String>();
	private final RCSBranch commitBranch;
	private final List<String>        tagNames;
	private final boolean      isMerge;
	
	public RevDependency(final String id, final RCSBranch commitBranch, final Set<String> parents,
			final List<String> tagNames, final boolean isMerge) {
		revId = id;
		this.commitBranch = commitBranch;
		this.parents = parents;
		this.tagNames = tagNames;
		this.isMerge = isMerge;
	}
	
	public RCSBranch getCommitBranch() {
		return commitBranch;
	}
	
	public String getId() {
		return revId;
	}
	
	public Set<String> getParents() {
		return parents;
	}
	
	public List<String> getTagNames() {
		return tagNames;
	}

	public boolean isMerge() {
	    return isMerge;
    }
	
}
