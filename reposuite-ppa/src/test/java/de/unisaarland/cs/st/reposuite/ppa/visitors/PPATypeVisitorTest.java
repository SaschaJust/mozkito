/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.visitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.ppa.CompilationUnitException;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.ppa.utils.RepoSuitePPAUtil;
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
		try {
			CompilationUnit cu = RepoSuitePPAUtil.getCU(testFile);
			PPATypeVisitor visitor = new PPATypeVisitor(cu, testFile, testFile.getParent(), new String[0],
					new HashSet<PPAVisitor>());
			cu.accept(visitor);
			
			Collection<JavaClassDefinition> classDefinitions = visitor.getClassDefinitions();
			Collection<JavaMethodDefinition> methodDefinitions = visitor.getMethodDefinitions();
			
			assertEquals(3, classDefinitions.size());
			
			JavaClassDefinition dataStructureDef = null;
			JavaClassDefinition innerEventIteratorDef = null;
			JavaClassDefinition innerEventIteratorDef1 = null;
			
			for (JavaClassDefinition classDef : classDefinitions) {
				if ((!classDef.getFullQualifiedName().equals("org.junit.sample.test.DataStructure"))
						&& (!classDef.getFullQualifiedName().equals("org.junit.sample.test.InnerEvenIterator"))
						&& (!classDef.getFullQualifiedName().equals("org.junit.sample.test.InnerEvenIterator$1"))) {
					fail();
				}
				if(classDef.getFullQualifiedName().equals("org.junit.sample.test.DataStructure")){
					dataStructureDef = classDef;
				} else if(classDef.getFullQualifiedName().equals("org.junit.sample.test.InnerEvenIterator")){
					innerEventIteratorDef = classDef;
				} else if(classDef.getFullQualifiedName().equals("org.junit.sample.test.InnerEvenIterator$1")){
					innerEventIteratorDef1 = classDef;
				}
			}
			
			for (JavaClassDefinition classDef : classDefinitions) {
				if(classDef.getFullQualifiedName().equals("org.junit.sample.test.DataStructure")){
					assertEquals("DataStructure.java",classDef.getFilePath());
					assertEquals(4, classDef.getChildren().size());
					assertEquals(63, classDef.getEndLine());
					assertEquals(null,classDef.getParent());
					assertEquals("DataStructure",classDef.getShortName());
					assertEquals(3,classDef.getStartLine());
				} else if (classDef.getFullQualifiedName().equals("org.junit.sample.test.InnerEvenIterator")) {
					assertEquals("DataStructure.java",classDef.getFilePath());
					assertEquals(4, classDef.getChildren().size());
					assertEquals(56, classDef.getEndLine());
					assertEquals(dataStructureDef, classDef.getParent());
					assertEquals("InnerEvenIterator",classDef.getShortName());
					assertEquals(24,classDef.getStartLine());
				} else if (classDef.getFullQualifiedName().equals("org.junit.sample.test.InnerEvenIterator$1")) {
					assertEquals("DataStructure.java", classDef.getFilePath());
					assertEquals(2, classDef.getChildren().size());
					assertEquals(53, classDef.getEndLine());
					assertEquals(innerEventIteratorDef, classDef.getParent());
					assertEquals("InnerEvenIterator$1", classDef.getShortName());
					assertEquals(43, classDef.getStartLine());
				}
			}
			
		} catch (CompilationUnitException ex) {
			ex.printStackTrace();
			fail();
		} catch (IOException ex) {
			ex.printStackTrace();
			fail();
		}
		
	}
}