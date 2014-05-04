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
package net.ownhero.dev.hiari.settings;

import static org.junit.Assert.fail;
import net.ownhero.dev.hiari.settings.PortArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class PortArgumentTest.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PortArgumentTest {
	
	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		assert (System.getProperty("port") == null);
	}
	
	/**
	 * After.
	 */
	@After
	public void after() {
		System.clearProperty("port");
	}
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		System.clearProperty("port");
	}
	
	/**
	 * Test.
	 */
	@Test
	public void test() {
		try {
			System.setProperty("port", "8584");
			final Settings settings = new Settings();
			final Options options = new PortArgument.Options(settings.getRoot(), "port", "", null,
			                                                 Requirement.required, true);
			final PortArgument argument = ArgumentFactory.create(options);
			argument.getValue();
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
}
