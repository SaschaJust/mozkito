/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.mantis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.junit.Test;

/**
 * The Class MantisParserTest.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class MantisParserTest {
	
	/**
	 * Test attachment id regex.
	 */
	@Test
	public void testAttachmentIdRegex() {
		final String url = "https://issues.openbravo.com/file_download.php?file_id=5008&amp;type=bug";
		final MantisParser mantisParser = new MantisParser();
		final Regex idRegex = mantisParser.getAttachmentIdRegex();
		final List<List<RegexGroup>> findAll = idRegex.findAll(url);
		assertEquals(1, findAll.size());
		assertEquals(1, findAll.get(0).size());
		assertEquals("FILE_ID", findAll.get(0).get(0).getName());
		assertEquals("5008", findAll.get(0).get(0).getMatch());
	}
	
	/**
	 * Test attachment regex.
	 */
	@Test
	public void testAttachmentRegex() {
		final String s = "Selection_031.png (37,363) 2012-02-20 10:39 https://issues.openbravo.com/file_download.php?file_id=5008&type=bug  Selection_032.png (150,567) 2012-02-20 10:40 https://issues.openbravo.com/file_download.php?file_id=5009&type=bug  test.html (1,073) 2012-02-20 10:40 https://issues.openbravo.com/file_download.php?file_id=5010&type=bug";
		final MantisParser mantisParser = new MantisParser();
		final Regex regex = mantisParser.getAttachmentRegex();
		final List<List<RegexGroup>> findAll = regex.findAll(s);
		assertTrue(findAll != null);
		assertEquals(3, findAll.size());
		for (int i = 0; i < 3; ++i) {
			final List<RegexGroup> list = findAll.get(i);
			assertEquals(4, list.size());
			for (int j = 0; j < 4; ++j) {
				final RegexGroup regexGroup = list.get(j);
				switch (j) {
					case 0:
						regexGroup.getName().equals("FILE");
						break;
					case 1:
						regexGroup.getName().equals("SIZE");
						break;
					case 2:
						regexGroup.getName().equals("DATE");
						break;
					case 3:
						regexGroup.getName().equals("URL");
						break;
				}
			}
		}
		findAll.get(0).get(0).getMatch().equals("Selection_031.png");
		findAll.get(0).get(1).getMatch().equals("37,363");
		findAll.get(0).get(2).getMatch().equals("2012-02-20 10:39");
		findAll.get(0)
		       .get(3)
		       .getMatch()
		       .equals("https://issues.openbravo.com/file_download.php?file_id=5008&type=bug  Selection_032.png (150,567) 2012-02-20 10:40 https://issues.openbravo.com/file_download.php?file_id=5009&type=bug");
	}
}
