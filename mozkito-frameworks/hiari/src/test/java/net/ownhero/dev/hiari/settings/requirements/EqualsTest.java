/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package net.ownhero.dev.hiari.settings.requirements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.StringArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class EqualsTest {
	
	private static enum TestEnum {
		ONE, TWO;
		
	}
	
	@BeforeClass
	public static void beforeClass() {
		assert (System.getProperty("AllTest") == null);
		assert (System.getProperty("AllTest2") == null);
		assert (System.getProperty("AllTest3") == null);
		assert (System.getProperty("AllTest4") == null);
	}
	
	@After
	public void after() {
		System.clearProperty("AllTest");
		System.clearProperty("AllTest2");
		System.clearProperty("AllTest3");
		System.clearProperty("AllTest4");
	}
	
	@Before
	public void before() {
		System.clearProperty("AllTest");
		System.clearProperty("AllTest2");
		System.clearProperty("AllTest3");
		System.clearProperty("AllTest4");
	}
	
	@Test
	public void failtest() {
		try {
			
			assert (System.getProperty("AllTest") == null);
			assert (System.getProperty("AllTest4") == null);
			System.setProperty("AllTest", "two");
			final Settings settings = new Settings();
			final net.ownhero.dev.hiari.settings.EnumArgument.Options<TestEnum> enumOptions = new EnumArgument.Options<TestEnum>(
			                                                                                                                     settings.getRoot(),
			                                                                                                                     "AllTest",
			                                                                                                                     "",
			                                                                                                                     TestEnum.ONE,
			                                                                                                                     Requirement.optional);
			
			final Options options = new StringArgument.Options(settings.getRoot(), "AllTest4", "", null,
			                                                   new Equals(enumOptions, TestEnum.TWO));
			
			final EnumArgument<TestEnum> arg = ArgumentFactory.create(enumOptions);
			assertEquals(TestEnum.TWO, arg.getValue());
			
			try {
				ArgumentFactory.create(options);
				System.err.println(options.toString());
				fail();
			} catch (final ArgumentRegistrationException e) {
				//
			}
			
		} catch (SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	};
	
	@Test
	public void passtest() {
		try {
			
			assert (System.getProperty("AllTest") == null);
			assert (System.getProperty("AllTest4") == null);
			final Settings settings = new Settings();
			final net.ownhero.dev.hiari.settings.EnumArgument.Options<TestEnum> enumOptions = new EnumArgument.Options<TestEnum>(
			                                                                                                                     settings.getRoot(),
			                                                                                                                     "AllTest",
			                                                                                                                     "",
			                                                                                                                     TestEnum.ONE,
			                                                                                                                     Requirement.optional);
			
			final Options options = new StringArgument.Options(settings.getRoot(), "AllTest4", "", null,
			                                                   new Equals(enumOptions, TestEnum.TWO));
			
			ArgumentFactory.create(enumOptions);
			ArgumentFactory.create(options);
			
		} catch (SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
}
