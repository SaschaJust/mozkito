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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.SortedSet;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.mozkito.issues.elements.Priority;
import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.elements.Severity;
import org.mozkito.issues.elements.Status;
import org.mozkito.issues.elements.Type;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.model.Report;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.datetime.DateTimeUtils;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class JiraParserLUCENE2222Test.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class JiraParserLUCENE2222Test {
	
	/** The parser. */
	private static JiraParser parser;
	
	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		try {
			final URI uri = JiraParserLUCENE2222Test.class.getResource(FileUtils.fileSeparator + "LUCENE-2222.xml")
			                                              .toURI();
			parser = new JiraParser(new PersonFactory());
			final IssueTracker issueTracker = new IssueTracker();
			final Report report = parser.setContext(issueTracker, new ReportLink(uri, "LUCENE-2222"));
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
	 * Test get assigned to.
	 */
	@Test
	public void testGetAssignedTo() {
		assertNull(parser.getAssignedTo());
	}
	
	/**
	 * Test get attachment entries.
	 */
	@Test
	@Ignore
	public void testGetAttachmentEntries() {
		final List<AttachmentEntry> attachmentEntries = parser.getAttachmentEntries();
		assertEquals(3, attachmentEntries.size());
		AttachmentEntry attachmentEntry = attachmentEntries.get(0);
		assertEquals("LUCENE-2222.patch", attachmentEntry.getFilename());
		assertEquals("12430620", attachmentEntry.getId());
		assertEquals("https://issues.apache.org/jira/secure/attachment/12430620/LUCENE-2222.patch",
		             attachmentEntry.getLink());
		assertEquals("text/plain", attachmentEntry.getMime());
		assertEquals(7674l, attachmentEntry.getSize());
		assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 11:20:11 +0000"), attachmentEntry.getTimestamp());
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
		assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 11:14:43 +0000"), attachmentEntry.getTimestamp());
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
		assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 00:45:29 +0000"), attachmentEntry.getTimestamp());
		author = attachmentEntry.getAuthor();
		assertTrue(author.getEmailAddresses().isEmpty());
		assertTrue(author.getFullnames().isEmpty());
		assertTrue(author.getUsernames().contains("renaud.delbru"));
		
	}
	
	/**
	 * Test get category.
	 */
	@Test
	public void testGetCategory() {
		assertEquals(null, parser.getCategory());
	}
	
	/**
	 * Test get comments.
	 */
	@Test
	public void testGetComments() {
		final SortedSet<Comment> comments = parser.getComments();
		assertEquals(11, comments.size());
		
		for (final Comment comment : comments) {
			Person author = null;
			switch (comment.getId()) {
				case 12801635:
					author = comment.getAuthor();
					assertEquals(0, author.getEmailAddresses().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getUsernames().contains("renaud.delbru"));
					assertEquals("<p>Simple patch that adds a call to blockReader.readBlock() in the Reader initialisation</p>",
					             comment.getMessage());
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 00:45:29 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801735:
					author = comment.getAuthor();
					assertEquals(0, author.getEmailAddresses().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getUsernames().contains("renaud.delbru"));
					assertEquals("<p>Fixed patch</p>", comment.getMessage());
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 11:14:43 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801736:
					author = comment.getAuthor();
					assertEquals(0, author.getEmailAddresses().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getUsernames().contains("mikemccand"));
					assertTrue(comment.getMessage()
					                  .startsWith("<p>It's great that you're working with the intblock codec, Renaud!</p>"));
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 11:20:11 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801770:
					author = comment.getAuthor();
					assertEquals(0, author.getEmailAddresses().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getUsernames().contains("renaud.delbru"));
					assertTrue(comment.getMessage()
					                  .startsWith("<p>For the moment, I first try to use FrameOfRef, and compare it with some simpler encoding methods such as VInt using the Codec interface. I would like to see if the BlockReader and Reader interface do not add too much overhead compared to a simple index input based on vint, and therefore loose the speed benefits we got on decompression."));
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 13:09:27 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801775:
					author = comment.getAuthor();
					assertEquals(0, author.getEmailAddresses().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getUsernames().contains("mikemccand"));
					assertTrue(comment.getMessage().startsWith("<p>OK I'll commit shortly.</p>"));
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 13:16:31 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801782:
					author = comment.getAuthor();
					assertEquals(0, author.getEmailAddresses().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getUsernames().contains("renaud.delbru"));
					assertTrue(comment.getMessage()
					                  .startsWith("<p>I have noticed also another problem with the block index I/O and PFOR I/O. The fixed int block index can be configured with any block size, but PFOR requires at least a block size of 32 (and even, I think it requires a block size which is a product of 32), otherwise the decompression do not work correctly (the inputSize in decompressFrame is based on frameOfRef.unComprSize). There should be a block size checking in the PFOR index I/O. Should I open a new issue ?"));
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 13:40:10 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801800:
					author = comment.getAuthor();
					assertEquals(0, author.getEmailAddresses().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getUsernames().contains("mikemccand"));
					assertTrue(comment.getMessage()
					                  .startsWith("<p>This is a check that should be added to the PFOR codec (currently lives"));
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 15:00:14 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801811:
					author = comment.getAuthor();
					assertEquals(0, author.getEmailAddresses().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getUsernames().contains("renaud.delbru"));
					assertTrue(comment.getMessage()
					                  .startsWith("<p>Yes, it is something that should be tested in the PFOR codec.</p>"));
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 15:18:32 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801864:
					author = comment.getAuthor();
					assertEquals(0, author.getUsernames().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getEmailAddresses().contains("paul.elschot@xs4all.nl"));
					assertTrue(comment.getMessage().startsWith("<blockquote>"));
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 17:06:44 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801874:
					author = comment.getAuthor();
					assertEquals(0, author.getEmailAddresses().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getUsernames().contains("renaud.delbru"));
					assertTrue(comment.getMessage().startsWith("<blockquote>"));
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 17:42:03 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				case 12801895:
					author = comment.getAuthor();
					assertEquals(0, author.getUsernames().size());
					assertEquals(0, author.getFullnames().size());
					assertTrue(author.getEmailAddresses().contains("paul.elschot@xs4all.nl"));
					assertTrue(comment.getMessage().startsWith("<p>ForDecompress.decodeAnyFrame() is pretty slow,"));
					assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 18:39:07 +0000",
					                                     new Regex(JiraParser.DATE_TIME_PATTERN)),
					             comment.getTimestamp());
					break;
				default:
					fail();
			}
		}
		
	}
	
	/**
	 * Test get component.
	 */
	@Test
	public void testGetComponent() {
		assertEquals("core/index", parser.getComponent());
	}
	
	/**
	 * Test get creation timestamp.
	 */
	@Test
	public void testGetCreationTimestamp() {
		assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 00:18:30 +0000", new Regex(JiraParser.DATE_TIME_PATTERN)),
		             parser.getCreationTimestamp());
	}
	
	/**
	 * Test get description.
	 */
	@Test
	public void testGetDescription() {
		assertTrue(parser.getDescription() != null);
		assertTrue(parser.getDescription()
		                 .startsWith("<p>The FixedIntBlockIndexInput.Reader.pending int array is not initialised. As a consequence, the FixedIntBlockIndexInput.Reader#next() method returns always 0.</p>"));
		assertTrue(parser.getDescription()
		                 .endsWith("<p>A call to FixedIntBlockIndexInput.Reader#blockReader.readBlock() during the Reader initialisation may solve the issue (to be tested).</p>"));
	}
	
	/**
	 * Test get id.
	 */
	@Test
	public void testGetId() {
		assertEquals("LUCENE-2222", parser.getId());
	}
	
	/**
	 * Test get keywords.
	 */
	@Test
	public void testGetKeywords() {
		assertEquals(0, parser.getKeywords().size());
	}
	
	/**
	 * Test get last update timestamp.
	 */
	@Test
	public void testGetLastUpdateTimestamp() {
		assertEquals(DateTimeUtils.parseDate("Tue, 12 Oct 2010 13:39:41 +0000", new Regex(JiraParser.DATE_TIME_PATTERN)),
		             parser.getLastUpdateTimestamp());
	}
	
	/**
	 * Test get priority.
	 */
	@Test
	public void testGetPriority() {
		assertEquals(Priority.NORMAL, parser.getPriority());
	}
	
	/**
	 * Test get product.
	 */
	@Test
	public void testGetProduct() {
		assertEquals(null, parser.getProduct());
	}
	
	/**
	 * Test get resolution.
	 */
	@Test
	public void testGetResolution() {
		assertEquals(Resolution.RESOLVED, parser.getResolution());
	}
	
	/**
	 * Test get resolution timestamp.
	 */
	@Test
	public void testGetResolutionTimestamp() {
		assertEquals(DateTimeUtils.parseDate("Mon, 18 Jan 2010 13:35:14 +0000", new Regex(JiraParser.DATE_TIME_PATTERN)),
		             parser.getResolutionTimestamp());
	}
	
	/**
	 * Test get scm fix version.
	 */
	@Test
	public void testGetScmFixVersion() {
		assertEquals(null, parser.getScmFixVersion());
	}
	
	/**
	 * Test get severity.
	 */
	@Test
	public void testGetSeverity() {
		assertEquals(Severity.MINOR, parser.getSeverity());
	}
	
	/**
	 * Test get siblings.
	 */
	@Test
	public void testGetSiblings() {
		assertEquals(0, parser.getSiblings().size());
	}
	
	/**
	 * Test get status.
	 */
	@Test
	public void testGetStatus() {
		assertEquals(Status.CLOSED, parser.getStatus());
	}
	
	/**
	 * Test get subject.
	 */
	@Test
	public void testGetSubject() {
		assertEquals("[LUCENE-2222] FixedIntBlockIndexInput.Reader does not initialise 'pending' int array",
		             parser.getSubject());
	}
	
	/**
	 * Test get submitter.
	 */
	@Test
	public void testGetSubmitter() {
		final Person submitter = parser.getSubmitter();
		assertEquals(0, submitter.getEmailAddresses().size());
		assertTrue(submitter.getFullnames().contains("Renaud Delbru"));
		assertTrue(submitter.getUsernames().contains("renaud.delbru"));
	}
	
	/**
	 * Testget summary.
	 */
	@Test
	public void testgetSummary() {
		assertEquals("[LUCENE-2222] FixedIntBlockIndexInput.Reader does not initialise 'pending' int array",
		             parser.getSummary());
	}
	
	/**
	 * Test get type.
	 */
	@Test
	public void testGetType() {
		assertEquals(Type.BUG, parser.getType());
	}
	
	/**
	 * Test get version.
	 */
	@Test
	public void testGetVersion() {
		assertEquals("4.0", parser.getVersion());
	}
	
}
