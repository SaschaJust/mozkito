package de.unisaarland.cs.st.reposuite.rcs.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;


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
