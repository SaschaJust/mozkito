/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.junit.Test;

public class JavaMethodDefinitionTest {
	
	@Test
	public void testXML() {
		List<String> argList = new ArrayList<String>(2);
		argList.add("String");
		argList.add("Object");
		JavaMethodDefinition orgDef = new JavaMethodDefinition(JavaMethodDefinition.class.getName(), "foo", argList);
		Element xmlRepresentation = orgDef.getXMLRepresentation();
		JavaMethodDefinition xmlDef = JavaMethodDefinition.fromXMLRepresentation(xmlRepresentation);
		assertEquals(orgDef, xmlDef);
		
		assertEquals(null, JavaMethodCall.fromXMLRepresentation(xmlRepresentation));
		assertEquals(null, JavaClassDefinition.fromXMLRepresentation(xmlRepresentation));
	}
	
}
