package org.mozkito.versions.mercurial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTimeZone;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class MercurialLogParserTest.
 */
public class MercurialLogParserTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * Test fixed date time zone.
	 */
	@Test
	public void testFixedDateTimeZone() {
		final int offset = 25200;
		final DateTimeZone timeZone = DateTimeZone.forOffsetMillis(offset * 1000);
		assertEquals("+07:00", timeZone.toString());
	}
	
	/**
	 * Test former path regex.
	 */
	@Test
	public void testFormerPathRegex() {
		final String line = "reposuite-rcs/src/main/java/net.ownhero.dev.ioda/CommandExecutor.java (reposuite-rcs/src/main/java/net.ownhero.dev.ioda/CMDExecutor.java)";
		final Match found = MercurialRepository.FORMER_PATH_REGEX.find(line);
		assertTrue(MercurialRepository.FORMER_PATH_REGEX.matches(line));
		assertEquals(1, found.getGroupCount());
		assertEquals("reposuite-rcs/src/main/java/net.ownhero.dev.ioda/CMDExecutor.java",
		             MercurialRepository.FORMER_PATH_REGEX.getGroup("result"));
	}
	
	/**
	 * Test plaine name.
	 */
	@Test
	public void testPlaineName() {
		Regex.analyzePattern(MercurialRepository.AUTHOR_REGEX.getPattern());
		final Match found = MercurialRepository.AUTHOR_REGEX.find("just");
		assertTrue(found.hasGroups());
		assertTrue(MercurialRepository.AUTHOR_REGEX.getGroup("plain") != null);
		assertEquals("just", MercurialRepository.AUTHOR_REGEX.getGroup("plain"));
	}
	
	/**
	 * Test pre filter lines.
	 */
	@Test
	public void testPreFilterLines() {
		List<String> lines = new ArrayList<String>();
		lines.add("1510979776500f102ff503949ea34cdbf8c653d8+~+just+~+2010-10-22 14:33 +0000+~+file_1;+~++~++~+creating file_1");
		lines.add("b9aff3c08f90cbd42361da158fbbe979405fba70+~+just+~+2010-10-22 14:35 +0000+~+file_2;file_3;+~++~+file_1;+~+adding file_2<br/>");
		lines.add("adding file_3<br/>");
		lines.add("setting content of file_* to: file_* content");
		lines.add("c09ba4fd1259c2421331b20dea435d414d2ab6b2+~+just+~+2010-10-22 14:36 +0000+~+dir_a/file_2_dir_a;+~+file_2;+~++~+moving file_2 to dir_a/file_2_dir_a");
		lines.add("5a61f0f67642e577f814650bc4507543153b1b22+~+just+~+2010-10-22 14:36 +0000+~+dir_b/file_2_dir_a;+~+dir_a/file_2_dir_a;+~++~+moving dir_a to dir_b");
		lines.add("42aa307236637be938f4126328234b5264af8bf8+~+just+~+2010-10-22 14:37 +0000+~++~++~++~+adding new dir_a");
		lines.add("7abcf1545dba655579d6d8a775ebd6e245441962+~+just+~+2010-10-22 14:40 +0000+~+dir_a/file_3_dir_a;+~+file_3;+~++~+moving file_3 to dir_a/file_3_dir_a<br/>");
		lines.add("changing content of dir_a/file_3_dir_a to file_3 content changed");
		lines.add("bf15b3e306ec4c95431d4b406a8ecb552b9f6397+~+just+~+2010-10-22 14:42 +0000+~++~++~+file_1;+~+applying change test 1 to file_1");
		lines.add("09b5a61648f7b6879108f26a47a2908e57a64139+~+just+~+2010-10-22 14:42 +0000+~++~++~+file_1;+~+applying change test 2 to file_1");
		lines.add("dafb394cd37cf40a3455bab046ceabb491bca028+~+just+~+2010-10-22 14:42 +0000+~++~++~+file_1;+~+applying change test 3 to file_1");
		lines.add("c8858a02dd857852643e280652f9d6726b9669bf+~+just+~+2010-10-22 14:42 +0000+~++~++~+file_1;+~+applying change test 4 to file_1");
		lines.add("733e0f08962b8e6f7b4a03294b27730184556eed+~+just+~+2010-10-22 14:42 +0000+~++~++~+file_1;+~+applying change test 5 to file_1");
		lines.add("7576d353c05c0c7197349f4e10439386c2ee9b8e+~+just+~+2010-10-22 14:49 +0000+~++~++~+file_1;+~+making change 1 to line 3");
		lines.add("8d22211a4ebb51d990fdd7cc52abcc5fc2215d76+~+just+~+2010-10-22 14:49 +0000+~++~++~+file_1;+~+making change 2 to line 10");
		lines.add("247d3e4faba0378c1ff97e4a010a411aa9ae059e+~+just+~+2010-10-22 14:49 +0000+~++~++~+file_1;+~+making change 3 to line 28");
		lines.add("1f3e10aaf808ef9c1a4350fa69a3e4a336c1a8d8+~+just+~+2010-10-22 14:49 +0000+~++~++~+file_1;+~+making change 4 to line 77");
		lines.add("ffa26340696b4ab1af8e54f7c76ad9370d6eb692+~+just+~+2010-10-22 14:51 +0000+~++~+file_1;+~++~+deleting file_1");
		lines.add("01bcd1a86fb7d47c977f41af6a3a8f2407ce9183+~+just+~+2010-10-22 14:53 +0000+~+file_1;+~++~+dir_b/file_2_dir_a;+~+adding fake file_1 and modifying file_2_dir_a");
		lines = MercurialLogParser.preFilterLines(lines);
		assertEquals(17, lines.size());
		assertEquals("b9aff3c08f90cbd42361da158fbbe979405fba70+~+just+~+2010-10-22 14:35 +0000+~+file_2;file_3;+~++~+file_1;+~+adding file_2<br/>adding file_3<br/>setting content of file_* to: file_* content",
		             lines.get(1));
	}
	
	/**
	 * Test replace line breaks.
	 */
	@Test
	public void testReplaceLineBreaks() {
		final String s = "hubba<br/>hubba<br/>hopp";
		final String newS = s.replaceAll("<br/>", FileUtils.lineSeparator);
		assertEquals("hubba" + FileUtils.lineSeparator + "hubba" + FileUtils.lineSeparator + "hopp", newS);
	}
	
	/**
	 * Test saschas mega reg exp.
	 */
	@Test
	public void testSaschasMegaRegExp() {
		final List<String> lines = new ArrayList<String>();
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: <?xml version=\"1.0\"?>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: <project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<parent>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 		<groupId>de.unisaarland.cs.st</groupId>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 		<artifactId>reposuite</artifactId>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 		<version>0.1-SNAPSHOT</version>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	</parent>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<modelVersion>4.0.0</modelVersion>");
		lines.add("   kim d5156a110af8 Wed Oct 20 17:25:58 2010 +0200 reposuite-fixindchanges/pom.xml: 	<groupId>de.unisaarland.cs.st</groupId>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<artifactId>reposuite-fixindchanges</artifactId>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<packaging>jar</packaging>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<name>reposuite-fixindchanges</name>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: </project>");
		
		final Regex regex = MercurialRepository.REGEX;
		
		int lineCounter = 0;
		for (final String s : lines) {
			++lineCounter;
			
			assertTrue(regex.matchesFull(s));
			assertEquals(new Integer(5), regex.getGroupCount());
			assertEquals(regex.getGroup(1), regex.getGroup("author"));
			assertEquals(regex.getGroup(2), regex.getGroup("hash"));
			assertEquals(regex.getGroup(3), regex.getGroup("date"));
			assertEquals(regex.getGroup(4), regex.getGroup("file"));
			assertEquals(regex.getGroup(5), regex.getGroup("codeline"));
			
			if (lineCounter == 1) {
				assertEquals("sascha", regex.getGroup(1));
				assertEquals("e63a20871c7f", regex.getGroup(2));
				assertEquals("Tue Oct 19 15:24:30 2010 +0200", regex.getGroup(3));
				assertEquals("reposuite-fixindchanges/pom.xml", regex.getGroup(4));
				assertEquals("<?xml version=\"1.0\"?>", regex.getGroup(5));
			}
			if (lineCounter == 9) {
				assertEquals("kim", regex.getGroup(1));
				assertEquals("d5156a110af8", regex.getGroup(2));
				assertEquals("Wed Oct 20 17:25:58 2010 +0200", regex.getGroup(3));
				assertEquals("reposuite-fixindchanges/pom.xml", regex.getGroup(4));
				assertEquals("	<groupId>de.unisaarland.cs.st</groupId>", regex.getGroup(5));
			}
		}
	}
}
