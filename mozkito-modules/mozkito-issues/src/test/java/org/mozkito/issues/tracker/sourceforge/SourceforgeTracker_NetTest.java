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
package org.mozkito.issues.tracker.sourceforge;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import org.mozkito.issues.elements.Type;
import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.sourceforge.SourceforgeTracker;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class SourceforgeTracker_NetTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class SourceforgeTracker_NetTest {
	
	/**
	 * Test live overview.
	 */
	@Test
	@Ignore
	public void testLiveOverview() {
		final String liveUrl = "http://sourceforge.net/";
		
		final SourceforgeTracker tracker = new SourceforgeTracker(new IssueTracker(), new PersonFactory());
		try {
			tracker.setup(new URI(liveUrl), null, null, 97367l, 617889l, Type.BUG);
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		
		final Set<ReportLink> reportLinks = tracker.getReportLinks();
		final Set<String> ids = new HashSet<String>();
		assertTrue(reportLinks.size() >= 111);
		for (final ReportLink reportLink : reportLinks) {
			ids.add(reportLink.getBugId());
		}
		assertTrue(ids.contains("3192457"));
		assertTrue(ids.contains("3175612"));
		assertTrue(ids.contains("3175561"));
		assertTrue(ids.contains("3175068"));
		assertTrue(ids.contains("3161586"));
		assertTrue(ids.contains("3150326"));
		assertTrue(ids.contains("3117838"));
		assertTrue(ids.contains("3117678"));
		assertTrue(ids.contains("3105865"));
		assertTrue(ids.contains("3102760"));
		assertTrue(ids.contains("3100939"));
		assertTrue(ids.contains("3097754"));
		assertTrue(ids.contains("3090209"));
		assertTrue(ids.contains("3073905"));
		assertTrue(ids.contains("3072758"));
		assertTrue(ids.contains("3056104"));
		assertTrue(ids.contains("3048747"));
		assertTrue(ids.contains("3048468"));
		assertTrue(ids.contains("2990841"));
		assertTrue(ids.contains("2986043"));
		assertTrue(ids.contains("2984388"));
		assertTrue(ids.contains("2952991"));
		assertTrue(ids.contains("2943836"));
		assertTrue(ids.contains("2935625"));
		assertTrue(ids.contains("2927403"));
		assertTrue(ids.contains("2908360"));
		assertTrue(ids.contains("2903029"));
		assertTrue(ids.contains("2891940"));
		assertTrue(ids.contains("2889499"));
		assertTrue(ids.contains("2888113"));
		assertTrue(ids.contains("2880475"));
		assertTrue(ids.contains("2877833"));
		assertTrue(ids.contains("2865266"));
		assertTrue(ids.contains("2862109"));
		assertTrue(ids.contains("2848058"));
		assertTrue(ids.contains("2842202"));
		assertTrue(ids.contains("2825955"));
		assertTrue(ids.contains("2820871"));
		assertTrue(ids.contains("2813207"));
		assertTrue(ids.contains("2804258"));
		assertTrue(ids.contains("2788283"));
		assertTrue(ids.contains("2783325"));
		assertTrue(ids.contains("2726972"));
		assertTrue(ids.contains("2723212"));
		assertTrue(ids.contains("2721880"));
		assertTrue(ids.contains("2690370"));
		assertTrue(ids.contains("2553453"));
		assertTrue(ids.contains("2495455"));
		assertTrue(ids.contains("2487417"));
		assertTrue(ids.contains("2476980"));
		assertTrue(ids.contains("2461322"));
		assertTrue(ids.contains("2182444"));
		assertTrue(ids.contains("2111763"));
		assertTrue(ids.contains("2105134"));
		assertTrue(ids.contains("2038742"));
		assertTrue(ids.contains("2030810"));
		assertTrue(ids.contains("2025928"));
		assertTrue(ids.contains("2018795"));
		assertTrue(ids.contains("2015343"));
		assertTrue(ids.contains("2012274"));
		assertTrue(ids.contains("1945908"));
		assertTrue(ids.contains("1944307"));
		assertTrue(ids.contains("1929964"));
		assertTrue(ids.contains("1915752"));
		assertTrue(ids.contains("1887104"));
		assertTrue(ids.contains("1878425"));
		assertTrue(ids.contains("1878274"));
		assertTrue(ids.contains("1877843"));
		assertTrue(ids.contains("1855430"));
		assertTrue(ids.contains("1850115"));
		assertTrue(ids.contains("1849969"));
		assertTrue(ids.contains("1841568"));
		assertTrue(ids.contains("1839440"));
		assertTrue(ids.contains("1827409"));
		assertTrue(ids.contains("1826894"));
		assertTrue(ids.contains("1788383"));
		assertTrue(ids.contains("1788282"));
		assertTrue(ids.contains("1775054"));
		assertTrue(ids.contains("1755161"));
		assertTrue(ids.contains("1755158"));
		assertTrue(ids.contains("1747219"));
		assertTrue(ids.contains("1733614"));
		assertTrue(ids.contains("1716305"));
		assertTrue(ids.contains("1710316"));
		assertTrue(ids.contains("1707532"));
		assertTrue(ids.contains("1700269"));
		assertTrue(ids.contains("1699760"));
		assertTrue(ids.contains("1682152"));
		assertTrue(ids.contains("1682046"));
		assertTrue(ids.contains("1665180"));
		assertTrue(ids.contains("1660490"));
		assertTrue(ids.contains("1620722"));
		assertTrue(ids.contains("1592342"));
		assertTrue(ids.contains("1576727"));
		assertTrue(ids.contains("1552358"));
		assertTrue(ids.contains("1530525"));
		assertTrue(ids.contains("1517962"));
		assertTrue(ids.contains("1490975"));
		assertTrue(ids.contains("1449760"));
		assertTrue(ids.contains("1449459"));
		assertTrue(ids.contains("1437757"));
		assertTrue(ids.contains("1375249"));
		assertTrue(ids.contains("1347976"));
		assertTrue(ids.contains("1277337"));
		assertTrue(ids.contains("1195817"));
		assertTrue(ids.contains("1184542"));
		assertTrue(ids.contains("1081969"));
		assertTrue(ids.contains("1081957"));
		assertTrue(ids.contains("954058"));
		assertTrue(ids.contains("949267"));
		assertTrue(ids.contains("943071"));
	}
	
	/**
	 * Test setup.
	 */
	@Test
	@Ignore
	public void testSetup() {
		final SourceforgeTracker tracker = new SourceforgeTracker(new IssueTracker(), new PersonFactory());
		try {
			tracker.setup(getClass().getResource(FileUtils.fileSeparator).toURI(), null, null, 97367l, 617889l,
			              Type.BUG);
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
}
