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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Set;

import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import org.mozkito.codeanalysis.model.JavaElementLocation.LineCover;

/**
 * The Class JavaElementLocationTest.
 */
public class JavaElementLocationTest {
	
	/** The element factory. */
	private JavaElementFactory     elementFactory;
	
	/** The set. */
	private JavaElementLocationSet set;
	
	/** The java type. */
	private JavaTypeDefinition     javaType;
	
	/** The location. */
	private JavaElementLocation    location;
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		this.elementFactory = new JavaElementFactory();
		this.set = new JavaElementLocationSet(new JavaElementFactory());
		this.javaType = new JavaTypeDefinition("org.mozkito.codeanalysis.model.TestClass");
		this.location = this.set.addAnonymousClassDefinition(this.javaType,
		                                                     "org.mozkito.codeanalysis.model.TestClass$1",
		                                                     "org/mozkito/codeanalysis/model/TestClass.java", 20, 25,
		                                                     43674, 21);
		this.location.addCommentLines(22, 23);
	}
	
	/**
	 * Test covers all lines.
	 */
	@Test
	public void testCoversAllLines() {
		final ArrayList<Integer> lineList = new ArrayList<Integer>();
		lineList.add(20);
		assertEquals(LineCover.DEFINITION, this.location.coversAllLines(lineList));
		lineList.add(21);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(22);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(23);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(24);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(25);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(26);
		assertEquals(LineCover.FALSE, this.location.coversAllLines(lineList));
		lineList.clear();
		lineList.add(19);
		assertEquals(LineCover.FALSE, this.location.coversAllLines(lineList));
		lineList.clear();
		lineList.add(22);
		assertEquals(LineCover.FALSE, this.location.coversAllLines(lineList));
		lineList.add(24);
		assertEquals(LineCover.BODY, this.location.coversAllLines(lineList));
	}
	
	/**
	 * Test covers any line.
	 */
	@Test
	public void testCoversAnyLine() {
		final ArrayList<Integer> lineList = new ArrayList<Integer>();
		lineList.add(19);
		assertEquals(LineCover.FALSE, this.location.coversAnyLine(lineList));
		lineList.add(20);
		assertEquals(LineCover.DEFINITION, this.location.coversAnyLine(lineList));
		lineList.add(21);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(22);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(23);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(24);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(25);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(26);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.clear();
		lineList.add(22);
		assertEquals(LineCover.FALSE, this.location.coversAnyLine(lineList));
		lineList.add(24);
		assertEquals(LineCover.BODY, this.location.coversAnyLine(lineList));
	}
	
	/**
	 * Test covers line.
	 */
	@Test
	public void testCoversLine() {
		assertEquals(LineCover.FALSE, this.location.coversLine(19));
		assertEquals(LineCover.DEFINITION, this.location.coversLine(20));
		assertEquals(LineCover.BODY, this.location.coversLine(21));
		assertEquals(LineCover.FALSE, this.location.coversLine(22));
		assertEquals(LineCover.FALSE, this.location.coversLine(23));
		assertEquals(LineCover.BODY, this.location.coversLine(24));
		assertEquals(LineCover.BODY, this.location.coversLine(25));
		assertEquals(LineCover.FALSE, this.location.coversLine(26));
	}
	
	/**
	 * Test get comment lines.
	 */
	@Test
	public void testGetCommentLines() {
		final Set<Integer> commentLines = this.location.getCommentLines();
		assertEquals(2, commentLines.size());
		assertTrue(commentLines.contains(22));
		assertTrue(commentLines.contains(23));
	}
	
	/**
	 * Test xml.
	 */
	@Test
	public void testXML() {
		final Element xmlRepresentation = this.location.getXMLRepresentation();
		final JavaElementLocation xmlLocation = JavaElementLocation.fromXMLRepresentation(xmlRepresentation,
		                                                                                  this.elementFactory);
		assertEquals(this.location, xmlLocation);
	}
	
}
