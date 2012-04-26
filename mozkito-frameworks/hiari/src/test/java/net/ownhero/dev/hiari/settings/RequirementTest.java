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
package net.ownhero.dev.hiari.settings;

import static org.junit.Assert.fail;
import net.ownhero.dev.hiari.settings.StringArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.Test;

/**
 * The Class RequirementTest.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class RequirementTest {
	
	@Test
	public void optionalIffFailTest() {
		try {
			
			System.setProperty("optionalIffFailTest", "hubba");
			
			final Settings settings = new Settings();
			final Options stringOptions = new StringArgument.Options(settings.getRoot(), "optionalIffFailTest", "",
			                                                         null, Requirement.optional);
			final Options iffOptions = new StringArgument.Options(settings.getRoot(), "optionalIffFailTest2", "", null,
			                                                      Requirement.iff(stringOptions));
			
			try {
				ArgumentFactory.create(stringOptions);
			} catch (final ArgumentRegistrationException e) {
				e.printStackTrace();
				fail();
			}
			ArgumentFactory.create(iffOptions);
			
		} catch (SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentRegistrationException e) {
			//
		} finally {
			//
		}
	}
	
	@Test
	public void optionalIffNot2Test() {
		try {
			
			System.setProperty("optionalIffNotTest", "hubba");
			
			final Settings settings = new Settings();
			final Options stringOptions = new StringArgument.Options(settings.getRoot(), "optionalIffNotTest", "",
			                                                         null, Requirement.optional);
			final Options iffOptions = new StringArgument.Options(settings.getRoot(), "optionalIffNotTest2", "", null,
			                                                      Requirement.not(Requirement.iff(stringOptions)));
			
			ArgumentFactory.create(stringOptions);
			ArgumentFactory.create(iffOptions);
			
		} catch (SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	@Test
	public void optionalIffNotFailTest() {
		try {
			
			final Settings settings = new Settings();
			final Options stringOptions = new StringArgument.Options(settings.getRoot(), "optionalIffNotFailTest", "",
			                                                         null, Requirement.optional);
			final Options iffOptions = new StringArgument.Options(settings.getRoot(), "optionalIffNotFailTest2", "",
			                                                      null, Requirement.not(Requirement.iff(stringOptions)));
			
			try {
				ArgumentFactory.create(stringOptions);
			} catch (final ArgumentRegistrationException e) {
				e.printStackTrace();
				fail();
			}
			ArgumentFactory.create(iffOptions);
			
		} catch (SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentRegistrationException e) {
			//
		} finally {
			//
		}
	}
	
	@Test
	public void optionalIffNotTest() {
		try {
			
			System.setProperty("optionalIffNotTest2", "hubba");
			
			final Settings settings = new Settings();
			final Options stringOptions = new StringArgument.Options(settings.getRoot(), "optionalIffNotTest", "",
			                                                         null, Requirement.optional);
			final Options iffOptions = new StringArgument.Options(settings.getRoot(), "optionalIffNotTest2", "", null,
			                                                      Requirement.not(Requirement.iff(stringOptions)));
			
			ArgumentFactory.create(stringOptions);
			ArgumentFactory.create(iffOptions);
			
		} catch (SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	@Test
	public void optionalIffTest() {
		try {
			
			System.setProperty("optionalIffTest2", "hubba");
			
			final Settings settings = new Settings();
			final Options stringOptions = new StringArgument.Options(settings.getRoot(), "optionalIffTest", "", null,
			                                                         Requirement.optional);
			final Options iffOptions = new StringArgument.Options(settings.getRoot(), "optionalIffTest2", "", null,
			                                                      Requirement.iff(stringOptions));
			
			ArgumentFactory.create(stringOptions);
			ArgumentFactory.create(iffOptions);
			
		} catch (SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
}
