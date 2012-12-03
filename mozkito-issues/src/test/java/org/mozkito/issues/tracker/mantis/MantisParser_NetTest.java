package org.mozkito.issues.tracker.mantis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kisa.Logger;

import org.junit.Before;
import org.junit.Test;

import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.model.AttachmentEntry;

/**
 * The Class MantisParser_NetTest.
 */
public class MantisParser_NetTest {
	
	private RawContent report19810;
	
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
		
	}
	
	/**
	 * Test attachments19810.
	 */
	@Test
	public void testAttachments19810() {
		
		final MantisTracker tracker = new MantisTracker();
		try {
			tracker.setUri(new URI("https://issues.openbravo.com/"));
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			fail();
		}
		
		final MantisParser parser = new MantisParser();
		parser.setTracker(tracker);
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
	
}
