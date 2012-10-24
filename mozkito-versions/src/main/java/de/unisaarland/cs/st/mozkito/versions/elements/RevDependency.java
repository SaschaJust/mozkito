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
package de.unisaarland.cs.st.mozkito.versions.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.mozkito.versions.model.RCSBranch;

public class RevDependency {
	
	private final String       revId;
	
	/** The map of children to their branches. */
	private Set<String>        parents = new HashSet<String>();
	private final RCSBranch    commitBranch;
	private final List<String> tagNames;
	private final boolean      isMerge;
	
	public RevDependency(final String id, final RCSBranch commitBranch, final Set<String> parents,
	        final List<String> tagNames, final boolean isMerge) {
		this.revId = id;
		this.commitBranch = commitBranch;
		this.parents = parents;
		this.tagNames = tagNames;
		this.isMerge = isMerge;
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
	
	public List<String> getTagNames() {
		return this.tagNames;
	}
	
	public boolean isMerge() {
		return this.isMerge;
	}
	
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
