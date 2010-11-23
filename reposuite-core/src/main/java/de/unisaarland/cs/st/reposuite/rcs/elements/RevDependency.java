package de.unisaarland.cs.st.reposuite.rcs.elements;

import java.util.HashSet;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;


public class RevDependency {
	
	private final String                    revId;
	
	/** The map of children to their branches. */
	private Set<String>  parents = new HashSet<String>();
	private final RCSBranch commitBranch;
	private final String        tagName;
	
	public RevDependency(final String id, final RCSBranch commitBranch, final Set<String> parents,
			final String tagName) {
		this.revId = id;
		this.commitBranch = commitBranch;
		this.parents = parents;
		this.tagName = tagName;
	}
	
	public RCSBranch getCommitBranch() {
		return this.commitBranch;
	}
	
	public String getId() {
		return this.revId;
	}
	
	public Set<String> getParents() {
		return this.parents;
	}
	
	public String getTagName() {
		return this.tagName;
	}
	
}
