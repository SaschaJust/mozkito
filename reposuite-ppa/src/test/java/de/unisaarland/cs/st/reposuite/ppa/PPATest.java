package de.unisaarland.cs.st.reposuite.ppa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElements;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;

public class PPATest {
	
	private static File testFile;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		URL fileURL = PPATest.class.getResource(FileUtils.fileSeparator + "PPATypeVisitor.java");
		if (fileURL == null) {
			fail();
		}
		testFile = new File((new URL(fileURL.toString()).toURI()));
	}
	
	@Test
	public void comparisonTest() {
		//FIXME add to plugin project
		
		/*
		 * Set<File> files = new HashSet<File>(); files.add(testFile);
		 * 
		 * Map<String, JavaElements> elementsByFile =
		 * PPAUtils.getJavaElementsByFile(files.iterator(),
		 * testFile.getParent(), new String[0], true);
		 * assertTrue(elementsByFile.containsKey("PPATypeVisitor.java"));
		 * 
		 * Collection<JavaElementDefinition> defs =
		 * elementsByFile.get("PPATypeVisitor.java").getJavaDefs();
		 * assertEquals(12, defs.size());
		 * 
		 * JavaClassDefinition classDef = null;
		 * 
		 * Map<String, JavaMethodDefinition> methodDefs = new HashMap<String,
		 * JavaMethodDefinition>(); for (JavaElementDefinition def : defs) { if
		 * (def instanceof JavaClassDefinition) { classDef =
		 * (JavaClassDefinition) def; assertEquals(11,
		 * classDef.getChildren().size()); assertEquals(327,
		 * classDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * classDef.getFilePath());
		 * assertEquals("de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor"
		 * , classDef.getFullQualifiedName()); assertEquals(null,
		 * classDef.getParent()); assertEquals("PPATypeVisitor",
		 * classDef.getShortName()); assertEquals(32, classDef.getStartLine());
		 * assertEquals(null, classDef.getSuperclass()); } if (def instanceof
		 * JavaMethodDefinition) { JavaMethodDefinition methDef =
		 * (JavaMethodDefinition) def;
		 * methodDefs.put(methDef.getFullQualifiedName(), methDef); } }
		 * 
		 * assertTrue(classDef != null); assertEquals(11, methodDefs.size());
		 * assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getMethodDefinitions()"
		 * )); JavaMethodDefinition methDef = methodDefs .get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getMethodDefinitions()"
		 * ); assertEquals(0,methDef.getChildren().size());
		 * assertEquals(171,methDef.getEndLine());
		 * assertEquals("PPATypeVisitor.java",methDef.getFilePath());
		 * assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getMethodDefinitions()"
		 * , methDef.getFullQualifiedName());
		 * assertEquals(classDef,methDef.getParent());
		 * assertEquals("getMethodDefinitions",methDef.getShortName());
		 * assertEquals(0,methDef.getSignature().size());
		 * assertEquals(164,methDef.getStartLine());
		 * 
		 * 
		 * assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.preVisit(ASTNode)"
		 * )); methDef = methodDefs.get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.preVisit(ASTNode)"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(315,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.preVisit(ASTNode)"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("preVisit",
		 * methDef.getShortName()); assertEquals(1,
		 * methDef.getSignature().size()); assertEquals(205,
		 * methDef.getStartLine());
		 * 
		 * assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getRelativeFilePath()"
		 * )); methDef = methodDefs.get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getRelativeFilePath()"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(175,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getRelativeFilePath()"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("getRelativeFilePath",
		 * methDef.getShortName()); assertEquals(0,
		 * methDef.getSignature().size()); assertEquals(173,
		 * methDef.getStartLine());
		 * 
		 * /// assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getCompilationUnit()"
		 * )); methDef = methodDefs.get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getCompilationUnit()"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(158,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getCompilationUnit()"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("getCompilationUnit",
		 * methDef.getShortName()); assertEquals(0,
		 * methDef.getSignature().size()); assertEquals(151,
		 * methDef.getStartLine());
		 * 
		 * assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.checkFilters(String)"
		 * )); methDef = methodDefs.get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.checkFilters(String)"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(128,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.checkFilters(String)"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("checkFilters",
		 * methDef.getShortName()); assertEquals(1,
		 * methDef.getSignature().size()); assertEquals(97,
		 * methDef.getStartLine());
		 * 
		 * assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.registerVisitor(PPAVisitor)"
		 * )); methDef = methodDefs .get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.registerVisitor(PPAVisitor)"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(326,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.registerVisitor(PPAVisitor)"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("registerVisitor",
		 * methDef.getShortName()); assertEquals(1,
		 * methDef.getSignature().size()); assertEquals(317,
		 * methDef.getStartLine());
		 * 
		 * assertTrue(methodDefs.containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getFile()"
		 * )); methDef = methodDefs.get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getFile()"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(162,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getFile()"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("getFile",
		 * methDef.getShortName()); assertEquals(0,
		 * methDef.getSignature().size()); assertEquals(160,
		 * methDef.getStartLine());
		 * 
		 * assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getClassDefinitions()"
		 * )); methDef = methodDefs.get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getClassDefinitions()"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(149,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.getClassDefinitions()"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("getClassDefinitions",
		 * methDef.getShortName()); assertEquals(0,
		 * methDef.getSignature().size()); assertEquals(142,
		 * methDef.getStartLine());
		 * 
		 * assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.PPATypeVisitor(CompilationUnit,File,String,String[],Set<PPAVisitor>)"
		 * )); methDef = methodDefs .get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.PPATypeVisitor(CompilationUnit,File,String,String[],Set<PPAVisitor>)"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(89,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.PPATypeVisitor(CompilationUnit,File,String,String[],Set<PPAVisitor>)"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("PPATypeVisitor",
		 * methDef.getShortName()); assertEquals(5,
		 * methDef.getSignature().size()); assertEquals(55,
		 * methDef.getStartLine());
		 * 
		 * assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.endVisit(CompilationUnit)"
		 * )); methDef = methodDefs .get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.endVisit(CompilationUnit)"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(140,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.endVisit(CompilationUnit)"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("endVisit",
		 * methDef.getShortName()); assertEquals(1,
		 * methDef.getSignature().size()); assertEquals(130,
		 * methDef.getStartLine());
		 * 
		 * assertTrue(methodDefs .containsKey(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.postVisit(ASTNode)"
		 * )); methDef = methodDefs.get(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.postVisit(ASTNode)"
		 * ); assertEquals(0, methDef.getChildren().size()); assertEquals(196,
		 * methDef.getEndLine()); assertEquals("PPATypeVisitor.java",
		 * methDef.getFilePath()); assertEquals(
		 * "de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor.postVisit(ASTNode)"
		 * , methDef.getFullQualifiedName()); assertEquals(classDef,
		 * methDef.getParent()); assertEquals("postVisit",
		 * methDef.getShortName()); assertEquals(1,
		 * methDef.getSignature().size()); assertEquals(177,
		 * methDef.getStartLine());
		 * 
		 * 
		 * Collection<JavaMethodCall> methodCalls =
		 * elementsByFile.get("PPATypeVisitor.java").getMethodCalls();
		 * assertEquals(114, methodCalls.size());
		 */
	}
	
}
