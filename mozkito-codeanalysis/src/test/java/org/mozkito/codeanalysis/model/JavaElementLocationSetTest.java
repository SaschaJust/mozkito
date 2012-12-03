/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.codeanalysis.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
	
	@Test
	public void testJavaElementLocations() {
		final JavaElementLocationSet set = new JavaElementLocationSet(new JavaElementFactory());
		
		final JavaTypeDefinition javaType = new JavaTypeDefinition("org.mozkito.codeanalysis.model.TestClass");
		
		final JavaElementLocation anonymousClassLocation = set.addAnonymousClassDefinition(javaType,
		                                                                                   "org.mozkito.codeanalysis.model.TestClass$1",
		                                                                                   "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                                   20, 23, 43674, 20);
		
		final JavaElementLocation classLocation = set.addClassDefinition("org.mozkito.codeanalysis.model.TestClass",
		                                                                 "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                 10, 200, 463, 11);
		
		final JavaElementLocation methodDefinitionLocation = set.addMethodDefinition("org.mozkito.codeanalysis.model.TestClass",
		                                                                             "test",
		                                                                             new ArrayList<String>(0),
		                                                                             "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                             34, 56, 7854, 34, true);
		
		final JavaElementLocation methodDefinitionLocation2 = set.addMethodDefinition("org.mozkito.codeanalysis.model.TestClass",
		                                                                              "test",
		                                                                              new ArrayList<String>(0),
		                                                                              "org/mozkito/codeanalysis/model/TestClass3.java",
		                                                                              34, 56, 7854, 34, true);
		
		final JavaElementLocation methodCallLocation = set.addMethodCall("org.mozkito.codeanalysis.model.TestClass",
		                                                                 "test",
		                                                                 new ArrayList<String>(0),
		                                                                 "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                 methodDefinitionLocation.getElement(), 34, 56,
		                                                                 7854);
		
		final JavaElementLocation methodCall2Location = set.addMethodCall("org.mozkito.codeanalysis.model.TestClass",
		                                                                  "test",
		                                                                  new ArrayList<String>(0),
		                                                                  "org/mozkito/codeanalysis/model/TestClass2.java",
		                                                                  methodDefinitionLocation.getElement(), 34,
		                                                                  56, 7854);
		final JavaElementLocations javaElementLocations = new JavaElementLocations();
		
		assertTrue(javaElementLocations.addClassDef(anonymousClassLocation));
		assertTrue(javaElementLocations.addClassDef(classLocation));
		assertFalse(javaElementLocations.addClassDef(classLocation));
		assertFalse(javaElementLocations.addClassDef(methodCallLocation));
		assertFalse(javaElementLocations.addClassDef(methodDefinitionLocation));
		
		assertTrue(javaElementLocations.addMethodCall(methodCallLocation));
		assertTrue(javaElementLocations.addMethodCall(methodCall2Location));
		assertFalse(javaElementLocations.addMethodCall(methodCallLocation));
		assertFalse(javaElementLocations.addMethodCall(methodDefinitionLocation));
		assertFalse(javaElementLocations.addMethodCall(classLocation));
		
		assertTrue(javaElementLocations.addMethodDef(methodDefinitionLocation));
		assertTrue(javaElementLocations.addMethodDef(methodDefinitionLocation2));
		assertFalse(javaElementLocations.addMethodDef(methodDefinitionLocation));
		assertFalse(javaElementLocations.addMethodDef(classLocation));
		assertFalse(javaElementLocations.addMethodDef(methodCallLocation));
		
		assertTrue(javaElementLocations.containsFilePath("org/mozkito/codeanalysis/model/TestClass.java"));
		assertTrue(javaElementLocations.containsFilePath("org/mozkito/codeanalysis/model/TestClass.java"));
		assertTrue(javaElementLocations.containsFilePath("org/mozkito/codeanalysis/model/TestClass2.java"));
		assertTrue(javaElementLocations.containsFilePath("org/mozkito/codeanalysis/model/TestClass3.java"));
		assertFalse(javaElementLocations.containsFilePath("org/mozkito/codeanalysis/model/TestClass"));
		
		final Collection<JavaElementLocation> defs = javaElementLocations.getDefs();
		assertEquals(4, defs.size());
		assertTrue(defs.contains(anonymousClassLocation));
		assertTrue(defs.contains(classLocation));
		assertTrue(defs.contains(methodDefinitionLocation));
		assertTrue(defs.contains(methodDefinitionLocation2));
		
		assertEquals(3, javaElementLocations.getDefs("org/mozkito/codeanalysis/model/TestClass.java").size());
		assertEquals(0, javaElementLocations.getDefs("org/mozkito/codeanalysis/model/TestClass2.java").size());
		assertEquals(1, javaElementLocations.getDefs("org/mozkito/codeanalysis/model/TestClass3.java").size());
		
		final Map<String, Collection<JavaElementLocation>> defsByFile = javaElementLocations.getDefsByFile();
		assertEquals(2, defsByFile.size());
		assertTrue(defsByFile.containsKey("org/mozkito/codeanalysis/model/TestClass.java"));
		assertFalse(defsByFile.containsKey("org/mozkito/codeanalysis/model/TestClass2.java"));
		assertTrue(defsByFile.containsKey("org/mozkito/codeanalysis/model/TestClass3.java"));
	}
}
