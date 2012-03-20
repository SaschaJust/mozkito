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
package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.History;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

public class SourceforgeTrackerTest {
	
	private static String url1 = "http://sourceforge.net/tracker/?group_id=97367&atid=617889";
	private static String url2 = "http://sourceforge.net/tracker/?words=tracker_browse&sort=open_date&sortdir=desc&offset=50&group_id=97367&atid=617889&assignee=&status=&category=&artgroup=&keyword=&submitter=&artifact_id=";
	
	@Test
	public void testAtIdRegex() {
		final Regex atIdRegex = new Regex(SourceforgeTracker.atIdRegex.getPattern());
		atIdRegex.find(url1);
		assertEquals(new Integer(1), atIdRegex.getGroupCount());
		assertEquals("617889", atIdRegex.getGroup("atid"));
		
		atIdRegex.find(url2);
		assertEquals(new Integer(1), atIdRegex.getGroupCount());
		assertEquals("617889", atIdRegex.getGroup("atid"));
		
		final String newUrl = atIdRegex.replaceAll(url1, "atid=123456");
		atIdRegex.find(newUrl);
		assertEquals(new Integer(1), atIdRegex.getGroupCount());
		assertEquals("123456", atIdRegex.getGroup("atid"));
	}
	
	@Test
	public void testGroupIdRegex() {
		final Regex groupIdRegex = new Regex(SourceforgeTracker.groupIdRegex.getPattern());
		groupIdRegex.find(url1);
		assertEquals(new Integer(1), groupIdRegex.getGroupCount());
		assertEquals("97367", groupIdRegex.getGroup("group_id"));
		
		groupIdRegex.find(url2);
		assertEquals(new Integer(1), groupIdRegex.getGroupCount());
		assertEquals("97367", groupIdRegex.getGroup("group_id"));
	}
	
