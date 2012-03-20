package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Pattern;

import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.junit.Test;

public class SourceforgeParserTest {
	
	@Test
	public void testAttachmentIdRegex() {
		final String link = "<a href=\"/tracker/download.php?group_id=97367&amp;atid=617889&amp;file_id=336228&amp;aid=2825955\">Download</a>";
		final List<RegexGroup> find = new Regex(SourceforgeParser.fileIdPattern.getPattern()).find(link);
		assertTrue(find != null);
		assertEquals(2, find.size());
		assertEquals("336228", find.get(1).getMatch());
	}
	
	@Test
	public void testHTMLCommentRegex() {
		final String s = "<!-- google_ad_section_start -->\n\"JodaTest.java\"<!-- google_ad_section_end -->";
		final Regex htmlCommentRegex = new Regex(SourceforgeParser.htmlCommentRegex.getPattern(), Pattern.MULTILINE
		        | Pattern.DOTALL);
		String result = htmlCommentRegex.removeAll(s);
		result = result.replaceAll("\"", "");
		assertEquals("JodaTest.java", result.trim());
	}
	
}
