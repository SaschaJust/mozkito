/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.versions.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import org.mozkito.versions.model.RCSBranch;

// TODO: Auto-generated Javadoc
/**
 * The Class RevDependencyTest.
 */
public class RevDependencyTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * Test rev dependency merge.
	 */
	@Test
	public void testRevDependencyMerge() {
		final String id = "r17";
		final RCSBranch branch = new RCSBranch("brachName");
		final Set<String> parents = new HashSet<>();
		parents.add("r13");
		parents.add("r4");
		final List<String> tagNames = new ArrayList<>(2);
		tagNames.add("myTag");
		tagNames.add("anotherTag");
		final RevDependency revDep = new RevDependency(id, branch, parents, tagNames, true);
		assertEquals(branch, revDep.getCommitBranch());
		assertEquals(id, revDep.getId());
		assertTrue(CollectionUtils.isEqualCollection(parents, revDep.getParents()));
		assertTrue(CollectionUtils.isEqualCollection(tagNames, revDep.getTagNames()));
		assertEquals(true, revDep.isMerge());
	}
	
	/**
	 * Test rev dependency non merge.
	 */
	@Test
	public void testRevDependencyNonMerge() {
		final String id = "r17";
		final RCSBranch branch = new RCSBranch("brachName");
		final Set<String> parents = new HashSet<>();
		parents.add("r13");
		final List<String> tagNames = new ArrayList<>(2);
		tagNames.add("myTag");
		tagNames.add("anotherTag");
		final RevDependency revDep = new RevDependency(id, branch, parents, tagNames, false);
		assertEquals(branch, revDep.getCommitBranch());
		assertEquals(id, revDep.getId());
		assertTrue(CollectionUtils.isEqualCollection(parents, revDep.getParents()));
		assertTrue(CollectionUtils.isEqualCollection(tagNames, revDep.getTagNames()));
		assertEquals(false, revDep.isMerge());
	}
}
