/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.rcs.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.ownhero.dev.regex.RegexGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GitRepositoryTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testFormerPathRegex() {
		String line = "R100    hello.py        python.py";
		List<RegexGroup> found = GitRepository.formerPathRegex.find(line);
		assertEquals(2, found.size());
		assertEquals("hello.py", GitRepository.formerPathRegex.getGroup("result"));
	}
	
	@Test
	public void testSaschasAndererMegaRegex() {
		String line = "^f554664a346629dc2b839f7292d06bad2db4aec hello.py (Mike Donaghy 2007-11-20 15:28:39 -0500 1) #!/usr/bin/env python";
		assertTrue(GitRepository.regex.matchesFull(line));
	}
	
}
