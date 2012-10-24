package de.unisaarland.cs.st.mozkito.versions.git;

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
