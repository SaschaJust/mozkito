package org.mozkito.codeanalysis.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Test;
import org.mozkito.codeanalysis.utils.JavaElementLocations;

public class JavaElementLocationSetTest {
	
	@Test
	public void test() {
		final JavaElementLocationSet set = new JavaElementLocationSet(new JavaElementFactory());
		
		final JavaTypeDefinition javaType = new JavaTypeDefinition("org.mozkito.codeanalysis.model.TestClass");
		
		final JavaElementLocation anonymousClassLocation = set.addAnonymousClassDefinition(javaType,
		                                                                                   "org.mozkito.codeanalysis.model.TestClass$1",
		                                                                                   "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                                   20, 23, 43674, 20);
		
		final JavaElementLocation classLocation = set.addClassDefinition("org.mozkito.codeanalysis.model.TestClass",
		                                                                 "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                 10, 200, 463, 11);
		
		final JavaElementLocation interfaceLocation = set.addInterfaceDefinition("org.mozkito.codeanalysis.model.TestInterface",
		                                                                         "org/mozkito/codeanalysis/model/TestInterface.java",
		                                                                         11, 456, 109, 12);
		
		final JavaElementLocation methodDefinitionLocation = set.addMethodDefinition("org.mozkito.codeanalysis.model.TestClass",
		                                                                             "test",
		                                                                             new ArrayList<String>(0),
		                                                                             "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                             34, 56, 7854, 34, true);
		
		final JavaElementLocation methodCallLocation = set.addMethodCall("org.mozkito.codeanalysis.model.TestClass",
		                                                                 "test",
		                                                                 new ArrayList<String>(0),
		                                                                 "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                 methodDefinitionLocation.getElement(), 34, 56,
		                                                                 7854);
		final JavaElementLocations javaElementLocations = set.getJavaElementLocations();
		
		assertEquals(3, javaElementLocations.getClassDefs().size());
		assertTrue(javaElementLocations.getClassDefs().contains(classLocation));
		assertTrue(javaElementLocations.getClassDefs().contains(anonymousClassLocation));
		assertTrue(javaElementLocations.getClassDefs().contains(interfaceLocation));
		
		assertEquals(2, javaElementLocations.getClassDefs("org/mozkito/codeanalysis/model/TestClass.java").size());
		assertTrue(javaElementLocations.getClassDefs("org/mozkito/codeanalysis/model/TestClass.java")
		                               .contains(anonymousClassLocation));
		assertTrue(javaElementLocations.getClassDefs("org/mozkito/codeanalysis/model/TestClass.java")
		                               .contains(classLocation));
		final Map<String, TreeSet<JavaElementLocation>> map = javaElementLocations.getClassDefsByFile();
		assertEquals(2, map.size());
		assertTrue(map.containsKey("org/mozkito/codeanalysis/model/TestClass.java"));
		assertEquals(2, map.get("org/mozkito/codeanalysis/model/TestClass.java").size());
		assertTrue(map.get("org/mozkito/codeanalysis/model/TestClass.java").contains(anonymousClassLocation));
		assertTrue(map.get("org/mozkito/codeanalysis/model/TestClass.java").contains(classLocation));
		assertEquals(1, map.get("org/mozkito/codeanalysis/model/TestInterface.java").size());
		assertTrue(map.get("org/mozkito/codeanalysis/model/TestInterface.java").contains(interfaceLocation));
		
		assertEquals(5, javaElementLocations.getElements().size());
		assertTrue(javaElementLocations.getElements().contains(anonymousClassLocation));
		assertTrue(javaElementLocations.getElements().contains(classLocation));
		assertTrue(javaElementLocations.getElements().contains(interfaceLocation));
		assertTrue(javaElementLocations.getElements().contains(methodCallLocation));
		assertTrue(javaElementLocations.getElements().contains(methodDefinitionLocation));
		
		assertEquals(4, javaElementLocations.getElements("org/mozkito/codeanalysis/model/TestClass.java").size());
		assertTrue(javaElementLocations.getElements("org/mozkito/codeanalysis/model/TestClass.java")
		                               .contains(anonymousClassLocation));
		assertTrue(javaElementLocations.getElements("org/mozkito/codeanalysis/model/TestClass.java")
		                               .contains(classLocation));
		assertTrue(javaElementLocations.getElements("org/mozkito/codeanalysis/model/TestClass.java")
		                               .contains(methodDefinitionLocation));
		assertTrue(javaElementLocations.getElements("org/mozkito/codeanalysis/model/TestClass.java")
		                               .contains(methodCallLocation));
		assertEquals(1, javaElementLocations.getElements("org/mozkito/codeanalysis/model/TestInterface.java").size());
		assertTrue(javaElementLocations.getElements("org/mozkito/codeanalysis/model/TestInterface.java")
		                               .contains(interfaceLocation));
		
		final Map<String, Collection<JavaElementLocation>> elementsByFile = javaElementLocations.getElementsByFile();
		assertEquals(2, elementsByFile.size());
		assertTrue(elementsByFile.containsKey("org/mozkito/codeanalysis/model/TestClass.java"));
		assertEquals(4, elementsByFile.get("org/mozkito/codeanalysis/model/TestClass.java").size());
		assertTrue(elementsByFile.get("org/mozkito/codeanalysis/model/TestClass.java").contains(anonymousClassLocation));
		assertTrue(elementsByFile.get("org/mozkito/codeanalysis/model/TestClass.java").contains(classLocation));
		assertTrue(elementsByFile.get("org/mozkito/codeanalysis/model/TestClass.java")
		                         .contains(methodDefinitionLocation));
		assertTrue(elementsByFile.get("org/mozkito/codeanalysis/model/TestClass.java").contains(methodCallLocation));
		
		assertTrue(elementsByFile.containsKey("org/mozkito/codeanalysis/model/TestInterface.java"));
		assertEquals(1, elementsByFile.get("org/mozkito/codeanalysis/model/TestInterface.java").size());
		assertTrue(elementsByFile.get("org/mozkito/codeanalysis/model/TestInterface.java").contains(interfaceLocation));
		
		assertEquals(1, javaElementLocations.getMethodCalls().size());
		assertTrue(javaElementLocations.getMethodCalls().contains(methodCallLocation));
		assertEquals(0, javaElementLocations.getMethodCalls("org/mozkito/codeanalysis/model/TestInterface.java").size());
		assertEquals(1, javaElementLocations.getMethodCalls("org/mozkito/codeanalysis/model/TestClass.java").size());
		assertTrue(javaElementLocations.getMethodCalls("org/mozkito/codeanalysis/model/TestClass.java")
		                               .contains(methodCallLocation));
		final Map<String, TreeSet<JavaElementLocation>> methodCallsByFile = javaElementLocations.getMethodCallsByFile();
		assertEquals(1, methodCallsByFile.size());
		assertTrue(methodCallsByFile.containsKey("org/mozkito/codeanalysis/model/TestClass.java"));
		assertEquals(1, methodCallsByFile.get("org/mozkito/codeanalysis/model/TestClass.java").size());
		assertTrue(methodCallsByFile.get("org/mozkito/codeanalysis/model/TestClass.java").contains(methodCallLocation));
		
		assertEquals(1, javaElementLocations.getMethodDefs().size());
		assertTrue(javaElementLocations.getMethodDefs().contains(methodDefinitionLocation));
		assertEquals(0, javaElementLocations.getMethodDefs("org/mozkito/codeanalysis/model/TestInterface.java").size());
		assertEquals(1, javaElementLocations.getMethodDefs("org/mozkito/codeanalysis/model/TestClass.java").size());
		assertTrue(javaElementLocations.getMethodDefs("org/mozkito/codeanalysis/model/TestClass.java")
		                               .contains(methodDefinitionLocation));
		final Map<String, TreeSet<JavaElementLocation>> methodDefsByFile = javaElementLocations.getMethodDefsByFile();
		assertEquals(1, methodDefsByFile.size());
		assertTrue(methodDefsByFile.containsKey("org/mozkito/codeanalysis/model/TestClass.java"));
		assertEquals(1, methodDefsByFile.get("org/mozkito/codeanalysis/model/TestClass.java").size());
		assertTrue(methodDefsByFile.get("org/mozkito/codeanalysis/model/TestClass.java")
		                           .contains(methodDefinitionLocation));
		
	}
}
