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
package de.unisaarland.cs.st.reposuite.bugs.tracker.issuezilla;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kisa.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.persistence.model.EnumTuple;
import de.unisaarland.cs.st.reposuite.persistence.model.StringTuple;

public class IssuezillaTrackerTest {
	
	@Before
	public void setUp() throws Exception {
		Logger.logDebug();
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testFetch(){
		IssuezillaTracker tracker = new IssuezillaTracker();
		File randomDir = FileUtils.createRandomDir(new File("/tmp/"), "test", "reposuite", FileShutdownAction.DELETE);
		try {
			tracker.setup(new URI("http://argouml.tigris.org/issues/"), null, "xml.cgi?id=<BUGID>", null, null, 6297l,
			              6297l, randomDir.getAbsolutePath());
			URI uri = tracker.getLinkFromId(6297l);
			assertEquals("http://argouml.tigris.org/issues/xml.cgi?id=6297", uri.toString());
			RawReport rawReport = tracker.fetchSource(uri);
			assertTrue(rawReport != null);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (FetchException e) {
			e.printStackTrace();
			fail();
		} catch (UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				FileUtils.deleteDirectory(randomDir);
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		
	}
	
	@Test
	public void testParse() {
		IssuezillaTracker tracker = new IssuezillaTracker();
		String url = IssuezillaTrackerTest.class.getResource(FileUtils.fileSeparator + "issuezilla-argouml-5818.xml")
		.toString();
		url = url.substring(0, url.lastIndexOf("issuezilla-argouml-5818.xml"));
		String pattern = "issuezilla-argouml-" + Tracker.bugIdPlaceholder + ".xml";
		
		try {
			tracker.setup(new URI(url), null, pattern, null, null, 5818l, 5818l, null);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		RawReport rawReport = null;
		try {
			rawReport = tracker.fetchSource(tracker.getLinkFromId(5818l));
		} catch (FetchException e) {
			e.printStackTrace();
			fail();
		} catch (UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		}
		XmlReport xmlReport = tracker.createDocument(rawReport);
		Report report = tracker.parse(xmlReport);
		
		assertTrue(report != null);
		assertTrue(report.getAssignedTo() != null);
		assertTrue(report.getAssignedTo().getUsernames().contains("bobtarling"));
		assertTrue(report.getAttachmentEntries() != null);
		assertEquals(1, report.getAttachmentEntries().size());
		assertEquals(null, report.getCategory());
		assertEquals("argouml", report.getComponent());
		assertEquals(DateTimeUtils.parseDate("2009-06-25 02:05:03"), report.getCreationTimestamp());
		assertTrue(report.getDescription().startsWith("When a class diagramm contains"));
		assertTrue(report.getDescription().endsWith("es from the second class to the containing component."));
		assertEquals(5818l, report.getId());
		assertEquals(DateTimeUtils.parseDate("2011-01-29 08:06:48"), report.getLastUpdateTimestamp());
		assertEquals(Priority.HIGH, report.getPriority());
		assertEquals("Diagrams", report.getProduct());
		assertEquals(Resolution.RESOLVED, report.getResolution());
		assertEquals(DateTimeUtils.parseDate("2009-06-28 08:52:35"), report.getResolutionTimestamp());
		assertTrue(report.getResolver() != null);
		assertTrue(report.getResolver().getUsernames().contains("bobtarling"));
		assertEquals(Severity.UNKNOWN, report.getSeverity());
		assertEquals(0, report.getSiblings().size());
		assertEquals(Status.VERIFIED, report.getStatus());
		assertEquals("Moving vertex in association corrupts model if within component", report.getSubject());
		assertTrue(report.getSubmitter() != null);
		assertTrue(report.getSubmitter().getUsernames().contains("rdi"));
		assertEquals(null, report.getSummary());
		assertEquals(Type.BUG, report.getType());
		assertEquals("0.28", report.getVersion());
		
		// checking comments
		assertEquals(11, report.getComments().size());
		Iterator<Comment> commentIter = report.getComments().iterator();
		int counter = 0;
		while(commentIter.hasNext()){
			++counter;
			Comment comment = commentIter.next();
			assertTrue(comment.getAuthor() != null);
			assertEquals(report,comment.getBugReport());
			assertEquals(counter,comment.getId());
			
			switch(counter){
				case 1:
					assertTrue(comment.getAuthor().getUsernames().contains("rdi"));
					assertTrue(comment.getMessage().startsWith("Created an attachment (id=1957)"));
					assertTrue(comment.getMessage().endsWith("\"blubber\" association is moved."));
					assertEquals(DateTimeUtils.parseDate("2009-06-25 02:07:48"), comment.getTimestamp());
					break;
				case 2:
					assertTrue(comment.getAuthor().getUsernames().contains("bobtarling"));
					assertTrue(comment.getMessage().startsWith("In theory you could say that there is "));
					assertTrue(comment.getMessage().endsWith("the line back to the correct place."));
					assertEquals(DateTimeUtils.parseDate("2009-06-25 07:15:27"), comment.getTimestamp());
					break;
				case 3:
					assertTrue(comment.getAuthor().getUsernames().contains("rdi"));
					assertTrue(comment.getMessage().startsWith("Well, this is not the behavior I meant."));
					assertTrue(comment.getMessage()
					           .endsWith("embedding     |\n  | component.                    |\n  |_______________________________|"));
					assertEquals(DateTimeUtils.parseDate("2009-06-26 00:13:37"), comment.getTimestamp());
					break;
				case 4:
					assertTrue(comment.getAuthor().getUsernames().contains("bobtarling"));
					assertTrue(comment.getMessage().startsWith("If I drag the edge end to the right I see no "));
					assertTrue(comment.getMessage().endsWith("there and not to some class?"));
					assertEquals(DateTimeUtils.parseDate("2009-06-26 00:30:54"), comment.getTimestamp());
					break;
				case 5:
					assertTrue(comment.getAuthor().getUsernames().contains("rdi"));
					assertTrue(comment.getMessage()
					           .startsWith("Well, I think we talk about different points in the line,"));
					assertTrue(comment.getMessage()
					           .endsWith("|             b--->(c) Class B| |\n  |                   |_________| |"));
					assertEquals(DateTimeUtils.parseDate("2009-06-26 04:56:21"), comment.getTimestamp());
					break;
				case 6:
					assertTrue(comment.getAuthor().getUsernames().contains("bobtarling"));
					assertTrue(comment.getMessage().startsWith("Aha - I see what you mean now, yes this is bad."));
					assertTrue(comment.getMessage().endsWith("reposition such edges ad then replace them later."));
					assertEquals(DateTimeUtils.parseDate("2009-06-26 06:06:42"), comment.getTimestamp());
					break;
				case 7:
					assertTrue(comment.getAuthor().getUsernames().contains("bobtarling"));
					assertEquals("Changing priority", comment.getMessage());
					assertEquals(DateTimeUtils.parseDate("2009-06-26 06:07:18"), comment.getTimestamp());
					break;
				case 8:
					assertTrue(comment.getAuthor().getUsernames().contains("bobtarling"));
					assertTrue(comment.getMessage().startsWith("Changing the summary with latest understanding."));
					assertTrue(comment.getMessage().endsWith("e.g. a usage between classifiers inside a package."));
					assertEquals(DateTimeUtils.parseDate("2009-06-26 06:49:08"), comment.getTimestamp());
					break;
				case 9:
					assertTrue(comment.getAuthor().getUsernames().contains("bobtarling"));
					assertTrue(comment.getMessage().startsWith("Fixed in r17160"));
					assertTrue(comment.getMessage().endsWith("Graphics2D rather than Graphics"));
					assertEquals(DateTimeUtils.parseDate("2009-06-28 08:18:36"), comment.getTimestamp());
					break;
				case 10:
					assertTrue(comment.getAuthor().getUsernames().contains("bobtarling"));
					assertTrue(comment.getMessage().startsWith("I found the problem in some of "));
					assertTrue(comment.getMessage().endsWith("This issue is now fixed release 0.29.1"));
					assertEquals(DateTimeUtils.parseDate("2009-06-28 08:52:35"), comment.getTimestamp());
					break;
				case 11:
					assertTrue(comment.getAuthor().getUsernames().contains("linus"));
					assertTrue(comment.getMessage().startsWith("The solution to this issue is"));
					assertTrue(comment.getMessage().endsWith("please create a new issue."));
					assertEquals(DateTimeUtils.parseDate("2011-01-29 08:06:48"), comment.getTimestamp());
					break;
				default:
					fail();
			}
		}
		
		
		// checking history
		assertEquals(5, report.getHistory().size());
		Iterator<HistoryElement> historyIter = report.getHistory().iterator();
		counter = 0;
		while (historyIter.hasNext()) {
			++counter;
			HistoryElement historyElement = historyIter.next();
			assertEquals(report.getId(), historyElement.getBugId());
			assertTrue(historyElement.getAuthor() != null);
			assertTrue(historyElement.getText() == null);
			switch (counter) {
				case 1:
					assertTrue(historyElement.getAuthor().getUsernames().contains("bobtarling"));
					assertEquals(0, historyElement.getChangedDateValues().size());
					assertEquals(0, historyElement.getChangedEnumValues().size());
					assertEquals(0, historyElement.getChangedPersonValues().size());
					assertEquals(1, historyElement.getChangedStringValues().size());
					
					assertTrue(historyElement.getChangedStringValues().containsKey("subject"));
					StringTuple stringTuple = historyElement.getChangedStringValues().get("subject");
					assertEquals("An association in a class diagram gets assigned to a component wehen a line point is moved",
					             stringTuple.getOldValue());
					assertEquals("An association from class to component should be attached to component edge.",
					             stringTuple.getNewValue());
					
					assertEquals(1, historyElement.getFields().size());
					assertTrue(historyElement.getFields().contains("subject"));
					
					assertEquals(DateTimeUtils.parseDate("2009-06-25 07:15:27"), historyElement.getTimestamp());
					break;
				case 2:
					assertTrue(historyElement.getAuthor().getUsernames().contains("bobtarling"));
					assertEquals(0, historyElement.getChangedDateValues().size());
					assertEquals(1, historyElement.getChangedEnumValues().size());
					assertTrue(historyElement.getChangedEnumValues().containsKey("priority"));
					EnumTuple enumTuple = historyElement.getChangedEnumValues().get("priority");
					assertEquals(IssuezillaXMLParser.getPriority("P3"), enumTuple.getOldValue());
					assertEquals(IssuezillaXMLParser.getPriority("P2"), enumTuple.getNewValue());
					
					assertEquals(0, historyElement.getChangedPersonValues().size());
					assertEquals(0, historyElement.getChangedStringValues().size());
					
					assertEquals(1, historyElement.getFields().size());
					assertTrue(historyElement.getFields().contains("priority"));
					
					assertEquals(DateTimeUtils.parseDate("2009-06-26 06:07:18"), historyElement.getTimestamp());
					break;
				case 3:
					assertTrue(historyElement.getAuthor().getUsernames().contains("bobtarling"));
					assertEquals(0, historyElement.getChangedDateValues().size());
					assertEquals(0, historyElement.getChangedEnumValues().size());
					assertEquals(0, historyElement.getChangedPersonValues().size());
					assertEquals(1, historyElement.getChangedStringValues().size());
					
					assertTrue(historyElement.getChangedStringValues().containsKey("subject"));
					stringTuple = historyElement.getChangedStringValues().get("subject");
					assertEquals("An association from class to component should be attached to component edge.",
					             stringTuple.getOldValue());
					assertEquals("Moving vertex in association corrupts model if within component",
					             stringTuple.getNewValue());
					
					assertEquals(1, historyElement.getFields().size());
					assertTrue(historyElement.getFields().contains("subject"));
					
					assertEquals(DateTimeUtils.parseDate("2009-06-26 06:49:08"), historyElement.getTimestamp());
					break;
				case 4:
					assertTrue(historyElement.getAuthor().getUsernames().contains("bobtarling"));
					assertEquals(0, historyElement.getChangedDateValues().size());
					assertEquals(2, historyElement.getChangedEnumValues().size());
					
					assertTrue(historyElement.getChangedEnumValues().containsKey("status"));
					enumTuple = historyElement.getChangedEnumValues().get("status");
					assertEquals(IssuezillaXMLParser.getStatus("NEW"), enumTuple.getOldValue());
					assertEquals(IssuezillaXMLParser.getStatus("RESOLVED"), enumTuple.getNewValue());
					
					assertTrue(historyElement.getChangedEnumValues().containsKey("resolution"));
					enumTuple = historyElement.getChangedEnumValues().get("resolution");
					assertEquals(IssuezillaXMLParser.getResolution(""), enumTuple.getOldValue());
					assertEquals(IssuezillaXMLParser.getResolution("FIXED"), enumTuple.getNewValue());
					
					assertEquals(0, historyElement.getChangedPersonValues().size());
					assertEquals(0, historyElement.getChangedStringValues().size());
					
					assertEquals(2, historyElement.getFields().size());
					assertTrue(historyElement.getFields().contains("status"));
					assertTrue(historyElement.getFields().contains("resolution"));
					
					assertEquals(DateTimeUtils.parseDate("2009-06-28 08:52:35"), historyElement.getTimestamp());
					break;
				case 5:
					assertTrue(historyElement.getAuthor().getUsernames().contains("linus"));
					assertEquals(0, historyElement.getChangedDateValues().size());
					assertEquals(1, historyElement.getChangedEnumValues().size());
					
					assertTrue(historyElement.getChangedEnumValues().containsKey("status"));
					enumTuple = historyElement.getChangedEnumValues().get("status");
					assertEquals(IssuezillaXMLParser.getStatus("RESOLVED"), enumTuple.getOldValue());
					assertEquals(IssuezillaXMLParser.getStatus("VERIFIED"), enumTuple.getNewValue());
					
					assertEquals(0, historyElement.getChangedPersonValues().size());
					assertEquals(0, historyElement.getChangedStringValues().size());
					
					assertEquals(1, historyElement.getFields().size());
					assertTrue(historyElement.getFields().contains("status"));
					
					assertEquals(DateTimeUtils.parseDate("2011-01-29 08:06:48"), historyElement.getTimestamp());
					break;
				default:
					fail();
			}
		}
	}
}
