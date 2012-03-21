package de.unisaarland.cs.st.moskito.bugs.tracker.mantis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;

import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;

public class MantisOverviewParserTest {
	
	private MantisOverviewParser parser;
	private URI                  trackerUri;
	private URI                  overviewUri;
	
	@Before
	public void setup() {
		
		try {
			this.trackerUri = getClass().getResource(FileUtils.fileSeparator).toURI();
			this.overviewUri = getClass().getResource(FileUtils.fileSeparator + "mantis_overview.html").toURI();
			this.parser = new MantisOverviewParser(this.trackerUri.toASCIIString());
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testNumPageDetection() {
		final int numPages = this.parser.determineNumPages(this.overviewUri);
		assertEquals(47, numPages);
	}
	
	@Test
	public void testParsePage() {
		final List<ReportLink> links = this.parser.handlePage(this.overviewUri);
		assertEquals(50, links.size());
		
		assertEquals("20069", links.get(0).getBugId());
		assertEquals(this.trackerUri + "/view.php?id=20069", links.get(0).getUri().toASCIIString());
		
	}
}
