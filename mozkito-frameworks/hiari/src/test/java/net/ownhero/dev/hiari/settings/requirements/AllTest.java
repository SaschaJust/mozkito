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

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;

import net.ownhero.dev.hiari.settings.ArgumentFactory;
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
public class AllTest {
	
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
	public void onetest() {
		try {
			
			assert (System.getProperty("AllTest") == null);
			assert (System.getProperty("AllTest2") == null);
			assert (System.getProperty("AllTest3") == null);
			assert (System.getProperty("AllTest4") == null);
			System.setProperty("AllTest", "hubba");
			
			final Settings settings = new Settings();
			final Options stringOptions = new StringArgument.Options(settings.getRoot(), "AllTest", "", null,
			                                                         Requirement.optional);
			final Options string2Options = new StringArgument.Options(settings.getRoot(), "AllTest2", "", null,
			                                                          Requirement.optional);
			final Options string3Options = new StringArgument.Options(settings.getRoot(), "AllTest3", "", null,
			                                                          Requirement.optional);
			
			final Collection<Requirement> allOptions = new LinkedList<Requirement>();
			allOptions.add(new If(stringOptions));
			allOptions.add(new Not(new If(string2Options)));
			allOptions.add(new If(string3Options));
			
			final Options options = new StringArgument.Options(settings.getRoot(), "AllTest4", "", null,
			                                                   new All(allOptions));
			
			ArgumentFactory.create(stringOptions);
			ArgumentFactory.create(string2Options);
			ArgumentFactory.create(string3Options);
			ArgumentFactory.create(options);
			
		} catch (SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	@Test
	public void twotest() {
		try {
			assert (System.getProperty("AllTest") == null);
			assert (System.getProperty("AllTest2") == null);
			assert (System.getProperty("AllTest3") == null);
			assert (System.getProperty("AllTest4") == null);
			
			System.setProperty("AllTest", "hubba");
			// System.setProperty("AllTest2", "hubba");
			System.setProperty("AllTest3", "hubba");
			
			final Settings settings = new Settings();
			final Options stringOptions = new StringArgument.Options(settings.getRoot(), "AllTest", "", null,
			                                                         Requirement.optional);
			final Options string2Options = new StringArgument.Options(settings.getRoot(), "AllTest2", "", null,
			                                                          Requirement.optional);
			final Options string3Options = new StringArgument.Options(settings.getRoot(), "AllTest3", "", null,
			                                                          Requirement.optional);
			
			final Collection<Requirement> allOptions = new LinkedList<Requirement>();
			allOptions.add(new If(stringOptions));
			allOptions.add(new Not(new If(string2Options)));
			allOptions.add(new If(string3Options));
			
			final Options options = new StringArgument.Options(settings.getRoot(), "AllTest4", "", null,
			                                                   new All(allOptions));
			
			ArgumentFactory.create(stringOptions);
			ArgumentFactory.create(string2Options);
			ArgumentFactory.create(string3Options);
			
			try {
				ArgumentFactory.create(options);
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
	}
	
}
