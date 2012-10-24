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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.ownhero.dev.regex.Match;

import org.junit.Test;
import org.mozkito.versions.git.GitRepository;


public class GitRepositoryTest {
	
	@Test
	public void testFormerPathRegex() {
		final String line = "R100    hello.py        python.py";
		final Match found = GitRepository.formerPathRegex.find(line);
		assertEquals(1, found.getGroupCount());
		assertEquals("hello.py", GitRepository.formerPathRegex.getGroup("result"));
	}
	
	@Test
	public void testSaschasAndererMegaRegex() {
		final String line = "^f554664a346629dc2b839f7292d06bad2db4aec hello.py (Mike Donaghy 2007-11-20 15:28:39 -0500 1) #!/usr/bin/env python";
		assertTrue(GitRepository.regex.matchesFull(line));
	}
	
}
