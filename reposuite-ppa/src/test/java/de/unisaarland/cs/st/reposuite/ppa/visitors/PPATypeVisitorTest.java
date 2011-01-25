/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.visitors;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.utils.FileUtils;

/**
 *
 * @author kim
 */
public class PPATypeVisitorTest {
	
	private static File testFile;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		URL fileURL = PPATypeVisitorTest.class.getResource(FileUtils.fileSeparator + "DataStructure.java");
		if (fileURL == null) {
			fail();
		}
		testFile = new File((new URL(fileURL.toString()).toURI()));
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
	}
	
	public PPATypeVisitorTest() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void test(){
		//FIXME add ppa plugin project
		
		/*
		 * Map<String, JavaElements> elementsByFile =
		 * PPAUtils.getJavaElementsByFile(testFile.getParentFile(), new
		 * String[0], true);
		 * assertTrue(elementsByFile.containsKey("DataStructure.java"));
		 * 
		 * Collection<JavaClassDefinition> classDefinitions =
		 * elementsByFile.get("DataStructure.java").getClassDefs();
		 * Collection<JavaMethodDefinition> methodDefinitions =
		 * elementsByFile.get("DataStructure.java") .getMethodDefs();
		 * 
		 * assertEquals(3, classDefinitions.size());
		 * 
		 * JavaClassDefinition dataStructureDef = null; JavaClassDefinition
		 * innerEventIteratorDef = null; JavaClassDefinition
		 * innerEventIteratorDef1 = null;
		 * 
		 * for (JavaClassDefinition classDef : classDefinitions) { if
		 * ((!classDef
		 * .getFullQualifiedName().equals("org.junit.sample.test.DataStructure"
		 * )) && (!classDef.getFullQualifiedName().equals(
		 * "org.junit.sample.test.InnerEvenIterator")) &&
		 * (!classDef.getFullQualifiedName
		 * ().equals("org.junit.sample.test.InnerEvenIterator$1"))) { fail(); }
		 * if(classDef.getFullQualifiedName().equals(
		 * "org.junit.sample.test.DataStructure")){ dataStructureDef = classDef;
		 * } else if(classDef.getFullQualifiedName().equals(
		 * "org.junit.sample.test.InnerEvenIterator")){ innerEventIteratorDef =
		 * classDef; } else if(classDef.getFullQualifiedName().equals(
		 * "org.junit.sample.test.InnerEvenIterator$1")){ innerEventIteratorDef1
		 * = classDef; } }
		 * 
		 * for (JavaClassDefinition classDef : classDefinitions) {
		 * if(classDef.getFullQualifiedName
		 * ().equals("org.junit.sample.test.DataStructure")){
		 * assertEquals("DataStructure.java",classDef.getFilePath());
		 * assertEquals(4, classDef.getChildren().size()); assertEquals(63,
		 * classDef.getEndLine()); assertEquals(null,classDef.getParent());
		 * assertEquals("DataStructure",classDef.getShortName());
		 * assertEquals(3,classDef.getStartLine()); } else if
		 * (classDef.getFullQualifiedName
		 * ().equals("org.junit.sample.test.InnerEvenIterator")) {
		 * assertEquals("DataStructure.java",classDef.getFilePath());
		 * assertEquals(4, classDef.getChildren().size()); assertEquals(56,
		 * classDef.getEndLine()); assertEquals(dataStructureDef,
		 * classDef.getParent());
		 * assertEquals("InnerEvenIterator",classDef.getShortName());
		 * assertEquals(24,classDef.getStartLine()); } else if
		 * (classDef.getFullQualifiedName
		 * ().equals("org.junit.sample.test.InnerEvenIterator$1")) {
		 * assertEquals("DataStructure.java", classDef.getFilePath());
		 * assertEquals(2, classDef.getChildren().size()); assertEquals(53,
		 * classDef.getEndLine()); assertEquals(innerEventIteratorDef,
		 * classDef.getParent()); assertEquals("InnerEvenIterator$1",
		 * classDef.getShortName()); assertEquals(43, classDef.getStartLine());
		 * } }
		 */
		
	}
}