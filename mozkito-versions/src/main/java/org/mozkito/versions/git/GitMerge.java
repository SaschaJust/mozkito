/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
package org.mozkito.versions.git;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

public class GitMerge {
	
	private final String      mergeHash;
	private final String      branchParent;
	private final Set<String> otherParents = new HashSet<String>();
	
	public GitMerge(@NotNull final String mergeHash, final String branchParent, final String... otherParents) {
		this.mergeHash = mergeHash;
		this.branchParent = branchParent;
		for (final String s : otherParents) {
			getOtherParents().add(s);
		}
	}
	
	public String getBranchParent() {
		// PRECONDITIONS
		
		try {
			return this.branchParent;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@NoneNull
	public GitRevDependencyType getEdgeType(final String parent) {
		// PRECONDITIONS
		
		try {
			if ((this.branchParent != null) && this.branchParent.equals(parent)) {
				return GitRevDependencyType.BRANCH_EDGE;
			} else if (this.otherParents.contains(parent)) {
				return GitRevDependencyType.MERGE_EDGE;
			} else {
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public String getMergeHash() {
		// PRECONDITIONS
		
		try {
			return this.mergeHash;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Set<String> getOtherParents() {
		// PRECONDITIONS
		
		try {
			return this.otherParents;
		} finally {
			// POSTCONDITIONS
		}
	}
}
