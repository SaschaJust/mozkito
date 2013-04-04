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
package org.mozkito.issues.tracker.bugzilla;

import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Test;

import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.persons.elements.PersonFactory;

/**
 * The Class BugzillaTracker_4_0_4_Test.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class BugzillaTracker_4_0_4_NetTest {
	
	/** The fetch uri. */
	private URI             fetchURI;
	
	/** The tracker. */
	private BugzillaTracker tracker;
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testSetup() throws Exception {
		this.tracker = new BugzillaTracker(new IssueTracker(), new PersonFactory());
		this.fetchURI = new URI(
		                        "https://bugs.eclipse.org/bugs/buglist.cgi?priority=P1;classification=Eclipse;query_format=advanced;version=1.0;product=e4");
		try {
			this.tracker.setup(this.fetchURI, null, null, this.fetchURI, "4.0.4");
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		}
	}
}
