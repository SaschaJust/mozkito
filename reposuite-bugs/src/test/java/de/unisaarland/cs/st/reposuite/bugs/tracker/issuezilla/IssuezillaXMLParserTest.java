package de.unisaarland.cs.st.reposuite.bugs.tracker.issuezilla;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;

public class IssuezillaXMLParserTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAttachemntParse() {
		
		URL url = IssuezillaTrackerTest.class.getResource(FileUtils.fileSeparator + "issuezilla-argouml-5818.xml");
		Report report = new Report(5818);
		
		Document document = null;
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(url.toURI())));
		} catch (FileNotFoundException e1) {
			fail();
		} catch (URISyntaxException e1) {
			fail();
		}
		try {
			SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			document = saxBuilder.build(reader);
			reader.close();
		} catch (TransformerFactoryConfigurationError e) {
			fail();
		} catch (IOException e) {
			fail();
		} catch (JDOMException e) {
			fail();
		}
		
		Element itemElement = document.getRootElement().getChild("issue");
		
		try {
			IssuezillaXMLParser.handleRoot(report, itemElement, new IssuezillaTracker(),
			                               new URI("http://argouml.tigris.org/issues/xml.cgi?id=5818"));
		} catch (URISyntaxException e) {
			fail();
		}
		assertTrue(report.getAttachmentEntries() != null);
		assertEquals(1, report.getAttachmentEntries().size());
		AttachmentEntry attachmentEntry = report.getAttachmentEntries().get(0);
		assertTrue(attachmentEntry.getAuthor() != null);
		assertTrue(attachmentEntry.getAuthor().getUsernames().contains("rdi"));
		assertEquals("A project File where this Problem apears when the edge of the \"blubber\" association is moved.",
		             attachmentEntry.getDescription());
		assertEquals("problem.zargo", attachmentEntry.getFilename());
		assertEquals("1957", attachmentEntry.getId());
		assertEquals("http://argouml.tigris.org/nonav/issues/showattachment.cgi/1957/",
		             attachmentEntry.getLink());
		assertEquals("application/x-compressed", attachmentEntry.getMime());
		assertEquals(DateTimeUtils.parseDate("2009-06-25 02:07:48"), attachmentEntry.getTimestamp());
	}
	
}
