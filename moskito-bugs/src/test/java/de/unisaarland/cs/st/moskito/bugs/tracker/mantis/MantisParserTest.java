/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.mantis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.persistence.model.PersonTuple;

/**
 * The Class MantisParserTest.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class MantisParserTest {
	
	/** The report19810. */
	private RawContent report19810;
	
	/** The report18828. */
	private RawContent report18828;
	
	/** The report8468. */
	private RawContent report8468;
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		
		this.report19810 = IOUtils.fetch(getClass().getResource(FileUtils.fileSeparator + "open-bravo-19810.html")
		                                           .toURI());
		this.report18828 = IOUtils.fetch(getClass().getResource(FileUtils.fileSeparator + "open-bravo-18828.html")
		                                           .toURI());
		this.report8468 = IOUtils.fetch(getClass().getResource(FileUtils.fileSeparator + "open-bravo-8468.html")
		                                          .toURI());
		
	}
	
	/**
	 * Test attachment id regex.
	 */
	@Test
	public void testAttachmentIdRegex() {
		final String url = "https://issues.openbravo.com/file_download.php?file_id=5008&amp;type=bug";
		final MantisParser mantisParser = new MantisParser();
		final Regex idRegex = mantisParser.getAttachmentIdRegex();
		final MultiMatch findAll = idRegex.findAll(url);
		assertEquals(1, findAll.size());
		assertEquals(1, findAll.getMatch(0).getGroupCount());
		assertEquals("FILE_ID", findAll.getMatch(0).getGroup(1).getName());
		assertEquals("5008", findAll.getMatch(0).getGroup(1).getMatch());
	}
	
	/**
	 * Test attachment regex.
	 */
	@Test
	public void testAttachmentRegex() {
		final String s = "Selection_031.png (37,363) 2012-02-20 10:39 https://issues.openbravo.com/file_download.php?file_id=5008&type=bug  Selection_032.png (150,567) 2012-02-20 10:40 https://issues.openbravo.com/file_download.php?file_id=5009&type=bug  test.html (1,073) 2012-02-20 10:40 https://issues.openbravo.com/file_download.php?file_id=5010&type=bug";
		final MantisParser mantisParser = new MantisParser();
		final Regex regex = mantisParser.getAttachmentRegex();
		final MultiMatch findAll = regex.findAll(s);
		assert (findAll != null);
		for (final Match match : findAll) {
			assertTrue(match.hasNamedGroup("FILE"));
			assertTrue(match.hasNamedGroup("SIZE"));
			assertTrue(match.hasNamedGroup("DATE"));
			assertTrue(match.hasNamedGroup("URL"));
			switch (match.getGroup("FILE").getMatch().trim()) {
				case "Selection_031.png":
					assertEquals("37,363", match.getGroup("SIZE").getMatch());
					assertEquals("2012-02-20 10:39", match.getGroup("DATE").getMatch());
					assertEquals("https://issues.openbravo.com/file_download.php?file_id=5008&type=bug",
					             match.getGroup("URL").getMatch());
					break;
				case "Selection_032.png":
					assertEquals("150,567", match.getGroup("SIZE").getMatch());
					assertEquals("2012-02-20 10:40", match.getGroup("DATE").getMatch());
					assertEquals("https://issues.openbravo.com/file_download.php?file_id=5009&type=bug",
					             match.getGroup("URL").getMatch());
					break;
				case "test.html":
					assertEquals("1,073", match.getGroup("SIZE").getMatch());
					assertEquals("2012-02-20 10:40", match.getGroup("DATE").getMatch());
					assertEquals("https://issues.openbravo.com/file_download.php?file_id=5010&type=bug",
					             match.getGroup("URL").getMatch());
					break;
				default:
					fail(String.format("Unknown FILE group with match `%s`", match.getGroup("FILE").getMatch().trim()));
			}
		}
	}
	
	/**
	 * Test attachments18828.
	 */
	@Test
	public void testAttachments18828() {
		final MantisParser parser = new MantisParser();
		parser.setURI(new ReportLink(this.report18828.getUri(), "18828"));
		final List<AttachmentEntry> attachments = parser.getAttachmentEntries();
		assertTrue(attachments.isEmpty());
	}
	
	/**
	 * Test attachments19810.
	 */
	@Test
	public void testAttachments19810() {
		
		final MantisParser parser = new MantisParser();
		parser.setURI(new ReportLink(this.report19810.getUri(), "19810"));
		final List<AttachmentEntry> attachments = parser.getAttachmentEntries();
		
		final String reportLink = this.report19810.getUri().toASCIIString();
		final int index = reportLink.lastIndexOf("/");
		
		assertEquals(3, attachments.size());
		assertEquals("5008", attachments.get(0).getId());
		assertTrue(attachments.get(0) != null);
		assertTrue(attachments.get(0).getAuthor() != null);
		assertTrue(attachments.get(0).getAuthor().getUsernames().contains("alostale"));
		assertEquals(DateTimeUtils.parseDate("2012-02-20 10:39"), attachments.get(0).getTimestamp());
		assertEquals(null, attachments.get(0).getDescription());
		assertEquals("Selection_031.png", attachments.get(0).getFilename());
		assertEquals(reportLink.substring(0, index + 1) + "file_download.php?file_id=5008&type=bug",
		             attachments.get(0).getLink().toString());
		assertEquals(37363, attachments.get(0).getSize());
		
		assertEquals("5009", attachments.get(1).getId());
		assertTrue(attachments.get(1).getAuthor().getUsernames().contains("alostale"));
		assertEquals(DateTimeUtils.parseDate("2012-02-20 10:40"), attachments.get(1).getTimestamp());
		assertEquals(null, attachments.get(1).getDescription());
		assertEquals("Selection_032.png", attachments.get(1).getFilename());
		assertEquals(reportLink.substring(0, index + 1) + "file_download.php?file_id=5009&type=bug",
		             attachments.get(1).getLink().toString());
		assertEquals(150567, attachments.get(1).getSize());
		
		assertEquals("5010", attachments.get(2).getId());
		assertTrue(attachments.get(2).getAuthor().getUsernames().contains("alostale"));
		assertEquals(DateTimeUtils.parseDate("2012-02-20 10:40"), attachments.get(2).getTimestamp());
		assertEquals(null, attachments.get(2).getDescription());
		assertEquals("test.html", attachments.get(2).getFilename());
		assertEquals(reportLink.substring(0, index + 1) + "file_download.php?file_id=5010&type=bug",
		             attachments.get(2).getLink().toString());
		assertEquals(1073, attachments.get(2).getSize());
		
	}
	
	/**
	 * Test keywords.
	 */
	@Test
	public void testKeywords() {
		final MantisParser parser = new MantisParser();
		parser.setURI(new ReportLink(this.report8468.getUri(), "8468"));
		final Set<String> keywords = parser.getKeywords();
		assertEquals(2, keywords.size());
		assertTrue(keywords.contains("main"));
		assertTrue(keywords.contains("tictech"));
	}
	
	/**
	 * Test parse.
	 */
	@Test
	public void testParse() {
		
		final MantisParser parser = new MantisParser();
		parser.setURI(new ReportLink(this.report19810.getUri(), "19810"));
		
		assertEquals("0019810", parser.getId());
		assertEquals("alostale", parser.getAssignedTo().getUsernames().iterator().next());
		
		assertEquals("[Openbravo ERP] A. Platform", parser.getCategory());
		
		final SortedSet<Comment> comments = parser.getComments();
		assertEquals(2, comments.size());
		assertTrue(comments.first().getTimestamp().isBefore(comments.last().getTimestamp()));
		
		assertEquals(45258, comments.first().getId());
		assertEquals("hgbot", comments.first().getAuthor().getUsernames().iterator().next());
		assertTrue(comments.first().getAuthor().getFullnames().isEmpty());
		assertTrue(comments.first().getAuthor().getEmailAddresses().isEmpty());
		final DateTime dt = DateTimeUtils.parseDate("2012-02-20 10:42");
		assertTrue(dt.isEqual(comments.first().getTimestamp()));
		assertTrue(comments.first().getMessage().startsWith("Repository: erp/devel/pi"));
		
		assertEquals(45259, comments.last().getId());
		assertEquals("alostale", comments.last().getAuthor().getUsernames().iterator().next());
		assertTrue(comments.last().getAuthor().getFullnames().isEmpty());
		assertTrue(comments.last().getAuthor().getEmailAddresses().isEmpty());
		
		assertTrue(DateTimeUtils.parseDate("2012-02-20 10:44").isEqual(comments.last().getTimestamp()));
		assertTrue(comments.last().getMessage().startsWith("The fix for this issue makes"));
		assertTrue(comments.last().getMessage().endsWith("instances within memory."));
		
		assertEquals("Core", parser.getComponent());
		assertEquals(DateTimeUtils.parseDate("2012-02-20 10:39"), parser.getCreationTimestamp());
		assertTrue(parser.getDescription().startsWith("Whenever a window in"));
		assertTrue(parser.getDescription().endsWith("dump suffering this issue."));
		
		final SortedSet<HistoryElement> history = parser.getHistoryElements();
		assertFalse(history.isEmpty());
		assertEquals(5, history.size());
		
		final Iterator<HistoryElement> iterator = history.iterator();
		HistoryElement hElement = iterator.next();
		assert (hElement != null);
		final Map<String, PersonTuple> changedPersonValues = hElement.getChangedPersonValues();
		assertEquals(1, changedPersonValues.size());
		assertTrue(changedPersonValues.get("assignedto") != null);
		assertTrue(changedPersonValues.get("assignedto").getNewValue().getPersons().iterator().next().getUsernames()
		                              .contains("alostale"));
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertFalse(hElement.getChangedStringValues().isEmpty());
		assertEquals("", hElement.getChangedStringValues().get("component").getOldValue());
		assertEquals("Core", hElement.getChangedStringValues().get("component").getNewValue());
		assertTrue(hElement.getChangedEnumValues().isEmpty());
		
		hElement = iterator.next();
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertTrue(hElement.getChangedStringValues().isEmpty());
		assertTrue(hElement.getChangedEnumValues().isEmpty());
		assertTrue(hElement.getChangedPersonValues().isEmpty());
		
		hElement = iterator.next();
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertFalse(hElement.getChangedStringValues().isEmpty());
		assertTrue(hElement.getChangedStringValues().containsKey("scmfixversion"));
		assertEquals("", hElement.getChangedStringValues().get("scmfixversion").getOldValue());
		assertEquals("http://code.openbravo.com/erp/devel/pi/rev/c3105cd9c9c4ebd834baceababedddf88de4fdb2",
		             hElement.getChangedStringValues().get("scmfixversion").getNewValue());
		
		assertFalse(hElement.getChangedEnumValues().isEmpty());
		assertEquals(2, hElement.getChangedEnumValues().size());
		assertTrue(hElement.getChangedEnumValues().containsKey("status"));
		assertTrue(hElement.getChangedEnumValues().containsKey("resolution"));
		assertEquals(Status.NEW, hElement.getChangedEnumValues().get("status").getOldValue());
		assertEquals(Status.CLOSED, hElement.getChangedEnumValues().get("status").getNewValue());
		
		assertEquals(Resolution.UNRESOLVED, hElement.getChangedEnumValues().get("resolution").getOldValue());
		assertEquals(Resolution.RESOLVED, hElement.getChangedEnumValues().get("resolution").getNewValue());
		
		assertTrue(hElement.getChangedPersonValues().isEmpty());
		
		hElement = iterator.next();
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertTrue(hElement.getChangedStringValues().isEmpty());
		assertTrue(hElement.getChangedEnumValues().isEmpty());
		assertTrue(hElement.getChangedPersonValues().isEmpty());
		
		hElement = iterator.next();
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertTrue(hElement.getChangedStringValues().isEmpty());
		assertFalse(hElement.getChangedEnumValues().isEmpty());
		assertEquals(1, hElement.getChangedEnumValues().size());
		assertTrue(hElement.getChangedEnumValues().containsKey("status"));
		assertEquals(Status.CLOSED, hElement.getChangedEnumValues().get("status").getOldValue());
		assertEquals(Status.CLOSED, hElement.getChangedEnumValues().get("status").getNewValue());
		assertTrue(hElement.getChangedPersonValues().isEmpty());
		
		assertTrue(DateTimeUtils.parseDate("2012-02-21 17:24").isEqual(parser.getLastUpdateTimestamp()));
		
		assertEquals(Priority.VERY_HIGH, parser.getPriority());
		assertEquals(Resolution.RESOLVED, parser.getResolution());
		assertEquals(Severity.MAJOR, parser.getSeverity());
		//
		assertEquals(0, parser.getSiblings().size());
		
		assertEquals(Status.CLOSED, parser.getStatus());
		assertEquals("0019810: ViewComponent memory leak", parser.getSubject());
		assertTrue(parser.getSubmitter() != null);
		assertEquals("alostale", parser.getSubmitter().getUsernames().iterator().next());
		assertTrue(parser.getSubmitter().getFullnames().isEmpty());
		assertEquals("0019810: ViewComponent memory leak", parser.getSummary());
		assertEquals(Type.BUG, parser.getType());
		assertEquals("", parser.getVersion());
		assertTrue(parser.getKeywords().isEmpty());
	}
	
	/**
	 * Test siblings.
	 */
	@Test
	public void testSiblings() {
		final MantisParser parser = new MantisParser();
		parser.setURI(new ReportLink(this.report18828.getUri(), "18828"));
		final Set<String> siblings = parser.getSiblings();
		assertEquals(2, siblings.size());
		assertTrue(siblings.contains("0019022"));
		assertTrue(siblings.contains("0018893"));
	}
}
