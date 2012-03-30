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
package de.unisaarland.cs.st.moskito.ppa.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.ppa.model.JavaTypeDefinition;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;

public class JavaMethodCallTest {
	
	@Test
	public void testXML() {
		List<String> argList = new ArrayList<String>(2);
		argList.add("String");
		argList.add("Object");
		JavaMethodCall orgCall = new JavaMethodCall(JavaMethodCall.class.getName(), "foo", argList);
		Element xmlRepresentation = orgCall.getXMLRepresentation();
		JavaMethodCall xmlCall = JavaMethodCall.fromXMLRepresentation(xmlRepresentation);
		assertEquals(orgCall, xmlCall);
		
		assertEquals(null, JavaTypeDefinition.fromXMLRepresentation(xmlRepresentation));
		assertEquals(null, JavaMethodDefinition.fromXMLRepresentation(xmlRepresentation));
		
	}
}
