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

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

public class JavaMethodCallTest {
	
	@Test
	public void testGetFullQualifiedName() {
		final List<String> argList = new ArrayList<String>(2);
		argList.add("String");
		argList.add("Object");
		final JavaMethodCall element = new JavaMethodCall(JavaMethodCall.class.getName(), "foo", argList);
		assertEquals("org.mozkito.codeanalysis.model.JavaMethodCall.foo(String,Object)", element.getFullQualifiedName());
	}
	
	@Test
	public void testGetShortName() {
		final List<String> argList = new ArrayList<String>(2);
		argList.add("String");
		argList.add("Object");
		final JavaMethodCall element = new JavaMethodCall(JavaMethodCall.class.getName(), "foo", argList);
		assertEquals("foo(String,Object)", element.getShortName());
	}
	
	@Test
	public void testPackageName() {
		final List<String> argList = new ArrayList<String>(2);
		argList.add("String");
		argList.add("Object");
		final JavaMethodCall element = new JavaMethodCall(JavaMethodCall.class.getName(), "foo", argList);
		assertEquals(JavaMethodCall.class.getPackage().getName(), element.getPackageName());
	}
	
	@Test
	public void testXML() {
		final List<String> argList = new ArrayList<String>(2);
		argList.add("String");
		argList.add("Object");
		final JavaMethodCall orgCall = new JavaMethodCall(JavaMethodCall.class.getName(), "foo", argList);
		final Element xmlRepresentation = orgCall.getXMLRepresentation();
		final JavaMethodCall xmlCall = JavaMethodCall.fromXMLRepresentation(xmlRepresentation);
		assertEquals(orgCall, xmlCall);
	}
}
