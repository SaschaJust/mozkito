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
package org.mozkito.versions.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.conditions.CollectionCondition;

import org.apache.commons.lang.StringUtils;
import org.mozkito.versions.model.RCSBranch;


/**
 * The Class RevDependency.
 */
public class RevDependency {
	
	/** The rev id. */
	private final String       revId;
	
	/** The map of children to their branches. */
	private Set<String>        parents = new HashSet<String>();
	
	/** The commit branch. */
	private final RCSBranch    commitBranch;
	
	/** The tag names. */
	private final List<String> tagNames;
	
	/** The is merge. */
	private final boolean      isMerge;
	
	/**
	 * Instantiates a new rev dependency.
	 *
	 * @param id the id
	 * @param commitBranch the commit branch
	 * @param parents the parents
	 * @param tagNames the tag names
	 * @param isMerge the is merge
	 */
	public RevDependency(final String id, final RCSBranch commitBranch, final Set<String> parents,
	        final List<String> tagNames, final boolean isMerge) {
		if (isMerge) {
			CollectionCondition.minSize(parents, 2, "Merges must have multiple parents.");
		} else {
			CollectionCondition.maxSize(parents, 1, "Non-merges must have at most one parent.");
		}
		this.revId = id;
		this.commitBranch = commitBranch;
		this.parents = parents;
		this.tagNames = tagNames;
		this.isMerge = isMerge;
	}
	
	/**
	 * Gets the commit branch.
	 *
	 * @return the commit branch
	 */
	public RCSBranch getCommitBranch() {
		return this.commitBranch;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return this.revId;
	}
	
	/**
	 * Gets the parents.
	 *
	 * @return the parents
	 */
	public Set<String> getParents() {
		return this.parents;
	}
	
	/**
	 * Gets the tag names.
	 *
	 * @return the tag names
	 */
	public List<String> getTagNames() {
		return this.tagNames;
	}
	
	/**
	 * Checks if is merge.
	 *
	 * @return true, if is merge
	 */
	public boolean isMerge() {
		return this.isMerge;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" [");
		sb.append("revId=");
		sb.append(this.revId);
		sb.append(",parents=[");
		sb.append(StringUtils.join(this.parents.toArray(new String[this.parents.size()]), ","));
		sb.append("],commitBranch=");
		sb.append(this.commitBranch);
		sb.append(",tagNames=[");
		sb.append(StringUtils.join(this.tagNames.toArray(new String[this.tagNames.size()]), ","));
		sb.append("],isMerge=");
		sb.append(this.isMerge);
		sb.append("]");
		return sb.toString();
	}
	
}
