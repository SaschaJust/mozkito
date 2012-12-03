/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.codeanalysis.model;

import static org.junit.Assert.assertEquals;

import org.jdom2.Element;
import org.junit.Test;

/**
 * The Class JavaClassDefinitionTest.
 */
public class JavaClassDefinitionTest {
	
	/**
	 * Test xml.
	 */
	@Test
	public void testXML() {
		final JavaElementFactory elementFactory = new JavaElementFactory();
		final JavaTypeDefinition orgDef = new JavaTypeDefinition(JavaTypeDefinition.class.getName());
		final Element xmlRepresentation = orgDef.getXMLRepresentation();
		final JavaTypeDefinition xmlDef = JavaTypeDefinition.fromXMLRepresentation(xmlRepresentation, elementFactory);
		assertEquals(orgDef, xmlDef);
	}
}
