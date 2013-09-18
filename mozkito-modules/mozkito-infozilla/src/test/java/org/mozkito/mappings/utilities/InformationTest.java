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
package org.mozkito.mappings.utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.kisa.Logger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.mozkito.infozilla.filters.enumeration.Enumeration;
import org.mozkito.infozilla.filters.enumeration.Enumeration.EnumerationEntry;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class InformationTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Ignore
public class InformationTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The text. */
	public static String text = null;
	
	/**
	 * Sets the up before class.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final InputStream stream = InformationTest.class.getResourceAsStream("/org/mozkito/mappings/enum_test.txt"); //$NON-NLS-1$
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		final StringBuilder builder = new StringBuilder();
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			builder.append(line).append(FileUtils.lineSeparator);
		}
		
		InformationTest.text = builder.toString();
		reader.close();
		stream.close();
	}
	
	/**
	 * Tear down after class.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (Logger.logInfo()) {
			Logger.info(InformationTest.class.getSimpleName() + " completed."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Test enumerations.
	 */
	@Test
	public final void testEnumerations() {
		final Collection<Enumeration> enumerations = Enumeration.extract(InformationTest.text);
		int i = 0;
		
		for (final Enumeration map : enumerations) {
			if (Logger.logInfo()) {
				Logger.info(String.format("===== Set number (%s) =====", ++i));
			}
			
			int j = 0;
			
			for (final EnumerationEntry bullet : map) {
				if (Logger.logInfo()) {
					Logger.info(String.format("Item %3s - Bullet: %s", ++j, bullet));
				}
			}
		}
		Assert.fail("Not yet implemented");
	}
	
}
