package de.unisaarland.cs.st.reposuite.rcs.mercurial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MercurialRepositoryTest {
	
	private File                cloneDir;
	private URI                 uri;
	private MercurialRepository repo;
	
	@Before
	public void setUp() throws Exception {
		//TODO replace this repo  and create an own repo on the fly 
		uri = new URI("file:///Users/kim/Projects/reposuite");
	}
	
	@After
	public void tearDown() throws Exception {
		if (cloneDir != null) {
			FileUtils.forceDelete(cloneDir);
		}
	}
	
	@Test
	public void testClone() {
		repo = new MercurialRepository();
		repo.setup(uri);
		cloneDir = repo.getCloneDir();
		File DOT_HG = new File(cloneDir, ".hg");
		assertTrue(DOT_HG.exists());
		assertTrue(DOT_HG.isDirectory());
	}
	
	@Test
	public void testCloneUsername() {
		//TODO implement
		//		repo = new MercurialRepository();
		//		try {
		//			repo.setup(new URI("https://hg.st.cs.uni-saarland.de/hg/reposuite"), "kim", "");
		//		} catch (URISyntaxException e) {
		//			e.printStackTrace();
		//			fail();
		//		}
		//		cloneDir = repo.getCloneDir();
		//		File DOT_HG = new File(cloneDir, ".hg");
		//		assertTrue(DOT_HG.exists());
		//		assertTrue(DOT_HG.isDirectory());
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
		
		Pattern pattern = Pattern.compile(MercurialRepository.regex);
		
		int lineCounter = 0;
		for (String s : lines) {
			++lineCounter;
			Matcher matcher2 = pattern.matcher(s);
			assertTrue(matcher2.matches());
			for (int i = 0; i <= matcher2.groupCount(); ++i) {
				System.out.println(i + ": " + matcher2.group(i));
			}
			if (lineCounter == 1) {
				assertEquals("sascha", matcher2.group(1));
				assertEquals("e63a20871c7f", matcher2.group(2));
				assertEquals("Tue Oct 19 15:24:30 2010 +0200", matcher2.group(3));
				assertEquals("reposuite-fixindchanges/pom.xml", matcher2.group(4));
				assertEquals("<?xml version=\"1.0\"?>", matcher2.group(5));
			}
			if (lineCounter == 9) {
				assertEquals("   kim", matcher2.group(1));
				assertEquals("e63a20871c7f", matcher2.group(2));
				assertEquals("Tue Oct 19 15:24:30 2010 +0200", matcher2.group(3));
				assertEquals("reposuite-fixindchanges/pom.xml", matcher2.group(4));
				assertEquals("<groupId>de.unisaarland.cs.st</groupId>", matcher2.group(5));
			}
		}
	}
}