	@Test
	public void testIssueHistory() {
		
		try {
			
			final SourceforgeTracker tracker = new SourceforgeTracker();
			
			final String url = getClass().getResource(FileUtils.fileSeparator + "sourceforge_issue_3107411.html")
			                             .toURI().toASCIIString();
			String pattern = url.substring(0, url.lastIndexOf("sourceforge_issue_3107411.html"));
			pattern = "sourceforge_issue_" + Tracker.getBugidplaceholder() + ".html";
			
			tracker.setup(new URI(url), null, pattern, null, null, 3107411l, 3107411l, null);
			final Report report = tracker.parse(new ReportLink(new URI(url), "3107411"));
			
			final History history = report.getHistory();
			assertEquals(9, history.size());
			
			final Iterator<HistoryElement> hElemIter = history.getElements().iterator();
			
			assertTrue(hElemIter.hasNext());
			HistoryElement hElem = hElemIter.next();
			
			assertTrue(hElem.contains("priority"));
			
			assertEquals(Priority.NORMAL, hElem.get("priority").getFirst());
			assertEquals(Priority.HIGH, hElem.get("priority").getSecond());
			assertTrue(hElem.getAuthor().getUsernames().contains("sascha-just"));
			assertEquals("3107411", hElem.getBugId());
			assertEquals(0, hElem.getChangedDateValues().size());
			assertEquals(1, hElem.getChangedEnumValues().size());
			assertEquals(0, hElem.getChangedPersonValues().size());
			assertEquals(0, hElem.getChangedStringValues().size());
			assertEquals(1, hElem.getFields().size());
			assertTrue(hElem.getForField("priority") != null);
			
			assertTrue(hElemIter.hasNext());
			hElem = hElemIter.next();
			assertEquals(Priority.HIGH, hElem.get("priority").getFirst());
			assertEquals(Priority.VERY_HIGH, hElem.get("priority").getSecond());
			assertTrue(hElem.getAuthor().getUsernames().contains("sascha-just"));
			assertEquals("3107411", hElem.getBugId());
			assertEquals(0, hElem.getChangedDateValues().size());
			assertEquals(1, hElem.getChangedEnumValues().size());
			assertEquals(0, hElem.getChangedPersonValues().size());
			assertEquals(0, hElem.getChangedStringValues().size());
			assertEquals(1, hElem.getFields().size());
			assertTrue(hElem.getForField("priority") != null);
			
			assertTrue(hElemIter.hasNext());
			hElem = hElemIter.next();
			assertEquals(Status.NEW, hElem.get("status").getFirst());
			assertEquals(Status.IN_PROGRESS, hElem.get("status").getSecond());
			assertTrue(hElem.getAuthor().getUsernames().contains("kimherzig"));
			assertEquals("3107411", hElem.getBugId());
			assertEquals(0, hElem.getChangedDateValues().size());
			assertEquals(1, hElem.getChangedEnumValues().size());
			assertEquals(0, hElem.getChangedPersonValues().size());
			assertEquals(0, hElem.getChangedStringValues().size());
			assertEquals(1, hElem.getFields().size());
			assertTrue(hElem.getForField("status") != null);
			assertEquals(DateTimeUtils.parseDate("2011-04-23 10:13:40 UTC"), hElem.getTimestamp());
			
			assertTrue(hElemIter.hasNext());
			hElem = hElemIter.next();
			assertEquals(Resolution.UNRESOLVED, hElem.get("resolution").getFirst());
			assertEquals(Resolution.UNRESOLVED, hElem.get("resolution").getSecond());
			assertTrue(hElem.getAuthor().getUsernames().contains("kimherzig"));
			assertEquals("3107411", hElem.getBugId());
			assertEquals(0, hElem.getChangedDateValues().size());
			assertEquals(1, hElem.getChangedEnumValues().size());
			assertEquals(0, hElem.getChangedPersonValues().size());
			assertEquals(0, hElem.getChangedStringValues().size());
			assertEquals(1, hElem.getFields().size());
			assertTrue(hElem.getForField("resolution") != null);
			assertEquals(DateTimeUtils.parseDate("2011-04-23 10:14:34 UTC"), hElem.getTimestamp());
			
			assertTrue(hElemIter.hasNext());
			hElem = hElemIter.next();
			assertEquals(Resolution.UNRESOLVED, hElem.get("resolution").getFirst());
			assertEquals(Resolution.UNRESOLVED, hElem.get("resolution").getSecond());
			assertTrue(hElem.getAuthor().getUsernames().contains("kimherzig"));
			assertEquals("3107411", hElem.getBugId());
			assertEquals(0, hElem.getChangedDateValues().size());
			assertEquals(1, hElem.getChangedEnumValues().size());
			assertEquals(0, hElem.getChangedPersonValues().size());
			assertEquals(0, hElem.getChangedStringValues().size());
			assertEquals(1, hElem.getFields().size());
			assertTrue(hElem.getForField("resolution") != null);
			assertEquals(DateTimeUtils.parseDate("2011-04-23 10:14:50 UTC"), hElem.getTimestamp());
			
			assertTrue(hElemIter.hasNext());
			hElem = hElemIter.next();
			assertEquals("None", hElem.get("category").getFirst());
			assertEquals("Interface (example)", hElem.get("category").getSecond());
			assertTrue(hElem.getAuthor().getUsernames().contains("kimherzig"));
			assertEquals("3107411", hElem.getBugId());
			assertEquals(0, hElem.getChangedDateValues().size());
			assertEquals(0, hElem.getChangedEnumValues().size());
			assertEquals(0, hElem.getChangedPersonValues().size());
			assertEquals(1, hElem.getChangedStringValues().size());
			assertEquals(1, hElem.getFields().size());
			assertTrue(hElem.getForField("category") != null);
			assertEquals(DateTimeUtils.parseDate("2011-04-23 10:15:12 UTC"), hElem.getTimestamp());
			
			assertTrue(hElemIter.hasNext());
			hElem = hElemIter.next();
			assertTrue(hElem.contains("assignedTo"));
			assertTrue(hElem.get("assignedTo").getFirst() instanceof Person);
			assertTrue(hElem.get("assignedTo").getSecond() instanceof Person);
			
			Person before = (Person) hElem.get("assignedTo").getFirst();
			Person after = (Person) hElem.get("assignedTo").getSecond();
			
			assertTrue(before.getUsernames().contains("nobody"));
			assertTrue(after.getUsernames().contains("kimherzig"));
			assertTrue(hElem.getAuthor().getUsernames().contains("kimherzig"));
			assertEquals("3107411", hElem.getBugId());
			assertEquals(0, hElem.getChangedDateValues().size());
			assertEquals(0, hElem.getChangedEnumValues().size());
			assertEquals(1, hElem.getChangedPersonValues().size());
			assertEquals(0, hElem.getChangedStringValues().size());
			assertEquals(1, hElem.getFields().size());
			assertTrue(hElem.getForField("assignedTo") != null);
			assertEquals(DateTimeUtils.parseDate("2011-04-23 10:16:15 UTC"), hElem.getTimestamp());
			
			assertTrue(hElemIter.hasNext());
			hElem = hElemIter.next();
			
			assertTrue(hElem.contains("summary"));
			
			assertEquals("test", hElem.get("summary").getFirst());
			assertEquals("test (neu)", hElem.get("summary").getSecond());
			assertTrue(hElem.getAuthor().getUsernames().contains("kimherzig"));
			assertEquals("3107411", hElem.getBugId());
			assertEquals(0, hElem.getChangedDateValues().size());
			assertEquals(0, hElem.getChangedEnumValues().size());
			assertEquals(0, hElem.getChangedPersonValues().size());
			assertEquals(1, hElem.getChangedStringValues().size());
			assertEquals(1, hElem.getFields().size());
			assertTrue(hElem.getForField("subject") != null);
			assertEquals(DateTimeUtils.parseDate("2011-04-23 11:29:26 UTC"), hElem.getTimestamp());
			
			assertTrue(hElemIter.hasNext());
			hElem = hElemIter.next();
			assertTrue(hElem.contains("assignedTo"));
			assertTrue(hElem.get("assignedTo").getFirst() instanceof Person);
			assertTrue(hElem.get("assignedTo").getSecond() instanceof Person);
			
			before = (Person) hElem.get("assignedTo").getFirst();
			after = (Person) hElem.get("assignedTo").getSecond();
			
			assertTrue(before.getUsernames().contains("kimherzig"));
			assertTrue(after.getFullnames().contains("Sascha Just"));
			assertTrue(hElem.getAuthor().getUsernames().contains("kimherzig"));
			assertEquals("3107411", hElem.getBugId());
			assertEquals(0, hElem.getChangedDateValues().size());
			assertEquals(0, hElem.getChangedEnumValues().size());
			assertEquals(1, hElem.getChangedPersonValues().size());
			assertEquals(0, hElem.getChangedStringValues().size());
			assertEquals(1, hElem.getFields().size());
			assertTrue(hElem.getForField("assignedTo") != null);
			assertEquals(DateTimeUtils.parseDate("2012-03-14 05:36:46 PDT"), hElem.getTimestamp());
			
			assertFalse(hElemIter.hasNext());
			
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testIssueParser() {
		final SourceforgeTracker tracker = new SourceforgeTracker();
		String url = SourceforgeTrackerTest.class.getResource(FileUtils.fileSeparator
		                                                              + "sourceforge_issue_1887104.html").toString();
		url = url.substring(0, url.lastIndexOf("sourceforge_issue_1887104.html"));
		final String pattern = "sourceforge_issue_" + Tracker.getBugidplaceholder() + ".html";
		
		try {
			tracker.setup(new URI(url), null, pattern, null, null, 1887104l, 1887104l, null);
			
			final Report report = tracker.parse(new ReportLink(new URI(url), "1887104"));
			
			assertEquals(null, report.getAssignedTo());
			assertEquals("None", report.getCategory());
			
			assertEquals(6, report.getComments().size());
			final Iterator<Comment> iterator = report.getComments().iterator();
			final Comment c1 = iterator.next();
			assertEquals(2658599, c1.getId());
			final Person daliboz = c1.getAuthor();
			assertTrue(daliboz != null);
			assertEquals(report, c1.getBugReport());
			assertTrue(c1.getMessage().startsWith("bumping up priority."));
			DateTime dt = DateTimeUtils.parseDate("2008-02-05 16:52:57 UTC");
			assertTrue(dt.isEqual(c1.getTimestamp()));
			
			final Comment c2 = iterator.next();
			assertEquals(2658950, c2.getId());
			final Person scolebourne = c2.getAuthor();
			assertTrue(scolebourne != null);
			assertEquals(report, c2.getBugReport());
			assertTrue(c2.getMessage().startsWith("Are you using v1.5.2?"));
			dt = DateTimeUtils.parseDate("2008-02-05 22:37:45 UTC");
			assertTrue(dt.isEqual(c2.getTimestamp()));
			
			final Comment c3 = iterator.next();
			assertEquals(2659081, c3.getId());
			assertEquals(daliboz, c3.getAuthor());
			assertEquals(report, c3.getBugReport());
			assertTrue(c3.getMessage().startsWith("I've tried this on 1.5,"));
			dt = DateTimeUtils.parseDate("2008-02-06 00:04:30 UTC");
			assertTrue(dt.isEqual(c3.getTimestamp()));
			
			final Comment c4 = iterator.next();
			assertEquals(2661926, c4.getId());
			assertEquals(scolebourne, c4.getAuthor());
			assertEquals(report, c4.getBugReport());
			assertTrue(c4.getMessage().startsWith("Fixed in svn rv 1323."));
			dt = DateTimeUtils.parseDate("2008-02-08 00:13:32 UTC");
			assertTrue(dt.isEqual(c4.getTimestamp()));
			
			final Comment c5 = iterator.next();
			assertEquals(2670573, c5.getId());
			assertEquals(daliboz, c5.getAuthor());
			assertEquals(report, c5.getBugReport());
			assertTrue(c5.getMessage().startsWith("Can confirm that this is passing our unit tests"));
			dt = DateTimeUtils.parseDate("2008-02-11 19:26:23 UTC");
			assertTrue(dt.isEqual(c5.getTimestamp()));
			
			final Comment c6 = iterator.next();
			assertEquals(2670632, c6.getId());
			assertEquals(daliboz, c6.getAuthor());
			assertEquals(report, c6.getBugReport());
			assertTrue(c6.getMessage().startsWith("Just noticed a difference for the Spring adjustment - though"));
			final DateTime c6Dt = DateTimeUtils.parseDate("2008-02-11 20:13:00 UTC");
			assertTrue(c6Dt.isEqual(c6.getTimestamp()));
			
			assertEquals("None", report.getComponent());
			dt = DateTimeUtils.parseDate("2008-02-05 16:24:58 UTC");
			assertTrue(dt.isEqual(report.getCreationTimestamp()));
			
			assertTrue(report.getDescription()
			                 .startsWith("On versions 1.5+, using roundFloorCopy on one of the ambiguous times "));
			assertEquals(1887104, report.getId());
			assertTrue(c6Dt.isEqual(report.getLastUpdateTimestamp()));
			assertEquals(Priority.VERY_HIGH, report.getPriority());
			assertEquals(null, report.getProduct());
			assertEquals(Resolution.UNRESOLVED, report.getResolution());
			assertEquals(null, report.getResolutionTimestamp());
			assertEquals(null, report.getResolver());
			assertEquals(new Report("0").getSeverity(), report.getSeverity());
			assertEquals(0, report.getSiblings().size());
			assertEquals(de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status.CLOSED, report.getStatus());
			assertEquals("joda-time 1.5+ issues with roundFloor and DST", report.getSubject());
			assertEquals(daliboz, report.getSubmitter());
			assertEquals(null, report.getSummary());
			assertEquals(Type.BUG, report.getType());
			assertEquals(null, report.getVersion());
			
			final List<AttachmentEntry> attachmentEntries = report.getAttachmentEntries();
			assertEquals(1, attachmentEntries.size());
			final AttachmentEntry attachment = attachmentEntries.get(0);
			assertEquals("265142", attachment.getId());
			assertTrue(attachment.getAuthor().getUsernames().contains("daliboz"));
			assertEquals(null, attachment.getDeltaTS());
			assertEquals("Test program that reproduces issue with 2 different methods", attachment.getDescription());
			assertEquals("RoundFloorDST.java", attachment.getFilename());
			assertEquals("http://sourceforge.net/tracker/download.php?group_id=97367&atid=617889&file_id=265142&aid=1887104",
			             attachment.getLink().toString());
			assertEquals(null, attachment.getMime());
			assertEquals(0, attachment.getSize());
			assertEquals(DateTimeUtils.parseDate("2008-02-05 16:24:59 UTC"), attachment.getTimestamp());
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
}
