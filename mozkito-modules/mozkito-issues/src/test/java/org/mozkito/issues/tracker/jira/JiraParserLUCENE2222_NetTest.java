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
package org.mozkito.issues.tracker.jira;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.junit.Before;
import org.junit.Test;
import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.elements.Severity;
import org.mozkito.issues.elements.Status;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.issues.model.HistoryElement;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.model.Report;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.persistence.model.EnumTuple;
import org.mozkito.persistence.model.StringTuple;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.datetime.DateTimeUtils;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class JiraParserLUCENE2222_NetTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class JiraParserLUCENE2222_NetTest {
	
	/** The parser. */
	private static JiraParser   parser;
	private static IssueTracker issueTracker;
	private static Report       report;
	
	/**
	 * Before class.
	 */
	@Before
	public void beforeClass() {
		try {
			final URI uri = JiraParserLUCENE2222_NetTest.class.getResource(FileUtils.fileSeparator + "LUCENE-2222.xml")
			                                                  .toURI();
			JiraParserLUCENE2222_NetTest.parser = new JiraParser(new PersonFactory());
			issueTracker = new IssueTracker();
			report = JiraParserLUCENE2222_NetTest.parser.setContext(issueTracker, new ReportLink(uri, "LUCENE-2222"));
			assertNotNull(report);
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} finally {
			//
		}
	}
	
	/**
	 * Test get attachment entries.
	 */
	@Test
	public void testGetAttachmentEntries() {
		final List<AttachmentEntry> attachmentEntries = JiraParserLUCENE2222_NetTest.parser.getAttachmentEntries();
		assertEquals(3, attachmentEntries.size());
		AttachmentEntry attachmentEntry = attachmentEntries.get(0);
		assertEquals("LUCENE-2222.patch", attachmentEntry.getFilename());
		assertEquals("12430620", attachmentEntry.getId());
		assertEquals("https://issues.apache.org/jira/secure/attachment/12430620/LUCENE-2222.patch",
		             attachmentEntry.getLink());
		assertEquals("text/plain", attachmentEntry.getMime());
		assertEquals(7674l, attachmentEntry.getSize());
		assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 11:20:11 +0000", new Regex(JiraParser.DATE_TIME_PATTERN)),
		             attachmentEntry.getTimestamp());
		Person author = attachmentEntry.getAuthor();
		assertTrue(author.getEmailAddresses().isEmpty());
		assertTrue(author.getFullnames().isEmpty());
		assertTrue(author.getUsernames().contains("mikemccand"));
		
		attachmentEntry = attachmentEntries.get(1);
		assertEquals("LUCENE-2222.patch", attachmentEntry.getFilename());
		assertEquals("12430619", attachmentEntry.getId());
		assertEquals("https://issues.apache.org/jira/secure/attachment/12430619/LUCENE-2222.patch",
		             attachmentEntry.getLink());
		assertEquals("text/plain", attachmentEntry.getMime());
		assertEquals(937l, attachmentEntry.getSize());
		assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 11:14:43 +0000", new Regex(JiraParser.DATE_TIME_PATTERN)),
		             attachmentEntry.getTimestamp());
		author = attachmentEntry.getAuthor();
		assertTrue(author.getEmailAddresses().isEmpty());
		assertTrue(author.getFullnames().isEmpty());
		assertTrue(author.getUsernames().contains("renaud.delbru"));
		
		attachmentEntry = attachmentEntries.get(2);
		assertEquals("LUCENE-2222.patch", attachmentEntry.getFilename());
		assertEquals("12430592", attachmentEntry.getId());
		assertEquals("https://issues.apache.org/jira/secure/attachment/12430592/LUCENE-2222.patch",
		             attachmentEntry.getLink());
		assertEquals("text/plain", attachmentEntry.getMime());
		assertEquals(3872l, attachmentEntry.getSize());
		assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 00:45:29 +0000", new Regex(JiraParser.DATE_TIME_PATTERN)),
		             attachmentEntry.getTimestamp());
		author = attachmentEntry.getAuthor();
		assertTrue(author.getEmailAddresses().isEmpty());
		assertTrue(author.getFullnames().isEmpty());
		assertTrue(author.getUsernames().contains("renaud.delbru"));
		
	}
	
	/**
	 * Test get history elements.
	 */
	@Test
	public void testGetHistoryElements() {
		JiraParserLUCENE2222_NetTest.parser.parseHistoryElements(report.getHistory());
		assertEquals(12, report.getHistory().size());
		
		int counter = 0;
		for (final HistoryElement hElem : report.getHistory()) {
			switch (counter) {
				case 0:
					final Person author = hElem.getAuthor();
					assert (author != null);
					assertTrue(author.getFullnames().contains("Renaud Delbru"));
					assertEquals(0, author.getEmailAddresses().size());
					assertTrue(author.getUsernames().contains("renaud.delbru"));
					assertEquals("LUCENE-2222", hElem.getBugId());
					assertEquals(0, hElem.getChangedDateValues().size());
					assertTrue(hElem.getChangedEnumValues().containsKey("severity"));
					final EnumTuple enumTuple = hElem.getChangedEnumValues().get("severity");
					assertEquals(Severity.MAJOR, enumTuple.getOldValue());
					assertEquals(Severity.MINOR, enumTuple.getNewValue());
					assertEquals(0, hElem.getChangedPersonValues().size());
					assertEquals(0, hElem.getChangedStringValues().size());
					assertEquals(null, hElem.getText());
					assertEquals(DateTimeUtils.parseDate("2010-01-18T00:43+0000",
					                                     new Regex(JiraHistoryParser.HISTORY_DATE_TIME_PATTERN)),
					             hElem.getTimestamp());
					break;
				case 4:
					final Person author1 = hElem.getAuthor();
					assert (author1 != null);
					assertTrue(author1.getFullnames().contains("Michael McCandless"));
					assertEquals(0, author1.getEmailAddresses().size());
					assertTrue(author1.getUsernames().contains("mikemccand"));
					assertEquals("LUCENE-2222", hElem.getBugId());
					assertEquals(0, hElem.getChangedDateValues().size());
					
					assertTrue(hElem.getChangedEnumValues().containsKey("status"));
					final EnumTuple statusEnumTuple = hElem.getChangedEnumValues().get("status");
					assertEquals(Status.NEW, statusEnumTuple.getOldValue());
					assertEquals(Status.CLOSED, statusEnumTuple.getNewValue());
					
					assertTrue(hElem.getChangedEnumValues().containsKey("resolution"));
					final EnumTuple resolutionEnumTuple = hElem.getChangedEnumValues().get("resolution");
					assertEquals(Resolution.UNKNOWN, resolutionEnumTuple.getOldValue());
					assertEquals(Resolution.RESOLVED, resolutionEnumTuple.getNewValue());
					
					assertEquals(0, hElem.getChangedPersonValues().size());
					assertEquals(0, hElem.getChangedStringValues().size());
					assertEquals(null, hElem.getText());
					assertEquals(DateTimeUtils.parseDate("2010-01-18T13:35+0000",
					                                     new Regex(JiraHistoryParser.HISTORY_DATE_TIME_PATTERN)),
					             hElem.getTimestamp());
					break;
				case 8:
					final Person author2 = hElem.getAuthor();
					assert (author2 != null);
					assertTrue(author2.getFullnames().contains("Uwe Schindler"));
					assertEquals(0, author2.getEmailAddresses().size());
					assertTrue(author2.getUsernames().contains("thetaphi"));
					assertEquals("LUCENE-2222", hElem.getBugId());
					assertEquals(0, hElem.getChangedDateValues().size());
					
					assertTrue(hElem.getChangedStringValues().containsKey("version"));
					final StringTuple versionEnumTuple = hElem.getChangedStringValues().get("version");
					assertEquals("flex branch", versionEnumTuple.getOldValue());
					assertEquals("4.0", versionEnumTuple.getNewValue());
					
					assertEquals(0, hElem.getChangedPersonValues().size());
					assertEquals(0, hElem.getChangedEnumValues().size());
					assertEquals(null, hElem.getText());
					assertEquals(DateTimeUtils.parseDate("2010-10-12T13:39+0000",
					                                     new Regex(JiraHistoryParser.HISTORY_DATE_TIME_PATTERN)),
					             hElem.getTimestamp());
					break;
				default:
					// do nothing
			}
			++counter;
		}
	}
}
