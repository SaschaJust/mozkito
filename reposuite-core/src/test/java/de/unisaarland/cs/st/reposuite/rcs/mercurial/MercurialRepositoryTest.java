package de.unisaarland.cs.st.reposuite.rcs.mercurial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.utils.Regex;

public class MercurialRepositoryTest {
	
	private File                cloneDir;
	private URI                 uri;
	private MercurialRepository repo;
	
	@Before
	public void setUp() throws Exception {
		// TODO replace this repo and create an own repo on the fly
		this.uri = new URI("file:///Users/kim/Projects/reposuite");
	}
	
	@After
	public void tearDown() throws Exception {
		if (this.cloneDir != null) {
			FileUtils.forceDelete(this.cloneDir);
		}
	}
	
	@Test
	public void testClone() {
		this.repo = new MercurialRepository();
		this.repo.setup(this.uri);
		this.cloneDir = this.repo.getCloneDir();
		File DOT_HG = new File(this.cloneDir, ".hg");
		assertTrue(DOT_HG.exists());
		assertTrue(DOT_HG.isDirectory());
	}
	
	@Test
	public void testCloneUsername() {
		// TODO implement
		// repo = new MercurialRepository();
		// try {
		// repo.setup(new URI("https://hg.st.cs.uni-saarland.de/hg/reposuite"),
		// "kim", "");
		// } catch (URISyntaxException e) {
		// e.printStackTrace();
		// fail();
		// }
		// cloneDir = repo.getCloneDir();
		// File DOT_HG = new File(cloneDir, ".hg");
		// assertTrue(DOT_HG.exists());
		// assertTrue(DOT_HG.isDirectory());
	}
	
	@Test
	public void testSaschasMegaRegExp() {
		List<String> lines = new ArrayList<String>();
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
		
		Regex regex = MercurialRepository.regex;
		
		int lineCounter = 0;
		for (String s : lines) {
			++lineCounter;
			
			assertTrue(regex.matchesFull(s));
			assertEquals(new Integer(5), regex.getGroupCount());
			assertEquals(regex.getGroup(1), regex.getGroup("author"));
			assertEquals(regex.getGroup(2), regex.getGroup("hash"));
			assertEquals(regex.getGroup(3), regex.getGroup("date"));
			assertEquals(regex.getGroup(4), regex.getGroup("file"));
			assertEquals(regex.getGroup(5), regex.getGroup("codeline"));
			
			for (int i = 0; i <= regex.getGroupCount(); ++i) {
				System.out.println(i + ": " + regex.getGroup(i));
			}
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
